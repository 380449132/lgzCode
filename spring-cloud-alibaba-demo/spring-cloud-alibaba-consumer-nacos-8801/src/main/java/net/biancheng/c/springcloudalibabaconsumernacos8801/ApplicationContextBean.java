package net.biancheng.c.springcloudalibabaconsumernacos8801;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
@Configuration
public class ApplicationContextBean {
    @Bean
    @LoadBalanced //与 Ribbon 集成，并开启负载均衡功能
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }
}
