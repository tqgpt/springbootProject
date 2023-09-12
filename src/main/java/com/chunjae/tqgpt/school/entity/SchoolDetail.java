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
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_idx")
    private School school;

    @Column(name = "sd_schul_code")
    private String sdSchulCode;

    @Column(name = "fond_sc_nm")
    private String fondScNm;

    @Column(name = "dght_sc_nm")
    private String dghtScNm;

    @Column(name = "org_rdnma")
    private String orgRdnma;

    @Column(name = "org_telno")
    private String orgTelNo;

    @Column(name = "hmpg_adres")
    private String hmpgAdres;

    @Column(name = "org_faxno")
    private String orgFaxNo;

    @Column(name = "coedu_sc_nm")
    private String coEduScNm;

    @Column(name = "latitude")
    private double latitude;

    @Column(name = "longitude")
    private double longitude;
}
