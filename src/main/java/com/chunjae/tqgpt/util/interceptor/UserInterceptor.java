package com.chunjae.tqgpt.util.interceptor;

import com.chunjae.tqgpt.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.stream.Collectors;

@Component
public class UserInterceptor implements HandlerInterceptor {

    @Override
    public void postHandle(HttpServletRequest req,
                           HttpServletResponse res,
                           Object handler,
                           ModelAndView modelAndView) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof User) {
            User user = (User) auth.getPrincipal();
            if (modelAndView != null) {
                modelAndView.addObject("userInfo", user.getName());
                modelAndView.addObject("userRoles", user.getRoles().stream()
                        .map(role -> role.getRole().getDescription())
                        .collect(Collectors.toList()));
            }
        }
    }
}