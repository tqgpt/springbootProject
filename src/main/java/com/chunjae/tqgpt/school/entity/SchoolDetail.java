package com.chunjae.tqgpt.school.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "t_school_detail")
public class SchoolDetail {

    @Id
    @Column(name = "school_detail_idx")
    private Long schoolDetailIdx;

    //학교pk
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "school_idx")
    private School school;

    //표준학교코드
    @Column(name = "sd_schul_code")
    private String sdSchulCode;

    //설립명
    @Column(name = "fond_sc_nm")
    private String fondScNm;

    //주야구분
    @Column(name = "dght_sc_nm")
    private String dghtScNm;

    //도로명주소
    @Column(name = "org_rdnma")
    private String orgRdnma;

    //우편번호
    @Column(name = "org_rdnzc")
    private String orgRdnzc;

    //전화번호
    @Column(name = "org_telno")
    private String orgTelNo;

    //홈페이지 주소
    @Column(name = "hmpg_adres")
    private String hmpgAdres;

    //팩스 번호
    @Column(name = "org_faxno")
    private String orgFaxNo;

    //남녀공학 구분
    @Column(name = "coedu_sc_nm")
    private String coEduScNm;

    public SchoolDetail(School school, String sdSchulCode, String fondScNm, String dghtScNm, String orgRdnma, String orgRdnzc, String orgTelNo, String hmpgAdres, String orgFaxNo, String coEduScNm) {
        this.school = school;
        this.sdSchulCode = sdSchulCode;
        this.fondScNm = fondScNm;
        this.dghtScNm = dghtScNm;
        this.orgRdnma = orgRdnma;
        this.orgRdnzc = orgRdnzc;
        this.orgTelNo = orgTelNo;
        this.hmpgAdres = hmpgAdres;
        this.orgFaxNo = orgFaxNo;
        this.coEduScNm = coEduScNm;
    }
}
