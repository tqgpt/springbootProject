package com.chunjae.tqgpt.api;

import com.chunjae.tqgpt.school.dto.SchoolDTO;
import com.google.code.geocoder.Geocoder;
import com.google.code.geocoder.GeocoderRequestBuilder;
import com.google.code.geocoder.model.*;
import java.io.IOException;

public class MapAPI {

    /**
     * 학교 주소로 위도 경도 찾기
     * @param schoolInfo
     * @return Float[latitude, longitude]
     */
    public static Float[] findGeoPoint(SchoolDTO.SchoolInfoDTO schoolInfo) {

        // 입력된 학교 정보가 null인 경우, 결과를 null로 반환
        if (schoolInfo == null || schoolInfo.getStreetAddr() == null)
            return null;

        // Google Geocoder API를 이용하여 주소를 변환하는 요청 생성
        GeocoderRequest geocoderRequest = new GeocoderRequestBuilder()
            .setAddress(schoolInfo.getStreetAddr())  // 변환하려는 주소 설정
            .setLanguage("ko")     // 인코딩 설정
            .getGeocoderRequest();

        try {
            // Geocoder 객체 생성
            Geocoder geocoder = new Geocoder();

            // 주소 변환 요청 수행
            GeocodeResponse geocoderResponse = geocoder.geocode(geocoderRequest);

            // 변환 결과가 OK이고 결과가 비어 있지 않은 경우
            if (geocoderResponse.getStatus() == GeocoderStatus.OK && !geocoderResponse.getResults().isEmpty()) {
                // 첫 번째 GeocoderResult 가져오기
                GeocoderResult geocoderResult = geocoderResponse.getResults().iterator().next();

                // 위도 경도 추출
                LatLng latlong = geocoderResult.getGeometry().getLocation();

                // 추출된 좌표를 Float 배열에 저장
                Float[] coords = new Float[2];
                coords[0] = latlong.getLat().floatValue(); // 위도
                coords[1] = latlong.getLng().floatValue(); // 경도

                // 좌표 배열 반환
                return coords;
            }
        } catch (IOException ex) {
            // 예외 처리: IOException이 발생한 경우 스택 트레이스 출력
            ex.printStackTrace();
        }

        // 변환 결과가 없거나 예외 발생 시 null 반환
        return null;
    }
}
