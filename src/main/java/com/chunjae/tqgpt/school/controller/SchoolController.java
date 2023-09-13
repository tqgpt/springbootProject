package com.chunjae.tqgpt.school.controller;

import com.chunjae.tqgpt.school.dto.SchoolDTO;
import com.chunjae.tqgpt.school.entity.SchoolDetail;
import com.chunjae.tqgpt.school.service.SchoolService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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

    @GetMapping("/modify/{school-idx}")
    public String modifySchool(@PathVariable("school-idx") Long schoolIdx, Model model) {
        log.info("modifySchool Controller start : " + schoolIdx);
        if(schoolIdx == null) {
        }
        SchoolDetail schoolOne = schoolService.getSchoolOne(schoolIdx);
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

    @PostMapping("/api/modify")
    public @ResponseBody ResponseEntity<SchoolDetail> modifyOk(@RequestBody SchoolDTO.SchoolModifyDto schoolModifyDto) {
        log.info("modifyOk start dto : " + schoolModifyDto.toString());
        //schoolService.modifySchoolOk(schoolModifyDto);

        return ResponseEntity.ok().body(schoolService.modifySchoolOk(schoolModifyDto));
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

    @GetMapping("/map")
    public String showMapPage() {
        return "views/map/map";
    }
}
