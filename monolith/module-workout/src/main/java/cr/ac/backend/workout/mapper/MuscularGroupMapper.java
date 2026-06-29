package cr.ac.backend.workout.mapper;

import cr.ac.backend.workout.domain.MuscularGroup;
import cr.ac.backend.workout.dto.MuscularGroupResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MuscularGroupMapper {
    MuscularGroupResponse toResponse(MuscularGroup group);
    List<MuscularGroupResponse> toResponseList(List<MuscularGroup> groups);
}
