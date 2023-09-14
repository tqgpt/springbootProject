package com.chunjae.tqgpt.util.auth;

import com.chunjae.tqgpt.user.entity.User;
import com.chunjae.tqgpt.util.CookieUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest req,
                                        HttpServletResponse res,
                                        Authentication auth) throws IOException, ServletException {
        User user = (User) auth.getPrincipal();
        String remember = req.getParameter("remember");
        log.info("userId: {}, password: {}, remember: {}", user.getUserId(), user.getPassword(), remember);

        if ("on".equals(remember)) {
            CookieUtil.createCookie("userId", user.getUserId(), 60 * 60 * 24 * 7, false);
            CookieUtil.addCookie(res, "userId", user.getUserId(), 60 * 60 * 24 * 7, false);
        } else {
            CookieUtil.deleteCookie(res, "userId", false);
        }
        res.sendRedirect("/user/success");
    }
}
