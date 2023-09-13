package com.chunjae.tqgpt.school.service;

import com.chunjae.tqgpt.school.dto.SchoolDTO;
import com.chunjae.tqgpt.school.entity.School;
import com.chunjae.tqgpt.school.entity.SchoolDetail;
import com.chunjae.tqgpt.school.repository.SchoolDetailRepository;
import com.chunjae.tqgpt.school.repository.SchoolRepository;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
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
    public School getSchoolById(Long id) {
        return schoolRepository.getReferenceById(id);
    }

    @Override
    public SchoolDetail getSchoolDetailById(Long id) {
        return schoolDetailRepository.getReferenceById(id);
    }

    @Override
    @Transactional
    public void upsertSchoolData(String userName) {
        deleteExistingData(userName);

        int pageIndex = 1;
        while (true) {
            try {
                JsonObject jsonObject = fetchSchoolData(pageIndex);
                log.info(String.valueOf(pageIndex));
                if (jsonObject == null) {
                    log.info("학교기본정보 API FINISH");
                    return;
                }

                processSchoolData(jsonObject, userName);
                pageIndex++;

            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }

    private void deleteExistingData(String userName) {
        schoolDetailRepository.deleteAll();
//        schoolRepository.deleteAllByUserName(userName);
    }

    private JsonObject fetchSchoolData(int pageIndex) throws IOException {
        String apiUrl = buildApiUrl(pageIndex);
        String jsonResult = makeApiRequest(apiUrl);

        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(jsonResult, JsonObject.class);
        JsonArray schoolInfoArray = jsonObject.getAsJsonArray("schoolInfo");

        if (schoolInfoArray != null) {
            return schoolInfoArray.get(1).getAsJsonObject();
        }
        return null;
    }

    private String buildApiUrl(int pageIndex) throws UnsupportedEncodingException {
        StringBuilder urlParametersBuilder = new StringBuilder()
                .append("&Type=json")
                .append("&SCHUL_KND_SC_NM=").append(URLEncoder.encode("고등학교", StandardCharsets.UTF_8))
                .append("&pSize=1000")
                .append("&pIndex=").append(pageIndex);

        return new StringBuilder()
                .append("https://open.neis.go.kr/hub/schoolInfo?KEY=")
                .append(apiKey)
                .append(urlParametersBuilder)
                .toString();
    }

    private String makeApiRequest(String apiUrl) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        try (BufferedReader br = (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) ?
                new BufferedReader(new InputStreamReader(conn.getInputStream())) :
                new BufferedReader(new InputStreamReader(conn.getErrorStream()))) {
            return br.lines().collect(Collectors.joining("\n"));
        }
    }

    private void processSchoolData(JsonObject schoolInfo, String userName) {
        JsonArray rowArray = schoolInfo.getAsJsonArray("row");

        for (JsonElement rowElement : rowArray) {
            School school = createSchool(rowElement.getAsJsonObject(), userName);
            SchoolDetail schoolDetail = createSchoolDetail(rowElement.getAsJsonObject(), school);

            schoolRepository.save(school);
            schoolDetailRepository.save(schoolDetail);
        }
    }

    private School createSchool(JsonObject row, String userName) {
        String cityName = row.get("LCTN_SC_NM").getAsString();                 //시도명(소재지명)
        String streetAddr = row.get("ORG_RDNMA").getAsString();               //시군구명(도로명주소)
        String schoolKind = row.get("SCHUL_KND_SC_NM").getAsString();      //학교급(학교종류명)
        String schoolName = row.get("SCHUL_NM").getAsString();              //학교명
        String cityEduOrg = row.get("ATPT_OFCDC_SC_NM").getAsString();   //시도교육청명
        String localEduOrg = row.get("JU_ORG_NM").getAsString();   //시도교육청명

        return new School(cityName, streetAddr, schoolKind, schoolName, cityEduOrg, localEduOrg, userName);
    }

    private SchoolDetail createSchoolDetail(JsonObject row, School school) {
        String schoolCode = row.get("SD_SCHUL_CODE").getAsString();        //표준학교코드
        String foundationName = row.get("FOND_SC_NM").getAsString();              //설립명
        String dayNightName = row.get("DGHT_SC_NM").getAsString();              //주야구분
        String streetDetailAddr = row.get("ORG_RDNDA").getAsString();               //도로명주소
        String postNum = row.get("ORG_RDNZC").getAsString();               //우편번호
        String telNum = row.get("ORG_TELNO").getAsString();               //전화번호
        String hmpgAddr = row.get("HMPG_ADRES").isJsonNull() ? "" : row.get("HMPG_ADRES").getAsString();   //홈페이지 주소
        String faxNum = row.get("ORG_FAXNO").isJsonNull() ? "" : row.get("ORG_FAXNO").getAsString();      //팩스 번호
        String coedu = row.get("COEDU_SC_NM").getAsString();            //남녀공학 구분

        return new SchoolDetail(school, schoolCode, foundationName, dayNightName, streetDetailAddr, postNum, telNum, hmpgAddr, faxNum, coedu);
    }

    @Override
    public void addSchool(SchoolDTO.SchoolAddDto schoolAddDto) {

    }

    @Override
    public List<School> findSchoolsByKeyword(String keyword) {
        return schoolRepository.findSchoolsByKeyword(keyword);
    }
}
