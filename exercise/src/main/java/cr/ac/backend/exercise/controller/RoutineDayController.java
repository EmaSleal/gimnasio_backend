package cr.ac.backend.exercise.controller;

import cr.ac.backend.exercise.model.RoutineDay;
import cr.ac.backend.exercise.service.RoutineDayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/rutineDay")
public class RoutineDayController {
    private final RoutineDayService rutineDayService;

    /*methods to read, create, edit and delete*/

    @GetMapping("/all")
    public ResponseEntity<List<RoutineDay>> getAll() {
        var list = rutineDayService.getAll();
        return list.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<RoutineDay> getById(@PathVariable Long id) {
        var rutineDay = rutineDayService.getById(id);
        return rutineDay.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PostMapping("/save")
    public ResponseEntity<RoutineDay> save(@RequestBody RoutineDay rutineDay) {
        var newRutineDay = rutineDayService.save(rutineDay);
        return newRutineDay.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Boolean> delete(@PathVariable Long id) {
        return ResponseEntity.ok(rutineDayService.delete(id));
    }

    @PutMapping("/update")
    public ResponseEntity<RoutineDay> update(@RequestBody RoutineDay rutineDay) {
        var newRutineDay = rutineDayService.update(rutineDay);
        return newRutineDay.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.badRequest().build());
    }


}
