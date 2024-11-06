package com.khanhdang.library_manage.controller;

import com.khanhdang.library_manage.dto.Loan;
import com.khanhdang.library_manage.request.loan.CreationLoanRequest;
import com.khanhdang.library_manage.response.*;
import com.khanhdang.library_manage.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/loan")
public class LoanController {
    @Autowired
    LoanService loanService;


    @PostMapping
    public ResponseEntity<ApiResponse<?>> createLoan(@RequestBody CreationLoanRequest request){
        ApiResponse<?> response = loanService.createLoanBook(request);
        return ResponseEntity.ok(response);
    }
    @GetMapping
    public ResponseEntity<ApiResponse<List<LoanResponseDTO>>> getLoanByIdUser(){
        var response = loanService.getLoanByUserId();
        return ResponseEntity.ok(response);
    }
    @GetMapping("/{idLoan}")
    public ResponseEntity<ApiResponse<List<LoanDetailResponse>>> getLoanDetailByIdLoan(@PathVariable Long idLoan){
        var response = loanService.getLoanDetailByIdLoan(idLoan);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/{idLoan}")
    public ResponseEntity<ApiResponse<ReturnResponseDTO>> returnBook(@PathVariable Long idLoan) {
        ApiResponse<ReturnResponseDTO> response = loanService.returnLoan(idLoan);
        return ResponseEntity.ok(response);
    }
}
