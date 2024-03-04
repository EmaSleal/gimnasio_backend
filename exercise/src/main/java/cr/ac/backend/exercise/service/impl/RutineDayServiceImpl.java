package cr.ac.backend.exercise.service.impl;

import cr.ac.backend.exercise.model.RutineDay;
import cr.ac.backend.exercise.repo.RutineDayRepo;
import cr.ac.backend.exercise.service.RutineDayService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RutineDayServiceImpl implements RutineDayService {
    private final RutineDayRepo rutineDayRepo;

    @Override
    public Optional<List<RutineDay>> getAll() {
        var list = rutineDayRepo.findAll();
        if (list.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(list);
    }

    @Override
    public Optional<RutineDay> getById(Long id) {
        return rutineDayRepo.findById(id);
    }

    @Override
    public Optional<RutineDay> save(RutineDay rutineDay) {
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
    public Optional<RutineDay> update(RutineDay rutineDay) {
        try {
            return Optional.of(rutineDayRepo.save(rutineDay));
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
