package com.khanhdang.library_manage.repository;

import com.khanhdang.library_manage.dto.Fine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FineRepository extends JpaRepository<Fine,Long> {
}
