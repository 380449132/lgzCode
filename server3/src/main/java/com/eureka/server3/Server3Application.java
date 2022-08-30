package com.eureka.server3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class Server3Application {
   public static void main(String[] args) {
		SpringApplication.run(Server3Application.class, args);


	}

}
