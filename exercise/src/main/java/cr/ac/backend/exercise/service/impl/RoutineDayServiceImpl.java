package cr.ac.backend.exercise.service.impl;

import cr.ac.backend.exercise.model.RoutineDay;
import cr.ac.backend.exercise.repo.RutineDayRepo;
import cr.ac.backend.exercise.service.RoutineDayService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoutineDayServiceImpl implements RoutineDayService {
    private final RutineDayRepo rutineDayRepo;

    @Override
    public Optional<List<RoutineDay>> getAll() {
        var list = rutineDayRepo.findAll();
        if (list.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(list);
    }

    @Override
    public Optional<RoutineDay> getById(Long id) {
        return rutineDayRepo.findById(id);
    }

    @Override
    public Optional<RoutineDay> save(RoutineDay rutineDay) {
        try {
            return Optional.of(rutineDayRepo.save(rutineDay));
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public Boolean delete(Long id) {
        try {
            rutineDayRepo.deleteById(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Optional<RoutineDay> update(RoutineDay rutineDay) {
        try {
            return Optional.of(rutineDayRepo.save(rutineDay));
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
