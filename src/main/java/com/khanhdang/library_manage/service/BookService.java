package com.khanhdang.library_manage.service;

import com.khanhdang.library_manage.dto.Book;
import com.khanhdang.library_manage.request.Book.BookCreationRequest;
import com.khanhdang.library_manage.request.Book.BookUpdateRequest;
import com.khanhdang.library_manage.response.*;

import java.util.List;

public interface BookService {
    public Book createBook(BookCreationRequest request);
    public ApiResponse<Book> updateBook(String id, BookUpdateRequest request);
    public void deleteBookById(String bookId);
    public List<BookResponseDTO> getAllBook();
    public BookResponseDTO findBookById(String id);
    public List<BookResponseDTO>  getBooksByCategory(String category);
    public List<BookResponseDTO> getBookByAuthor(String authorName);
    public List<BookResponseDTO> getBookByTitle(String title);
    public List<BookResponseDTO> getBookByPublishedYear(Long publishedYear);
    public List<BookResponseDTO> SearchBook(String title, String authorName, Long publishedYear, String category);
    public long countTotalBooks();
}
