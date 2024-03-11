package cr.ac.backend.exercise.service;


import cr.ac.backend.exercise.model.MuscularGroup;

import java.util.List;
import java.util.Optional;

public interface MuscularGroupService {

    //select, save, delete, update

    public Optional<List<MuscularGroup>> getAll();

    public Optional<MuscularGroup> getById(Long id);

    public Optional<MuscularGroup> save(MuscularGroup muscularGroup);

    public Boolean delete(Long id);

    public Optional<MuscularGroup> update(MuscularGroup muscularGroup);

}
