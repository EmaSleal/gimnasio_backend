package cr.ac.backend.exercise.controller;

import cr.ac.backend.exercise.model.WorkoutSpecification;
import cr.ac.backend.exercise.service.WorkoutSpecificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/workoutSpecification")
public class WorkoutSpecificationController {
    private final WorkoutSpecificationService workoutSpecificationService;

    /*methods to read, create, edit and delete*/

    @GetMapping("/all")
    public ResponseEntity<List<WorkoutSpecification>> getAll() {
        var list = workoutSpecificationService.getAll();
        return list.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<WorkoutSpecification> getById(@PathVariable Long id) {
        var workoutSpecification = workoutSpecificationService.getById(id);
        return workoutSpecification.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PostMapping("/save")
    public ResponseEntity<WorkoutSpecification> save(@RequestBody WorkoutSpecification workoutSpecification) {
        var newWorkoutSpecification = workoutSpecificationService.save(workoutSpecification);
        return newWorkoutSpecification.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Boolean> delete(@PathVariable Long id) {
        return ResponseEntity.ok(workoutSpecificationService.delete(id));
    }

    @PutMapping("/update")
    public ResponseEntity<WorkoutSpecification> update(@RequestBody WorkoutSpecification workoutSpecification) {
        var newWorkoutSpecification = workoutSpecificationService.update(workoutSpecification);
        return newWorkoutSpecification.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.badRequest().build());
    }

}
