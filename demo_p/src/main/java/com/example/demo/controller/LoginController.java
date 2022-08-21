package com.example.demo.controller;

import com.example.demo.bean.UserBean;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.eureka.consumer.ConsumerApplication;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.RedisConfig;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;



@Controller
public class LoginController {

    //将Service注入Web层
    @Autowired
    UserService userService;

    @Autowired
    RedisTemplate redisTemplate;

    @RequestMapping("/login")
    public String show(){
        return "login";
    }

    @RequestMapping(value = "/loginIn",method = RequestMethod.POST)
    public String login(String name,String password){

        System.out.print("进来了1!!!");
        UserBean userBean = userService.loginIn(name,password);
        System.out.print("太棒了!!!"+userBean.getId()+userBean.getName());
        if(userBean!=null){
            return "success";
        }else {
            return "error";
        }
    }

    @RequestMapping(value = "/getUser",method = RequestMethod.GET)
    public String getUser(String name, String password){

        System.out.print("进来了1!!!");
        //userService.addUser(name,password);
        return "success";
    }

    public void changeRedisDB(int i)
    {
        LettuceConnectionFactory lettuceConnectionFactory = (LettuceConnectionFactory) redisTemplate.getConnectionFactory();
        lettuceConnectionFactory.setDatabase(i);
        redisTemplate.setConnectionFactory(lettuceConnectionFactory);
        lettuceConnectionFactory.resetConnection();
        lettuceConnectionFactory.afterPropertiesSet();

    }

    @RequestMapping(value = "/addUser",method = RequestMethod.POST)
    public String addUser(String name, String password){


//        LettuceConnectionFactory lettuceConnectionFactory = (LettuceConnectionFactory) redisTemplate.getConnectionFactory();
//        lettuceConnectionFactory.setDatabase(1);
//        redisTemplate.setConnectionFactory(lettuceConnectionFactory);
//        lettuceConnectionFactory.resetConnection();
//        lettuceConnectionFactory.afterPropertiesSet();

        //JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
        //jedisConnectionFactory.setDatabase(9);
        //factory.setDatabase("1");
        //LettuceFactories jedisConnectionFactory = (LettuceFactories) redisTemplate.getConnectionFactory();

        //jedisConnectionFactory.setDatabase(1);
        //RedisTemplate redisTemplate = new RedisTemplate();
        //更换redisDB
        changeRedisDB(3);
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //redisTemplate.opsForValue().set("stringValue2","stringValue2");
        //redisTemplate.opsForValue().sets
        //valueOperations.set("myFistRedis","goodgood");

        Map redisMap = new HashMap();
        redisMap.put("a","1");
//        redisMap.put("b","2");
        redisTemplate.opsForValue().set("redisMap1",redisMap);
//        Object data = valueOperations.get("redisMap");
//        redisMap = (Map) data;
//        String a = (String) redisMap.get("a");

//        ArrayList list = new ArrayList<Map>();
//        list.add(redisMap);
//        list.add(redisMap);
//        list.add(redisMap);
//        redisTemplate.opsForValue().set("redislist",list);
//        ArrayList Outlist = new ArrayList<Map>();
//        Object dataList = valueOperations.get("redislist");
//        Outlist = (ArrayList) dataList;
//        redisTemplate.opsForValue().set("Outlist",Outlist);

       // System.out.print("进来了1!!!"+a);
        userService.addUser(name,password);
        return "success";
    }

    @ResponseBody
    @RequestMapping(value = "/addUserInter",method = RequestMethod.POST)
    public Map addUserInter(@RequestBody Map map){
        Map outmap = new HashMap();
        outmap.put("retCode","1");
        outmap.put("retMsg","接口调用成功666!!");

        String name = (String) map.get("name");
        String password = (String) map.get("password");
        if (org.thymeleaf.util.StringUtils.isEmpty(name)){
            outmap.put("retCode","-1");
            outmap.put("retMsg","入参姓名不能为空!!");
            return outmap;

        }
        if (org.thymeleaf.util.StringUtils.isEmpty(password)){
            outmap.put("retCode","-1");
            outmap.put("retMsg","入参密码不能为空!!");
            return outmap;

        }


        System.out.print("进来了1!!!");
        //System.out.print("进来了3333!!!");
        userService.addUser(name,password);

//        Map map1 = new HashMap();
//        map1.put("id","123456");
//        map1.put("url","http://service-provider/testUser");
//
//        ConsumerApplication consumerApplication = new ConsumerApplication();
//        Map map2 = new HashMap();
//        map2 = consumerApplication.testUser(map1);

        return outmap;
    }

    @ResponseBody
    @RequestMapping(value = "/addUserInter1",method = RequestMethod.POST)
    public Map addUserInter1(@RequestBody Map map){
        Map outmap = new HashMap();
        outmap.put("retCode","1");
        outmap.put("retMsg","接口调用成功666!!");

        String name = (String) map.get("name");
        String password = (String) map.get("password");
        if (org.thymeleaf.util.StringUtils.isEmpty(name)){
            outmap.put("retCode","-1");
            outmap.put("retMsg","入参姓名不能为空!!");
            return outmap;

        }
        if (org.thymeleaf.util.StringUtils.isEmpty(password)){
            outmap.put("retCode","-1");
            outmap.put("retMsg","入参密码不能为空!!");
            return outmap;

        }


        System.out.print("进来了1!!!");
        //System.out.print("进来了3333!!!");
        userService.addUser(name,password);


//        Map map1 = new HashMap();
//        map1.put("id","123456");
//        map1.put("url","http://service-provider/testUser");
//
//        ConsumerApplication consumerApplication = new ConsumerApplication();
//        Map map2 = new HashMap();
//        map2 = consumerApplication.testUser(map1);

        return outmap;
    }

    @ResponseBody
    @RequestMapping(value = "/micService001",method = RequestMethod.POST)
    public Map micService001(@RequestBody Map map){
        System.out.println("入参:"+JSONObject.toJSONString(map));
        Map outmap = new HashMap();
        outmap.put("retCode","1");
        outmap.put("retMsg","接口调用成功666!!");

        String name = (String) map.get("name");
        String password = (String) map.get("password");
        if (org.thymeleaf.util.StringUtils.isEmpty(name)){
            outmap.put("retCode","-1");
            outmap.put("retMsg","入参姓名不能为空!!");
            System.out.println("入参:"+JSONObject.toJSONString(map)+"出参:"+JSONObject.toJSONString(outmap));
            return outmap;

        }
        if (org.thymeleaf.util.StringUtils.isEmpty(password)){
            outmap.put("retCode","-1");
            outmap.put("retMsg","入参密码不能为空!!");
            System.out.println("入参:"+JSONObject.toJSONString(map)+"出参:"+JSONObject.toJSONString(outmap));
            return outmap;

        }

        userService.addUser(name,password);
        System.out.println("入参:"+JSONObject.toJSONString(map)+"出参:"+JSONObject.toJSONString(outmap));
        return outmap;
    }


}