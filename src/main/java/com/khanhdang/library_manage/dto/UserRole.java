package com.khanhdang.library_manage.dto;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "role_user")
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults (level = AccessLevel.PRIVATE)
public class UserRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;
    @ManyToOne // Mối quan hệ nhiều với User
    @JoinColumn(name = "id_user",referencedColumnName = "id") // Cột tham chiếu đến bảng users
    private User user;

    @ManyToOne // Mối quan hệ nhiều với Role
    @JoinColumn(name = "id_role",referencedColumnName = "id") // Cột tham chiếu đến bảng roles
    private Roles role;

}
