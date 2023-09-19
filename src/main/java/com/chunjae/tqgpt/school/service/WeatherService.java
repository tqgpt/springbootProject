package com.chunjae.tqgpt.school.service;

import com.chunjae.tqgpt.school.dto.SchoolDTO;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class WeatherService {

    @Value("${weather-api-clientId}")
    String clientId;

    @Value("${weather-api-clientPw}")
    String clientSecret;

    //도로명 -> 위도/경도
    private JSONObject latLong(String address) {
        String addr = URLEncoder.encode(address, StandardCharsets.UTF_8);
        String apiURL = "https://naveropenapi.apigw.ntruss.com/map-geocode/v2/geocode?query=" + addr;    // JSON

        // HTTP 연결 설정
        try {
            URL url = null;
            url = new URL(apiURL);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("X-NCP-APIGW-API-KEY-ID", clientId);
            connection.setRequestProperty("X-NCP-APIGW-API-KEY", clientSecret);
            connection.setRequestProperty("Accept", "application/json");

            // 요청 결과 확인. 정상 호출인 경우 200
            int responseCode = connection.getResponseCode();
            BufferedReader br;
            if (responseCode == 200) {
                br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            } else {
                br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }

            String inputLine;

            StringBuilder response = new StringBuilder();

            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();

            JSONTokener tokener = new JSONTokener(response.toString());
            JSONObject object = new JSONObject(tokener);

            JSONArray arr = object.getJSONArray("addresses");

            JSONObject temp = null;
            for (int i = 0; i < arr.length(); i++) {
                temp = (JSONObject) arr.get(i);
            }
            return temp;

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (ProtocolException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //위도/경도 -> 기상청 좌표계
    private Map<String, Double> convertToCoordinates(double latitude, double longitude) {
        Map<String, Double> rs = new HashMap<>();

        double lati = latitude;
        double longi = longitude;
        double grid = 5.0;
        double xo = 43;
        double yo = 136;

        double degrad = Math.PI / 180.0;
        double re = 6371.00877 / grid;

        double slat1 = 30.0 * degrad;
        double slat2 = 60.0 * degrad;
        double oLong = 126.0 * degrad;
        double oLat = 38.0 * degrad;


        double sn = Math.tan(Math.PI * 0.25 + slat2 * 0.5) / Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sn = Math.log(Math.cos(slat1) / Math.cos(slat2)) / Math.log(sn);
        double sf = Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sf = Math.pow(sf, sn) * Math.cos(slat1) / sn;
        double ro = Math.tan(Math.PI * 0.25 + oLat * 0.5);
        ro = re * sf / Math.pow(ro, sn);

        double part1 = Math.PI * 0.25;
        double part2 = lati * degrad * 0.5;
        double angle = part1 + part2;
        angle = Math.round(angle * Math.pow(10, 10)) / Math.pow(10, 10);
        double ra = Math.tan(angle);
        ra = re * sf / Math.pow(ra, sn);

        double theta = longi * degrad - oLong;
        if (theta > Math.PI) theta -= 2.0 * Math.PI;
        if (theta < -Math.PI) theta += 2.0 * Math.PI;
        theta *= sn;

        double xCoordinate = ra * Math.sin(theta) + xo + 0.5;
        double yCoordinate = ro - ra * Math.cos(theta) + yo + 0.5;

        if (!Double.isNaN(xCoordinate) && !Double.isNaN(yCoordinate)) {
            rs.put("x", xCoordinate);
            rs.put("y", yCoordinate);
        }
        return rs;
    }

    //기상청 좌표계 포함 데이터 요청
    public SchoolDTO.weatherResponseDto weather(String streetAddr) {

        LocalDate currentDay = LocalDate.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String formattedDate = currentDay.format(dateFormatter);


        /* 기상청 실황 API는 발표자료가 매 시 30분에 생성, 40분에 API가 제공되며 그것도 시간이 조금 지체되기에 1시간 전을 호출하여야 한다. */
        LocalTime currentTime = LocalTime.now();
        LocalTime oneHourAgo = currentTime.minusHours(1);
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HHmm");
        String formattedTime = oneHourAgo.format(timeFormatter);

        try {
            String apiUrl = "https://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtNcst";
            String apiKey = "bjzcjXp5BZXRA5vnoLptqnIRMijNPrHGZAwXuxDO1XkJ5j8V5zSpfRVB4OedKWbyiVdgfUGga8zoxwTnQnO00w%3D%3D";
            String pageNo = "1";
            String numOfRows = "1000";
            String dataType = "JSON";

            JSONObject latlong = latLong(streetAddr);

            Map<String, Double> rs = convertToCoordinates(latlong.getDouble("y"), latlong.getDouble("x"));

            double nx = rs.get("x"); // X 좌표
            double ny = rs.get("y"); // Y 좌표

            String requestUrl = apiUrl + "?serviceKey=" + apiKey + "&pageNo=" + pageNo +
                "&numOfRows=" + numOfRows + "&dataType=" + dataType + "&base_date=" +
                formattedDate + "&base_time=" + formattedTime + "&nx=" + (int) nx + "&ny=" + (int) ny;

            // HTTP GET 요청 보내기
            URL url = new URL(requestUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // 응답 읽기
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                StringBuffer response = new StringBuffer();
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                System.out.println(response);

                // 응답 데이터 출력
                JsonParser jsonParser = new JsonParser();
                JsonObject jsonObject = jsonParser.parse(String.valueOf(response)).getAsJsonObject();

                // JSON 경로를 따라 이동하여 obsrValue 값을 추출
                JsonObject itemsObject = jsonObject
                    .getAsJsonObject("response")
                    .getAsJsonObject("body")
                    .getAsJsonObject("items");

                JsonArray itemArray = itemsObject.getAsJsonArray("item");

                SchoolDTO.weatherResponseDto responseDto = new SchoolDTO.weatherResponseDto();

                for (int i = 0; i < itemArray.size(); i++) {
                    JsonObject item = itemArray.get(i).getAsJsonObject();
                    String obsrValue = item.get("obsrValue").getAsString();
                    String category = item.get("category").getAsString();
                    switch (category) {
                        case "PTY" -> responseDto.setPty(obsrValue);
                        case "REH" -> responseDto.setReh(obsrValue);
                        case "RN1" -> responseDto.setRn1(obsrValue);
                        case "T1H" -> responseDto.setT1h(obsrValue);
                        case "UUU" -> responseDto.setUuu(obsrValue);
                        case "VEC" -> responseDto.setVec(obsrValue);
                        case "VVV" -> responseDto.setVvv(obsrValue);
                        case "WSD" -> responseDto.setWsd(obsrValue);
                        // 필요한 다른 카테고리에 대한 처리를 추가할 수 있습니다.
                    }
                }
                return responseDto;
            } else {
                log.error("응답 데이터 에러:" + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
