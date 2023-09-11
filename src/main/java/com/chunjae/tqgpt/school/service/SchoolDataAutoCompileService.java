package com.chunjae.tqgpt.school.service;

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
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

@Service
@RequiredArgsConstructor
@Slf4j
public class SchoolDataAutoCompileService {

    private final UserRepository userRepository;
    private final SchoolRepository schoolRepository;
    private final SchoolDetailRepository schoolDetailRepository;

    @Value("${nice-admin-key}")
    private String apiKey;

    public void upsertSchoolData() {
        schoolRepository.deleteAll();
        schoolDetailRepository.deleteAll();

        //여기서부터 nice api에서 데이터 받아오는 코드
        int pIndex = 1;
        while (true) {
            String urlParameters = "&Type=json&SCHUL_KND_SC_NM=%EA%B3%A0%EB%93%B1%ED%95%99%EA%B5%90&pSize=1000&pIndex=" + pIndex;

            StringBuilder apiUrlBuilder = new StringBuilder();
            apiUrlBuilder.append("https://open.neis.go.kr/hub/schoolInfo?KEY=");
            apiUrlBuilder.append(apiKey);
            apiUrlBuilder.append(urlParameters);

            String apiUrl = apiUrlBuilder.toString();
            try {
                URL url = new URL(apiUrl);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("Content-Length", Integer.toString(urlParameters.length()));
                conn.setDoOutput(true);

                DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                wr.writeBytes(urlParameters);
                wr.flush();
                wr.close();

                BufferedReader br;

                if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                    br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                } else {
                    br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                }
                StringBuilder jsonResultBuilder = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    jsonResultBuilder.append(line);
                }

                //여기서 부터 json -> java 데이터 변환
                String jsonResult = jsonResultBuilder.toString();
                Gson gson = new Gson();
                JsonObject jsonObject = gson.fromJson(jsonResult, JsonObject.class);

                JsonArray schoolInfoArray = jsonObject.getAsJsonArray("schoolInfo");

                if (schoolInfoArray != null) {
                    JsonObject schoolInfo = schoolInfoArray.get(1).getAsJsonObject();
                    JsonArray rowArray = schoolInfo.getAsJsonArray("row");

                    //여기에 유저 생성
                    User user = userRepository.findByUserId("admin1").orElseThrow(
                            () -> new RuntimeException()
                    );

                    //여기서부터 db에 하나씩 저장
                    for (int j = 0; j < rowArray.size(); j++) {
                        JsonObject row = rowArray.get(j).getAsJsonObject();

                        //시도명(소재지명)
                        String lctnScNm = row.get("LCTN_SC_NM").getAsString();
                        //시군구명(도로명주소)
                        String orgRdnda = row.get("ORG_RDNMA").getAsString();
                        //학교급(학교종류명)
                        String schulKndScNm = row.get("SCHUL_KND_SC_NM").getAsString();
                        //학교명
                        String schoolName = row.get("SCHUL_NM").getAsString();
                        //시도교육청명
                        String atptOfcdcScNm = row.get("ATPT_OFCDC_SC_NM").getAsString();


                        //표준학교코드
                        String sdSchulCode = row.get("SD_SCHUL_CODE").getAsString();
                        //설립명
                        String fondScNm = row.get("FOND_SC_NM").getAsString();
                        //주야구분
                        String dghtScNm = row.get("DGHT_SC_NM").getAsString();
                        //도로명주소
                        String orgRdnma = row.get("ORG_RDNMA").getAsString();
                        //우편번호
                        String orgRdnzc = row.get("ORG_RDNZC").getAsString();
                        //전화번호
                        String orgTelNo = row.get("ORG_TELNO").getAsString();
                        //홈페이지 주소
                        String hmpgAdres = "";
                        JsonElement hmpgAdresElement = row.get("HMPG_ADRES");
                        if (!hmpgAdresElement.isJsonNull()) {
                            hmpgAdres = hmpgAdresElement.getAsString();
                        }
                        //팩스 번호
                        String orgFaxNo = "";
                        JsonElement orgFaxNoElement = row.get("ORG_FAXNO");
                        if (!orgFaxNoElement.isJsonNull()) {
                            orgFaxNo = orgFaxNoElement.getAsString();
                        }

                        //남녀공학 구분
                        String coEduScNm = row.get("COEDU_SC_NM").getAsString();

                        School school = new School(lctnScNm, orgRdnda, schulKndScNm, schoolName, atptOfcdcScNm, user);
                        SchoolDetail schoolDetail = new SchoolDetail(school, sdSchulCode, fondScNm, dghtScNm, orgRdnma, orgRdnzc, orgTelNo, hmpgAdres, orgFaxNo, coEduScNm);
                        schoolRepository.save(school);
                        schoolDetailRepository.save(schoolDetail);
                    }
                } else {
                    //데이터가 없다면 무한 루프 탈출
                    br.close();
                    conn.disconnect();
                    return;
                }

                br.close();
                conn.disconnect();
            } catch (MalformedURLException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
            pIndex++;
        }
    }
}
