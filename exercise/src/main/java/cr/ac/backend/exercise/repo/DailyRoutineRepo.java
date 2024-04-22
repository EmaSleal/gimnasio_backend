package cr.ac.backend.exercise.repo;

import cr.ac.backend.exercise.model.DailyRoutine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DailyRoutineRepo extends JpaRepository<DailyRoutine, Long> {

    Optional<List<DailyRoutine>> findByDays(String days);
}
