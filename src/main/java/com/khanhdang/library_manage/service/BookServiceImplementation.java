package com.khanhdang.library_manage.service;
import com.khanhdang.library_manage.dto.Author;
import com.khanhdang.library_manage.dto.Category;
import com.khanhdang.library_manage.dto.Book;
import com.khanhdang.library_manage.exception.ApiStatus;
import com.khanhdang.library_manage.exception.BookException;
import com.khanhdang.library_manage.mapper.BookMapper;
import com.khanhdang.library_manage.repository.AuthorRepository;
import com.khanhdang.library_manage.repository.BookRepository;
import com.khanhdang.library_manage.repository.CategoryRepository;
import com.khanhdang.library_manage.request.Book.BookCreationRequest;
import com.khanhdang.library_manage.request.Book.BookUpdateRequest;
import com.khanhdang.library_manage.response.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class BookServiceImplementation implements BookService{
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    private final BookMapper bookMapper;

    @Autowired
    public BookServiceImplementation(BookMapper bookMapper) {
        this.bookMapper = bookMapper;
    }


    @Override
    public Book createBook(BookCreationRequest request) {
        boolean checkBook = bookRepository.existsByTitle(request.getTitle());
        if (checkBook) {
            throw new BookException("Book already exists");
        }

        if (request.getId_author() == null || request.getId_category() == null) {
            throw new BookException("Author ID and Category ID are required");
        }

        Book book = bookMapper.bookCreationRqToBook(request);

        if (book.getAuthor() == null) {
            throw new BookException("Author not found");
        }
        if (book.getCategory() == null) {
            throw new BookException("Category not found");
        }
        return bookRepository.save(book);
    }


    @Override
    public ApiResponse<Book> updateBook(String id, BookUpdateRequest request) {
        Book books = bookRepository.findById(Long.valueOf(id))
                .orElseThrow(() -> new BookException("BOOK NOT FOUND"));

        if (request.getId_category() != null) {
            books.setCategory(categoryRepository.findById(request.getId_category()).orElse(null));
        }
        if (request.getId_author() != null) {
            books.setAuthor(authorRepository.findById(request.getId_author()).orElse(null));
        }
        if (request.getTitle() != null) {
            books.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            books.setDescription(request.getDescription());
        }
        if (request.getPublished_year() != null) {
            books.setPublishedYear(request.getPublished_year());
        }
        if (request.getQuantity() != null) {
            books.setQuantity(request.getQuantity());
        }
        if (request.getImage_url() != null) {
            books.setImageUrl(request.getImage_url());
        }
        Book updatedBook = bookRepository.save(books);
        return ApiResponse.<Book>builder()
                .status(ApiStatus.SUCCESS)
                .message("Book updated successfully.")
                .data(updatedBook)
                .build();
    }


    @Override
    public void deleteBookById(String bookId) {
        // Kiểm tra xem sách có tồn tại không
        if (!bookRepository.existsById(Long.valueOf(bookId))) {
            throw new NoSuchElementException("Book not found with ID: " + bookId);
        }
        // Nếu sách tồn tại, xóa sách
        bookRepository.deleteById(Long.valueOf(bookId));
    }


    @Override
    public List<BookResponseDTO> getAllBook() {
        List<Book> booksList = bookRepository.findAll();
        return booksList.stream()
                .map(bookMapper::bookToBookResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public BookResponseDTO findBookById(String id) {
        Book book = bookRepository.findById(Long.valueOf(id))
                .orElseThrow(() -> new BookException("Book not found with id : "
                + id));
        return bookMapper.bookToBookResponseDTO(book);
    }

    @Override
    public List<BookResponseDTO> getBookByTitle(String title) {
        List<Book> booksList = bookRepository.getAllBookByTitle(title);
        // Kiểm tra xem danh sách có rỗng không
        if (booksList.isEmpty()) {
            throw new BookException("No books found with the given title");
        }
        // Chuyển đổi danh sách sách sang danh sách BookResponeDTO
        return booksList.stream()
                .map(bookMapper::bookToBookResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookResponseDTO> getBooksByCategory(String categoryName) {
        // Tìm tác giả dựa trên tên
        Category category = categoryRepository.findByCategoryName(categoryName);
        if (category == null) {
            throw new BookException("Category not found");
        }
        Long idCategory = category.getId();
        // Tìm sách theo id tác giả
        List<Book> booksList = bookRepository.findByCategoryId(idCategory);
        if (booksList.isEmpty()) {
            throw new BookException("No books found for this Category");
        }
        // Convert danh sách sách sang DTO
        return booksList.stream()
                .map(bookMapper::bookToBookResponseDTO)
                .collect(Collectors.toList());
    }


    @Override
    public List<BookResponseDTO> getBookByAuthor(String authorName) {
        // Tìm tác giả dựa trên tên
        Author author = authorRepository.findByAuthorName(authorName);
        if (author == null) {
            throw new BookException("Author not found");
        }
        Long idAuthor = author.getId();
        // Tìm sách theo id tác giả
        List<Book> booksList = bookRepository.findByAuthorId(idAuthor);
        if (booksList.isEmpty()) {
            throw new BookException("No books found for this author");
        }
       return booksList.stream()
               .map(bookMapper::bookToBookResponseDTO)
               .collect(Collectors.toList());
    }
    @Override
    public List<BookResponseDTO> SearchBook(String title, String authorName, Long publishedYear, String category) {
        List<BookResponseDTO> result = new ArrayList<>();
        // Lọc theo từng tiêu chí
        if (title != null && !title.trim().isEmpty()) {
            result.addAll(getBookByTitle(title));
        }
        if (authorName != null && !authorName.trim().isEmpty()) {
            result.addAll(getBookByAuthor(authorName));
        }
        if (publishedYear != null) {
            result.addAll(getBookByPublishedYear(publishedYear));
        }
        if (category != null && !category.trim().isEmpty()) {
            result.addAll(getBooksByCategory(category));
        }
        // Loại bỏ trùng lặp
        return result.stream().distinct().collect(Collectors.toList());
    }

    @Override
    public List<BookResponseDTO> getBookByPublishedYear(Long publishedYear) {
        List<Book> booksList = bookRepository.getAllBookByPublishedYear(publishedYear);
        if (booksList == null || booksList.isEmpty()){
            throw new BookException("No books found for this published year");
        }
        return booksList.stream()
                .map(bookMapper::bookToBookResponseDTO)
                .collect(Collectors.toList());

    }
    @Override
    public long countTotalBooks() {
        return bookRepository.count();
    }


}
