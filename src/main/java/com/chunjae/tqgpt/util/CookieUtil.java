package com.chunjae.tqgpt.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Arrays;
import java.util.Optional;

public class CookieUtil {

    public static Cookie createCookie(String name, String value, int age, boolean secure) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(age);
        cookie.setSecure(secure);
        return cookie;
    }

    public static void addCookie(HttpServletResponse res, String name, String value, int age, boolean secure) {
        Cookie cookie = createCookie(name, value, age, secure);
        res.addCookie(cookie);
    }

    public static Optional<Cookie> getCookie(HttpServletRequest req, String name) {
        return Arrays.stream(Optional.ofNullable(req.getCookies()).orElse(new Cookie[]{}))
                .filter(cookie -> name.equals(cookie.getName()))
                .findFirst();
    }

    public static void deleteCookie(HttpServletResponse res, String name, boolean secure) {
        addCookie(res, name, "", 0, secure);
    }
}
