package net.biancheng.c.springcloudalibabasentinelservice8401;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient

public class SpringCloudAlibabaSentinelService8401Application {

	public static void main(String[] args) {
		SpringApplication.run(SpringCloudAlibabaSentinelService8401Application.class, args);
	}

}
