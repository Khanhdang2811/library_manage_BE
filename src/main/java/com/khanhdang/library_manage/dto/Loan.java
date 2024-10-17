package com.khanhdang.library_manage.dto;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Table (name = "loan")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults (level = AccessLevel.PRIVATE)
public class Loan {
    @Id
    @GeneratedValue ( strategy = GenerationType.IDENTITY)
    @Column ( name =  "id_loan")
    Long id;
    @ManyToOne
    @JoinColumn(name = "id_user",referencedColumnName = "id")
    Users user;
    @Column(name = "borrowing_date")
    LocalDateTime borrowingDate;
    @Column(name = "due_date")
    LocalDateTime dueDate;
    @Column(name = "actual_return_date")
    LocalDateTime actualReturndDate;
    @Column(name = "status")
    String status;

    @PrePersist
    protected void onCreated(){
        borrowingDate = LocalDateTime.now();
        //thiết lập ngày trả sẽ là 15 ngày kể từ ngày mượn sách ( quy định )
        dueDate = borrowingDate.plus(15, ChronoUnit.DAYS);
    }
}
