package cr.ac.backend.exercise.controller;

import cr.ac.backend.exercise.model.Routine;
import cr.ac.backend.exercise.service.RoutineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/rutine")
@RequiredArgsConstructor
@RestController
public class RoutineController {
    private final RoutineService rutineService;

    @GetMapping("/all")
    public ResponseEntity<List<Routine>> getAll(){
        var list = rutineService.getAll();
        return list.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Routine> getById(@PathVariable Long id){
        var rutine = rutineService.getById(id);
        return rutine.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PostMapping("/save")
    public ResponseEntity<Routine> save(@RequestBody Routine rutine){
        var newRutine = rutineService.save(rutine);
        return newRutine.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Boolean> delete(@PathVariable Long id){
        return ResponseEntity.ok(rutineService.delete(id));
    }

    @PutMapping("/update")
    public ResponseEntity<Routine> update(@RequestBody Routine rutine){
        var newRutine = rutineService.update(rutine);
        return newRutine.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.badRequest().build());
    }
}
