package com.github.kieranpringle.pushbullet.config;

import java.net.http.HttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConnectorConfig {

    /**
     * @return Default Apache HTTP connector
     */
    @Bean
    public HttpClient httpClient() {
        return HttpClient.newHttpClient();
    }
}
