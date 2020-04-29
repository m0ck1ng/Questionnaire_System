package com.questionnaire.demo.controller;

import com.questionnaire.demo.model.Token;
import com.questionnaire.demo.model.User;
import com.questionnaire.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.util.HashMap;

@Controller
public class UserController {
    @Autowired
    RedisTemplate redisTemplate; //默认提供的用来操作对象的redis操作实例
    private UserService userService = new UserService();

    @CrossOrigin
    @RequestMapping(value = "/api/login", method = RequestMethod.POST)
    @ResponseBody
    HashMap<String, Object> login(@RequestBody User requestUser) {
        HashMap<String, Object> response = new HashMap<>();
        if (userService.login(requestUser, redisTemplate)) {
            HashMap<String, String> data = new HashMap<>();
            data.put("token", Token.buildToken(requestUser.getID()));
            response.put("code", 20000);
            response.put("message", "Login Success.");
            response.put("data", data);
        } else {
            response.put("code", 60204);
            response.put("message", "Account or password Incorrect.");
        }
        return response;
    }

    @CrossOrigin
    @RequestMapping(value = "/api/register", method = RequestMethod.POST)
    @ResponseBody
    public HashMap<String, Object> register(@RequestBody User regiUser) {
        HashMap<String, Object> response = new HashMap<>();
        if (redisTemplate.hasKey(regiUser.getUserName())) {
            response.put("code", 61204);
            response.put("message", "User exists.");
            return response;
        }
        if (userService.register(regiUser, redisTemplate)) {
            HashMap<String, String> data = new HashMap<>();
            data.put("token", Token.buildToken(regiUser.getID()));
            response.put("code", 20000);
            response.put("message", "Register Success.");
            response.put("data", data);
        } else {
            response.put("code", 61205);
            response.put("message", "Register Fail.");
        }
        return response;
    }

    @CrossOrigin
    @RequestMapping(value = "/api/user/info", method = RequestMethod.GET)
    @ResponseBody
    public HashMap<String, Object> getUserInfo(@RequestParam("token") String token){
        HashMap<String, Object> response = new HashMap<>();
        HashMap<String, Object> data = userService.getUserInfo("", redisTemplate);
        if (true)
        {
            response.put("code",20000);
            response.put("data",data);
        }
        else {
            response.put("code",50008);
            response.put("message", "Login failed, unable to get user details.");
        }
        return response;
    }

    @CrossOrigin
    @RequestMapping(value = "/api/user/logout")
    @ResponseBody
    public HashMap<String, Object> logout() {
        HashMap<String, Object> response = new HashMap<>();
        response.put("code",20000);
        response.put("data","success");
        return response;
    }
}
