package com.khanhdang.library_manage.response;

import com.khanhdang.library_manage.dto.Loan;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults ( level = AccessLevel.PRIVATE)
public class ReturnResponseDTO {
    Long idLoan;
    LocalDateTime returnDate;
    BigDecimal totalFine;
    String description;
}
