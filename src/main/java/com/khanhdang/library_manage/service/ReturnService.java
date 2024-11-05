package com.khanhdang.library_manage.service;

import com.khanhdang.library_manage.response.ApiResponse;

public interface ReturnService {
    public ApiResponse<?> returnLoan(Long idLoan);

}
