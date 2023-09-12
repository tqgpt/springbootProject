package com.chunjae.tqgpt.school.controller;

import com.chunjae.tqgpt.school.dto.SchoolDTO;
import com.chunjae.tqgpt.school.service.SchoolService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/high/school")
public class SchoolController {
    private final SchoolService schoolService;

    @GetMapping("/add")
    public String addSchool() {
        return "addSchool";
    }

    @PostMapping("/add")
    public String addSchool(SchoolDTO.SchoolAddDto schoolDto) {
        log.info("Controller addSchool start : " + schoolDto.toString());
        schoolService.addSchool(schoolDto);

        return "addSchool";
    }

    @GetMapping("/search")
    public String showSchoolManageHomePage(Model model) {
        model.addAttribute("schoolList", schoolService.getTop10Schools());
        model.addAttribute("count", schoolService.getAllSchoolsCnt());
        return "views/schoolManage/manageHome";
    }

    @GetMapping("/info")
    public String showSchoolManageInfoPage() {
        return "views/schoolManage/infoSchool";
    }

    @GetMapping("/init-official-data")
    public String initOfficialData() {
        schoolService.upsertSchoolData();
        return "redirect:/high/school/search";
    }
}
