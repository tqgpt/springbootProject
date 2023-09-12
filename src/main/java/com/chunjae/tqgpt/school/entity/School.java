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
    @Column(name = "city_name")
    private String cityName;

    //도로명상세주소
    @Column(name = "street_detail_addr")
    private String streetDetailAddr;

    //학교급(학교종류명)
    @Column(name = "school_kind")
    private String schoolKind;

    //학교명
    @Column(name = "school_name")
    private String schoolName;

    //시도교육청명
    @Column(name = "city_edu_org")
    private String cityEduOrg;

    //지역교육청명
    @Column(name = "local_edu_org")
    private String localEduOrg;

    //회원
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_idx")
    private User user;

    public School(String cityName, String streetDetailAddr, String schoolKind, String schoolName, String cityEduOrg, String localEduOrg, User user) {
        this.cityName = cityName;
        this.streetDetailAddr = streetDetailAddr;
        this.schoolKind = schoolKind;
        this.schoolName = schoolName;
        this.cityEduOrg = cityEduOrg;
        this.localEduOrg = localEduOrg;
        this.user = user;
    }
}
