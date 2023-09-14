package com.chunjae.tqgpt.school.repository;

import com.chunjae.tqgpt.school.entity.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.awt.print.Pageable;
import java.util.List;

public interface SchoolRepository extends JpaRepository<School, Long> {
    void deleteAllByUserName(String userName);

    List<School> findBySchoolNameContaining(String schoolName);

    // schoolName이 주어진 키워드와 일치하는 학교 검색
    @Query("SELECT s FROM t_school s WHERE s.schoolName LIKE %:keyword%")
    List<School> findSchoolsByKeyword(@Param("keyword") String keyword);

    // schoolName 또는 cityName이 주어진 키워드와 일치하는 학교 검색 (상위 10개만)
//    @Query("SELECT s FROM t_school s WHERE s.schoolName LIKE %:keyword% OR s.cityName LIKE %:keyword%")
//    List<School> findSchoolsByKeyword(@Param("keyword") String keyword, Pageable pageable);

}