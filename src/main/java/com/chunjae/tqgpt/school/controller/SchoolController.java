package com.chunjae.tqgpt.school.controller;

import com.chunjae.tqgpt.school.dto.SchoolDTO;
import com.chunjae.tqgpt.school.entity.School;
import com.chunjae.tqgpt.school.service.SchoolService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

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

    /*페이지 로딩 시 학교 list 출력
     * GET
     * */


    @GetMapping("/search")
    public String search(Model model, @PageableDefault(sort = "idx") Pageable pageable) {
        Page<School> allList = schoolService.getAllList(pageable);
        model.addAttribute("searchList", allList);
        return "views/schoolManage/manageHome";
    }

    /*검색 시 학교 list 출력
     * POST
     * */
    @ResponseBody
    @GetMapping("/search-list")
    public ResponseEntity<List<School>> search(@RequestBody SchoolDTO.searchRequestDto requestDto,
                                               @PageableDefault(sort = "idx") Pageable pageable) {
        log.info("실행됨");
        List<School> searchList = schoolService.search(requestDto, pageable);
        if (!searchList.isEmpty()) {
            return new ResponseEntity<>(searchList, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
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
}
