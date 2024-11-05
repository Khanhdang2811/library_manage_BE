package com.khanhdang.library_manage.request.loan;

import com.khanhdang.library_manage.dto.LoanDetail;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults ( level = AccessLevel.PRIVATE)
public class CreationLoanRequest {
    public List<LoanDetailRequest> loanDetails;
}
