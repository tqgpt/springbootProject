package com.chunjae.tqgpt.school.dto;

import com.chunjae.tqgpt.school.entity.School;
import com.chunjae.tqgpt.user.entity.User;
import lombok.*;

public class SchoolInfoDTO {

    @Getter
    @Setter
    @Builder
    public static class SchoolDTO {
        //학교
        private Long idx;
        //시도명
        private String lctnScNm;
        //시군구명
        private String orgRdnda;
        //학교급
        private String schulKndScNm;
        //시도교육청명
        private String atptOfcdcScNm;
        //지역교육청명
        private String juOrgNm;
        //회원
        private User user;
    }


    @Getter
    @Setter
    @Builder
    public static class SchoolDetailDTO {
        //학교
        private School school;
        //표준학교코드
        private String sdSchulCode;
        //설립명
        private String fondScNm;
        //주야구분
        private String dghtScNm;
        //도로명주소
        private String orgRdnma;
        //전화번호
        private String orgTelNo;
        //홈페이지주소
        private String hmpgAdres;
        //팩스번호
        private String orgFaxNo;
        //남녀공학구분
        private String coEduScNm;
        //지도 위도
        private double latitude;
        //지도 경도
        private double longitude;
    }
}