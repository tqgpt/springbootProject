package com.chunjae.tqgpt.user.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "t_user_access")
public class UserAccess {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_access_idx")
    private Long idx;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "access_at")
    private LocalDateTime accessAt;

    @Column(name = "user_ip")
    private String ip;
}
