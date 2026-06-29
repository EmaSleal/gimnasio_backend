package cr.ac.backend.workout.controller;

import cr.ac.backend.shared.dto.ApiResponse;
import cr.ac.backend.workout.dto.CreateWorkoutRequest;
import cr.ac.backend.workout.dto.UpdateWorkoutRequest;
import cr.ac.backend.workout.dto.WorkoutResponse;
import cr.ac.backend.workout.service.WorkoutService;
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
@RequestMapping("/api/v1/workouts")
@RequiredArgsConstructor
public class WorkoutController {

    private final WorkoutService workoutService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<WorkoutResponse>>> findAll() {
        return ResponseEntity.ok(ApiResponse.of(workoutService.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<WorkoutResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.of(workoutService.findById(id)));
    }

    @GetMapping("/muscular-group/{muscularGroupId}")
    public ResponseEntity<ApiResponse<List<WorkoutResponse>>> findByMuscularGroup(
            @PathVariable Long muscularGroupId) {
        return ResponseEntity.ok(ApiResponse.of(workoutService.findByMuscularGroup(muscularGroupId)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<WorkoutResponse>> create(
            @RequestBody @Valid CreateWorkoutRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of(workoutService.create(request), "Workout created"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<WorkoutResponse>> update(
            @PathVariable Long id, @RequestBody @Valid UpdateWorkoutRequest request) {
        return ResponseEntity.ok(ApiResponse.of(workoutService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        workoutService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
