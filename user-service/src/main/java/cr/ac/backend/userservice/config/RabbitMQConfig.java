package cr.ac.backend.userservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de RabbitMQ para User Service
 * 
 * Exchanges configurados:
 * - user.exchange: Para eventos relacionados con usuarios
 * 
 * Queues configuradas:
 * - user.created.queue: Eventos de usuario creado
 * - user.created.dlq: Dead Letter Queue para user.created
 * 
 * @author Sprint 3 - Fase 1
 * @since 2025-11-02
 */
@Configuration
public class RabbitMQConfig {

    // ==================== EXCHANGE NAMES ====================
    public static final String USER_EXCHANGE = "user.exchange";
    public static final String DLX_EXCHANGE = "dlx.exchange"; // Dead Letter Exchange
    
    // ==================== QUEUE NAMES ====================
    public static final String USER_CREATED_QUEUE = "user.created.queue";
    public static final String USER_CREATED_DLQ = "user.created.dlq";
    
    // ==================== ROUTING KEYS ====================
    public static final String USER_CREATED_ROUTING_KEY = "user.created";
    public static final String USER_CREATED_DLQ_ROUTING_KEY = "user.created.dlq";
    
    // ==================== TTL (Time To Live) ====================
    private static final Integer MESSAGE_TTL = 86400000; // 24 horas en milisegundos
    
    // ==================== EXCHANGES ====================
    
    /**
     * Exchange principal para eventos de usuario
     * Tipo: Topic - permite routing flexible con patrones
     */
    @Bean
    public TopicExchange userExchange() {
        return ExchangeBuilder
                .topicExchange(USER_EXCHANGE)
                .durable(true) // Sobrevive a reinicios de RabbitMQ
                .build();
    }
    
    /**
     * Dead Letter Exchange - recibe mensajes que fallaron
     */
    @Bean
    public DirectExchange deadLetterExchange() {
        return ExchangeBuilder
                .directExchange(DLX_EXCHANGE)
                .durable(true)
                .build();
    }
    
    // ==================== QUEUES ====================
    
    /**
     * Queue para eventos de usuario creado
     * Configurada con:
     * - DLX: Para mensajes que fallan
     * - TTL: 24 horas de vida del mensaje
     */
    @Bean
    public Queue userCreatedQueue() {
        return QueueBuilder
                .durable(USER_CREATED_QUEUE)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", USER_CREATED_DLQ_ROUTING_KEY)
                .withArgument("x-message-ttl", MESSAGE_TTL)
                .build();
    }
    
    /**
     * Dead Letter Queue para user.created
     * Almacena mensajes que fallaron después de reintentos
     */
    @Bean
    public Queue userCreatedDLQ() {
        return QueueBuilder
                .durable(USER_CREATED_DLQ)
                .build();
    }
    
    // ==================== BINDINGS ====================
    
    /**
     * Binding: user.exchange -> user.created.queue
     * Routing key: user.created
     */
    @Bean
    public Binding userCreatedBinding(Queue userCreatedQueue, TopicExchange userExchange) {
        return BindingBuilder
                .bind(userCreatedQueue)
                .to(userExchange)
                .with(USER_CREATED_ROUTING_KEY);
    }
    
    /**
     * Binding: dlx.exchange -> user.created.dlq
     * Routing key: user.created.dlq
     */
    @Bean
    public Binding userCreatedDLQBinding(Queue userCreatedDLQ, DirectExchange deadLetterExchange) {
        return BindingBuilder
                .bind(userCreatedDLQ)
                .to(deadLetterExchange)
                .with(USER_CREATED_DLQ_ROUTING_KEY);
    }
    
    // ==================== MESSAGE CONVERTER ====================
    
    /**
     * Converter para serializar/deserializar mensajes como JSON
     * Usa Jackson para convertir objetos Java a/desde JSON
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    
    /**
     * RabbitTemplate configurado con converter JSON
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}
