package com.khanhdang.library_manage.repository;

import com.khanhdang.library_manage.dto.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository extends JpaRepository<Author,Long> {
    Author findByAuthorName(String authorName);
}
