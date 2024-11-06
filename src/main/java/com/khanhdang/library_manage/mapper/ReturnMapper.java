package com.khanhdang.library_manage.mapper;

import com.khanhdang.library_manage.dto.LoanDetail;
import com.khanhdang.library_manage.dto.Return;
import com.khanhdang.library_manage.dto.ReturnDetail;
import com.khanhdang.library_manage.response.ReturnResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper (componentModel = "spring")
public interface ReturnMapper {
    ReturnMapper INSTANCE = Mappers.getMapper(ReturnMapper.class);

    ReturnDetail loanDetailToFineDetail(Return r , List<LoanDetail> loanDetails );

    @Mapping(source = "loan.id", target = "idLoan")
    @Mapping(source = "returnDate", target = "returnDate")
    @Mapping(source = "totalFine", target = "totalFine")
    @Mapping(source = "description", target = "description")
    ReturnResponseDTO returnToReturnResponseDTO(Return aReturn);
}
