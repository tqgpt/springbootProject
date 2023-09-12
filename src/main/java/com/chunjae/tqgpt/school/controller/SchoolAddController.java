package com.chunjae.tqgpt.school.controller;

import com.chunjae.tqgpt.school.dto.SchoolAddDto;
import com.chunjae.tqgpt.school.service.SchoolAddService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class SchoolAddController {
    private final SchoolAddService schoolAddService;
    @Autowired
    public SchoolAddController(SchoolAddService schoolAddService) {
        this.schoolAddService = schoolAddService;
    }

    @GetMapping("/school/add")
    public String addSchool() {
        return "addSchool";
    }

    @PostMapping("/school/add")
    public String addSchool(SchoolAddDto schoolDto) {
        System.out.println("Controller addSchool start : " + schoolDto.toString());
        schoolAddService.addSchool(schoolDto);

        return "addSchool";
    }
}
