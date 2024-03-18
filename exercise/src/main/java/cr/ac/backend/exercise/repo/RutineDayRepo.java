package cr.ac.backend.exercise.repo;

import cr.ac.backend.exercise.model.RoutineDay;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RutineDayRepo extends JpaRepository<RoutineDay, Long> {

    Optional<List<RoutineDay>> findByDays(String days);
}
