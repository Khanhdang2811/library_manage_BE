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
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImplementation implements UserService{
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Autowired
    public UserServiceImplementation(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            UserMapper userMapper
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserException("Current user not found"));
    }

    private boolean isAdmin(User user) {
        return user.getRoles().stream()
                .anyMatch(role -> role.getRoleName().equals("ADMIN"));
    }
    @Override
    @Transactional
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
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserException("User not found wih id :" + id);
        }
         userRepository.deleteById(id);
    }
    @Override
    @PreAuthorize("hasRole('ADMIN') or #id == principal.id")
    @Transactional
    public UserResponseDTO updateUser(Long id, UserUpdateRequest request) {
        User currentUser = getCurrentUser();
        User targetUser = userRepository.findById(id)
                .orElseThrow(() -> new UserException("User not found"));
        // Check if user has permission to update
        if (!currentUser.getId().equals(id) && !isAdmin(currentUser)) {
            throw new AccessDeniedException("No permission to update other users");
        }
        // Update username if provided and not already taken
        if (request.getUsername() != null && !request.getUsername().equals(targetUser.getUsername())) {
            if (userRepository.existsByUsername(request.getUsername())) {
                throw new UserException("Username already exists");
            }
            targetUser.setUsername(request.getUsername());
        }
        // Update other fields if provided
        if (request.getEmail() != null) {
            targetUser.setEmail(request.getEmail());
        }
        if (request.getPassword() != null) {
            targetUser.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        targetUser.setUpdatedId(currentUser.getId());
        targetUser.setUpdatedDate(LocalDateTime.now());
        User updatedUser = userRepository.save(targetUser);
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
        User currentUser = getCurrentUser();
        User targetUser = userRepository.findById(id)
                .orElseThrow(() -> new UserException("User not found"));
        if (!currentUser.getId().equals(id) && !isAdmin(currentUser)) {
            throw new AccessDeniedException("No permission to view other users' information");
        }
        return userMapper.userToUserResponse(targetUser);
    }

    @Override
    public UserResponseDTO getMyInfo() {
        User currentUser = getCurrentUser();
        return userMapper.userToUserResponse(currentUser);
    }

}
