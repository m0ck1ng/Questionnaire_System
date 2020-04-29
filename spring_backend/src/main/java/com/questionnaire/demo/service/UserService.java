package com.questionnaire.demo.service;

import com.questionnaire.demo.model.User;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

public class UserService {
    public boolean login(User requestUser, RedisTemplate redisTemplate)
    {
//        HashOperations ops = redisTemplate.opsForHash();
//        String username = requestUser.getUserName();
//        Object passwd = ops.get(username,"password");
//        if (passwd == null || !passwd.equals(requestUser.getPassswd()))
//            return false;
//        requestUser.setID(ops.get(username, "userid").toString());
//        return true;

        // for test
        if (requestUser.getUserName().equals("admin@qq.com") && requestUser.getPassswd().equals("123456")) {
            requestUser.setID("20200421190249XdOAMK");
            return true;
        }
        return false;
    }

    public boolean register(User regiUser, RedisTemplate redisTemplate) {
        try {
            String id = generateID();
            HashOperations ops = redisTemplate.opsForHash();
            ops.put(regiUser.getUserName(), "password", regiUser.getPassswd());
            ops.put(regiUser.getUserName(), "userid", id);
            ops.put(regiUser.getUserName(), "name", regiUser.getName());
            return true;
        }
        catch (Error e) {
            return false;
        }
    }

    public HashMap<String, Object> getUserInfo(String username, RedisTemplate redisTemplate)
    {
//        HashOperations ops = redisTemplate.opsForHash();
        HashMap<String, Object> data = new HashMap<>();
        data.put("roles", Arrays.asList("admin"));
        data.put("avatar","https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif");
//        data.put("name", ops.get(username, "name").toString());
        data.put("name", "stitch");
        data.put("introduction", "I am an happy administrator");
        return data;
    }

    public String generateID(){
        LocalDateTime now = LocalDateTime.now();
        String prefix = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String base = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        String id = prefix+sb.toString();
        return id;
    }
}
