package com.example.demo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.net.URL;

import java.net.URLClassLoader;




@SpringBootApplication
@MapperScan("com.example.demo.mapper")
public class DemoApplication {

	public static void main(String[] args) {


		SpringApplication.run(DemoApplication.class, args);

		//String command = "cmd /c d:\\eureka_server\\启动消费者-8083.bat";
//        try {
//			Runtime.getRuntime().exec(command);
//		}catch (IOException e)
//		{
//			e.printStackTrace();
//		}

		//JarClassLoader cl=new JarClassLoader("com.eureka.consumer.ConsumerApplication");

		//cl.invokeMain("com.energyxxer.inject_demo.treegen.TreeGenDemo", args);

//		JarClassLoader jcl = new JarClassLoader();
//		try {
//			jcl.invokeMain("com.eureka.consumer.ConsumerApplication", args);
//		} catch (Throwable e) {
//			e.printStackTrace();
//		}

	}


}