package com.chunjae.tqgpt.school.repository;

import com.chunjae.tqgpt.school.entity.SchoolDetail;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@Transactional
public interface SchoolDetailRepository extends JpaRepository<SchoolDetail, Long> {

}