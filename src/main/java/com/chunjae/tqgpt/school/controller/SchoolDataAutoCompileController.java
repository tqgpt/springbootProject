package com.chunjae.tqgpt.school.controller;

import com.chunjae.tqgpt.school.service.SchoolDataAutoCompileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SchoolDataAutoCompileController {

    private final SchoolDataAutoCompileService schoolDataAutoCompileService;

    @PostMapping("/schoolDataCompile")
    public void setSchoolData() {
        schoolDataAutoCompileService.upsertSchoolData();
    }
}
