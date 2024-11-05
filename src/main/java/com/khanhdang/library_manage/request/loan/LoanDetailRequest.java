package com.khanhdang.library_manage.request.loan;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults( level = AccessLevel.PRIVATE)
public class LoanDetailRequest {
    Long bookId;
    Integer quantity;
}
