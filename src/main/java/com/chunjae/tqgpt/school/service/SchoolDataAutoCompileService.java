package com.chunjae.tqgpt.school.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
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
@Slf4j
public class SchoolDataAutoCompileService {
    @Value("${nice-admin-key}")
    private String apiKey;

    public void upsertSchoolData() {
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

                    for (int j = 0; j < rowArray.size(); j++) {
                        JsonObject row = rowArray.get(j).getAsJsonObject();

                        String schoolName = row.get("SCHUL_NM").getAsString();

                        //여기서부터 db에 하나씩 저장
                        log.info(schoolName);
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
