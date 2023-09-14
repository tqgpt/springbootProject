package com.chunjae.tqgpt.school.service;

import com.chunjae.tqgpt.school.dto.SchoolDTO;
import com.chunjae.tqgpt.school.entity.School;
import com.chunjae.tqgpt.school.entity.SchoolDetail;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface SchoolServiceImpl {
    List<School> getTop10Schools();

    int getAllSchoolsCnt();

    SchoolDetail addSchool(SchoolDTO.SchoolAddDto schoolAddDto);

    Optional<SchoolDetail> getSchoolOne(Long SchoolIdx);

    ResponseEntity<SchoolDetail> modifySchool(SchoolDTO.SchoolModifyDto schoolModifyDto);

    void upsertSchoolData(String userName);

    School getSchoolById(Long id);

    SchoolDetail getSchoolDetailById(Long id);
}