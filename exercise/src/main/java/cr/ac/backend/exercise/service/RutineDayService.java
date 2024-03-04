package cr.ac.backend.exercise.service;

import cr.ac.backend.exercise.model.RutineDay;

import java.util.List;
import java.util.Optional;

public interface RutineDayService {
    /*methods to read, create, edit and delete*/

    public Optional<List<RutineDay>> getAll();

    public Optional<RutineDay> getById(Long id);

    public Optional<RutineDay> save(RutineDay rutineDay);

    public Boolean delete(Long id);

    public Optional<RutineDay> update(RutineDay rutineDay);
}
