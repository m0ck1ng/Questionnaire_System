package com.questionnaire.demo.controller;

import com.questionnaire.demo.model.Token;
import com.questionnaire.demo.model.User;
import com.questionnaire.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@Controller
public class UserController {
    @Autowired
    RedisTemplate redisTemplate; //默认提供的用来操作对象的redis操作实例
//    @Autowired
//    StringRedisTemplate stringRedisTemplate; //默认提供的用来操作字符串的redis操作实例

    private UserService userService = new UserService();

    @CrossOrigin
    @RequestMapping(value = "/api/login", method = RequestMethod.POST)
    @ResponseBody
    HashMap<String, Object> login(@RequestBody User requestUser) {
        HashMap<String, Object> response = new HashMap<>();
        HashMap<String, String> data = userService.login(requestUser, redisTemplate);
        if (data != null) {
            data.put("token", Token.buildToken(data.get("uuid")));
            response.put("code", 0);
            response.put("msg", "登录成功");
            response.put("data", data);
        } else {
            response.put("code", 401);
            response.put("msg", "账号或密码错误");
        }
        return response;
    }

    @CrossOrigin
    @RequestMapping(value = "/api/register", method = RequestMethod.POST)
    @ResponseBody
    public HashMap<String, Object> register(@RequestBody User regiUser) {
        HashMap<String, Object> response = new HashMap<>();
        if (redisTemplate.hasKey(regiUser.getUserName())) {
            response.put("code", 401);
            response.put("msg", "用户已存在");
            return response;
        }
        HashMap<String, String> data = userService.register(regiUser, redisTemplate);
        if (data != null) {
            data.put("token", Token.buildToken(regiUser.getID()));
            response.put("code", 0);
            response.put("msg", "注册成功");
            response.put("data", data);
        } else {
            response.put("code", 401);
            response.put("message", "注册失败");
        }
        return response;
    }
}
