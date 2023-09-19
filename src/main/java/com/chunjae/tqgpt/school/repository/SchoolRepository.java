package com.chunjae.tqgpt.school.repository;

import com.chunjae.tqgpt.school.entity.School;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SchoolRepository extends JpaRepository<School, Long> {
    //서울 : 전체 : 전체 : 검색어
    @Query("SELECT s FROM t_school s WHERE s.streetAddr LIKE %:addr% AND (s.userName = :searchValue OR s.schoolName LIKE %:searchValue% OR s.streetAddr LIKE %:searchValue%)")
    Page<School> findSchoolsByAddr(@Param("addr") String addr, @Param("searchValue") String searchValue, Pageable pageable);
    
    //서울 : 전체 : 학교명 : 검색어
    Page<School> findAllByStreetAddrContainingAndSchoolNameContaining(String addr, String schoolName, Pageable pageable);

    //서울 : 전체 : 학교주소 : 검색어
    @Query("SELECT s FROM t_school s WHERE s.streetAddr LIKE %:addr% AND (s.streetAddr LIKE %:searchValue%)")
    Page<School> findSchoolsByAddrDetail(@Param("addr") String addr, @Param("searchValue") String searchValue, Pageable pageable);

    //서울 : 전체 : 등록자 : 검색어
    Page<School> findAllByStreetAddrContainingAndUserName(String addr, String userName, Pageable pageable);

    Page<School> findAllBySchoolNameContainingOrStreetAddrContainingOrUserName(String schoolName, String addr, String userName, Pageable pageable);

    Page<School> findAllBySchoolNameContaining(String schoolName, Pageable pageable);
    Page<School> findAllByStreetAddrContaining(String schoolName, Pageable pageable);
    Page<School> findAllByUserName(String schoolName, Pageable pageable);

    // schoolName이 주어진 키워드와 일치하는 학교 검색
    @Query("SELECT s FROM t_school s WHERE s.schoolName LIKE %:keyword%")
    List<School> findSchoolsByKeyword(@Param("keyword") String keyword);

    List<School> findByUserName(String userName);
    List<School> findAllBySchoolNameContaining(@Param("keyword") String keyword);

    School findSchoolBySchoolName(@Param("keyword") String schoolName);


    @Query("SELECT s FROM t_school s WHERE s.streetAddr LIKE %:address%")
    List<School> findSchoolsByStreetAddress(@Param("address") String address);
}