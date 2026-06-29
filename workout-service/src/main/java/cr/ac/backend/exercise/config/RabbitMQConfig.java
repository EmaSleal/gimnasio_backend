package cr.ac.backend.exercise.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de RabbitMQ para Workout Service
 * 
 * Exchanges configurados:
 * - notification.exchange: Para eventos de notificaciones
 * 
 * Queues configuradas:
 * - workout.assigned.queue: Notificaciones de workout asignado
 * - workout.completed.queue: Notificaciones de workout completado
 * 
 * @author Sprint 3 - Fase 1
 * @since 2025-11-02
 */
@Configuration
public class RabbitMQConfig {

    // ==================== EXCHANGE NAMES ====================
    public static final String NOTIFICATION_EXCHANGE = "notification.exchange";
    public static final String DLX_EXCHANGE = "dlx.exchange";
    
    // ==================== QUEUE NAMES ====================
    public static final String WORKOUT_ASSIGNED_QUEUE = "workout.assigned.queue";
    public static final String WORKOUT_COMPLETED_QUEUE = "workout.completed.queue";
    
    // Dead Letter Queues
    public static final String WORKOUT_ASSIGNED_DLQ = "workout.assigned.dlq";
    public static final String WORKOUT_COMPLETED_DLQ = "workout.completed.dlq";
    
    // ==================== ROUTING KEYS ====================
    public static final String WORKOUT_ASSIGNED_ROUTING_KEY = "workout.assigned";
    public static final String WORKOUT_COMPLETED_ROUTING_KEY = "workout.completed";
    
    // DLQ Routing Keys
    public static final String WORKOUT_ASSIGNED_DLQ_ROUTING_KEY = "workout.assigned.dlq";
    public static final String WORKOUT_COMPLETED_DLQ_ROUTING_KEY = "workout.completed.dlq";
    
    // ==================== TTL ====================
    private static final Integer MESSAGE_TTL = 86400000; // 24 horas
    
    // ==================== EXCHANGES ====================
    
    @Bean
    public TopicExchange notificationExchange() {
        return ExchangeBuilder
                .topicExchange(NOTIFICATION_EXCHANGE)
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
     * Queue para notificaciones de workout asignado
     */
    @Bean
    public Queue workoutAssignedQueue() {
        return QueueBuilder
                .durable(WORKOUT_ASSIGNED_QUEUE)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", WORKOUT_ASSIGNED_DLQ_ROUTING_KEY)
                .withArgument("x-message-ttl", MESSAGE_TTL)
                .build();
    }
    
    /**
     * Dead Letter Queue para workout.assigned
     */
    @Bean
    public Queue workoutAssignedDLQ() {
        return QueueBuilder
                .durable(WORKOUT_ASSIGNED_DLQ)
                .build();
    }
    
    /**
     * Queue para notificaciones de workout completado
     */
    @Bean
    public Queue workoutCompletedQueue() {
        return QueueBuilder
                .durable(WORKOUT_COMPLETED_QUEUE)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", WORKOUT_COMPLETED_DLQ_ROUTING_KEY)
                .withArgument("x-message-ttl", MESSAGE_TTL)
                .build();
    }
    
    /**
     * Dead Letter Queue para workout.completed
     */
    @Bean
    public Queue workoutCompletedDLQ() {
        return QueueBuilder
                .durable(WORKOUT_COMPLETED_DLQ)
                .build();
    }
    
    // ==================== BINDINGS ====================
    
    @Bean
    public Binding workoutAssignedBinding(Queue workoutAssignedQueue, TopicExchange notificationExchange) {
        return BindingBuilder
                .bind(workoutAssignedQueue)
                .to(notificationExchange)
                .with(WORKOUT_ASSIGNED_ROUTING_KEY);
    }
    
    @Bean
    public Binding workoutAssignedDLQBinding(Queue workoutAssignedDLQ, DirectExchange deadLetterExchange) {
        return BindingBuilder
                .bind(workoutAssignedDLQ)
                .to(deadLetterExchange)
                .with(WORKOUT_ASSIGNED_DLQ_ROUTING_KEY);
    }
    
    @Bean
    public Binding workoutCompletedBinding(Queue workoutCompletedQueue, TopicExchange notificationExchange) {
        return BindingBuilder
                .bind(workoutCompletedQueue)
                .to(notificationExchange)
                .with(WORKOUT_COMPLETED_ROUTING_KEY);
    }
    
    @Bean
    public Binding workoutCompletedDLQBinding(Queue workoutCompletedDLQ, DirectExchange deadLetterExchange) {
        return BindingBuilder
                .bind(workoutCompletedDLQ)
                .to(deadLetterExchange)
                .with(WORKOUT_COMPLETED_DLQ_ROUTING_KEY);
    }
    
    // ==================== MESSAGE CONVERTER ====================
    
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}
