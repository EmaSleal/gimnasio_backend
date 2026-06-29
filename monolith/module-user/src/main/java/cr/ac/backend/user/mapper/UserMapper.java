package cr.ac.backend.user.mapper;

import cr.ac.backend.user.domain.User;
import cr.ac.backend.user.dto.UpdateUserRequest;
import cr.ac.backend.user.dto.UserCredentials;
import cr.ac.backend.user.dto.UserResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface UserMapper {
    UserResponse toResponse(User user);
    UserCredentials toCredentials(User user);
    List<UserResponse> toResponseList(List<User> users);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget User user, UpdateUserRequest request);
}
