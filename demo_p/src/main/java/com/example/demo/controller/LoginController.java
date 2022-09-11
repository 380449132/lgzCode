package com.example.demo.controller;

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
import com.alibaba.fastjson.JSONObject;
import org.thymeleaf.util.StringUtils;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@PropertySource("classpath:demo.properties")
@Controller
public class LoginController {

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

    @RequestMapping("/login")
    public String show(){
        System.out.print("进来了1!!!"+testName);
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


    @RequestMapping(value = "/addUser",method = RequestMethod.POST)
    public String addUser(String name, String password) throws IOException {


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

        commToolService.changeRedisDB(3);
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //redisTemplate.opsForValue().set("stringValue2","stringValue2");
        //redisTemplate.opsForValue().sets
        //valueOperations.set("myFistRedis","goodgood");

        Map redisMap = new HashMap();
        redisMap.put("abc","abc");
//        redisMap.put("b","2");
        //String StringJson = (String) JSONObject.toJSONString(redisMap);
        //redisTemplate.opsForValue().set("redisMap1",StringJson);
//        Object data = valueOperations.get("redisMap");
//        redisMap = (Map) data;
//        String a = (String) redisMap.get("a");

//        ArrayList list = new ArrayList<Map>();
//        list.add(redisMap);
//        list.add(redisMap);
//        list.add(redisMap);
//        redisTemplate.opsForValue().set("redislist",list);
//        ArrayList Outlist = new ArrayList<Map>();
       // Object redisMap1 = valueOperations.get("redisMap1");
//        Outlist = (ArrayList) dataList;
//        redisTemplate.opsForValue().set("Outlist",Outlist);

       // System.out.print("进来了1!!!"+a);
        String StringRedisMap  = (String) valueOperations.get("redisMap1");
        Map RedisMap = new HashMap();
        RedisMap = JSONObject.parseObject(StringRedisMap,Map.class);
        String a = (String) redisMap.get("a");
        System.out.print(a);

        //redissLockUtil.tryLock("firstKey", 10, 10);
        //获取锁
//        Boolean flag =  commToolService.getLock("Mic001","1",lockTime);
//        //释放锁
//        if (flag == true) {
//            commToolService.unLock("Mic001", "1");
//        }

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
            outmap.put("retMsg","入参姓名不能为空!!"+name+password);
            return outmap;

        }
        if (org.thymeleaf.util.StringUtils.isEmpty(password)){
            outmap.put("retCode","-1");
            outmap.put("retMsg","入参密码不能为空!!"+name+password);
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

    /*
     *micService001 使用redis锁
     * */
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
        //获取锁
        Boolean flag =  commToolService.getLock("micService001","1",lockTime);
        if (flag==false){
            outmap.put("retCode","-2");
            outmap.put("retMsg","获取微服务micService001锁失败!!");
            System.out.println("入参:"+JSONObject.toJSONString(map)+"出参:"+JSONObject.toJSONString(outmap));
            return outmap;
        }

        userService.addUser(name,password);
        //释放锁
        commToolService.unLock("micService001","1");
        System.out.println("入参:"+JSONObject.toJSONString(map)+"出参:"+JSONObject.toJSONString(outmap));
        return outmap;
    }




}