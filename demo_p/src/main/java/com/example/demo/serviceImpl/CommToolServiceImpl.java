package com.example.demo.serviceImpl;

import com.example.demo.bean.UserBean;
import com.example.demo.mapper.UserMapper;
import com.example.demo.service.CommToolService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class CommToolServiceImpl implements CommToolService {

    @Autowired
    RedisTemplate redisTemplate;

    //获取锁超时时间
    private long timeout = 5000;

    public void changeRedisDB(int i)
    {
        LettuceConnectionFactory lettuceConnectionFactory = (LettuceConnectionFactory) redisTemplate.getConnectionFactory();
        lettuceConnectionFactory.setDatabase(i);
        redisTemplate.setConnectionFactory(lettuceConnectionFactory);
        lettuceConnectionFactory.resetConnection();
        lettuceConnectionFactory.afterPropertiesSet();

    }

    public Boolean getLock(String key, String value, Long ms) {
        long startTime = System.currentTimeMillis();
        while (true) {
            Boolean flag = redisTemplate.opsForValue().setIfAbsent(key, value, ms, TimeUnit.MILLISECONDS);
            if (flag) {
                return true;
            }

            //避免一直无限获取锁
            if (System.currentTimeMillis() - startTime > timeout) {
                return false;
            }

            try {
                System.out.print("{}重试锁"+  key);
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 解锁
     * @param key
     * @param value
     * @return
     */
    public Boolean unLock(String key, String value) {
        String script =
                "if redis.call('get',KEYS[1]) == ARGV[1] then" +
                        "   return redis.call('del',KEYS[1]) " +
                        "else" +
                        "   return 0 " +
                        "end";

        // 构造RedisScript并指定返回类类型
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(script, Long.class);
        // 参数一：redisScript，参数二：key列表，参数三：arg（可多个）
        Object result = redisTemplate.execute(redisScript, Arrays.asList(key), value);

        return "1".equals(result.toString());
    }





}