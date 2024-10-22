//package com.khanhdang.library_manage.config;
//
//import com.khanhdang.library_manage.dto.Roles;
//import com.khanhdang.library_manage.dto.User;
//import com.khanhdang.library_manage.repository.RoleRepository;
//import com.khanhdang.library_manage.repository.UserRepository;
//import lombok.extern.slf4j.Slf4j;
//import org.aspectj.weaver.ast.Var;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//import java.util.HashSet;
//import java.util.Set;
//
//@Configuration
//@Slf4j
//public class ApplicationInitConfig {
//
//    @Autowired
//    PasswordEncoder passwordEncoder;
//
//
//    @Bean
//    ApplicationRunner applicationRunner(UserRepository userRepository, RoleRepository roleRepository){
//        return args -> {
//            if(userRepository.findByUsername("admin").isEmpty()){
//                Roles adminRole = roleRepository.findByRoleName("ADMIN")
//                        .orElseThrow(() -> new RuntimeException("ADMIN role not found"));
//
//                 var roles =  new HashSet<Roles>();
//                 roles.add(adminRole);
//                User user = User.builder()
//                        .username("admin")
//                        .password(passwordEncoder.encode("admin"))
//                        .roles(roles)
//                        .build();
//                userRepository.save(user);
//                log.warn("admin user has been created with default password: admin, please change it");
//            }
//        };
//    }
//}
