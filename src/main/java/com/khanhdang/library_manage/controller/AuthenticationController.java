package com.khanhdang.library_manage.controller;

import com.khanhdang.library_manage.exception.ApiStatus;
import com.khanhdang.library_manage.exception.AuthenticateException;
import com.khanhdang.library_manage.request.authentication.AuthenticationRequest;
import com.khanhdang.library_manage.request.authentication.IntrospectRequest;
import com.khanhdang.library_manage.response.ApiResponse;
import com.khanhdang.library_manage.response.AuthenticationResponse;
import com.khanhdang.library_manage.response.IntrospectResponse;
import com.khanhdang.library_manage.service.AuthenticationService;
import com.khanhdang.library_manage.service.UserService;
import com.nimbusds.jose.JOSEException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    @Autowired
    AuthenticationService authenticationService;

    @PostMapping("/token")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> authenticate(@RequestBody @Valid AuthenticationRequest request) {
        try {
            var result = authenticationService.authenticate(request);
            ApiResponse<AuthenticationResponse> response = ApiResponse.<AuthenticationResponse>builder()
                    .status(ApiStatus.SUCCESS)
                    .message("authenticate successful")
                    .data(result)
                    .build();
            return ResponseEntity.ok(response);
        } catch (AuthenticateException e) {
            ApiResponse<AuthenticationResponse> response = ApiResponse.<AuthenticationResponse>builder()
                    .status(ApiStatus.ERROR)
                    .message("authenticate not successful")
                    .data(AuthenticationResponse.builder()
                            .authenticated(false)
                            .build())
                    .build();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
    @PostMapping("/introspect")
    public ResponseEntity<ApiResponse<IntrospectResponse>> authenticate(@RequestBody IntrospectRequest request)
            throws ParseException, JOSEException {
            var result = authenticationService.introspectResponse(request);
            ApiResponse<IntrospectResponse> response = ApiResponse.<IntrospectResponse>builder()
                    .status(ApiStatus.SUCCESS)
                    .data(result)
                    .build();
            return ResponseEntity.ok(response);
    }
}
