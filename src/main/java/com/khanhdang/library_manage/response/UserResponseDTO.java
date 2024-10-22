package com.khanhdang.library_manage.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.khanhdang.library_manage.dto.Roles;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Set;
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponseDTO {
    Long id;
    String username;
    String email;
    LocalDateTime createdDate;
    LocalDateTime updatedDate;
    Set<String> roles;
}
