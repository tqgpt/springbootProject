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

    //시도명(소재지명)
    @Column(name = "lctn_sc_nm")
    private String lctnScNm;

    //시군구명(도로명주소)
    @Column(name = "org_rdnma")
    private String orgRdnda;

    //학교급(학교종류명)
    @Column(name = "schul_knd_sc_nm")
    private String schulKndScNm;

    //학교명
    @Column(name = "schul_nm")
    private String schulNm;

    //시도교육청명
    @Column(name = "atpt_ofcdc_sc_nm")
    private String atptOfcdcScNm;

    //지역교육청명
    @Column(name = "ju_org_nm")
    private String juOrgNm;

    //회원
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_idx")
    private User user;

    public School(String lctnScNm, String orgRdnda, String schulKndScNm, String atptOfcdcScNm, String juOrgNm, User user) {
        this.lctnScNm = lctnScNm;
        this.orgRdnda = orgRdnda;
        this.schulKndScNm = schulKndScNm;
        this.atptOfcdcScNm = atptOfcdcScNm;
        this.juOrgNm = juOrgNm;
        this.user = user;
    }
}
