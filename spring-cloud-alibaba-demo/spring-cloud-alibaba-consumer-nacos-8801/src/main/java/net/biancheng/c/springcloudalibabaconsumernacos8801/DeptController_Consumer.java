package net.biancheng.c.springcloudalibabaconsumernacos8801;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import javax.annotation.Resource;
import java.util.HashMap;
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

}