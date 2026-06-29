package cr.ac.backend.workout.service.impl;

import cr.ac.backend.shared.exception.BusinessRuleViolationException;
import cr.ac.backend.shared.exception.DuplicateResourceException;
import cr.ac.backend.shared.exception.ResourceNotFoundException;
import cr.ac.backend.workout.domain.MuscularGroup;
import cr.ac.backend.workout.dto.CreateMuscularGroupRequest;
import cr.ac.backend.workout.dto.MuscularGroupResponse;
import cr.ac.backend.workout.mapper.MuscularGroupMapper;
import cr.ac.backend.workout.repository.MuscularGroupRepository;
import cr.ac.backend.workout.repository.WorkoutRepository;
import cr.ac.backend.workout.service.MuscularGroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MuscularGroupServiceImpl implements MuscularGroupService {

    private final MuscularGroupRepository muscularGroupRepository;
    private final WorkoutRepository workoutRepository;
    private final MuscularGroupMapper muscularGroupMapper;

    @Override
    public MuscularGroupResponse create(CreateMuscularGroupRequest request) {
        if (muscularGroupRepository.existsByName(request.name())) {
            throw new DuplicateResourceException("name already exists", "name");
        }
        var group = MuscularGroup.builder()
                .name(request.name())
                .build();
        var saved = muscularGroupRepository.save(group);
        return muscularGroupMapper.toResponse(saved);
    }

    @Override
    public MuscularGroupResponse findById(Long id) {
        var group = muscularGroupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Muscular group not found with id: " + id));
        return muscularGroupMapper.toResponse(group);
    }

    @Override
    public List<MuscularGroupResponse> findAll() {
        return muscularGroupMapper.toResponseList(muscularGroupRepository.findAll());
    }

    @Override
    public MuscularGroupResponse update(Long id, CreateMuscularGroupRequest request) {
        var group = muscularGroupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Muscular group not found with id: " + id));
        if (!group.getName().equals(request.name()) && muscularGroupRepository.existsByName(request.name())) {
            throw new DuplicateResourceException("name already exists", "name");
        }
        group.setName(request.name());
        var saved = muscularGroupRepository.save(group);
        return muscularGroupMapper.toResponse(saved);
    }

    @Override
    public void delete(Long id) {
        muscularGroupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Muscular group not found with id: " + id));
        if (workoutRepository.existsByMuscularGroupId(id)) {
            throw new BusinessRuleViolationException("cannot delete muscular group with associated workouts");
        }
        muscularGroupRepository.deleteById(id);
    }
}
