package cr.ac.backend.workout.mapper;

import cr.ac.backend.workout.domain.Workout;
import cr.ac.backend.workout.dto.WorkoutResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {MuscularGroupMapper.class})
public interface WorkoutMapper {
    WorkoutResponse toResponse(Workout workout);
    List<WorkoutResponse> toResponseList(List<Workout> workouts);
}
