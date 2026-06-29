package cr.ac.backend.plan.controller;

import cr.ac.backend.plan.dto.CreateWorkoutPlanRequest;
import cr.ac.backend.plan.dto.UpdateWorkoutPlanRequest;
import cr.ac.backend.plan.dto.WorkoutPlanResponse;
import cr.ac.backend.plan.service.WorkoutPlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/v1/workout-plans")
@RequiredArgsConstructor
public class WorkoutPlanController {

    private final WorkoutPlanService workoutPlanService;

    @GetMapping
    public ResponseEntity<List<WorkoutPlanResponse>> findAll() {
        return ResponseEntity.ok(workoutPlanService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkoutPlanResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(workoutPlanService.findById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<WorkoutPlanResponse>> findByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(workoutPlanService.findByUser(userId));
    }

    @GetMapping("/trainer/{trainerId}")
    public ResponseEntity<List<WorkoutPlanResponse>> findByTrainer(@PathVariable Long trainerId) {
        return ResponseEntity.ok(workoutPlanService.findByTrainer(trainerId));
    }

    @GetMapping("/templates")
    public ResponseEntity<List<WorkoutPlanResponse>> findTemplates() {
        return ResponseEntity.ok(workoutPlanService.findTemplates());
    }

    @PostMapping
    public ResponseEntity<WorkoutPlanResponse> create(@RequestBody @Valid CreateWorkoutPlanRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(workoutPlanService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkoutPlanResponse> update(@PathVariable Long id, @RequestBody @Valid UpdateWorkoutPlanRequest request) {
        return ResponseEntity.ok(workoutPlanService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        workoutPlanService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
