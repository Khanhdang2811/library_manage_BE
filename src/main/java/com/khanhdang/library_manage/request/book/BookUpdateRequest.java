package com.khanhdang.library_manage.request.book;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults (level = AccessLevel.PRIVATE)
public class BookUpdateRequest {
    Long id_author;
    Long id_category;
    String title;
    String description;
    Long published_year;
    Long quantity;
    String image_url;
}
