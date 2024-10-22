package com.khanhdang.library_manage.controller;

import com.khanhdang.library_manage.exception.ApiStatus;
import com.khanhdang.library_manage.exception.UserException;
import com.khanhdang.library_manage.request.User.UserCreationRequest;
import com.khanhdang.library_manage.request.User.UserUpdateRequest;
import com.khanhdang.library_manage.response.ApiResponse;
import com.khanhdang.library_manage.response.UserResponseDTO;
import com.khanhdang.library_manage.service.UserService;
import jakarta.servlet.http.PushBuilder;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;


@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponseDTO>> createUser(@RequestBody @Valid UserCreationRequest request){
        UserResponseDTO userResponseDTO = userService.createUser(request);
        ApiResponse<UserResponseDTO> response = ApiResponse.<UserResponseDTO>builder()
                    .status(ApiStatus.SUCCESS)
                    .message("Create user is successful")
                    .data(userResponseDTO)
                    .build();
        return ResponseEntity.ok(response);
    }
    @GetMapping("/get-user")
    public ResponseEntity<ApiResponse<List<UserResponseDTO>>> getUser(){
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        log.warn(authentication.getName());
        authentication.getAuthorities().forEach(grantedAuthority -> log.info(grantedAuthority.getAuthority()));

        List<UserResponseDTO> userResponseDTO = userService.getUser();
        ApiResponse<List<UserResponseDTO>> response = ApiResponse.<List<UserResponseDTO>>builder()
                .status(ApiStatus.SUCCESS)
                .message("Get user successful")
                .data(userResponseDTO)
                .build();
        return ResponseEntity.ok(response);
    }
    @GetMapping("/get-user/{id}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> getUserById(@PathVariable Long id){
        try{
            UserResponseDTO userResponseDTO = userService.getUserById(id);
            ApiResponse<UserResponseDTO> response = ApiResponse.<UserResponseDTO>builder()
                    .status(ApiStatus.SUCCESS)
                    .message("Get user by id successful")
                    .data(userResponseDTO)
                    .build();
            return ResponseEntity.ok(response);
        }catch (UserException e){ //truong hop id trong
            ApiResponse<UserResponseDTO> response = ApiResponse.<UserResponseDTO>builder()
                    .status(ApiStatus.ERROR)
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/my-info")
    public ResponseEntity<ApiResponse<UserResponseDTO>> getMyInfo(){
            UserResponseDTO userResponseDTO = userService.getMyInfo();
            ApiResponse<UserResponseDTO> response = ApiResponse.<UserResponseDTO>builder()
                    .status(ApiStatus.SUCCESS)
                    .message("Get info successful")
                    .data(userResponseDTO)
                    .build();
            return ResponseEntity.ok(response);
    }


    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> updateUser(@PathVariable Long id, @RequestBody @Valid UserUpdateRequest request) {
        try {
            UserResponseDTO updatedUser = userService.updateUser(id, request);
            ApiResponse<UserResponseDTO> response = ApiResponse.<UserResponseDTO>builder()
                    .status(ApiStatus.SUCCESS)
                    .message("Update user successful")
                    .data(updatedUser)
                    .build();
            return ResponseEntity.ok(response);
        } catch (UserException e) {
            ApiResponse<UserResponseDTO> response = ApiResponse.<UserResponseDTO>builder()
                    .status(ApiStatus.ERROR)
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUserById(@PathVariable Long id)
    {
        try {
            userService.deleteUser(id);
            ApiResponse<Void> response = ApiResponse.<Void>builder()
                    .status(ApiStatus.SUCCESS)
                    .message("Delete by id successful")
                    .build();
            return ResponseEntity.ok(response);
        } catch (UserException e) {
            ApiResponse<Void> response = ApiResponse.<Void>builder()
                    .status(ApiStatus.ERROR)
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            ApiResponse<Void> response = ApiResponse.<Void>builder()
                    .status(ApiStatus.ERROR)
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
