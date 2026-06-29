package cr.ac.backend.workout.controller;

import cr.ac.backend.shared.dto.ApiResponse;
import cr.ac.backend.workout.dto.CreateWorkoutSpecificationRequest;
import cr.ac.backend.workout.dto.UpdateWorkoutSpecificationRequest;
import cr.ac.backend.workout.dto.WorkoutSpecificationResponse;
import cr.ac.backend.workout.service.WorkoutSpecificationService;
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
@RequestMapping("/api/v1/workout-specifications")
@RequiredArgsConstructor
public class WorkoutSpecificationController {

    private final WorkoutSpecificationService workoutSpecificationService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<WorkoutSpecificationResponse>>> findAll() {
        return ResponseEntity.ok(ApiResponse.of(workoutSpecificationService.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<WorkoutSpecificationResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.of(workoutSpecificationService.findById(id)));
    }

    @GetMapping("/workout/{workoutId}")
    public ResponseEntity<ApiResponse<List<WorkoutSpecificationResponse>>> findByWorkout(
            @PathVariable Long workoutId) {
        return ResponseEntity.ok(ApiResponse.of(workoutSpecificationService.findByWorkout(workoutId)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<WorkoutSpecificationResponse>> create(
            @RequestBody @Valid CreateWorkoutSpecificationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of(workoutSpecificationService.create(request), "Workout specification created"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<WorkoutSpecificationResponse>> update(
            @PathVariable Long id, @RequestBody @Valid UpdateWorkoutSpecificationRequest request) {
        return ResponseEntity.ok(ApiResponse.of(workoutSpecificationService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        workoutSpecificationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
