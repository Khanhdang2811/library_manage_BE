package com.khanhdang.library_manage.service;

import com.khanhdang.library_manage.dto.Loan;
import com.khanhdang.library_manage.dto.User;
import com.khanhdang.library_manage.request.loan.CreationLoanRequest;
import com.khanhdang.library_manage.response.ApiResponse;
import com.khanhdang.library_manage.response.LoanDetailResponse;
import com.khanhdang.library_manage.response.LoanResponse;
import com.khanhdang.library_manage.response.LoanResponseDTO;

import java.util.List;

public interface LoanService {
    public ApiResponse<?> createLoanBook(CreationLoanRequest request);
    public ApiResponse<List<LoanResponseDTO>> getLoanByUserId();
    public ApiResponse<List<LoanDetailResponse>> getLoanDetailByIdLoan(Long idLoan);
}
