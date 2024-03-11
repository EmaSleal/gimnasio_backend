package cr.ac.backend.exercise.service.impl;

import cr.ac.backend.exercise.model.MuscularGroup;
import cr.ac.backend.exercise.repo.MuscularGroupRepo;
import cr.ac.backend.exercise.service.MuscularGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MuscularGroupServiceImpl implements MuscularGroupService {

    private final MuscularGroupRepo muscularGroupRepository;

    @Override
    public Optional<List<MuscularGroup>> getAll() {
        return Optional.of(muscularGroupRepository.findAll());
    }

    @Override
    public Optional<MuscularGroup> getById(Long id) {
        return muscularGroupRepository.findById(id);
    }

    @Override
    public Optional<MuscularGroup> save(MuscularGroup muscularGroup) {
        try {
            return Optional.of(muscularGroupRepository.save(muscularGroup));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Boolean delete(Long id) {
        try {
            muscularGroupRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Optional<MuscularGroup> update(MuscularGroup muscularGroup) {
        try {
            return Optional.of(muscularGroupRepository.save(muscularGroup));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}
