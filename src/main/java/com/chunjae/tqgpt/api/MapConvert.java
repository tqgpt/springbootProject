package com.chunjae.tqgpt.api;


import java.util.HashMap;
import java.util.Map;


//pi가 들어가는 연산은 double로 타입을 설정할 것
public class MapConvert {
    // 지구 반경(km) - (Radius Earth)
    private static final double RE = 6371.00877;
    // 격자 간격(km)
    private static final double GRID = 5.0;
    // 투영 위도1(degree)
    private static final double SLAT1 = 30.0;
    // 투영 위도2(degree)
    private static final double SLAT2 = 60.0;
    // 경도(degree) - (LONGITUDE) - X축
    private static final double OLONG = 126.0;
    // 위도(degree) - (LATITUDE) - Y축
    private static final double OLAT = 38.0;
    // X좌표(GRID)
    private static final double XO = 43;
    // Y좌표(GRID)
    private static final double YO = 136;


    public static Map<String, Double> convertToCoordinates(String latitude, String longitude) {
        Map<String, Double> rs = new HashMap<>();

        double lati = Double.parseDouble(latitude);
        double longi = Double.parseDouble(longitude);

        double DEGRAD = Math.PI / 180.0;
        double re = RE / GRID;
        double slat1 = SLAT1 * DEGRAD;
        double slat2 = SLAT2 * DEGRAD;
        double olong = OLONG * DEGRAD;
        double olat = OLAT * DEGRAD;


        double sn = Math.tan(Math.PI * 0.25 + slat2 * 0.5) / Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sn = Math.log(Math.cos(slat1) / Math.cos(slat2)) / Math.log(sn);
        double sf = Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sf = Math.pow(sf, sn) * Math.cos(slat1) / sn;
        double ro = Math.tan(Math.PI * 0.25 + olat * 0.5);
        ro = re * sf / Math.pow(ro, sn);


        double ra = Math.tan(Math.PI * 0.25 + lati * DEGRAD * 0.5);

        ra = re * sf / Math.pow(ra, sn);

        double theta = longi * DEGRAD - olong;
        if (theta > Math.PI) theta -= 2.0 * Math.PI;
        if (theta < -Math.PI) theta += 2.0 * Math.PI;
        theta *= sn;

        double xCoordinate = ra * Math.sin(theta) + XO + 0.5;
        double yCoordinate = ro - ra * Math.cos(theta) + YO + 0.5;

        if (!Double.isNaN(xCoordinate) && !Double.isNaN(yCoordinate)) {
            rs.put("x", xCoordinate);
            rs.put("y", yCoordinate);
        }

        return rs;
    }
}
