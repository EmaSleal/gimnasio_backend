package cr.ac.backend.exercise.controller;

import cr.ac.backend.exercise.model.MuscularGroup;
import cr.ac.backend.exercise.service.MuscularGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/muscularGroup")
@RequiredArgsConstructor
@RestController
public class MuscularGroupController {

    private final MuscularGroupService muscularGroupService;

    @GetMapping("/all")
    public ResponseEntity<List<MuscularGroup>> getAll(){
        var result = muscularGroupService.getAll();
        return result.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<MuscularGroup> getById(@PathVariable Long id){
        var result = muscularGroupService.getById(id);
        return result.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PostMapping("/save")
    public ResponseEntity<MuscularGroup> save(@RequestBody MuscularGroup muscularGroup){
        var result = muscularGroupService.save(muscularGroup);
        return result.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Boolean> delete(@PathVariable Long id){
        return ResponseEntity.ok(muscularGroupService.delete(id));
    }

    @PutMapping("/update")
    public ResponseEntity<MuscularGroup> update(@RequestBody MuscularGroup muscularGroup){
        var result = muscularGroupService.update(muscularGroup);
        return result.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
    }


}
