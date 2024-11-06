package com.khanhdang.library_manage.mapper;

import com.khanhdang.library_manage.dto.*;
import com.khanhdang.library_manage.response.LoanDetailResponse;
import com.khanhdang.library_manage.response.LoanResponse;
import com.khanhdang.library_manage.response.LoanResponseDTO;
import com.khanhdang.library_manage.response.ReturnResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LoanMapper {
    LoanMapper INSTANCE = Mappers.getMapper(LoanMapper.class);

    LoanResponseDTO LoanToLoanResponseDTO(Loan loan);

    // Ánh xạ từ Loan sang LoanResponse, chuyển user thông qua userId và username
    @Mapping(source = "loan.user.id", target = "userId")
    @Mapping(source = "loan.user.username", target = "username")
    @Mapping(source = "loanDetails", target = "loanDetails")
    LoanResponse loanToLoanResponse(Loan loan, List<LoanDetail> loanDetails);

    // Ánh xạ từ LoanDetail sang LoanDetailResponse, lấy thông tin bookId và bookTitle từ Book
    @Mapping(source = "book.id", target = "bookId")
    @Mapping(source = "book.title", target = "bookTitle")
    LoanDetailResponse loanDetailToLoanDetailResponse(LoanDetail loanDetail);

//    ReturnDetail loanDetailToFineDetail(Return r , List<LoanDetail> loanDetails );
//
//    ReturnResponseDTO returnToReturnResponseDTO(Return aReturn);
}




