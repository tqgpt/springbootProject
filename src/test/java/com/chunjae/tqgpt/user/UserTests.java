package com.chunjae.tqgpt.user;

import com.chunjae.tqgpt.user.entity.Role;
import com.chunjae.tqgpt.user.entity.User;
import com.chunjae.tqgpt.user.entity.UserRole;
import com.chunjae.tqgpt.user.repository.RoleRepository;
import com.chunjae.tqgpt.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
public class UserTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

//    @Autowired
//    private PasswordEncoder passwordEncoder;

    @Test
    void insertUser(){

        User user = User.builder()
                .userId("admin2")
//                .password(passwordEncoder.encode("1234"))
                .password("1234")
                .userName("관리자2")
                .flag("N")
                .build();
        userRepository.save(user);
        Role role = Role.builder()
                .user(user)
                .role(UserRole.MANAGER)
                .build();
        roleRepository.save(role);
    }
}