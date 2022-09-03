package com.example.demo;

import com.alibaba.fastjson.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
//指定启动类
@SpringBootTest(classes = {DemoApplication.class})
@AutoConfigureMockMvc

public class TestDeom {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc ;

    @Before
    public void setUp() throws Exception {
//        mockMvc  = MockMvcBuilders.standaloneSetup(new UserController()).build();
        //使用上下文构建MockMvc
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test1(){
            System.out.println("abccc");
            //mockMvc.perform(post("/user").contentType(MediaType.APPLICATION_JSON).content(example);
                        }
    @Test
    public void doGetTest() throws Exception{
        //执行请求（使用GET请求）
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/login").param("index","1").param("size","10")  //若无参数则不写param.
                .accept(MediaType.APPLICATION_JSON)).andReturn();
        mvcResult.getResponse().setCharacterEncoding("UTF-8");
        //获取返回编码
        int status = mvcResult.getResponse().getStatus();
        //获取返回结果
        String content = mvcResult.getResponse().getContentAsString();
        //断言，判断返回编码是否正确
        System.out.println("--------返回的json = " + content);
    }

    @Test
    public void doPostTest() throws Exception{
        //多个参数的传递
//        OperatorEntity operatorEntity=new OperatorEntity();
//        //创建新用户
//        RequestOperatorData requestOperatorData = new RequestOperatorData();
//        requestOperatorData.setCreatedBy("admin");
//        requestOperatorData.setOperatorId(1);
//        requestOperatorData.setOperatorName("test");
//        requestOperatorData.setOperatorType("1");
//        requestOperatorData.setUpdatedBy("admin");
//
//        //将参数转换成JSON对象
//        ObjectMapper mapper = new ObjectMapper();
//        String json = mapper.writeValueAsString(requestOperatorData);
        Map inputmap = new HashMap();
        inputmap.put("name","abc");
        inputmap.put("password","7777");
        String json = JSONObject.toJSONString(inputmap);


        //执行请求（使用POST请求，传递多个参数）

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/addUserInter")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        mvcResult.getResponse().setCharacterEncoding("UTF-8");

        //获取返回编码
        int status = mvcResult.getResponse().getStatus();
        //获取返回结果
        String content = mvcResult.getResponse().getContentAsString();
        System.out.println(content);

    }


}
