package net.biancheng.c.springcloudalibabaconsumernacos8801;


import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.degrade.circuitbreaker.CircuitBreakerStrategy;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.alibaba.fastjson.JSONObject;

@RestController
@Slf4j
public class DeptController_Consumer {
    @Resource
    private RestTemplate restTemplate;
    @Value("${service-url.nacos-user-service}")
    private String serverURL; //服务提供者的服务名
    @GetMapping("/consumer/dept/nacos/{id}")
    public String paymentInfo(@PathVariable("id") Long id) {
        System.out.println("我是提供者"+serverURL);
        return restTemplate.getForObject(serverURL + "/dept/nacos/" + id, String.class);
    }

    @HystrixCommand(fallbackMethod = "fallConfigOfRemote",
            commandProperties = {@HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "5000")})
    @ResponseBody
    @PostMapping("/micSerDoPost")
    @SentinelResource(value = "micSerDoPost-resource", blockHandler = "blockHandler", fallback = "handlerFallback") //通过注解定义资源
    public Map<String,Object> micSerDoPost(@RequestBody Map inputMap) {

        System.out.println("我是消费者micSerDoPost，服务访问成功------micSerDoPost：");
        //调用consum
        //取出inputMap 中的 proName 和 serUrl  拼成 url,再来调用服务者
        String proName = (String) inputMap.get("proName");
        String serUrl = (String) inputMap.get("serUrl");
        Map<String,Object> data = new HashMap<>();
        if (org.apache.commons.lang.StringUtils.isEmpty(proName)){
            data.put("retCode","-1");
            data.put("retMsg","proName服务提供者不能为空!");
            return data;
        }
        if (org.apache.commons.lang.StringUtils.isEmpty(serUrl)){
            data.put("retCode","-1");
            data.put("retMsg","serUrl服务地址不能为空!");
            return data;
        }
        initFlowRules("micSerDoPost-resource",2);
        initDegradeRule("micSerDoPost-resource");

        //System.out.println("调用服务者开始!"+serverURL);
        //data = restTemplate.postForObject(serverURL + "/micSer001", inputMap, Map.class);
        data = restTemplate.postForObject(proName + serUrl, inputMap, Map.class);

        data.putAll(inputMap);
        data.put("retCode","1");
        data.put("retMsg","调用post业务成功!");
        System.out.println("调用服务者结束!"+serverURL);
        System.out.println("入参:"+ JSONObject.toJSONString(inputMap) + " 出参:" + JSONObject.toJSONString(data));


        //return "c语言中文网提醒您，服务访问成功------micSer001：" + serverPort+",微服务返回:";
        return data;
    }


    @HystrixCommand(fallbackMethod = "fallConfigOfRemote",
            commandProperties = {@HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "5000")})
    @ResponseBody
    @PostMapping("/micSer001")
    public Map<String,Object> micSer001(@RequestBody Map inputMap) {

        System.out.println("我是消费者micSer001，服务访问成功------micSer001：");
        //调用consum
        //String httpReturn = sendPost(conSumIp+"/consumer/dept/nacos/123");
        String userId = (String) inputMap.get("userId");
        System.out.println(userId);

        Map outMap = new HashMap();
        outMap.put("userId",userId);
        Map<String,Object> data = new HashMap<>();
        System.out.println("调用服务者开始!"+serverURL);
        data = restTemplate.postForObject(serverURL + "/micSer001", inputMap, Map.class);
        data.putAll(inputMap);
        data.put("retCode","1");
        data.put("retMsg","调用post业务成功!");
        System.out.println("调用服务者结束!"+serverURL);
        System.out.println("入参:"+ JSONObject.toJSONString(inputMap) + " 出参:" + JSONObject.toJSONString(data));


        //return "c语言中文网提醒您，服务访问成功------micSer001：" + serverPort+",微服务返回:";
        return data;
    }

    //单个微服务请求的熔断方法
    private Map<String,Object> fallConfigOfRemote(Map map) {
        System.out.println(JSONObject.toJSONString(map)+"超时熔断......");
        Map<String,Object> data = new HashMap<>();
        data.put("retCode","-1");
        data.put("retMsg",JSONObject.toJSONString(map)+"系统繁忙，请稍后再试!!!");
        return data;
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

    /**
     * 限流之后的逻辑
     * @param exception
     * @return
     */
    public Map<String,Object> blockHandler(Map inputMap, BlockException  exception) {
        System.out.println(Thread.currentThread().getName() + "您已被限流，请稍后重试"+JSONObject.toJSONString(inputMap));
        Map<String,Object> data = new HashMap<>();
        data.put("retCode","-1");
        data.put("retMsg",JSONObject.toJSONString(inputMap)+"您已被限流，请稍后重试!");
        return data;
    }


    //处理异常的回退方法（服务降级）
    public Map<String,Object> handlerFallback(Map inputMap, Throwable e) {
        System.out.println(JSONObject.toJSONString(inputMap)+"--------->>>>服务熔断"+  e.getMessage());
        Map<String,Object> data = new HashMap<>();
        data.put("retCode","-1");
        data.put("retMsg",JSONObject.toJSONString(inputMap)+"您已被服务熔断，请稍后重试!");
        return data;
    }

}