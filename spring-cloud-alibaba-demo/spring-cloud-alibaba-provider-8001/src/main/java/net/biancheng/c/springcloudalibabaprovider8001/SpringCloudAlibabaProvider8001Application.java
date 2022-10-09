package net.biancheng.c.springcloudalibabaprovider8001;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.degrade.circuitbreaker.CircuitBreakerStrategy;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.alibaba.fastjson.JSONObject;

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


	@PostMapping(value = "/micSer001")
	@ResponseBody
	public Map micSer001(@RequestBody Map inputMap) {
		Map outMap = new HashMap();
		outMap.putAll(inputMap);
		outMap.put("desc","我是服务提供者micSer001!");
		System.out.println("我是服务提供者micSer001!");

		System.out.println("入参:"+JSONObject.toJSONString(inputMap)+"出参:"+JSONObject.toJSONString(outMap));
		return outMap;
	}

	@PostMapping(value = "/micSerDoPost")
	@ResponseBody
	@SentinelResource(value = "micSerDoPost-resource", blockHandler = "blockHandler", fallback = "handlerFallback") //通过注解定义资源
	public Map micSerDoPost(@RequestBody Map inputMap) {
		initFlowRules("micSerDoPost-resource",2); //调用初始化流控规则的方法
		initDegradeRule("micSerDoPost-resource");
		Map outMap = new HashMap();
		outMap.putAll(inputMap);
		outMap.put("desc","我是服务提供者micSerDoPost!");
		System.out.println("我是服务提供者micSerDoPost!");

		System.out.println("入参:"+JSONObject.toJSONString(inputMap)+"出参:"+JSONObject.toJSONString(outMap));
		return outMap;
	}

	/**
	 * 通过代码定义流量控制规则
	 */
	private static void initFlowRules(String resName,int qps) {
		List<FlowRule> rules = new ArrayList<>();
		//定义一个限流规则对象
		FlowRule rule = new FlowRule();
		//资源名称
		rule.setResource(resName);
		//限流阈值的类型
		rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
		// 设置 QPS 的阈值为 2
		rule.setCount(qps);
		rules.add(rule);
		//定义限流规则
		FlowRuleManager.loadRules(rules);
	}

	/**
	 * 初始化熔断策略
	 */
	private static void initDegradeRule(String resName) {
		List<DegradeRule> rules = new ArrayList<>();
		DegradeRule rule = new DegradeRule(resName);
		//熔断策略为异常比例
		rule.setGrade(CircuitBreakerStrategy.ERROR_RATIO.getType());
		//异常比例阈值
		rule.setCount(0.7);
		//最小请求数
		rule.setMinRequestAmount(100);
		//统计市场，单位毫秒
		rule.setStatIntervalMs(30000);
		//熔断市场，单位秒
		rule.setTimeWindow(10);
		rules.add(rule);
		DegradeRuleManager.loadRules(rules);
	}

	public Map<String,Object> blockHandler(Map inputMap, BlockException  exception) {
		System.out.println(Thread.currentThread().getName() + JSONObject.toJSONString(inputMap)+"您已被限流，请稍后重试");
		Map<String,Object> data = new HashMap<>();
		data.put("retCode","-1");
		data.put("retMsg",JSONObject.toJSONString(inputMap)+"您已被限流，请稍后重试!");
		return data;
	}


	//处理异常的回退方法（服务降级）
	public Map<String,Object> handlerFallback(Map inputMap, Throwable e) {
		System.out.println(JSONObject.toJSONString(inputMap) + "--------->>>>服务熔断"+  e.getMessage());
		Map<String,Object> data = new HashMap<>();
		data.put("retCode","-1");
		data.put("retMsg",JSONObject.toJSONString(inputMap)+"您已被服务熔断，请稍后重试!");
		return data;
	}

}
