package com.example.demo.mapper;

import com.example.demo.bean.UserBean;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface UserMapper {
   //UserBean getInfo(String name,String password);
   UserBean getInfo(@Param("name")String name,@Param("password")String password);
   void addUser(@Param("id")String id,@Param("name")String name,@Param("password")String password);
   String selectIdByName(@Param("name")String name,@Param("password")String password);
}