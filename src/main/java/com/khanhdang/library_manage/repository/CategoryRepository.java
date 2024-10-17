package com.khanhdang.library_manage.repository;

import com.khanhdang.library_manage.dto.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findByCategoryName(String categoryName);
}
