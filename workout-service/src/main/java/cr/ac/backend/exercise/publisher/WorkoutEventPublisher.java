package cr.ac.backend.exercise.publisher;

import cr.ac.backend.exercise.event.WorkoutAssignedEvent;
import cr.ac.backend.exercise.event.WorkoutCompletedEvent;
import cr.ac.backend.exercise.model.WorkoutPlan;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Publisher para eventos de workout
 * Publica eventos relacionados con asignación y completación de planes de entrenamiento
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WorkoutEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    
    private static final String NOTIFICATION_EXCHANGE = "notification.exchange";
    private static final String WORKOUT_ASSIGNED_ROUTING_KEY = "workout.assigned";
    private static final String WORKOUT_COMPLETED_ROUTING_KEY = "workout.completed";

    /**
     * Publica evento cuando se asigna un workout plan a un usuario
     * @param workoutPlan Plan de entrenamiento asignado
     */
    public void publishWorkoutAssigned(WorkoutPlan workoutPlan) {
        WorkoutAssignedEvent event = WorkoutAssignedEvent.builder()
                .workoutPlanId(workoutPlan.getId())
                .userId(workoutPlan.getIdUser())
                .trainerId(workoutPlan.getIdTrainer())
                .description(workoutPlan.getDescription())
                .startDate(workoutPlan.getStartDate())
                .endDate(workoutPlan.getEndDate())
                .status(workoutPlan.getStatus())
                .timestamp(System.currentTimeMillis())
                .isTemplate(workoutPlan.isTemplate())
                .build();
        
        log.info("📤 Publicando WorkoutAssignedEvent - Plan ID: {}, Usuario: {}, Trainer: {}", 
                workoutPlan.getId(), workoutPlan.getIdUser(), workoutPlan.getIdTrainer());
        
        rabbitTemplate.convertAndSend(NOTIFICATION_EXCHANGE, WORKOUT_ASSIGNED_ROUTING_KEY, event);
        
        log.debug("✅ WorkoutAssignedEvent publicado exitosamente");
    }

    /**
     * Publica evento cuando un usuario completa un workout plan
     * @param workoutPlan Plan de entrenamiento completado
     */
    public void publishWorkoutCompleted(WorkoutPlan workoutPlan) {
        // Calcular duración del plan
        Integer durationDays = calculateDurationDays(workoutPlan.getStartDate(), workoutPlan.getEndDate());
        
        WorkoutCompletedEvent event = WorkoutCompletedEvent.builder()
                .workoutPlanId(workoutPlan.getId())
                .userId(workoutPlan.getIdUser())
                .trainerId(workoutPlan.getIdTrainer())
                .description(workoutPlan.getDescription())
                .startDate(workoutPlan.getStartDate())
                .completionDate(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE))
                .timestamp(System.currentTimeMillis())
                .durationDays(durationDays)
                .build();
        
        log.info("📤 Publicando WorkoutCompletedEvent - Plan ID: {}, Usuario: {}, Duración: {} días", 
                workoutPlan.getId(), workoutPlan.getIdUser(), durationDays);
        
        rabbitTemplate.convertAndSend(NOTIFICATION_EXCHANGE, WORKOUT_COMPLETED_ROUTING_KEY, event);
        
        log.debug("✅ WorkoutCompletedEvent publicado exitosamente");
    }

    /**
     * Calcula la duración en días entre dos fechas
     * @param startDate Fecha de inicio (formato: yyyy-MM-dd)
     * @param endDate Fecha de fin (formato: yyyy-MM-dd)
     * @return Número de días de duración
     */
    private Integer calculateDurationDays(String startDate, String endDate) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
            LocalDateTime start = LocalDateTime.parse(startDate + "T00:00:00");
            LocalDateTime end = LocalDateTime.parse(endDate + "T00:00:00");
            return (int) ChronoUnit.DAYS.between(start, end);
        } catch (Exception e) {
            log.warn("⚠️ Error calculando duración del plan, usando valor por defecto: {}", e.getMessage());
            return 0;
        }
    }
}
