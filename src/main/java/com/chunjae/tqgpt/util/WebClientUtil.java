package com.chunjae.tqgpt.util;

import io.netty.channel.ChannelOption;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Component
public class WebClientUtil {

    public static WebClient getBaseUrl(final String url) {
        return WebClient
                .builder()
                .baseUrl(url)
                .clientConnector(new ReactorClientHttpConnector(HttpClient.newConnection()
                        .tcpConfiguration(client ->
                                client
                                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                        )
                ))
                .build()
                .mutate()
                .build();
    }
}
