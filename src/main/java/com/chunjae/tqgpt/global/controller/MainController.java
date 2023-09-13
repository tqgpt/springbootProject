package com.chunjae.tqgpt.global.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/admin")
    public String admin(){
        return "redirect:/high/school/search";
    }

    @GetMapping("/")
    public String index(){
        return "views/main/index";
    }
}
