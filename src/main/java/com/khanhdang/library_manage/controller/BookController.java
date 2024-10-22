package com.khanhdang.library_manage.controller;

import com.khanhdang.library_manage.dto.Book;
import com.khanhdang.library_manage.exception.ApiStatus;
import com.khanhdang.library_manage.exception.BookException;
import com.khanhdang.library_manage.request.book.BookCreationRequest;
import com.khanhdang.library_manage.request.book.BookUpdateRequest;
import com.khanhdang.library_manage.response.ApiResponse;
import com.khanhdang.library_manage.response.BookResponseDTO;
import com.khanhdang.library_manage.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/books")

public class BookController {
    @Autowired
    BookService bookService;

    @PostMapping
    public ResponseEntity<ApiResponse<Book>> createBook(@RequestBody BookCreationRequest request) {
        try {
            Book createdBook = bookService.createBook(request);
            ApiResponse<Book> apiResponse = ApiResponse.<Book>builder()
                    .status(ApiStatus.SUCCESS)
                    .message("Book created successfully.")
                    .data(createdBook)
                    .build();
            return ResponseEntity.ok(apiResponse);
        } catch (BookException e) {
            // Xử lý ngoại lệ trùng sách hoặc không tìm thấy author/category
            ApiResponse<Book> response = ApiResponse.<Book>builder()
                    .status(ApiStatus.ERROR)
                    .message(e.getMessage()) // Hiển thị thông điệp từ ngoại lệ
                    .build();
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response); // Trả về mã 409
        } catch (Exception e) {
            // Ghi log lỗi chi tiết
            e.printStackTrace();
            ApiResponse<Book> response = ApiResponse.<Book>builder()
                    .status(ApiStatus.ERROR)
                    .message("An error occurred while creating the book.")
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/{bookId}")
    public ResponseEntity<ApiResponse<Void>> deleteBookById(@PathVariable String bookId) {
        try {
            bookService.deleteBookById(bookId);
            // Nếu xóa thành công, trả về phản hồi thành công
            ApiResponse<Void> response = ApiResponse.<Void>builder()
                    .status(ApiStatus.SUCCESS)
                    .message("Book deleted successfully.")
                    .build();
            return ResponseEntity.ok(response);
        } catch (NoSuchElementException e) {
            // Nếu không tìm thấy sách, trả về mã 404 và thông điệp lỗi
            ApiResponse<Void> response = ApiResponse.<Void>builder()
                    .status(ApiStatus.ERROR)
                    .message("Book not found.")
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            // Nếu có lỗi khác, trả về mã 500 và thông điệp lỗi
            ApiResponse<Void> response = ApiResponse.<Void>builder()
                    .status(ApiStatus.ERROR)
                    .message("An error occurred while deleting the book.")
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Book>> updateBook(@PathVariable("id") String id, @RequestBody BookUpdateRequest request) {
        try {
            ApiResponse<Book> apiResponse = bookService.updateBook(id, request);
            return ResponseEntity.ok(apiResponse);
        } catch (BookException e) {
            // Xử lý ngoại lệ khi không tìm thấy sách
            ApiResponse<Book> response = ApiResponse.<Book>builder()
                    .status(ApiStatus.ERROR)
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response); // Trả về mã 404 : SÁCH KHÔNG TÌM THẤY
        } catch (Exception e) {
            // Nếu có lỗi khác, trả về mã 500 và thông điệp lỗi chung
            ApiResponse<Book> response = ApiResponse.<Book>builder()
                    .status(ApiStatus.ERROR)
                    .message("An error occurred while updating the book.")
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookResponseDTO>> findBookById(@PathVariable String id) {

        try {
            BookResponseDTO bookResponseDTO = bookService.findBookById(id);
            ApiResponse<BookResponseDTO> response = ApiResponse.<BookResponseDTO>builder()
                    .status(ApiStatus.SUCCESS)
                    .message("Find book by id successfully")
                    .data(bookResponseDTO)
                    .build();
            return ResponseEntity.ok(response);

        }catch (BookException e){
            ApiResponse<BookResponseDTO> response = ApiResponse.<BookResponseDTO>builder()
                    .status(ApiStatus.ERROR)
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }catch (Exception e){
            ApiResponse<BookResponseDTO> response = ApiResponse.<BookResponseDTO>builder()
                    .status(ApiStatus.ERROR)
                    .message("An error occurred while searching for the book.")
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    @GetMapping("/category/{categoryname}")
    public ResponseEntity<ApiResponse<List<BookResponseDTO>>> getAllBooksByCategoryName(@PathVariable("categoryname") String categoryname) {
        if (categoryname == null || categoryname.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        List<BookResponseDTO> list = bookService.getBooksByCategory(categoryname);
        ApiResponse<List<BookResponseDTO>> response = ApiResponse.<List<BookResponseDTO>>builder()
                .status(ApiStatus.SUCCESS)
                .message("Books retrieved successfully")
                .data(list)
                .build();
        return ResponseEntity.ok(response);
    }


    @GetMapping("/search/{title}")
    public ResponseEntity<ApiResponse<List<BookResponseDTO>>> getBooksByTitle(@PathVariable("title") String title){
        if (title == null || title.trim().isEmpty()){
            return ResponseEntity.badRequest().build();
        }
        List<BookResponseDTO> bookResponseDTOList = bookService.getBookByTitle(title);
        ApiResponse<List<BookResponseDTO>> response = ApiResponse.<List<BookResponseDTO>>builder()
                .status(ApiStatus.SUCCESS)
                .message("Books retrieved successfully")
                .data(bookResponseDTOList)
                .build();
        return ResponseEntity.ok(response);
    }
    @GetMapping
    public ResponseEntity<List<BookResponseDTO>> getAllBooks(){
        List<BookResponseDTO> books = bookService.getAllBook();
        return ResponseEntity.ok(books);
    }
    @GetMapping("/publishedyear/{publishYear}")
    public ResponseEntity<ApiResponse<List<BookResponseDTO>>> getAllBooksByPublishedYear(@PathVariable("publishYear")Long publishYear){
        if (publishYear == null )
        {
           return ResponseEntity.badRequest().build();
        }
        ApiResponse<List<BookResponseDTO>>  response = ApiResponse.<List<BookResponseDTO>>builder()
                .status(ApiStatus.SUCCESS)
                .message("Books retrieved successfully ")
                .data(bookService.getBookByPublishedYear(publishYear))
                .build();

        return ResponseEntity.ok(response);
    }
    @GetMapping("/books/total")
    public ResponseEntity<ApiResponse<Long>> totalBook() {
        long total = bookService.countTotalBooks();
        ApiResponse<Long> response = ApiResponse.<Long>builder()
                .status(ApiStatus.SUCCESS)
                .message("Total Books: " + total)
                .data(total)
                .build();
        return ResponseEntity.ok(response);
    }
}
