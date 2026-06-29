package cr.ac.backend.plan.mapper;

import cr.ac.backend.plan.domain.DailyRoutine;
import cr.ac.backend.plan.dto.DailyRoutineResponse;
import cr.ac.backend.workout.mapper.WorkoutSpecificationMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring", uses = {WorkoutSpecificationMapper.class})
public interface DailyRoutineMapper {
    @Mapping(source = "workoutPlan.id", target = "workoutPlanId")
    DailyRoutineResponse toResponse(DailyRoutine routine);
    List<DailyRoutineResponse> toResponseList(List<DailyRoutine> routines);
}
