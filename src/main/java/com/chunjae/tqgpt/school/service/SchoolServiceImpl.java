package com.chunjae.tqgpt.school.service;

import com.chunjae.tqgpt.school.dto.SchoolDTO;
import com.chunjae.tqgpt.school.entity.School;

import java.util.List;

public interface SchoolServiceImpl {
    List<School> getTop10Schools();
    int getAllSchoolsCnt();

    void addSchool(SchoolDTO.SchoolAddDto schoolAddDto);

    void upsertSchoolData(String userName);

}
