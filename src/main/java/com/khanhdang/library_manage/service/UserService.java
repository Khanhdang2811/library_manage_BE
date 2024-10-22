package com.khanhdang.library_manage.service;

import com.khanhdang.library_manage.dto.User;
import com.khanhdang.library_manage.request.User.UserCreationRequest;
import com.khanhdang.library_manage.request.User.UserUpdateRequest;
import com.khanhdang.library_manage.response.ApiResponse;
import com.khanhdang.library_manage.response.UserResponseDTO;
import org.springframework.stereotype.Service;

import java.util.List;


public interface UserService {
    public UserResponseDTO createUser(UserCreationRequest request);
    public void deleteUser(Long id);
    public UserResponseDTO updateUser(Long id, UserUpdateRequest request);
    public List<UserResponseDTO> getUser();
    public UserResponseDTO getUserById(Long id);
    public UserResponseDTO getMyInfo();




}
