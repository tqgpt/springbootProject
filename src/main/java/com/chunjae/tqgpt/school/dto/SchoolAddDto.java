package com.chunjae.tqgpt.school.dto;

import com.chunjae.tqgpt.school.entity.School;
import com.chunjae.tqgpt.school.entity.SchoolDetail;
import lombok.Data;

@Data
public class SchoolAddDto {

    private String lctnScNm;    //시도명
    private String orgRdnda;    //시군구명
    private String schulKndScNm;//학교급   초등학교,중학교,고등학교
    private String schulNm;     //학교명
    private String atptOfcdcScNm;//시도교육청명
    private String juOrgNm;     //지역교유청명
    private String sdSchulCode; //표준학교코드
    private String fondScNm;    //설립명   공립,사립
    private String dghtScNm;    //주야구분
    private String orgRdnma;    //도로명주소
    private String orgRdnzc;    //우편번호
    private String orgTelNo;    //전화번호
    private String hmpgAdres;   //홈페이지주소
    private String orgFaxNo;    //팩스번호
    private String coeduScNm;   //남여공학구분
    private double latitude;    //위도
    private double longitude;   //경도

    /*public SchoolDetail toEntity() {
        School school = new School(lctnScNm,orgRdnda,schulKndScNm,schulNm,atptOfcdcScNm,juOrgNm,null);
        return new SchoolDetail(school,sdSchulCode,fondScNm,dghtScNm,orgRdnma,orgRdnzc,orgTelNo,hmpgAdres,orgFaxNo,coeduScNm);
    }*/
}