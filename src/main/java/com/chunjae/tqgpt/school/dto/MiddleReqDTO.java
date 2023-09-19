package com.chunjae.tqgpt.school.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MiddleReqDTO {
    private String scName; //학교명
    private String ofoesCode; //지역교육지원청명
    private String  rnAddress; //학교도로명 상세주소내역
}
