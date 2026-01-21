package com.example.workflow.config;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.core5.util.Timeout;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        // 1. Create a Connection Manager with high limits
        PoolingHttpClientConnectionManager connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
                .setMaxConnTotal(200)       // Maximum total connections in the pool
                .setMaxConnPerRoute(100)    // Maximum connections to your Nginx host
                .build();

        // 2. Create the HttpClient with custom timeouts
        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .build();

        // 3. Connect the HttpClient to Spring's RequestFactory
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
        
        // Timeouts are critical to prevent "Hanging" threads
        factory.setConnectTimeout(2000);  // 2s to establish the TCP connection
        factory.setConnectionRequestTimeout(3000); // 3s to wait for an available connection from the pool

        return new RestTemplate(factory);
    }
}
