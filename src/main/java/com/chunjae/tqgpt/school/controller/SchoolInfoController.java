package com.chunjae.tqgpt.school.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@AllArgsConstructor
@RequestMapping("/high/info")
public class SchoolInfoController {
    @GetMapping("")
    public String showSchoolManagePage() {
        return "views/schoolManage/infoSchool";
    }
}
