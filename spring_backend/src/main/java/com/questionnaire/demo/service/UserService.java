package com.questionnaire.demo.service;

import com.questionnaire.demo.model.User;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Random;

public class UserService {
    public HashMap<String, String> login(User requestUser, RedisTemplate redisTemplate)
    {
//        HashOperations ops = redisTemplate.opsForHash();
//        Object res = ops.get(requestUser.getName(),"password");
//        if (res == null || !res.equals(requestUser.getPassswd()))
//            return null;
//        return true;
        if (requestUser.getName().equals("578001344@qq.com") && requestUser.getPassswd().equals("admin"))
        {
            HashMap<String, String> ret = new HashMap<String, String>();
            ret.put("username","57800134@qq.com");
            ret.put("name","stitch");
            ret.put("uuid","admin-uuid");
            return ret;
        }
        return null;
    }

    public HashMap<String, String> register(User regiUser, RedisTemplate redisTemplate) {
        try {
            String id = generateID();
            HashOperations ops = redisTemplate.opsForHash();
            ops.put(regiUser.getUserName(), "password", regiUser.getPassswd());
            ops.put(regiUser.getUserName(), "userid", id);
            ops.put(regiUser.getUserName(), "name", regiUser.getName());
            HashMap<String, String> data = new HashMap<>();
            data.put("username", regiUser.getUserName());
            data.put("uuid",regiUser.getID());
            data.put("name", regiUser.getName());
            return data;
        }
        catch (Error e) {
            return null;
        }
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
