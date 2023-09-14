package com.chunjae.tqgpt.user.repository;

import com.chunjae.tqgpt.user.entity.UserAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAccessRepository extends JpaRepository<UserAccess, Long> {
}
