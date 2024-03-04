package cr.ac.backend.exercise.controller;

import cr.ac.backend.exercise.model.RutineDay;
import cr.ac.backend.exercise.service.RutineDayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/rutineDay")
public class RutineDayController {
    private final RutineDayService rutineDayService;

    /*methods to read, create, edit and delete*/

    @GetMapping("/all")
    public ResponseEntity<List<RutineDay>> getAll() {
        var list = rutineDayService.getAll();
        return list.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<RutineDay> getById(@PathVariable Long id) {
        var rutineDay = rutineDayService.getById(id);
        return rutineDay.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PostMapping("/save")
    public ResponseEntity<RutineDay> save(@RequestBody RutineDay rutineDay) {
        var newRutineDay = rutineDayService.save(rutineDay);
        return newRutineDay.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Boolean> delete(@PathVariable Long id) {
        return ResponseEntity.ok(rutineDayService.delete(id));
    }

    @PutMapping("/update")
    public ResponseEntity<RutineDay> update(@RequestBody RutineDay rutineDay) {
        var newRutineDay = rutineDayService.update(rutineDay);
        return newRutineDay.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.badRequest().build());
    }


}
