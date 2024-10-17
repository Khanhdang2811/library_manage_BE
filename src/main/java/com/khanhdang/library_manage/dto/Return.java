package com.khanhdang.library_manage.dto;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.logging.Level;

@Entity
@Table ( name = "return")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults (level = AccessLevel.PRIVATE)

public class Return {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column ( name = "id_return")
    Long id;
    @OneToOne
    @JoinColumn(name = "id_loan")
    Loan loan;
    @Column ( name = "return_date")
    LocalDateTime returnDate;
    @Column ( name = "total_fine")
    BigDecimal totalFine;
    @Column ( name = "description")
    String description;
}
