package cr.ac.backend.exercise.controller;

import cr.ac.backend.exercise.model.ExerciseSpecified;
import cr.ac.backend.exercise.service.ExerciseSpecifiedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/exerciseSpecified")
public class ExerciseSpecifiedController {
    private final ExerciseSpecifiedService exerciseSpecifiedService;

    /*methods to read, create, edit and delete*/

    @GetMapping("/all")
    public ResponseEntity<List<ExerciseSpecified>> getAll() {
        var list = exerciseSpecifiedService.getAll();
        return list.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<ExerciseSpecified> getById(@PathVariable Long id) {
        var exerciseSpecified = exerciseSpecifiedService.getById(id);
        return exerciseSpecified.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PostMapping("/save")
    public ResponseEntity<ExerciseSpecified> save(@RequestBody ExerciseSpecified exerciseSpecified) {
        var newExerciseSpecified = exerciseSpecifiedService.save(exerciseSpecified);
        return newExerciseSpecified.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Boolean> delete(@PathVariable Long id) {
        return ResponseEntity.ok(exerciseSpecifiedService.delete(id));
    }

    @PutMapping("/update")
    public ResponseEntity<ExerciseSpecified> update(@RequestBody ExerciseSpecified exerciseSpecified) {
        var newExerciseSpecified = exerciseSpecifiedService.update(exerciseSpecified);
        return newExerciseSpecified.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.badRequest().build());
    }

}
