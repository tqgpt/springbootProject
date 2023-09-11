package com.chunjae.tqgpt.user.entity;


import com.chunjae.tqgpt.util.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "t_user_access")
public class UserAccess {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "access_at")
    private LocalDateTime accessAt;

    @Column(name = "user_ip")
    private String ip;
}
