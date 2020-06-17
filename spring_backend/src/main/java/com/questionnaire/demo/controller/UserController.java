package com.questionnaire.demo.controller;

import com.questionnaire.demo.mapper.UserMapper;
import com.questionnaire.demo.model.Token;
import com.questionnaire.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;

import com.questionnaire.demo.model.User;
@Controller
public class UserController {
    @Autowired
    private UserMapper userMapper;
    private UserService userService = new UserService();

    @CrossOrigin
    @RequestMapping(value = "/api/login", method = RequestMethod.POST)
    @ResponseBody
    HashMap<String, Object> login(@RequestBody User requestUser) {
        HashMap<String, Object> response = new HashMap<>();
        if (userService.login(requestUser, userMapper)) {
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
        if (userMapper.getByUsername(regiUser.getUserName()) != null) {
            response.put("code", 61204);
            response.put("message", "User exists.");
            return response;
        }
        if (userMapper.getByName(regiUser.getName()) != null) {
            response.put("code", 61204);
            response.put("message", "Name exists.");
            return response;
        }
        if (userService.register(regiUser, userMapper)) {
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
        String id = Token.decode(token);
        HashMap<String, Object> data = userService.getUserInfo(id, userMapper);
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

    @CrossOrigin
    @RequestMapping(value = "/api/user/modify", method = RequestMethod.POST)
    @ResponseBody
    public HashMap<String, Object> modify(@RequestBody HashMap<String, String> request) {
        HashMap<String, Object> response = new HashMap<>();
        String token = request.get("token");
        String id = Token.decode(token);
        String origin_password = request.get("origin_passwd");
        String new_password = request.get("new_passwd");
        if (userMapper.getByIdAndPassword(id, origin_password) != null) {
            try {
                userMapper.updatePassword(new_password, id);
                response.put("code", 20000);
                response.put("message", "Password Modify Success");
            }
            catch (Exception e) {
                System.out.println(e);
                response.put("code", 61210);
                response.put("message", "Password Modify Failure");
            }
        }
        else {
            response.put("code", 61210);
            response.put("message", "Password Wrong");
        }
        return response;
    }
}
