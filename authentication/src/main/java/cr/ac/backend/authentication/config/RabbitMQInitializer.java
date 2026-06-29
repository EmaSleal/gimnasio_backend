package cr.ac.backend.authentication.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Inicializador que fuerza la creación de exchanges, queues y bindings en RabbitMQ
 * Se ejecuta después de que la aplicación esté completamente iniciada
 */
@Component
public class RabbitMQInitializer {
    
    private static final Logger logger = LoggerFactory.getLogger(RabbitMQInitializer.class);
    
    @Autowired
    private RabbitAdmin rabbitAdmin;
    
    @Autowired
    private List<TopicExchange> topicExchanges;
    
    @Autowired
    private List<DirectExchange> directExchanges;
    
    @Autowired
    private List<Queue> queues;
    
    @Autowired
    private List<Binding> bindings;
    
    @EventListener(ApplicationReadyEvent.class)
    public void initializeRabbitMQ() {
        logger.info("🔧 Inicializando configuración de RabbitMQ...");
        
        try {
            // Declarar todos los exchanges
            logger.info("📤 Declarando {} TopicExchanges...", topicExchanges.size());
            topicExchanges.forEach(exchange -> {
                rabbitAdmin.declareExchange(exchange);
                logger.info("✅ Exchange creado: {}", exchange.getName());
            });
            
            logger.info("📤 Declarando {} DirectExchanges...", directExchanges.size());
            directExchanges.forEach(exchange -> {
                rabbitAdmin.declareExchange(exchange);
                logger.info("✅ Exchange creado: {}", exchange.getName());
            });
            
            // Declarar todas las queues
            logger.info("📥 Declarando {} queues...", queues.size());
            queues.forEach(queue -> {
                rabbitAdmin.declareQueue(queue);
                logger.info("✅ Queue creada: {}", queue.getName());
            });
            
            // Declarar todos los bindings
            logger.info("🔗 Declarando {} bindings...", bindings.size());
            bindings.forEach(binding -> {
                rabbitAdmin.declareBinding(binding);
                logger.debug("✅ Binding creado: {}", binding);
            });
            
            logger.info("🎉 Configuración de RabbitMQ completada exitosamente");
            
        } catch (Exception e) {
            logger.error("❌ Error al inicializar RabbitMQ: {}", e.getMessage(), e);
        }
    }
}
