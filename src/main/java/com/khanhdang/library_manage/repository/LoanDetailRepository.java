package com.khanhdang.library_manage.repository;

import com.khanhdang.library_manage.dto.Loan;
import com.khanhdang.library_manage.dto.LoanDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoanDetailRepository extends JpaRepository<LoanDetail, Long> {
    List<LoanDetail> findByLoan(Loan loan);
}
