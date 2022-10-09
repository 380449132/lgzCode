package net.biancheng.c.springcloudalibabasentinelservice8401;


import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.degrade.circuitbreaker.CircuitBreakerStrategy;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class userController {
    @Value("${server.port}")
    private String serverPort;

    @Value("${userinfo.conSumIp}")
    private String conSumIp;



    /**
     * 限流之后的逻辑
     * @param exception
     * @return
     */
    public String blockHandlerMicSer001(BlockException exception) {
        System.out.println(Thread.currentThread().getName() + "c语言中文网提醒您，blockHandlerMicSer001服务访问失败! 您已被限流，请稍后重试");
        return "c语言中文网提醒您，TestD服务访问失败! 您已被限流，请稍后重试";
    }

    @PostMapping("/micSerDoPost")
    @SentinelResource(value = "micSerDoPost-resource", blockHandler = "blockHandler", fallback = "handlerFallback") //通过注解定义资源
    @ResponseBody
    public Map<String,Object> micSerDoPost(@RequestBody Map inputMap) throws UnsupportedEncodingException {

        initFlowRules(); //调用初始化流控规则的方法
        initDegradeRule();
        System.out.println("我是流控者micSerDoPost：" + serverPort);
        //调用consum
        String param = JSONObject.toJSONString(inputMap);
        String httpReturn = sendPost(conSumIp+"/micSerDoPost",param);
        String userId = (String) inputMap.get("userId");
        System.out.println(httpReturn);

        Map outMap = new HashMap();
        outMap=JSONObject.parseObject(httpReturn);
        System.out.println("入参:"+JSONObject.toJSONString(inputMap) + "出参:"+JSONObject.toJSONString(outMap));
        //return "c语言中文网提醒您，服务访问成功------micSer001：" + serverPort+",微服务返回:";
        return outMap;
    }

    @PostMapping("/micSer001")
    @SentinelResource(value = "micSer001-resource", blockHandler = "blockHandler", fallback = "handlerFallback") //通过注解定义资源
    @ResponseBody
    public Map<String,Object> micSer001(@RequestBody Map inputMap) throws UnsupportedEncodingException {

        initFlowRules(); //调用初始化流控规则的方法
        initDegradeRule();
        System.out.println("我是流控者micSer001：" + serverPort);
        //调用consum
        String param = JSONObject.toJSONString(inputMap);
        String httpReturn = sendPost(conSumIp+"/micSer001",param);
        String userId = (String) inputMap.get("userId");
        System.out.println(httpReturn);

        Map outMap = new HashMap();
        outMap=JSONObject.parseObject(httpReturn);
        System.out.println("入参:"+JSONObject.toJSONString(inputMap) + "出参:"+JSONObject.toJSONString(outMap));
        //return "c语言中文网提醒您，服务访问成功------micSer001：" + serverPort+",微服务返回:";
        return outMap;
    }


//    public static void loadRules(List<FlowRule> rules) {
//        currentProperty.updateValue(rules);
//    }
    /**
     * 通过代码定义流量控制规则
     */
    private static void initFlowRules() {
        List<FlowRule> rules = new ArrayList<>();
        //定义一个限流规则对象
        FlowRule rule = new FlowRule();
        //资源名称
        rule.setResource("micSer001-resource");
        //限流阈值的类型
        rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        // 设置 QPS 的阈值为 2
        rule.setCount(2);
        rules.add(rule);
        //定义限流规则
        FlowRuleManager.loadRules(rules);
    }

    /**
     * 初始化熔断策略
     */
    private static void initDegradeRule() {
        List<DegradeRule> rules = new ArrayList<>();
        DegradeRule rule = new DegradeRule("micSer001-resource");
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
    public String blockHandler(BlockException exception) {
        System.out.println(Thread.currentThread().getName() + "c语言中文网提醒您，TestE服务访问失败! 您已被限流，请稍后重试");
        return "c语言中文网提醒您，服务访问失败! 您已被限流，请稍后重试";
    }


    //处理异常的回退方法（服务降级）
    public String handlerFallback( Throwable e) {
        System.out.println("--------->>>>服务降级逻辑"+  e.getMessage());
        String returnString ;
        returnString = "本服务有可能已熔断，服务降级逻辑" +  e.getMessage();
        return returnString;
    }


    public static String sendPost(String urlStr, String dataStr) throws UnsupportedEncodingException {
        System.out.println("业务服务地址:"+ urlStr);
        String result = "";
        try {

            // 创建url资源
            URL url = new URL(urlStr);
            // 建立http连接
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // 设置允许输入输出
            conn.setDoOutput(true);
            conn.setDoInput(true);

            // 设置不用缓存
            conn.setUseCaches(false);
            // 设置传递方式
            conn.setRequestMethod("POST");
            // 设置维持长连接
            conn.setRequestProperty("Connection", "Keep-Alive");
            // 设置文件字符集:
            conn.setRequestProperty("Charset", "UTF-8");
            //转换为字节数组
            byte[] data = dataStr.getBytes("UTF-8");
            // 设置文件长度
            conn.setRequestProperty("Content-Length", String.valueOf(data.length));
            // 设置文件类型:
            //conn.setRequestProperty("Content-Type", "text/xml");// 开始连接请求
            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");

            conn.connect();
            OutputStream out = conn.getOutputStream();
            // 写入请求的字符串
            out.write(data);
            out.flush();
            out.close();

            System.out.println(conn.getResponseCode());

            // 请求返回的状态
            if (conn.getResponseCode() == 200) {
                System.out.println("连接成功");
                // 请求返回的数据
                InputStream in = conn.getInputStream();try {
                    byte[] data1 = new byte[in.available()];
                    in.read(data1);
                    result = new String(data1,"UTF-8");
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            } else {
                result = String.valueOf(conn.getResponseCode());
                System.out.println("no++");
            }

        } catch (Exception e) {

        }



        return result;
    }




}
