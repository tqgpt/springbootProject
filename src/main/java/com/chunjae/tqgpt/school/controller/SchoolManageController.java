package com.chunjae.tqgpt.school.controller;

import com.chunjae.tqgpt.school.entity.School;
import com.chunjae.tqgpt.school.service.SchoolService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/high/search")
public class SchoolManageController {
    private SchoolService schoolService;
    @GetMapping("")
    public String showSchoolManagePage(Model model) {
        List<School> schoolList = schoolService.getTop10Schools();
        for(School school : schoolList) {
            System.out.println(school.toString());
        }
        model.addAttribute("schoolList", schoolList);
        return "views/schoolManage/searchSchool";
    }
}
