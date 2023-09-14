package com.chunjae.tqgpt.school.controller;

import com.chunjae.tqgpt.school.dto.SchoolDTO;
import com.chunjae.tqgpt.school.entity.School;
import com.chunjae.tqgpt.school.service.SchoolService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/high/school")
public class SchoolController {
    private final SchoolService schoolService;

    /*학교정보 추가 페이지
    * GET
    * */
    @GetMapping("/add")
    public String addSchool() {
        return "addSchool";
    }

    /*학교 정보 추가 페이지
    * POST
    */
    @PostMapping("/add")
    public String addSchool(SchoolDTO.SchoolAddDto schoolDto) {
        log.info("Controller addSchool start : " + schoolDto.toString());
        schoolService.addSchool(schoolDto);

        return "addSchool";
    }

    /**/
    @GetMapping("/search")
    public String showSchoolManageHomePage(Model model) { 
        model.addAttribute("schoolList", schoolService.getTop10Schools());
        model.addAttribute("count", schoolService.getAllSchoolsCnt());
        return "views/schoolManage/manageHome";
    }

    @GetMapping("/info/{id}")    // 해당 경로에 요청이 오면 이 메서드 실행
    public String showSchoolManageInfoPage(@PathVariable Long id, Model model) { // Model 객체로 파라미터 받음  모델로 받아야 뷰에 뿌릴 수 있음
        model.addAttribute("school", schoolService.getSchoolById(id)); // 서비스의 해당 메서드의 반환값을 모델에 추가함
        model.addAttribute("schoolDetail", schoolService.getSchoolDetailById(id)); // 서비스의 해당 메서드의 반환값을 모델에 추가함

        return "views/schoolManage/infoSchool"; // 뷰 템플릿 반환
    }

    @GetMapping("/init-official-data")
    public String initOfficialData() {
        //유저 객체 받아서 이름 받아넣기
        schoolService.upsertSchoolData("user1");
        return "redirect:/high/school/search";
    }

    @GetMapping("/search/{keyword}")
    public ResponseEntity<List<School>> searchSchoolInfo(@PathVariable String keyword) {
        List<School> schools = schoolService.findSchoolsByKeyword(keyword);
        if (!schools.isEmpty()) {
            return new ResponseEntity<>(schools, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

//    @GetMapping("/search/{keyword}")
//    public ResponseEntity<List<School>> searchRelated(@PathVariable String keyword, @PathVariable String address) {
//        List<School> schools = schoolService.findRelated(keyword);
//        if (!schools.isEmpty()) {
//            return new ResponseEntity<>(schools, HttpStatus.OK);
//        } else {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//    }
}
