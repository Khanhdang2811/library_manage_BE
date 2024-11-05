package com.khanhdang.library_manage.repository;

import com.khanhdang.library_manage.dto.Loan;
import com.khanhdang.library_manage.dto.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    boolean existsByUserAndStatus(User user, String statusOverdue);
    List<Loan> findByUserAndStatus(User user, String statusBorrowed);

    List<Loan> findByUserId( Long userId);

    List<Loan> findByUser(User user);
}
