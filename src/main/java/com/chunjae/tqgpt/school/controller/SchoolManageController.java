package com.chunjae.tqgpt.school.controller;

import com.chunjae.tqgpt.school.entity.School;
import com.chunjae.tqgpt.school.service.SchoolService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/high")
public class SchoolManageController {

    private SchoolService schoolService;

    @GetMapping("/search")
    public String showSchoolManagePage(Model model) {
        model.addAttribute("schoolList", schoolService.getTop10Schools());
        model.addAttribute("count", schoolService.getAllSchoolsCnt());
        return "views/schoolManage/searchSchool";
    }

    @GetMapping("/info")
    public String showSchoolManagePage() {
        return "views/schoolManage/infoSchool";
    }
}
