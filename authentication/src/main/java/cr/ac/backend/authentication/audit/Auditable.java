package cr.ac.backend.authentication.audit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotación para marcar métodos que deben ser auditados
 * El aspecto AuditAspect capturará automáticamente la ejecución
 * y publicará un AuditEvent a RabbitMQ
 * 
 * Ejemplo:
 * @Auditable(action = "LOGIN", resource = "Authentication")
 * public AuthResponse login(LoginRequest request) { ... }
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Auditable {
    
    /**
     * Acción que se está auditando
     * Ejemplos: "LOGIN", "REGISTER", "UPDATE_USER", "DELETE_USER"
     */
    String action();
    
    /**
     * Recurso sobre el que se realiza la acción
     * Ejemplos: "Authentication", "User", "WorkoutPlan"
     */
    String resource() default "System";
    
    /**
     * Detalles adicionales opcionales
     */
    String details() default "";
}
