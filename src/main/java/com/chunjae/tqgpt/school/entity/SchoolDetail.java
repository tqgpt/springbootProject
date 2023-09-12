package com.chunjae.tqgpt.school.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "t_school_detail")
public class SchoolDetail {

    @Id
    @Column(name = "school_detail_idx")
    private Long idx;

    //학교pk
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "school_idx")
    private School school;

    //표준학교코드
    @Column(name = "school_code")
    private String schoolCode;

    //설립명
    @Column(name = "foundation_name")
    private String foundationName;

    //주야구분
    @Column(name = "day_night_name")
    private String dayNightName;

    //도로명주소
    @Column(name = "street_addr")
    private String streetAddr;

    //우편번호
    @Column(name = "post_num")
    private String postNum;

    //전화번호
    @Column(name = "tel_num")
    private String telNum;

    //홈페이지 주소
    @Column(name = "hmpg_addr")
    private String hmpgAddr;

    //팩스 번호
    @Column(name = "fax_num")
    private String faxNum;

    //남녀공학 구분
    @Column(name = "coedu")
    private String coedu;

    public SchoolDetail(School school, String schoolCode, String foundationName, String dayNightName, String streetAddr, String postNum, String telNum, String hmpgAddr, String faxNum, String coedu) {
        this.school = school;
        this.schoolCode = schoolCode;
        this.foundationName = foundationName;
        this.dayNightName = dayNightName;
        this.streetAddr = streetAddr;
        this.postNum = postNum;
        this.telNum = telNum;
        this.hmpgAddr = hmpgAddr;
        this.faxNum = faxNum;
        this.coedu = coedu;
    }
}
