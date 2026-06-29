package cr.ac.backend.plan.mapper;

import cr.ac.backend.plan.domain.WorkoutPlan;
import cr.ac.backend.plan.dto.WorkoutPlanResponse;
import org.mapstruct.Mapper;
import java.util.List;

@Mapper(componentModel = "spring")
public interface WorkoutPlanMapper {
    WorkoutPlanResponse toResponse(WorkoutPlan plan);
    List<WorkoutPlanResponse> toResponseList(List<WorkoutPlan> plans);
}
