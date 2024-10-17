package com.khanhdang.library_manage.request.Book;

import com.khanhdang.library_manage.dto.Author;
import com.khanhdang.library_manage.dto.Category;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookCreationRequest {
    Long id_author;
    Long id_category;
    String title;
    String description;
    Long publishedYear;
    Long quantity;
    String image_url;
}
