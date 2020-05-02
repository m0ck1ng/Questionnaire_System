package com.questionnaire.demo.service;

import com.questionnaire.demo.mapper.UserMapper;
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
    public boolean login(User requestUser, UserMapper usermapper)
    {
        User user = usermapper.getByUsernameAndPassword(requestUser.getUserName(), requestUser.getPassswd());
        if (user == null)
            return false;
        requestUser.setID(user.getID());
        return true;
        // for test
//        if (requestUser.getUserName().equals("admin@qq.com") && requestUser.getPassswd().equals("123456")) {
//            requestUser.setID("20200421190249XdOAMK");
//            return true;
//        }
 //       return false;
    }

    public boolean register(User regiUser, UserMapper usermapper) {
        try {
            String id = generateID();
            regiUser.setID(id);
            usermapper.insert(regiUser);
            return true;
        }
        catch (Error e) {
            return false;
        }
    }

    public HashMap<String, Object> getUserInfo(String id, UserMapper userMapper)
    {
        HashMap<String, Object> data = new HashMap<>();
        User user = userMapper.getById(id);
        data.put("roles", Arrays.asList("admin"));
        data.put("avatar","https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif");
        data.put("name", user.getName());
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
