package cr.ac.backend.authentication.audit;

import cr.ac.backend.authentication.event.AuditEvent;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;

/**
 * Aspecto que intercepta métodos anotados con @Auditable
 * y publica eventos de auditoría a RabbitMQ
 */
@Aspect
@Component
public class AuditAspect {
    
    private static final Logger logger = LoggerFactory.getLogger(AuditAspect.class);
    
    private static final String AUDIT_EXCHANGE = "audit.exchange";
    private static final String AUDIT_ROUTING_KEY = "audit.event";
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    /**
     * Intercepta todos los métodos anotados con @Auditable
     */
    @Around("@annotation(cr.ac.backend.authentication.audit.Auditable)")
    public Object auditMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Auditable auditable = method.getAnnotation(Auditable.class);
        
        AuditEvent auditEvent = new AuditEvent();
        auditEvent.setAction(auditable.action());
        auditEvent.setResource(auditable.resource());
        
        // Extraer información del usuario del request (desde JWT token header si está disponible)
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String authHeader = request.getHeader("Authorization");
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    // El userName se puede extraer del token si es necesario
                    // Por ahora, se marcará como autenticado
                    auditEvent.setUserName("AUTHENTICATED_USER");
                }
            }
        } catch (Exception e) {
            logger.debug("No se pudo extraer usuario del token: {}", e.getMessage());
        }
        
        // Extraer IP address del request HTTP
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String ipAddress = getClientIpAddress(request);
                auditEvent.setIpAddress(ipAddress);
            }
        } catch (Exception e) {
            logger.debug("No se pudo extraer IP address: {}", e.getMessage());
        }
        
        // Agregar detalles adicionales si están especificados
        if (!auditable.details().isEmpty()) {
            auditEvent.setDetails(auditable.details());
        }
        
        long startTime = System.currentTimeMillis();
        Object result = null;
        
        try {
            // Ejecutar el método original
            result = joinPoint.proceed();
            
            // Marcar como exitoso
            auditEvent.setStatus("SUCCESS");
            
            // Si el resultado contiene información de userId, intentar extraerlo
            tryExtractUserIdFromResult(result, auditEvent);
            
            return result;
            
        } catch (Exception e) {
            // Marcar como fallido
            auditEvent.setStatus("FAILURE");
            auditEvent.setErrorMessage(e.getMessage());
            throw e;
            
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            
            // Agregar duración al detalle
            String durationDetail = "Duration: " + duration + "ms";
            if (auditEvent.getDetails() != null && !auditEvent.getDetails().isEmpty()) {
                auditEvent.setDetails(auditEvent.getDetails() + " | " + durationDetail);
            } else {
                auditEvent.setDetails(durationDetail);
            }
            
            // Publicar evento de auditoría
            publishAuditEvent(auditEvent);
        }
    }
    
    /**
     * Publica el evento de auditoría a RabbitMQ
     */
    private void publishAuditEvent(AuditEvent event) {
        try {
            logger.info("📋 Auditando acción: {} en recurso: {} - Usuario: {} - IP: {} - Status: {}", 
                    event.getAction(), 
                    event.getResource(), 
                    event.getUserName() != null ? event.getUserName() : "ANONYMOUS",
                    event.getIpAddress(),
                    event.getStatus());
            
            rabbitTemplate.convertAndSend(AUDIT_EXCHANGE, AUDIT_ROUTING_KEY, event);
            
            logger.debug("✅ Evento de auditoría publicado exitosamente");
            
        } catch (Exception e) {
            logger.error("❌ Error al publicar evento de auditoría: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Intenta extraer el userId del resultado del método
     */
    private void tryExtractUserIdFromResult(Object result, AuditEvent auditEvent) {
        if (result == null) return;
        
        try {
            // Intentar obtener userId mediante reflexión
            Method getUserIdMethod = result.getClass().getMethod("getUserId");
            Object userId = getUserIdMethod.invoke(result);
            if (userId instanceof Long) {
                auditEvent.setUserId((Long) userId);
            }
        } catch (Exception e) {
            // Si no se puede extraer, no es crítico
            logger.debug("No se pudo extraer userId del resultado: {}", e.getMessage());
        }
    }
    
    /**
     * Obtiene la IP del cliente, considerando proxies
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String[] headerNames = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR"
        };
        
        for (String header : headerNames) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                // X-Forwarded-For puede contener múltiples IPs separadas por coma
                if (ip.contains(",")) {
                    ip = ip.split(",")[0].trim();
                }
                return ip;
            }
        }
        
        return request.getRemoteAddr();
    }
}
