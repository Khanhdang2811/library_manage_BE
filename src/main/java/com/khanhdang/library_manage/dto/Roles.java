package com.khanhdang.library_manage.dto;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Entity
@Table ( name = "roles")
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults (level = AccessLevel.PRIVATE)
public class Roles {
    @Id
    @GeneratedValue ( strategy = GenerationType.IDENTITY)
    @Column (name = "id",nullable = false)
    Long id;
    @Column (name = "role_name",nullable = false)
    String roleName;

}
