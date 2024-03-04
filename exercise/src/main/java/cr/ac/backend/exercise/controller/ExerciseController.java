package cr.ac.backend.exercise.controller;

import cr.ac.backend.exercise.model.Exercise;
import cr.ac.backend.exercise.service.ExerciseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/exercise")
public class ExerciseController {
    private final ExerciseService exerciseService;

    /*methods to read, create, edit and delete*/


    @GetMapping("/all")
    public ResponseEntity<List<Exercise>> getAll() {
        var list = exerciseService.getAll();
        return list.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Exercise> getById(@PathVariable Long id) {
        var exercise = exerciseService.getById(id);
        return exercise.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PostMapping("/save")
    public ResponseEntity<Exercise> save(@RequestBody Exercise exercise) {
        var newExercise = exerciseService.save(exercise);
        return newExercise.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Boolean> delete(@PathVariable Long id) {
        return ResponseEntity.ok(exerciseService.delete(id));
    }

    @PutMapping("/update")
    public ResponseEntity<Exercise> update(@RequestBody Exercise exercise) {
        var newExercise = exerciseService.update(exercise);
        return newExercise.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.badRequest().build());
    }



}
