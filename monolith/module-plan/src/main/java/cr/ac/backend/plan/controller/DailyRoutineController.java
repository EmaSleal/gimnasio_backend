package cr.ac.backend.plan.controller;

import cr.ac.backend.plan.dto.CreateDailyRoutineRequest;
import cr.ac.backend.plan.dto.DailyRoutineResponse;
import cr.ac.backend.plan.dto.UpdateDailyRoutineRequest;
import cr.ac.backend.plan.service.DailyRoutineService;
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
@RequestMapping("/api/v1/daily-routines")
@RequiredArgsConstructor
public class DailyRoutineController {

    private final DailyRoutineService dailyRoutineService;

    @GetMapping("/{id}")
    public ResponseEntity<DailyRoutineResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(dailyRoutineService.findById(id));
    }

    @GetMapping("/plan/{workoutPlanId}")
    public ResponseEntity<List<DailyRoutineResponse>> findByWorkoutPlan(@PathVariable Long workoutPlanId) {
        return ResponseEntity.ok(dailyRoutineService.findByWorkoutPlan(workoutPlanId));
    }

    @PostMapping
    public ResponseEntity<DailyRoutineResponse> create(@RequestBody @Valid CreateDailyRoutineRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(dailyRoutineService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DailyRoutineResponse> update(@PathVariable Long id, @RequestBody @Valid UpdateDailyRoutineRequest request) {
        return ResponseEntity.ok(dailyRoutineService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        dailyRoutineService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
