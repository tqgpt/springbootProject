package com.chunjae.tqgpt.util;

public class AddressSplitter {
    public static void main(String[] args) {
        // 주소 문자열
        String streetAddr1 = "강원도 춘천시 서부대성로 257"; // 도가 있는 경우
        String streetAddr2 = "대구광역시 중구 달구벌대로 2178"; // 도가 없는 경우

        // 주소 분리 함수 호출
        String result1 = extractCity(streetAddr1);
        String result2 = extractCity(streetAddr2);

        // 결과 출력
        System.out.println("도가 있는 경우: " + result1);
        System.out.println("도가 없는 경우: " + result2);
    }

    // 시(군 또는 구)까지 추출하는 함수
    private static String extractCity(String streetAddr) {
        // "도"가 포함되어 있는 경우
        if (streetAddr.contains("도")) {
            // "도" 다음부터 "시"까지의 문자열 추출
            int startIndex = streetAddr.indexOf("도") + 1;
            int endIndex = streetAddr.indexOf("시") + 1;
            if (startIndex < endIndex) {
                return streetAddr.substring(startIndex, endIndex).trim();
            }
        }
        // "도"가 없는 경우
        else {
            // "광역시", "특별시" 다음부터 "구" 또는 "군"까지의 문자열 추출
            if (streetAddr.contains("광역시")) {
                int startIndex = streetAddr.indexOf("광역시") + 3;
                int endIndex = streetAddr.indexOf("구") + 1;
                if (startIndex < endIndex) {
                    return streetAddr.substring(startIndex, endIndex).trim();
                }
            } else if (streetAddr.contains("특별시")) {
                int startIndex = streetAddr.indexOf("특별시") + 3;
                int endIndex = streetAddr.indexOf("구") + 1;
                if (startIndex < endIndex) {
                    return streetAddr.substring(startIndex, endIndex).trim();
                }
            } else {
                int endIndex = streetAddr.indexOf("구") + 1;
                if (endIndex > 0) {
                    return streetAddr.substring(0, endIndex).trim();
                }
            }
        }
        return "";
    }
}