package com.chunjae.tqgpt.global.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {
        @SneakyThrows
        @RequestMapping(value = "/error")
        public String handleError(HttpServletRequest request, Model model) {
            Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
            int statusCode = Integer.parseInt(status.toString());

            String errorMessage = switch (statusCode) {
                case 400 -> "서버 요청 실패";
                case 404 -> "요청한 페이지를 찾을 수 없습니다";
                case 500 -> "서버 오류";
                default -> "예기치 못한 오류가 발생했습니다";
            };

            model.addAttribute("status", statusCode);
            model.addAttribute("message", errorMessage);

            return "fragments/error";
        }
}