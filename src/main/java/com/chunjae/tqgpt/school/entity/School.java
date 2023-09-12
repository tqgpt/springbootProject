package com.chunjae.tqgpt.school.entity;


import com.chunjae.tqgpt.user.entity.User;
import com.chunjae.tqgpt.util.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "t_school")
public class School extends BaseEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "school_idx")
    private Long idx;

    @Column(name = "lctn_sc_nm")
    private String lctnScNm;

    @Column(name = "org_rdnma")
    private String orgRdnda;

    @Column(name = "schul_knd_sc_nm")
    private String schulKndScNm;

    @Column(name = "atpt_ofcdc_sc_nm")
    private String atptOfcdcScNm;

    @Column(name = "ju_org_nm")
    private String juOrgNm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_idx")
    private User user;

}
