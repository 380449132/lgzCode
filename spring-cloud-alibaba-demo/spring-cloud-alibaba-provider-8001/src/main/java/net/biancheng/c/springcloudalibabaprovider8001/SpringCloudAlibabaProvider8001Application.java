package net.biancheng.c.springcloudalibabaprovider8001;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableDiscoveryClient //开启服务发现功能
@RestController
@Slf4j
public class SpringCloudAlibabaProvider8001Application {
	@Value("${server.port}")
	private String serverPort;
	public static void main(String[] args) {
		SpringApplication.run(SpringCloudAlibabaProvider8001Application.class, args);
	}


	@GetMapping(value = "/dept/nacos/{id}")
	public String getPayment(@PathVariable("id") Integer id) {
		return "<h2>c语言中文网提醒您，服务访问成功！</h2>服务名：spring-cloud-alibaba-provider<br /> 端口号： " + serverPort + "<br /> 传入的参数：" + id;
	}

}
