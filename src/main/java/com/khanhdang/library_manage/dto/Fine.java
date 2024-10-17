package com.khanhdang.library_manage.dto;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "fine")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults ( level = AccessLevel.PRIVATE)
public class Fine {
    @Id
    @GeneratedValue ( strategy = GenerationType.IDENTITY)
    @Column(name = "id_fine",nullable = false)
    Long id;
    @OneToOne
    @JoinColumn(name = "id_return",referencedColumnName = "id_return")
    Return aReturn;
    @Column(name = "days_overdue")
    Long daysOverdue;
    @Column(name = "fine_amount")
    BigDecimal fineAmount;
}
