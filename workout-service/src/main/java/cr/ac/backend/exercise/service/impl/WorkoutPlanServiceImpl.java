package cr.ac.backend.exercise.service.impl;

import cr.ac.backend.exercise.model.DailyRoutine;
import cr.ac.backend.exercise.model.WorkoutPlan;
import cr.ac.backend.exercise.model.WorkoutSpecification;
import cr.ac.backend.exercise.publisher.WorkoutEventPublisher;
import cr.ac.backend.exercise.repo.WorkoutPlanRepo;
import cr.ac.backend.exercise.service.DailyRoutineService;
import cr.ac.backend.exercise.service.WorkoutPlanService;
import cr.ac.backend.exercise.service.WorkoutSpecificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkoutPlanServiceImpl implements WorkoutPlanService {
    private final WorkoutPlanRepo workoutPlanRepo;
    private final DailyRoutineService dailyRoutineService;
    private final WorkoutSpecificationService workoutSpecificationService;
    private final WorkoutEventPublisher workoutEventPublisher;

    @Override
    public Optional<List<WorkoutPlan>> getAll() {
        return Optional.of(workoutPlanRepo.findAll());
    }

    @Override
    public Optional<WorkoutPlan> getById(Long id) {
        return workoutPlanRepo.findById(id);
    }

    @Override
    public Optional<List<WorkoutPlan>> getByIdUser(Long id) {
        var list = workoutPlanRepo.findByIdUser(id);
        if (list.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(list);
    }

    @Override
    public Optional<WorkoutPlan> save(WorkoutPlan workoutPlan) {
        log.info("💾 Guardando WorkoutPlan para usuario: {}, trainer: {}", 
                workoutPlan.getIdUser(), workoutPlan.getIdTrainer());
        
        WorkoutPlan savedPlan = workoutPlanRepo.save(workoutPlan);
        
        // Publicar evento de asignación de workout (solo si no es template)
        if (!savedPlan.isTemplate()) {
            workoutEventPublisher.publishWorkoutAssigned(savedPlan);
            log.info("✅ WorkoutPlan guardado y evento publicado - ID: {}", savedPlan.getId());
        } else {
            log.info("✅ Template guardado - ID: {} (no se publica evento)", savedPlan.getId());
        }
        
        return Optional.of(savedPlan);
    }

    @Override
    public Boolean delete(Long id) {
        workoutPlanRepo.deleteById(id);
        return true;
    }

    @Override
    public Optional<WorkoutPlan> update(WorkoutPlan workoutPlan) {
        log.info("🔄 Actualizando WorkoutPlan ID: {}, nuevo status: {}", 
                workoutPlan.getId(), workoutPlan.getStatus());
        
        // Obtener el plan anterior para detectar cambio de status
        Optional<WorkoutPlan> previousPlan = workoutPlanRepo.findById(workoutPlan.getId());
        
        // Actualizar timestamp
        var currentDate = java.time.LocalDateTime.now();
        workoutPlan.setUpdatedAt(currentDate);
        
        WorkoutPlan updatedPlan = workoutPlanRepo.save(workoutPlan);
        
        // Publicar evento de completación si el status cambió a "completed"
        if (previousPlan.isPresent() && 
            "completed".equalsIgnoreCase(updatedPlan.getStatus()) && 
            !updatedPlan.isTemplate()) {
            
            String previousStatus = previousPlan.get().getStatus();
            if (!"completed".equalsIgnoreCase(previousStatus)) {
                workoutEventPublisher.publishWorkoutCompleted(updatedPlan);
                log.info("🎉 WorkoutPlan completado y evento publicado - ID: {}", updatedPlan.getId());
            }
        }
        
        return Optional.of(updatedPlan);
    }

    @Override
    public Optional<List<WorkoutPlan>> getTempletes() {
        var list = workoutPlanRepo.findByIsTemplate(true);
        if (list.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(list);
    }
}
