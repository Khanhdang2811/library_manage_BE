package com.khanhdang.library_manage.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults ( level = AccessLevel.PRIVATE)
public class BookResponseDTO {
    Long id;
    String authorName;
    String categoryName;
    String title;
    String description;
    Long publishedYear;
    Long quantity;
    String imageUrl;
}
