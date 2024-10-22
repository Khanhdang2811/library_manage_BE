package com.khanhdang.library_manage.dto;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table (name = "users")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults (level = AccessLevel.PRIVATE)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
     Long id;
    @Column(name = "username")
     String username;
    @Column(name = "password")
     String password;
    @Column(name ="email")
     String email;
    @Column(name = "created_date")
     LocalDateTime createdDate;
    @Column(name = "updated_date")
     LocalDateTime updatedDate;
    @Column(name = "deleted_date")
     LocalDateTime deletedDate;
    @Column(name = "created_id")
     int createdId;
    @Column(name = "updated_id")
     int updatedId;
    @Column(name = "deleted_id")
     int deletedId;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "role_user",
            joinColumns = @JoinColumn(name = "id_user"),
            inverseJoinColumns = @JoinColumn(name = "id_role")
    )
    Set<Roles> roles;


    //Phương thức này được gọi trước khi thực thể được lưu lần đầu tiên vào cơ sở dữ liệu
    @PrePersist
    protected void onCreate(){
        createdDate = LocalDateTime.now();
        updatedDate = LocalDateTime.now();
    }
    //Phương thức này được gọi trước khi thực thể được cập nhật trong cơ sở dữ liệu
    @PreUpdate
    protected void onUpdate() {
        updatedDate = LocalDateTime.now();
    }
}
