package com.chunjae.tqgpt.school.service;

import com.chunjae.tqgpt.school.entity.School;

import java.util.List;

public interface SchoolServiceImpl {
    List<School> getTop10Schools();

    int getAllSchoolsCnt();
}
