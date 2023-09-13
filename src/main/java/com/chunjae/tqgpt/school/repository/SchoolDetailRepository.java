package com.chunjae.tqgpt.school.repository;

import com.chunjae.tqgpt.school.entity.SchoolDetail;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Transactional
public interface SchoolDetailRepository extends JpaRepository<SchoolDetail, Long> {
    @Query(value = "DELETE t_school_detail FROM t_school_detail" +
            "JOIN school ON t_school_detail.school_idx = school.id" +
            "WHERE school.user_name = :username", nativeQuery = true)
    @Modifying
    void deleteAllByUserName(@Param("username") String username);

}
