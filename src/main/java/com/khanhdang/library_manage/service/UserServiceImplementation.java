package com.khanhdang.library_manage.service;

import com.khanhdang.library_manage.dto.Roles;
import com.khanhdang.library_manage.dto.User;
import com.khanhdang.library_manage.exception.RoleException;
import com.khanhdang.library_manage.exception.UserException;
import com.khanhdang.library_manage.mapper.UserMapper;
import com.khanhdang.library_manage.repository.RoleRepository;
import com.khanhdang.library_manage.repository.UserRepository;
import com.khanhdang.library_manage.request.User.UserCreationRequest;
import com.khanhdang.library_manage.request.User.UserUpdateRequest;
import com.khanhdang.library_manage.response.ApiResponse;
import com.khanhdang.library_manage.response.UserResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImplementation implements UserService{
    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    private final UserMapper userMapper;
    @Autowired
    public UserServiceImplementation(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public UserResponseDTO createUser(UserCreationRequest request) {
        if(userRepository.existsByUsername(request.getUsername())){
            throw new UserException("user already exists");
        }
        User user = userMapper.userRqToUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        Set<Roles> roles = new HashSet<>();
        Roles role = roleRepository.findByRoleName("USER")
                .orElseThrow(() -> new RoleException("Role 'USER' not found"));
        roles.add(role);
        user.setRoles(roles);
        return userMapper.userToUserResponse(userRepository.save(user));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)){
             throw new UserException("User not found wih id :" + id);
        }
         userRepository.deleteById(id);
    }

    @Override
    public UserResponseDTO updateUser(Long id, UserUpdateRequest request) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserException("User not found"));
        // Cập nhật thông tin user
        if (request.getUsername() != null) {
            user.setUsername(request.getUsername());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        // Lưu user đã cập nhật
        User updatedUser = userRepository.save(user);
        // Chuyển đổi và trả về UserResponseDTO
        return userMapper.userToUserResponse(updatedUser);
    }
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponseDTO> getUser() {
        List<User> list = userRepository.findAll();
        if (list.isEmpty())
        {
             throw new UserException("User list is empty");
        }
        return list.stream().map(userMapper::userToUserResponse).collect(Collectors.toList());
    }
    @Override
    @PostAuthorize("returnObject.username == authentication.name")
    public UserResponseDTO getUserById(Long id) {
        log.info("Authenticated username: {}", SecurityContextHolder.getContext().getAuthentication().getName());
        User user = userRepository.findById(id).orElseThrow(() -> new UserException("user not found"));
        log.info("Retrieved user from database: {}", user.getUsername());
        UserResponseDTO userResponseDTO = userMapper.userToUserResponse(user);
        log.info("Mapped UserResponseDTO username: {}", userResponseDTO.getUsername());
        return userResponseDTO;
    }

    @Override
    public UserResponseDTO getMyInfo() {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        User user = userRepository.findByUsername(name).orElseThrow( ()->  new UserException("cannot found user by username" + context));
        return  userMapper.userToUserResponse(user);
    }

}
