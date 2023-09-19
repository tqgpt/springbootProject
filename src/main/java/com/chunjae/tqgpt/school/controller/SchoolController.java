package com.chunjae.tqgpt.school.controller;

import com.chunjae.tqgpt.school.dto.SchoolDTO;
import com.chunjae.tqgpt.school.entity.School;
import com.chunjae.tqgpt.school.entity.SchoolDetail;
import com.chunjae.tqgpt.school.service.SchoolAPIService;
import com.chunjae.tqgpt.school.service.SchoolService;
import com.chunjae.tqgpt.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.util.*;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/high/school")
public class SchoolController {
    private final SchoolService schoolService;
    private final SchoolAPIService schoolAPIService;

    /*학교정보 추가 페이지
     * GET
     * */
    @GetMapping("/add")
    public String addSchool() {
        return "views/schoolManage/addSchool";
    }

    /*학교 정보 추가 페이지
     * POST
     */
    @PostMapping("/add")
    public String addSchool(SchoolDTO.SchoolAddDto schoolDto, @AuthenticationPrincipal User user) {
        if(user == null)
            return "redirect:/login";
        SchoolDetail schoolDetail = schoolService.addSchool(schoolDto, user.getName());

        return "redirect:/high/school/search";
    }

    @GetMapping("/modify/{school-idx}")
    public String modifySchool(@PathVariable("school-idx") Long schoolIdx, Model model) {
        if (schoolIdx == null) {
            return "redirect:/";
        }

        Optional<SchoolDetail> getSchool = schoolService.getSchoolOne(schoolIdx);
        if (getSchool.isEmpty()) {
            return "redirect:/";
        }

        model.addAttribute("info", getSchool.get());

        return "views/schoolManage/modifySchool";
    }

    @PostMapping("/api/modify")
    public @ResponseBody ResponseEntity<SchoolDetail> modifyOk(@RequestBody SchoolDTO.SchoolModifyDto schoolModifyDto, @AuthenticationPrincipal User user) {
        if(user == null)
            return new ResponseEntity<>(HttpStatus.BAD_GATEWAY);
        return schoolService.modifySchool(schoolModifyDto, user.getName());
    }

    @ResponseBody
    @PostMapping("/remove/{school-idx}")
    public ResponseEntity<String> removeSchool(@PathVariable("school-idx") Long idx) {
        return schoolService.removeSchool(idx);
    }

    @GetMapping("/search")
    public String search() {
        return "views/schoolManage/manageHome";
    }

    /*검색 시 학교 list 출력
     * POST
     * */
    @ResponseBody
    @PostMapping("/search-list")
    public ResponseEntity<Map<String, Object>> search(@RequestBody SchoolDTO.searchRequestDto requestDto) {
        Page<School> contents = schoolService.search(requestDto);
        if (!contents.isEmpty()) {
            String count = String.valueOf(contents.getTotalElements());

            Map<String, Object> response = new HashMap<>();
            response.put("contents", contents);
            response.put("count", count);

            return new ResponseEntity<>(response, HttpStatus.OK);
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

    @GetMapping("/search/{keyword}")
    public ResponseEntity<List<School>> searchSchoolInfo(@PathVariable String keyword) {
        List<School> schools = schoolService.findSchoolsByKeyword(keyword);
        if (!schools.isEmpty()) {
            return new ResponseEntity<>(schools, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/search/address")
    public ResponseEntity<List<School>> searchSchoolInfoByAddresses(@RequestBody List<String> addresses) {
        List<School> schools = new ArrayList<>();
        for (String address : addresses) {
            List<School> school = schoolService.findSchoolsByAddress(address);
            if(!school.isEmpty()) {
                schools.addAll(school);
            }
        }

        System.out.println(schools);
        if (!schools.isEmpty()) {
            return new ResponseEntity<>(schools, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    /*@SneakyThrows
    @ResponseBody
    @GetMapping("/xlsx-download")
    public void ExcelDownloader(HttpServletResponse res, @RequestParam String cityName, @RequestParam String streetAddr,
                                @RequestParam String searchOption, @RequestParam String searchValue) {
        SchoolDTO.searchRequestDto requestDto = new SchoolDTO.searchRequestDto(cityName, streetAddr, searchOption, searchValue, "1");
        schoolService.xlsxDownloadService(res, requestDto);
        log.info("성공");
    }*/
    @SneakyThrows
    @ResponseBody
    @GetMapping("/xlsx-download")
    public ResponseEntity<byte[]> ExcelDownloader(@RequestParam String cityName, @RequestParam String streetAddr,
                                @RequestParam String searchOption, @RequestParam String searchValue) {

        SchoolDTO.searchRequestDto requestDto = new SchoolDTO.searchRequestDto(cityName, streetAddr, searchOption, searchValue, "1");

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        schoolService.xlsxDownloadService(bos, requestDto);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=school_information.xlsx");
        log.info("성공");

        return ResponseEntity.ok()
                .headers(headers)
                .body(bos.toByteArray());
    }

    @ResponseBody
    @GetMapping("/search-relate")
    public ResponseEntity<List<String>> searchRelateSchool(@RequestParam("keyword")String keyword){
        log.info("keyword: {}", keyword);
        List<School> schoolList = schoolService.findTop5SchoolsByKeyword(keyword);
        if (!schoolList.isEmpty()) {
            return new ResponseEntity<>(schoolList.stream().map(School::getSchoolName).toList(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @ResponseBody
    @GetMapping("/search-one")
    public ResponseEntity<School> searchGetOneSchool(@RequestParam("keyword")String schoolName){
        School school = schoolService.searchSchoolOne(schoolName);
        if (school != null) {
            return new ResponseEntity<>(school, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @ResponseBody
    @GetMapping("/search-middle")
    public  ResponseEntity<List<SchoolDTO.SchoolInfoDTO>> searchGetMiddleSchool2(@RequestParam String keyword){
        log.info("keyword: {}", keyword);
        List<SchoolDTO.SchoolInfoDTO> middleList = schoolAPIService.getMiddleList(keyword);
        if (middleList != null){
            return new ResponseEntity<>(middleList, HttpStatus.OK);
        }else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

    }
}
