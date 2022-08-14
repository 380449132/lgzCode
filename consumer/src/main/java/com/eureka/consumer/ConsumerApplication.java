package com.eureka.consumer;
import com.alibaba.fastjson.JSONObject;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;

import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.context.annotation.Bean;

import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.client.RestTemplate;


import java.util.HashMap;

import java.util.Map;


/**

 * Eureka客户端-消费者

 */

@RestController

@EnableEurekaClient

@SpringBootApplication

@RibbonClient(name = "service-consumer",configuration = MySelfRule.class)
@EnableHystrix

public class ConsumerApplication {



	@Autowired
    RestTemplate restTemplate;

//	@Bean
//	@LoadBalanced
//	public RestTemplate  restTemplate(){
//		return  new RestTemplate(new OkHttp3ClientHttpRequestFactory());
//	}


	public static void main(String[] args) {

		SpringApplication.run(ConsumerApplication.class, args);

	}


	/**

	 * 实例化RestTemplate

	 * @return

	 */

	@LoadBalanced

	@Bean

	public RestTemplate rest() {

		return new RestTemplate();
		//return  new RestTemplate(new OkHttp3ClientHttpRequestFactory());

	}


	/**

	 * Rest服务端使用RestTemplate发起http请求,然后得到数据返回给前端----gotoUser是为了区分getUser怕小伙伴晕头

	 * @param id

	 * @return

	*/

	 @GetMapping(value = "/gotoUser")
     @ResponseBody
	 public Map<String,Object> getUser(@RequestParam Integer id){

		 Map<String,Object> data = new HashMap<>();
         data = restTemplate.getForObject("http://service-provider/getUser?id="+id,Map.class);
         return data;

}

    @HystrixCommand(fallbackMethod = "fallConfigOfRemote",
                    commandProperties = {@HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "5000")})
    @RequestMapping(value = "/doPostBus",method = RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> testUser(@RequestBody Map map){
		System.out.println("入参:"+JSONObject.toJSONString(map));
		Map<String,Object> data = new HashMap<>();
		//String url ="http://service-provider/testUser";
		String url =(String) map.get("url");
		if (StringUtils.isEmpty(url)){
			data.put("retCode","-1");
			data.put("retMsg","调用的url[参考:http://service-provider/testUser]不能为空!!");
			return data;
		}
		//本为post请求的调用方式
		data = restTemplate.postForObject(url, map, Map.class);
		data.put("retCode","1");
		data.put("retMsg","调用post业务成功!");
		System.out.println("入参:"+ JSONObject.toJSONString(map) + " 出参:" + JSONObject.toJSONString(data));
		return data;

	}
    //单个微服务请求的熔断方法
    private Map<String,Object> fallConfigOfRemote(Map map) {
        System.out.println("超时熔断......");
        Map<String,Object> data = new HashMap<>();
        data.put("retCode","-1");
        data.put("retMsg","系统繁忙，请稍后再试!!!");
        return data;
    }

}