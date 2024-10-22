package com.khanhdang.library_manage.service;


import com.khanhdang.library_manage.request.authentication.AuthenticationRequest;
import com.khanhdang.library_manage.request.authentication.IntrospectRequest;
import com.khanhdang.library_manage.response.AuthenticationResponse;
import com.khanhdang.library_manage.response.IntrospectResponse;
import com.nimbusds.jose.JOSEException;

import java.text.ParseException;

public interface AuthenticationService {
    AuthenticationResponse authenticate(AuthenticationRequest request);
    public IntrospectResponse introspectResponse(IntrospectRequest request) throws JOSEException, ParseException;
}
