package com.khanhdang.library_manage.mapper;

import com.khanhdang.library_manage.dto.Roles;
import com.khanhdang.library_manage.dto.User;
import com.khanhdang.library_manage.request.User.UserCreationRequest;
import com.khanhdang.library_manage.request.User.UserUpdateRequest;
import com.khanhdang.library_manage.response.UserResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.Mapping;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "roles", source = "roles", qualifiedByName = "rolesToStringSet")
    UserResponseDTO userToUserResponse(User user);

    @Named("rolesToStringSet")
    default Set<String> rolesToStringSet(Set<Roles> roles) {
        if (roles == null) {
            return null;
        }
        return roles.stream()
                .map(role -> role.getRoleName())
                .collect(Collectors.toSet());
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Mapping(target = "deletedDate", ignore = true)
    User userRqToUser(UserCreationRequest request);
}
