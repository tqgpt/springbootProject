package com.chunjae.tqgpt.school.controller;

import com.chunjae.tqgpt.school.dto.SchoolDTO;
import com.chunjae.tqgpt.school.entity.SchoolDetail;
import com.chunjae.tqgpt.school.service.SchoolService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.HttpURLConnection;

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

    @GetMapping("/modify/{school-idx}")
    public String modifySchool(@PathVariable("school-idx") Long schoolIdx, Model model) {
        log.info("modifySchool Controller start : " + schoolIdx);
        if(schoolIdx == null) {
        }
        SchoolDetail schoolOne = schoolService.getSchoolOne(schoolIdx);
        System.out.println(schoolOne.getClass());
        //log.info("modifySchool Get schoolOne : " + schoolOne.getSchool().getIdx());
        model.addAttribute("info", schoolOne);

        return "modifySchool";
    }

    @PostMapping("/modify")
    public String modifySchool(SchoolDTO.SchoolAddDto modifiedSchoolInfo,@RequestParam Long schoolIdx) {
        log.info("modifySchool Post modifiedSchool : " +schoolIdx + modifiedSchoolInfo.toString());
        schoolService.modifySchool(schoolIdx, modifiedSchoolInfo);

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
        //유저 객체 받아서 이름 받아넣기
        schoolService.upsertSchoolData("user1");
        return "redirect:/high/school/search";
    }
}
