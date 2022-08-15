package com.eureka.consumer;

import com.alibaba.fastjson.JSONObject;
import com.netflix.loadbalancer.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class MySelfRule {

    @Value("${userinfo.myRibbonRule}")
    private String myRibbonRule;

    @Bean
    public IRule myRule(){
        System.out.println("随机算法:"+myRibbonRule);
        //轮询
        if (StringUtils.equals(myRibbonRule,"1")){
            return new RoundRobinRule();
        }
        //随机
        if (StringUtils.equals(myRibbonRule,"2")){
            return new RandomRule();
        }
        //并发量小的服务优先
        if (StringUtils.equals(myRibbonRule,"3")){
            return new BestAvailableRule();
        }
        //综合选择最优服务
        if (StringUtils.equals(myRibbonRule,"4")){
            return new ZoneAvoidanceRule();
        }
        //针对响应时间加权轮询
        if (StringUtils.equals(myRibbonRule,"5")){
            return new WeightedResponseTimeRule();
        }
        //先选择一个server,若失败再选下一个server
        if (StringUtils.equals(myRibbonRule,"6")){
            return new RetryRule();
        }
        //默认轮询
        return new RoundRobinRule();

    }
}
