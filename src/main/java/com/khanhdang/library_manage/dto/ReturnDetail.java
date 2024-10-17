package com.khanhdang.library_manage.dto;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Entity
@Table (name = "return_detail")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults ( level = AccessLevel.PRIVATE)
public class ReturnDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;
    @ManyToOne
    @JoinColumn(name = "id_return",referencedColumnName = "id_return")
    Return aReturn;
    @ManyToOne
    @JoinColumn(name = "id_book",referencedColumnName = "id_book")
    Book books;
    @Column ( name = "quantity")
    Long quantity;
    @Column (name = "fine_amount")
    BigDecimal fineAmount;
}
