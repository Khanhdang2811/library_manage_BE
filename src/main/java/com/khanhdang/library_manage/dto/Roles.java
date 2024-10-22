package com.khanhdang.library_manage.dto;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.mapstruct.Mapper;

import java.util.Set;

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

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToMany(mappedBy = "roles")
    Set<User> users;
}
