package com.khanhdang.library_manage.dto;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table (name = "loan_detail")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults (level = AccessLevel.PRIVATE)
public class LoanDetail {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;
    @ManyToOne
    @JoinColumn(name = "id_loan",referencedColumnName = "id_loan")
    Loan loan;
    @ManyToOne
    @JoinColumn(name = "id_book",referencedColumnName = "id_book")
    Book book;
    @Column ( name = "quantity")
    Integer quantity;
}
