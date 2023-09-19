package com.chunjae.tqgpt.api;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
public class MapNaver {

//    @Value("${client-id}")
//    private static String clientId;
//
//    @Value("${client-secret}")
//    private static String clientSecret;

    @SneakyThrows
    public static JSONObject Latlong(String address) {
        String addr = URLEncoder.encode(address, StandardCharsets.UTF_8);

        // Geocoding 개요에 나와있는 API URL 입력.
        String apiURL = "https://naveropenapi.apigw.ntruss.com/map-geocode/v2/geocode?query=" + addr;    // JSON

        // API 키 설정
        String clientId = "mo4ez0j3y6";
        String clientSecret = "eL4E7y91BcNq0Aq5WsCb9AkN28PByh5IpaJtA1Gz";
        String Accept = "application/json";  //  필수는 아니지만 JSON으로 받을거라...

        // HTTP 연결 설정
        URL url = new URL(apiURL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("X-NCP-APIGW-API-KEY-ID", clientId);
        connection.setRequestProperty("X-NCP-APIGW-API-KEY", clientSecret);
        connection.setRequestProperty("Accept", Accept);  //  필수는 아니지만 JSON으로 받을거라...

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

        System.out.println(object);
        JSONArray arr = object.getJSONArray("addresses");

        JSONObject temp = null;
        for (int i = 0; i < arr.length(); i++) {
            temp = (JSONObject) arr.get(i);
            System.out.println("address : " + temp.get("roadAddress"));
            System.out.println("jibunAddress : " + temp.get("jibunAddress"));
            System.out.println("위도 : " + temp.get("y"));
            System.out.println("경도 : " + temp.get("x"));
        }
        return temp;
    }
}