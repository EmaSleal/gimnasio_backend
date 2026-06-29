package cr.ac.backend.workout.mapper;

import cr.ac.backend.workout.domain.WorkoutSpecification;
import cr.ac.backend.workout.dto.UpdateWorkoutSpecificationRequest;
import cr.ac.backend.workout.dto.WorkoutSpecificationResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface WorkoutSpecificationMapper {

    @Mapping(source = "workout.id", target = "workoutId")
    WorkoutSpecificationResponse toResponse(WorkoutSpecification spec);

    List<WorkoutSpecificationResponse> toResponseList(List<WorkoutSpecification> specs);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "workout", ignore = true)
    @Mapping(target = "id", ignore = true)
    void updateEntity(@MappingTarget WorkoutSpecification spec, UpdateWorkoutSpecificationRequest request);
}
