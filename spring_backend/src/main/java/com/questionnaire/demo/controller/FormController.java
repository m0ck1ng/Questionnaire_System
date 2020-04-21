package com.questionnaire.demo.controller;

import com.questionnaire.demo.model.formtemplate.Form;
import com.questionnaire.demo.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class FormController {
    private UserService userService = new UserService();
    @CrossOrigin
    @RequestMapping(value = "api/form", method= RequestMethod.POST)
    @ResponseBody
    public int login(@RequestBody Form form){

        System.out.println("form received");
        return 200;
    }
}
