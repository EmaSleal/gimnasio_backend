package cr.ac.backend.exercise.controller;

import cr.ac.backend.exercise.model.DailyRoutine;
import cr.ac.backend.exercise.service.DailyRoutineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/dailyRoutine")
public class DailyRoutineController {
    private final DailyRoutineService dailyRoutineService;

    /*methods to read, create, edit and delete*/

    @GetMapping("/all")
    public ResponseEntity<List<DailyRoutine>> getAll() {
        var list = dailyRoutineService.getAll();
        return list.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<DailyRoutine> getById(@PathVariable Long id) {
        var dailyRoutine = dailyRoutineService.getById(id);
        return dailyRoutine.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PostMapping("/save")
    public ResponseEntity<DailyRoutine> save(@RequestBody DailyRoutine dailyRoutine) {
        var newDailyRoutine = dailyRoutineService.save(dailyRoutine);
        return newDailyRoutine.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Boolean> delete(@PathVariable Long id) {
        return ResponseEntity.ok(dailyRoutineService.delete(id));
    }

    @PutMapping("/update")
    public ResponseEntity<DailyRoutine> update(@RequestBody DailyRoutine dailyRoutine) {
        var newDailyRoutine = dailyRoutineService.update(dailyRoutine);
        return newDailyRoutine.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.badRequest().build());
    }


}
