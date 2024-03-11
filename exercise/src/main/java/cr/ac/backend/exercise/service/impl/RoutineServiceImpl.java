package cr.ac.backend.exercise.service.impl;

import cr.ac.backend.exercise.model.Routine;
import cr.ac.backend.exercise.repo.RutineRepo;
import cr.ac.backend.exercise.service.RoutineService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoutineServiceImpl implements RoutineService {
    private final RutineRepo rutineRepo;


    @Override
    public Optional<List<Routine>> getAll() {
        return Optional.of(rutineRepo.findAll());
    }

    @Override
    public Optional<Routine> getById(Long id) {
        return rutineRepo.findById(id);
    }

    @Override
    public Optional<Routine> save(Routine rutine) {
        return Optional.of(rutineRepo.save(rutine));
    }

    @Override
    public Boolean delete(Long id) {
        rutineRepo.deleteById(id);
        return true;
    }

    @Override
    public Optional<Routine> update(Routine rutine) {
        return Optional.of(rutineRepo.save(rutine));
    }
}
