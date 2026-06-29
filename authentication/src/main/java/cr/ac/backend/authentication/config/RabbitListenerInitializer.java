package cr.ac.backend.authentication.config;

import cr.ac.backend.authentication.listener.AuditEventListener;
import cr.ac.backend.authentication.listener.EmailEventListener;
import cr.ac.backend.authentication.listener.UserEventListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Componente para forzar la inicialización de RabbitListeners.
 * 
 * PROBLEMA: spring.main.lazy-initialization=true previene que los @RabbitListener
 * se registren automáticamente, causando que los mensajes se acumulen en las queues
 * sin ser procesados.
 * 
 * SOLUCIÓN: Este componente autowirea todos los listeners y los referencia en
 * un método @EventListener(ApplicationReadyEvent), forzando su inicialización
 * cuando la aplicación está lista.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RabbitListenerInitializer {
    
    private final EmailEventListener emailEventListener;
    private final UserEventListener userEventListener;
    private final AuditEventListener auditEventListener;
    
    /**
     * Fuerza la inicialización de todos los RabbitListeners cuando la aplicación está lista.
     * Esto asegura que los listeners se registren correctamente incluso con lazy-initialization.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void initializeListeners() {
        log.info("🎧 Inicializando RabbitMQ Listeners...");
        
        // Solo autowirear los beans es suficiente para inicializarlos
        // Spring registrará automáticamente los @RabbitListener una vez instanciados
        
        log.info("✅ EmailEventListener inicializado: {}", emailEventListener.getClass().getSimpleName());
        log.info("✅ UserEventListener inicializado: {}", userEventListener.getClass().getSimpleName());
        log.info("✅ AuditEventListener inicializado: {}", auditEventListener.getClass().getSimpleName());
        
        log.info("🎉 Todos los RabbitMQ Listeners inicializados exitosamente");
    }
}
