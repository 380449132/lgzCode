package com.example.demo.serviceImpl;

import com.example.demo.bean.UserBean;
import com.example.demo.mapper.UserMapper;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class UserServiceImpl implements UserService {

    //将DAO注入Service层

    @Autowired
    //@Resource
    private UserMapper userMapper;
//   @Autowired()
//    public UserServiceImpl(UserMapper userBeanMapper) {
//        this.setMapper(userBeanMapper);
//    }

    @Override
    public UserBean loginIn(String name, String password) {
        System.out.print("进来了2!!!");
        return userMapper.getInfo(name,password);


    }

    @Override
    public void addUser(String name, String password) {
        System.out.print("进来了2!!!");
        String uuid = "";
        Date today = new Date();
        SimpleDateFormat simpleDateFormat  = new SimpleDateFormat("yyyymmddhhmmss");
        uuid = simpleDateFormat.format(today);

        userMapper.addUser(uuid,name,password);


    }




}