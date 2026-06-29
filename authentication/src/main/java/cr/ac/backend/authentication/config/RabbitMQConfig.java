package cr.ac.backend.authentication.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de RabbitMQ para Authentication Service
 * 
 * Exchanges configurados:
 * - email.exchange: Para eventos de envío de emails
 * - user.exchange: Para recibir eventos de usuario (listener)
 * 
 * Queues configuradas:
 * - email.welcome.queue: Emails de bienvenida
 * - email.password-reset.queue: Emails de recuperación de contraseña
 * - user.created.auth.queue: Eventos de usuario creado (para auth)
 * 
 * @author Sprint 3 - Fase 1
 * @since 2025-11-02
 */
@Configuration
public class RabbitMQConfig {

    // ==================== EXCHANGE NAMES ====================
    public static final String EMAIL_EXCHANGE = "email.exchange";
    public static final String USER_EXCHANGE = "user.exchange";
    public static final String AUDIT_EXCHANGE = "audit.exchange";
    public static final String DLX_EXCHANGE = "dlx.exchange";
    
    // ==================== QUEUE NAMES ====================
    public static final String EMAIL_WELCOME_QUEUE = "email.welcome.queue";
    public static final String EMAIL_PASSWORD_RESET_QUEUE = "email.password-reset.queue";
    public static final String USER_CREATED_AUTH_QUEUE = "user.created.auth.queue";
    public static final String AUDIT_QUEUE = "audit.queue";
    
    // Dead Letter Queues
    public static final String EMAIL_WELCOME_DLQ = "email.welcome.dlq";
    public static final String EMAIL_PASSWORD_RESET_DLQ = "email.password-reset.dlq";
    public static final String AUDIT_DLQ = "audit.dlq";
    
    // ==================== ROUTING KEYS ====================
    public static final String EMAIL_WELCOME_ROUTING_KEY = "email.welcome";
    public static final String EMAIL_PASSWORD_RESET_ROUTING_KEY = "email.password-reset";
    public static final String USER_CREATED_ROUTING_KEY = "user.created";
    public static final String AUDIT_ROUTING_KEY = "audit.event";
    
    // DLQ Routing Keys
    public static final String EMAIL_WELCOME_DLQ_ROUTING_KEY = "email.welcome.dlq";
    public static final String EMAIL_PASSWORD_RESET_DLQ_ROUTING_KEY = "email.password-reset.dlq";
    public static final String AUDIT_DLQ_ROUTING_KEY = "audit.dlq";
    
    // ==================== TTL ====================
    private static final Integer MESSAGE_TTL = 86400000; // 24 horas
    
    // ==================== EXCHANGES ====================
    
    @Bean
    public TopicExchange emailExchange() {
        return ExchangeBuilder
                .topicExchange(EMAIL_EXCHANGE)
                .durable(true)
                .build();
    }
    
    @Bean
    public TopicExchange userExchange() {
        return ExchangeBuilder
                .topicExchange(USER_EXCHANGE)
                .durable(true)
                .build();
    }
    
    @Bean
    public TopicExchange auditExchange() {
        return ExchangeBuilder
                .topicExchange(AUDIT_EXCHANGE)
                .durable(true)
                .build();
    }
    
    @Bean
    public DirectExchange deadLetterExchange() {
        return ExchangeBuilder
                .directExchange(DLX_EXCHANGE)
                .durable(true)
                .build();
    }
    
    // ==================== QUEUES ====================
    
    /**
     * Queue para emails de bienvenida
     */
    @Bean
    public Queue emailWelcomeQueue() {
        return QueueBuilder
                .durable(EMAIL_WELCOME_QUEUE)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", EMAIL_WELCOME_DLQ_ROUTING_KEY)
                .withArgument("x-message-ttl", MESSAGE_TTL)
                .build();
    }
    
    /**
     * Dead Letter Queue para email.welcome
     */
    @Bean
    public Queue emailWelcomeDLQ() {
        return QueueBuilder
                .durable(EMAIL_WELCOME_DLQ)
                .build();
    }
    
