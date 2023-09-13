package com.chunjae.tqgpt.school.repository;

import com.chunjae.tqgpt.school.entity.School;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SchoolRepository extends JpaRepository<School, Long> {
    void deleteAllByUserName(String userName);

    List<School> findBySchoolNameContaining(String schoolName);
}