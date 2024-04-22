package cr.ac.backend.exercise.controller;

import cr.ac.backend.exercise.model.Workout;
import cr.ac.backend.exercise.service.WorkoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/workout")
public class WorkoutController {
    private final WorkoutService workoutService;

    /*methods to read, create, edit and delete*/


    @GetMapping("/all")
    public ResponseEntity<List<Workout>> getAll() {
        var list = workoutService.getAll();
        return list.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Workout> getById(@PathVariable Long id) {
        var workout = workoutService.getById(id);
        return workout.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PostMapping("/save")
    public ResponseEntity<Workout> save(@RequestBody Workout workout) {
        var newWorkout = workoutService.save(workout);
        return newWorkout.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Boolean> delete(@PathVariable Long id) {
        return ResponseEntity.ok(workoutService.delete(id));
    }

    @PutMapping("/update")
    public ResponseEntity<Workout> update(@RequestBody Workout workout) {
        var newWorkout = workoutService.update(workout);
        return newWorkout.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.badRequest().build());
    }



}
