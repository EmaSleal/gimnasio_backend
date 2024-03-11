package cr.ac.backend.exercise.service;



import cr.ac.backend.exercise.model.Routine;

import java.util.List;
import java.util.Optional;

public interface RoutineService {
    /*methods to read, create, edit and delete*/

    public Optional<List<Routine>> getAll();

    public Optional<Routine> getById(Long id);

    public Optional<Routine> save(Routine rutine);

    public Boolean delete(Long id);

    public Optional<Routine> update(Routine rutine);

}
