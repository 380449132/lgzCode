package com.example.demo;
import com.example.demo.DemoApplication;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import java.io.IOException;

@SpringBootApplication
@MapperScan("com.example.demo.mapper")
public class SpringBootStartApplication extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
//		String command1 = "cmd /c d:\\eureka_server\\provider8082\\启动提供者-8082.bat";
//		String command2 = "cmd /c d:\\eureka_server\\consumer8083\\启动消费者-8083.bat";
//
//		try {
//			Runtime.getRuntime().exec(command1);
//			Runtime.getRuntime().exec(command2);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		// 注意这里要指向原先用main方法执行的Application启动类3
        return builder.sources(DemoApplication.class);
	}
}