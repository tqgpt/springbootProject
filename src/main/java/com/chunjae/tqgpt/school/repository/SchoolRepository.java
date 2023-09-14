package com.chunjae.tqgpt.school.repository;

import com.chunjae.tqgpt.school.entity.School;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SchoolRepository extends JpaRepository<School, Long> {
    //서울 : 전체 : 전체 : 검색어
    @Query("SELECT s FROM t_school s WHERE s.streetAddr LIKE %:addr% AND (s.userName = :searchValue OR s.schoolName LIKE %:searchValue% OR s.streetAddr LIKE %:searchValue%)")
    List<School> findSchoolsByAddr(@Param("addr") String addr, @Param("searchValue") String searchValue, Pageable pageable);
    
    //서울 : 전체 : 학교명 : 검색어
    List<School> findAllByStreetAddrContainingAndSchoolNameContaining(String addr, String schoolName, Pageable pageable);

    //서울 : 전체 : 학교주소 : 검색어
    @Query("SELECT s FROM t_school s WHERE s.streetAddr LIKE %:addr% AND (s.streetAddr LIKE %:searchValue%)")
    List<School> findSchoolsByAddrDetail(@Param("addr") String addr, @Param("searchValue") String searchValue, Pageable pageable);

    //서울 : 전체 : 등록자 : 검색어
    List<School> findAllByStreetAddrContainingAndUserName(String addr, String userName, Pageable pageable);

    List<School> findAllBySchoolNameContainingOrStreetAddrContainingOrUserName(String schoolName, String addr, String userName, Pageable pageable);

    List<School> findAllBySchoolNameContaining(String schoolName, Pageable pageable);
    List<School> findAllByStreetAddrContaining(String schoolName, Pageable pageable);
    List<School> findAllByUserName(String schoolName, Pageable pageable);

    // schoolName이 주어진 키워드와 일치하는 학교 검색
    @Query("SELECT s FROM t_school s WHERE s.schoolName LIKE %:keyword%")
    List<School> findSchoolsByKeyword(@Param("keyword") String keyword);
}