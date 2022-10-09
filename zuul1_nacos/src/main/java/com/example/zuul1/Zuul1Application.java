package com.example.zuul1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;


 @SpringBootApplication
 @EnableDiscoveryClient // 开启服务注册与发现功能
 @EnableZuulProxy

public class Zuul1Application {

	public static void main(String[] args) {
		SpringApplication.run(Zuul1Application.class, args);
	}

}
