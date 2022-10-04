package net.biancheng.c.springcloudalibabasentinelservice8401;


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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;

@RestController
@Slf4j
public class SentinelFlowLimitController {
    @Value("${server.port}")
    private String serverPort;
    @GetMapping("/testA")
    public String testA() {
        return "c语言中文网提醒您，服务访问成功------testA";
    }
    @GetMapping("/testB")
    public String testB() {
        return "c语言中文网提醒您，服务访问成功------testB";
    }
    @Value("${userinfo.conSumIp}")
    private String conSumIp;


    /**
     * 通过 Sentinel 控制台定义流控规则
     *
     */
    @GetMapping("/testD")
    @SentinelResource(value = "testD-resource", blockHandler = "blockHandlerTestD") //通过注解定义资源
    public String testD() {
        System.out.println("c语言中文网提醒您，服务访问成功------testD：" + serverPort);
        return "c语言中文网提醒您，服务访问成功------testD：" + serverPort;
    }

    /**
     * 限流之后的逻辑
     * @param exception
     * @return
     */
    public String blockHandlerTestD(BlockException exception) {
        System.out.println(Thread.currentThread().getName() + "c语言中文网提醒您，TestD服务访问失败! 您已被限流，请稍后重试");
        return "c语言中文网提醒您，TestD服务访问失败! 您已被限流，请稍后重试";
    }

    @GetMapping("/testE")
    @SentinelResource(value = "testE-resource", blockHandler = "blockHandlerTestE", fallback = "handlerFallback") //通过注解定义资源
    public String testE() {

        initFlowRules(); //调用初始化流控规则的方法
        initDegradeRule();
        System.out.println("c语言中文网提醒您，服务访问成功------testE：" + serverPort);
        //调用consum
        String httpReturn = httpURLGETCase(conSumIp+"/consumer/dept/nacos/123");
        return "c语言中文网提醒您，服务访问成功------testE：" + serverPort+",微服务返回:"+httpReturn;
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
        rule.setResource("testE-resource");
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
        DegradeRule rule = new DegradeRule("testE-resource");
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
    public String blockHandlerTestE(BlockException exception) {
        System.out.println(Thread.currentThread().getName() + "c语言中文网提醒您，TestE服务访问失败! 您已被限流，请稍后重试");
        return "c语言中文网提醒您，TestE服务访问失败! 您已被限流，请稍后重试";
    }


    //处理异常的回退方法（服务降级）
    public String handlerFallback( Throwable e) {
        System.out.println("--------->>>>服务降级逻辑"+  e.getMessage());
        String returnString ;
        returnString = "服务降级逻辑" +  e.getMessage();
        return returnString;
    }

    private String httpURLGETCase(String targetUrl) {
        System.out.println("业务服务地址:"+ targetUrl);
        String methodUrl = targetUrl;
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        String line = null;
        String outString = null;
        try {
            URL url = new URL(methodUrl);
            connection = (HttpURLConnection) url.openConnection();// 根据URL生成HttpURLConnection
            connection.setRequestMethod("GET");// 默认GET请求
            connection.connect();// 建立TCP连接
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));// 发送http请求
                StringBuilder result = new StringBuilder();
                // 循环读取流
                while ((line = reader.readLine()) != null) {
                    result.append(line).append(System.getProperty("line.separator"));// "\n"
                }
                outString = result.toString();
                System.out.println(result.toString());
                System.out.println(url);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            connection.disconnect();
            return outString;
        }
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
