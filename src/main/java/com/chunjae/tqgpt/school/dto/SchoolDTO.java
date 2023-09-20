package com.chunjae.tqgpt.school.dto;

import com.chunjae.tqgpt.school.entity.School;
import com.chunjae.tqgpt.school.entity.SchoolDetail;
import com.chunjae.tqgpt.user.entity.User;
import lombok.*;

public class SchoolDTO {

    @Getter
    @Setter
    @Builder
    public static class SchoolInfoDTO {
        //학교
        private Long idx;
        //시도명
        private String cityName;
        //도로명주소
        private String streetAddr;
        //학교급
        private String schoolKind;
        //학교명
        private String schoolName;
        //시도교육청명
        private String cityEduOrg;
        //지역교육청명
        private String localEduOrg;
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
        private String schoolCode;
        //설립명
        private String foundationName;
        //주야구분
        private String dayNightName;
        //도로명 상세주소
        private String streetDetailAddr;
        //우편번호
        private String postNum;
        //전화번호
        private String telnum;
        //홈페이지주소
        private String hmpgAddr;
        //팩스번호
        private String faxNum;
        //남녀공학구분
        private String coedu;
    }

    @Getter
    @Setter
    @Builder
    public static class SchoolAddDto {
        private String cityName;    //시도명
        private String streetAddr;    //시군구명
        private String schoolKind;//학교급   초등학교,중학교,고등학교
        private String schoolName;     //학교명
        private String cityEduOrg;//시도교육청명
        private String localEduOrg;     //지역교육청명
        private String schoolCode; //표준학교코드
        private String foundationName;    //설립명   공립,사립
        private String dayNightName;    //주야구분
        private String streetDetailAddr;    //도로명 상세주소
        private String postNum;    //우편번호
        private String telNum;    //전화번호
        private String hmpgAddr;   //홈페이지주소
        private String faxNum;    //팩스번호
        private String coedu;   //남여공학구분

        public SchoolDetail toEntity(String userName) {
            School school = new School(cityName, streetAddr, schoolKind, schoolName, cityEduOrg, localEduOrg, userName);
            return new SchoolDetail(school, schoolCode, foundationName, dayNightName, streetDetailAddr, postNum, telNum, hmpgAddr, faxNum, coedu);
        }
    }
    @Getter
    @Setter
    public static class SchoolModifyDto {
        private Long schoolIdx;
        private String cityName;    //시도명
        private String streetAddr;    //시군구명
        private String schoolKind;//학교급   초등학교,중학교,고등학교
        private String schoolName;     //학교명
        private String cityEduOrg;//시도교육청명
        private String localEduOrg;     //지역교유청명
        private String schoolCode; //표준학교코드
        private String foundationName;    //설립명   공립,사립
        private String dayNightName;    //주야구분
        private String streetDetailAddr;    //도로명주소
        private String postNum;    //우편번호
        private String telNum;    //전화번호
        private String hmpgAddr;   //홈페이지주소
        private String faxNum;    //팩스번호
        private String coedu;   //남여공학구분

        public SchoolDetail toEntity(String userName) {
            School school = new School(schoolIdx, cityName, streetAddr, schoolKind, schoolName, cityEduOrg, localEduOrg, userName);
            return new SchoolDetail(school, schoolCode, foundationName, dayNightName, streetDetailAddr, postNum, telNum, hmpgAddr, faxNum, coedu);
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class searchRequestDto {
        //시
        private String cityName;
        //구/군
        private String streetAddr;
        //검색 옵션
        private String searchOption;
        //검색어
        private String searchValue;

        private String page;
    }

}