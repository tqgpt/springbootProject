package com.chunjae.tqgpt.user.entity;

import com.chunjae.tqgpt.util.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "t_user")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_idx")
    private Long idx;

    @Column(name = "user_id", unique = true)
    private String userId;

    @Column(name = "user_password")
    private String password;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "flag")
    private String flag;


}