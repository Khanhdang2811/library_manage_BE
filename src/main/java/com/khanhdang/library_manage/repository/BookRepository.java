package com.khanhdang.library_manage.repository;

import com.khanhdang.library_manage.dto.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book,Long> {


    boolean existsByTitle(String title);

    @Query("SELECT b FROM Book b WHERE b.category.id = :categoryId")
    List<Book> findByCategoryId(@Param("categoryId") Long categoryId);

    @Query("SELECT b.author.authorName, COUNT(b) AS bookCount" +
            " FROM Book b" +
            " GROUP BY b.author.authorName" +
            " ORDER BY bookCount DESC")
    List<Object[]> findPopularAuthors();

    @Query("SELECT EXTRACT(YEAR FROM b.publicationDate) AS year, COUNT(b) AS bookCount " +
            "FROM Book b " +
            "GROUP BY year " +
            "ORDER BY bookCount DESC")
    List<Object[]> findMostPopularYear();

    @Query("SELECT b FROM Book b WHERE b.title LIKE CONCAT('%', :title, '%')")
    List<Book> getAllBookByTitle(@Param("title") String title);

    @Query("SELECT b " +
            "FROM Book b  " +
            "Where b.author.id = :idAuthor")
    List<Book> findByAuthorId(@Param("idAuthor")Long idAuthor);

    @Query("SELECT b " +
            "FROM Book b " +
            "Where b.publishedYear = :publishedYear ")
    List<Book> getAllBookByPublishedYear(@Param("publishedYear") Long publishYear);



}
