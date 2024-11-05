package com.khanhdang.library_manage.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults (level = AccessLevel.PRIVATE)
public class LoanDetailResponse {
    Long id;
    Long bookId;
    String bookTitle;
    Integer quantity;
}
