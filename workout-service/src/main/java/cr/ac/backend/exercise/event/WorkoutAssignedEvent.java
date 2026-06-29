package cr.ac.backend.exercise.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Evento que representa la asignación de un workout plan a un usuario
 * Se publica cuando un trainer asigna un plan de entrenamiento a un cliente
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutAssignedEvent implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * ID del workout plan asignado
     */
    private Long workoutPlanId;
    
    /**
     * ID del usuario a quien se le asignó el plan
     */
    private Long userId;
    
    /**
     * ID del trainer que asignó el plan
     */
    private Long trainerId;
    
    /**
     * Descripción del plan de entrenamiento
     */
    private String description;
    
    /**
     * Fecha de inicio del plan
     */
    private String startDate;
    
    /**
     * Fecha de fin del plan
     */
    private String endDate;
    
    /**
     * Estado del plan (active, completed, paused)
     */
    private String status;
    
    /**
     * Timestamp de cuando se asignó
     */
    private Long timestamp;
    
    /**
     * Indica si es un template o plan personalizado
     */
    private boolean isTemplate;
}
