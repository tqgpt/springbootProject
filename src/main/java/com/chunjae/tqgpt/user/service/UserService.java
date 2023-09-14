package com.chunjae.tqgpt.user.service;

import com.chunjae.tqgpt.user.entity.User;
import com.chunjae.tqgpt.user.entity.UserAccess;
import com.chunjae.tqgpt.user.repository.UserAccessRepository;
import com.chunjae.tqgpt.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserAccessRepository userAccessRepository;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        log.info("userId:{}", userId);
        return userRepository.findByUserId(userId).orElseThrow(() -> new UsernameNotFoundException(userId));
    }

    @Transactional
    public void logUserAccess(User user, String ip) {
        userAccessRepository.save(UserAccess.builder()
                .userId(user.getUserId())
                .accessAt(LocalDateTime.now())
                .ip(ip)
                .build());
    }

}
