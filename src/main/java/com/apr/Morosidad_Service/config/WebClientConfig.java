package com.apr.Morosidad_Service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${service.socio.url:http://socio-service}")
    private String socioServiceUrl;

    @Value("${service.facturacion.url:http://facturacion-service}")
    private String facturacionServiceUrl;

    @Bean
    @LoadBalanced
    public WebClient.Builder loadBalancedWebClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public WebClient socioWebClient(WebClient.Builder loadBalancedWebClientBuilder) {
        if (socioServiceUrl.contains(".") || socioServiceUrl.contains("localhost")) {
            return WebClient.builder().baseUrl(socioServiceUrl).build();
        }
        return loadBalancedWebClientBuilder.baseUrl(socioServiceUrl).build();
    }

    @Bean
    public WebClient facturacionWebClient(WebClient.Builder loadBalancedWebClientBuilder) {
        if (facturacionServiceUrl.contains(".") || facturacionServiceUrl.contains("localhost")) {
            return WebClient.builder().baseUrl(facturacionServiceUrl).build();
        }
        return loadBalancedWebClientBuilder.baseUrl(facturacionServiceUrl).build();
    }
}
