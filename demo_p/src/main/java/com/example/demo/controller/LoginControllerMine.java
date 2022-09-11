package com.example.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.bean.UserBean;
import com.example.demo.service.CommToolService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.util.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@PropertySource("classpath:demo.properties")
@Controller
public class LoginControllerMine {

    //将Service注入Web层
    @Autowired
    UserService userService;
    @Autowired
    CommToolService commToolService;
    @Autowired
    RedisTemplate redisTemplate;

    //set后自动释放这个key,释放时间
    private long lockTime = 5000;

    @Value("${test.name}")
    private  String testName;

    //@Autowired
    //RedissLockUtil redissLockUtil;


    /*
     *micService001 控制本服务的流量
     * */
    @ResponseBody
    @RequestMapping(value = "/micService002",method = RequestMethod.POST)
    public Map micService002(@RequestBody Map map){
        System.out.println("入参:"+JSONObject.toJSONString(map));
        Map outmap = new HashMap();
        outmap.put("retCode","1");
        outmap.put("retMsg","接口调用成功666!!");



        String name = (String) map.get("name");
        String password = (String) map.get("password");
        if (StringUtils.isEmpty(name)){
            outmap.put("retCode","-1");
            outmap.put("retMsg","入参姓名不能为空!!");
            System.out.println("入参:"+JSONObject.toJSONString(map)+"出参:"+JSONObject.toJSONString(outmap));
            return outmap;

        }
        if (StringUtils.isEmpty(password)){
            outmap.put("retCode","-1");
            outmap.put("retMsg","入参密码不能为空!!");
            System.out.println("入参:"+JSONObject.toJSONString(map)+"出参:"+JSONObject.toJSONString(outmap));
            return outmap;

        }
        //获取本服务的 访问上限和计数，执行时间每次计数加1，完成后减1
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String upLim  =  (String) valueOperations.get("micService002UpLim");

        String curCount  =  (String) valueOperations.get("micService002CurCount");
        int intCurCount = Integer.parseInt(curCount);
        if  (Integer.parseInt(upLim) <= intCurCount ){
            outmap.put("retCode","-1");
            outmap.put("retMsg","本微服务同时在线已超上限,请稍后再试;当前:"+curCount+ "  上限:"+ upLim);
            System.out.println("入参:"+JSONObject.toJSONString(map)+"出参:"+JSONObject.toJSONString(outmap));
            return outmap;
        }


        intCurCount = intCurCount + 1;
        valueOperations.set("micService002CurCount",String.valueOf(intCurCount) );
        String userId = userService.selectIdByName(name,password);
        if (StringUtils.isEmpty(userId)||userId == "null" ){
            outmap.put("retCode","-1");
            outmap.put("retMsg","对不起，未找到对应的id,请核实!");
            System.out.println("入参:"+JSONObject.toJSONString(map)+"出参:"+JSONObject.toJSONString(outmap));
            return outmap;
        }

        intCurCount = intCurCount - 1;
        valueOperations.set("micService002CurCount",String.valueOf(intCurCount) );
        outmap.put("userId",userId);

        System.out.println("入参:"+JSONObject.toJSONString(map)+"出参:"+JSONObject.toJSONString(outmap));
        return outmap;
    }


}