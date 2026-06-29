package cr.ac.backend.authentication.listener;

import cr.ac.backend.authentication.event.AuditEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Listener que consume eventos de auditoría desde RabbitMQ
 * y los registra en los logs del sistema
 * 
 * En el futuro, podría persistir los eventos en una tabla audit_log
 * o enviarlos a un sistema de monitoreo externo
 */
@Component
public class AuditEventListener {
    
    private static final Logger logger = LoggerFactory.getLogger(AuditEventListener.class);
    
    /**
     * Procesa eventos de auditoría
     * 
     * @param event el evento de auditoría recibido
     */
    @RabbitListener(queues = "audit.queue")
    public void handleAuditEvent(AuditEvent event) {
        try {
            logger.info("📋 AUDIT - Action: {} | Resource: {} | User: {} | IP: {} | Status: {} | Timestamp: {} | Details: {}",
                    event.getAction(),
                    event.getResource(),
                    event.getUserName() != null ? event.getUserName() : "ANONYMOUS",
                    event.getIpAddress() != null ? event.getIpAddress() : "UNKNOWN",
                    event.getStatus(),
                    event.getTimestamp(),
                    event.getDetails());
            
            // Si el evento fue un error, registrar el error también
            if ("FAILURE".equals(event.getStatus()) || "ERROR".equals(event.getStatus())) {
                logger.warn("⚠️ AUDIT ERROR - Action: {} | Error: {}", 
                        event.getAction(), 
                        event.getErrorMessage());
            }
            
            // TODO: Futuras mejoras:
            // - Persistir en tabla audit_log en base de datos
            // - Enviar alertas para acciones críticas fallidas
            // - Integrar con sistema de métricas (Prometheus)
            // - Exportar a herramientas de SIEM (Security Information and Event Management)
            // - updateAuditStatistics(event) para contadores de métricas
            
        } catch (Exception e) {
            logger.error("❌ Error al procesar evento de auditoría: {}", e.getMessage(), e);
            // No lanzar excepción para evitar reintento innecesario
            // El evento ya fue registrado en logs
        }
    }
}
