package com.chunjae.tqgpt.school.service;

import com.chunjae.tqgpt.school.dto.SchoolDTO;
import com.chunjae.tqgpt.school.entity.School;
import com.chunjae.tqgpt.school.entity.SchoolDetail;
import com.chunjae.tqgpt.school.repository.SchoolDetailRepository;
import com.chunjae.tqgpt.school.repository.SchoolRepository;
import com.chunjae.tqgpt.user.entity.User;
import com.chunjae.tqgpt.user.repository.UserRepository;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class SchoolService implements SchoolServiceImpl {
    private final SchoolRepository schoolRepository;
    private final UserRepository userRepository;
    private final SchoolDetailRepository schoolDetailRepository;

    @Value("${nice-admin-key}")
    private String apiKey;

    @Override
    public List<School> getTop10Schools() {
        Pageable pageable = PageRequest.of(0, 10);
        return schoolRepository.findAll(pageable).getContent();
    }

    @Override
    public int getAllSchoolsCnt() {
        return (int) schoolRepository.count();
    }

    @Override
    public void upsertSchoolData() {
        schoolRepository.deleteAll();
        schoolDetailRepository.deleteAll();

        for (int pageIndex = 1; ; pageIndex++) {
            try {
                StringBuilder urlParametersBuilder = new StringBuilder()
                        .append("&Type=json")
                        .append("&SCHUL_KND_SC_NM=").append(URLEncoder.encode("고등학교", StandardCharsets.UTF_8))
                        .append("&pSize=1000")
                        .append("&pIndex=").append(pageIndex);

                StringBuilder apiUrlBuilder = new StringBuilder()
                        .append("https://open.neis.go.kr/hub/schoolInfo?KEY=")
                        .append(apiKey)
                        .append(urlParametersBuilder);

                HttpURLConnection conn = (HttpURLConnection) new URL(apiUrlBuilder.toString()).openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("Content-Length", String.valueOf(urlParametersBuilder.toString().length()));

                conn.setDoOutput(true);

                String jsonResult;
                try (BufferedReader br = (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) ?
                        new BufferedReader(new InputStreamReader(conn.getInputStream())) :
                        new BufferedReader(new InputStreamReader(conn.getErrorStream()))) {
                    jsonResult = br.lines().collect(Collectors.joining("\n"));
                }

                Gson gson = new Gson();
                JsonObject jsonObject = gson.fromJson(jsonResult, JsonObject.class);

                JsonArray schoolInfoArray = jsonObject.getAsJsonArray("schoolInfo");
                if (schoolInfoArray != null) {
                    JsonObject schoolInfo = schoolInfoArray.get(1).getAsJsonObject();
                    JsonArray rowArray = schoolInfo.getAsJsonArray("row");

                    User user = userRepository.findByUserId("admin1").orElseThrow(RuntimeException::new);

                    for (JsonElement rowElement : rowArray) {
                        JsonObject row = rowElement.getAsJsonObject();

                        String cityName = row.get("LCTN_SC_NM").getAsString();              //시도명(소재지명)
                        String streetDetailAddr = row.get("ORG_RDNMA").getAsString();               //시군구명(도로명주소)
                        String schoolKind = row.get("SCHUL_KND_SC_NM").getAsString();     //학교급(학교종류명)
                        String schoolName = row.get("SCHUL_NM").getAsString();              //학교명
                        String cityEduOrg = row.get("ATPT_OFCDC_SC_NM").getAsString();   //시도교육청명
                        String localEduOrg = row.get("JU_ORG_NM").getAsString();   //시도교육청명

                        String schoolCode = row.get("SD_SCHUL_CODE").getAsString();        //표준학교코드
                        String foundationName = row.get("FOND_SC_NM").getAsString();              //설립명
                        String dayNightName = row.get("DGHT_SC_NM").getAsString();              //주야구분
                        String streetAddr = row.get("ORG_RDNMA").getAsString();               //도로명주소
                        String postNum = row.get("ORG_RDNZC").getAsString();               //우편번호
                        String telNum = row.get("ORG_TELNO").getAsString();               //전화번호
                        String hmpgAddr = row.get("HMPG_ADRES").isJsonNull() ? "" : row.get("HMPG_ADRES").getAsString();   //홈페이지 주소
                        String faxNum = row.get("ORG_FAXNO").isJsonNull() ? "" : row.get("ORG_FAXNO").getAsString();      //팩스 번호
                        String coedu = row.get("COEDU_SC_NM").getAsString();            //남녀공학 구분

                        School school = new School(cityName, streetDetailAddr, schoolKind, schoolName, cityEduOrg, localEduOrg, user);
                        SchoolDetail schoolDetail = new SchoolDetail(school, schoolCode, foundationName, dayNightName, streetAddr, postNum, telNum, hmpgAddr, faxNum, coedu);

                        schoolRepository.save(school);
                        schoolDetailRepository.save(schoolDetail);
                    }
                } else {
                    log.info("학교기본정보 API FINISH");
                    return; // 데이터가 없다면 무한 루프 탈출
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void addSchool(SchoolDTO.SchoolAddDto schoolAddDto) {

    }
}
