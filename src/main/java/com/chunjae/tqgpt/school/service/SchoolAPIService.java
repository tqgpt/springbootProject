package com.chunjae.tqgpt.school.service;


import com.chunjae.tqgpt.school.dto.MiddleReqDTO;
import com.chunjae.tqgpt.school.dto.SchoolDTO;
import com.chunjae.tqgpt.util.WebClientUtil;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class SchoolAPIService {

    public List<SchoolDTO.SchoolInfoDTO> getMiddleList(String keyword) {
        List<SchoolDTO.SchoolInfoDTO> middleSchoolInfo = new ArrayList<>();

        String url = "https://midd.genia-academy.net/middle/search/" + keyword;
        Mono<List<MiddleReqDTO>> MiddleReqDTO = WebClientUtil
                .getBaseUrl(url)
                .get()
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(MiddleReqDTO.class)
                .collectList()
                .onErrorReturn(Collections.emptyList());
        List<MiddleReqDTO> schoolInfoList = MiddleReqDTO.block();


        schoolInfoList.forEach(res -> {
            middleSchoolInfo.add(SchoolDTO.SchoolInfoDTO
                    .builder()
                    .schoolName(res.getScName())
                    .schoolKind("중학교")
                    .streetAddr(res.getRnAddress())
                    .localEduOrg(res.getOfoesCode())
                    .build());
        });
        return middleSchoolInfo;
    }
}