    /**
     * Queue para emails de recuperación de contraseña
     */
    @Bean
    public Queue emailPasswordResetQueue() {
        return QueueBuilder
                .durable(EMAIL_PASSWORD_RESET_QUEUE)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", EMAIL_PASSWORD_RESET_DLQ_ROUTING_KEY)
                .withArgument("x-message-ttl", MESSAGE_TTL)
                .build();
    }
    
    /**
     * Dead Letter Queue para email.password-reset
     */
    @Bean
    public Queue emailPasswordResetDLQ() {
        return QueueBuilder
                .durable(EMAIL_PASSWORD_RESET_DLQ)
                .build();
    }
    
    /**
     * Queue para recibir eventos de usuario creado (listener)
     * Authentication escucha estos eventos para procesamiento
     */
    @Bean
    public Queue userCreatedAuthQueue() {
        return QueueBuilder
                .durable(USER_CREATED_AUTH_QUEUE)
                .withArgument("x-message-ttl", MESSAGE_TTL)
                .build();
    }
    
    /**
     * Queue para eventos de auditoría
     */
    @Bean
    public Queue auditQueue() {
        return QueueBuilder
                .durable(AUDIT_QUEUE)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", AUDIT_DLQ_ROUTING_KEY)
                .withArgument("x-message-ttl", MESSAGE_TTL)
                .build();
    }
    
    /**
     * Dead Letter Queue para audit
     */
    @Bean
    public Queue auditDLQ() {
        return QueueBuilder
                .durable(AUDIT_DLQ)
                .build();
    }
    
    // ==================== BINDINGS ====================
    
    @Bean
    public Binding emailWelcomeBinding(Queue emailWelcomeQueue, TopicExchange emailExchange) {
        return BindingBuilder
                .bind(emailWelcomeQueue)
                .to(emailExchange)
                .with(EMAIL_WELCOME_ROUTING_KEY);
    }
    
    @Bean
    public Binding emailWelcomeDLQBinding(Queue emailWelcomeDLQ, DirectExchange deadLetterExchange) {
        return BindingBuilder
                .bind(emailWelcomeDLQ)
                .to(deadLetterExchange)
                .with(EMAIL_WELCOME_DLQ_ROUTING_KEY);
    }
    
    @Bean
    public Binding emailPasswordResetBinding(Queue emailPasswordResetQueue, TopicExchange emailExchange) {
        return BindingBuilder
                .bind(emailPasswordResetQueue)
                .to(emailExchange)
                .with(EMAIL_PASSWORD_RESET_ROUTING_KEY);
    }
    
    @Bean
    public Binding emailPasswordResetDLQBinding(Queue emailPasswordResetDLQ, DirectExchange deadLetterExchange) {
        return BindingBuilder
                .bind(emailPasswordResetDLQ)
                .to(deadLetterExchange)
                .with(EMAIL_PASSWORD_RESET_DLQ_ROUTING_KEY);
    }
    
    /**
     * Binding para escuchar eventos de usuario creado
     * Authentication consume estos eventos desde User Service
     */
    @Bean
    public Binding userCreatedAuthBinding(Queue userCreatedAuthQueue, TopicExchange userExchange) {
        return BindingBuilder
                .bind(userCreatedAuthQueue)
                .to(userExchange)
                .with(USER_CREATED_ROUTING_KEY);
    }
    
    /**
     * Binding para eventos de auditoría
     */
    @Bean
    public Binding auditBinding(Queue auditQueue, TopicExchange auditExchange) {
        return BindingBuilder
                .bind(auditQueue)
                .to(auditExchange)
                .with(AUDIT_ROUTING_KEY);
    }
    
    @Bean
    public Binding auditDLQBinding(Queue auditDLQ, DirectExchange deadLetterExchange) {
        return BindingBuilder
                .bind(auditDLQ)
                .to(deadLetterExchange)
                .with(AUDIT_DLQ_ROUTING_KEY);
    }
    
    // ==================== MESSAGE CONVERTER ====================
    
    @Bean
    public MessageConverter jsonMessageConverter() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return new Jackson2JsonMessageConverter(objectMapper);
    }
    
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
    
    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }
}
