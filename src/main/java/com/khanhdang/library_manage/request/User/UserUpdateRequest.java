package com.khanhdang.library_manage.request.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults (level = AccessLevel.PRIVATE)
public class UserUpdateRequest {
    @Size(min = 3,message = "Username must be at least 3 characters long")
    String username;
    @Size(min = 8,message = "Password must have at least 8 characters")
    @Pattern(regexp = ".*[!@#$%^&*()].*", message = "Password must  have unique signatures")
    String password;
    @Email
    String email;
}
