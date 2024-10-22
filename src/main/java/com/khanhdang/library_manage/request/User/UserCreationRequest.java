package com.khanhdang.library_manage.request.User;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import jakarta.validation.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {
    @Size(min = 3,message = "Username must be at least 3 characters long")
    String username;
    @Size(min = 8,message = "Password must have at least 8 characters")
    @Pattern(regexp = ".*[!@#$%^&*()].*", message = "Password must  have unique signatures")
    String password;
    @Email
    String email;
}
