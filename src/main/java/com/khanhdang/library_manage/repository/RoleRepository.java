package com.khanhdang.library_manage.repository;

import com.khanhdang.library_manage.dto.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Roles,Long> {
    @Query("SELECT r FROM Roles r JOIN FETCH r.users WHERE r.id = :id")
    Optional<Roles> findByIdWithUsers(@Param("id") Long id);

    Optional<Roles> findByRoleName(String name_role);
}
