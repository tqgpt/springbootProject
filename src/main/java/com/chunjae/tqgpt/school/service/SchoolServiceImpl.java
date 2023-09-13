package com.chunjae.tqgpt.school.service;

import com.chunjae.tqgpt.school.dto.SchoolDTO;
import com.chunjae.tqgpt.school.entity.School;
import com.chunjae.tqgpt.school.entity.SchoolDetail;

import java.util.List;
import java.util.Map;

public interface SchoolServiceImpl {
    List<School> getTop10Schools();
    int getAllSchoolsCnt();

    void addSchool(SchoolDTO.SchoolAddDto schoolAddDto);

    SchoolDetail getSchoolOne(Long SchoolIdx);

    void modifySchool(Long schoolIdx, SchoolDTO.SchoolAddDto schoolAddDto);

    SchoolDetail modifySchoolOk(SchoolDTO.SchoolModifyDto schoolModifyDto);

    void upsertSchoolData(String userName);

}
