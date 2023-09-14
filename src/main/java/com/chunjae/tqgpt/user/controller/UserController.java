package com.chunjae.tqgpt.user.controller;

import com.chunjae.tqgpt.user.entity.User;
import com.chunjae.tqgpt.user.service.UserService;
import com.chunjae.tqgpt.util.ConnectUtil;
import com.chunjae.tqgpt.util.CookieUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/login")
    public String login(HttpServletRequest req, Model model) {
        Optional<Cookie> cookie = CookieUtil.getCookie(req, "userId");
        cookie.ifPresent(value -> model.addAttribute("rememberId", value.getValue()));

        return "views/user/login";
    }

    @GetMapping("/user/success")
    public String loginSuccess(@AuthenticationPrincipal User user, HttpServletRequest req, HttpServletResponse response) {
        if (user == null) {
            return "redirect:/login";
        }
        String ip = ConnectUtil.getClientIP(req);
        userService.logUserAccess(user, ip);
        log.info("user-toString: {}", user.toString());
        log.info("userAuth: {}", user.getRoles().stream()
                .map(role -> role.getRole().toString())
                .collect(Collectors.toList()));

        return "views/schoolManage/manageHome";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest req, HttpServletResponse res) {
        new SecurityContextLogoutHandler()
                .logout(req, res, SecurityContextHolder.getContext().getAuthentication());

        return "redirect:/login";
    }
}
