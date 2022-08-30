package com.example.demo.service;

import com.example.demo.bean.UserBean;

public interface CommToolService {

    public void changeRedisDB(int i);
    public Boolean getLock(String key, String value, Long ms);
    public Boolean unLock(String key, String value);

}