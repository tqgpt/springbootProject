package com.chunjae.tqgpt.util;

import jakarta.servlet.http.HttpServletRequest;

public class ConnectUtil {
    public static String getClientIP(HttpServletRequest req) {
        String[] headers = {
                "X-FORWARDED-FOR",
                "Proxy-client-ip",
                "WL-proxy-client-ip",
                "HTTP_X_FORWARDED_FOR",
                "HTTP_X_FORWARDED",
                "HTTP_X_CLUSTER_CLIENT_IP",
                "HTTP_CLIENT_IP"
        };

        for (String header : headers) {
            String ip = req.getHeader(header);

            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                return ip;
            }
        }
        return req.getRemoteAddr();
    }
}
