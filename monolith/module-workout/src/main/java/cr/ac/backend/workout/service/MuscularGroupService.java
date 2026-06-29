package cr.ac.backend.workout.service;

import cr.ac.backend.workout.dto.CreateMuscularGroupRequest;
import cr.ac.backend.workout.dto.MuscularGroupResponse;

import java.util.List;

public interface MuscularGroupService {
    MuscularGroupResponse create(CreateMuscularGroupRequest request);
    MuscularGroupResponse findById(Long id);
    List<MuscularGroupResponse> findAll();
    MuscularGroupResponse update(Long id, CreateMuscularGroupRequest request);
    void delete(Long id);
}
