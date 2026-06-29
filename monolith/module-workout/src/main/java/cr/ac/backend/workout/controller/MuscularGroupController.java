package cr.ac.backend.workout.controller;

import cr.ac.backend.shared.dto.ApiResponse;
import cr.ac.backend.workout.dto.CreateMuscularGroupRequest;
import cr.ac.backend.workout.dto.MuscularGroupResponse;
import cr.ac.backend.workout.service.MuscularGroupService;
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
@RequestMapping("/api/v1/muscular-groups")
@RequiredArgsConstructor
public class MuscularGroupController {

    private final MuscularGroupService muscularGroupService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<MuscularGroupResponse>>> findAll() {
        return ResponseEntity.ok(ApiResponse.of(muscularGroupService.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MuscularGroupResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.of(muscularGroupService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MuscularGroupResponse>> create(
            @RequestBody @Valid CreateMuscularGroupRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of(muscularGroupService.create(request), "Muscular group created"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MuscularGroupResponse>> update(
            @PathVariable Long id, @RequestBody @Valid CreateMuscularGroupRequest request) {
        return ResponseEntity.ok(ApiResponse.of(muscularGroupService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        muscularGroupService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
