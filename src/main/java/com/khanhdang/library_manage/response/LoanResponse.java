package com.khanhdang.library_manage.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoanResponse {
     Long id;
     Long userId;
     String username;
     LocalDateTime borrowingDate;
     LocalDateTime dueDate;
     LocalDateTime actualReturnDate;
     String status;
     List<LoanDetailResponse> loanDetails;
}
