package cr.ac.backend.exercise.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Evento que representa la finalización de un workout plan
 * Se publica cuando un usuario completa su plan de entrenamiento
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutCompletedEvent implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * ID del workout plan completado
     */
    private Long workoutPlanId;
    
    /**
     * ID del usuario que completó el plan
     */
    private Long userId;
    
    /**
     * ID del trainer que asignó el plan
     */
    private Long trainerId;
    
    /**
     * Descripción del plan completado
     */
    private String description;
    
    /**
     * Fecha de inicio del plan
     */
    private String startDate;
    
    /**
     * Fecha de finalización real
     */
    private String completionDate;
    
    /**
     * Timestamp de cuando se completó
     */
    private Long timestamp;
    
    /**
     * Duración total del plan en días
     */
    private Integer durationDays;
}
