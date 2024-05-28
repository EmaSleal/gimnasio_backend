package cr.ac.backend.exercise.controller;

import cr.ac.backend.exercise.model.WorkoutPlan;
import cr.ac.backend.exercise.service.WorkoutPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/workoutPlan")
@RequiredArgsConstructor
@RestController
public class WorkoutPlanController {
    private final WorkoutPlanService workoutPlanService;

    @GetMapping("/all")
    public ResponseEntity<List<WorkoutPlan>> getAll(){
        var list = workoutPlanService.getAll();
        return list.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<WorkoutPlan> getById(@PathVariable Long id){
        var workoutPlan = workoutPlanService.getById(id);
        return workoutPlan.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PostMapping("/save")
    public ResponseEntity<WorkoutPlan> save(@RequestBody WorkoutPlan workoutPlan){
        var newWorkout = workoutPlanService.save(workoutPlan);
        return newWorkout.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Boolean> delete(@PathVariable Long id){
        return ResponseEntity.ok(workoutPlanService.delete(id));
    }

    @PutMapping("/update")
    public ResponseEntity<WorkoutPlan> update(@RequestBody WorkoutPlan workoutPlan){
        var newWorkout = workoutPlanService.update(workoutPlan);
        return newWorkout.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @GetMapping("/getTemplates")
    public ResponseEntity<List<WorkoutPlan>> getTemplates(){
        var list = workoutPlanService.getTempletes();
        return list.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
    }
}
