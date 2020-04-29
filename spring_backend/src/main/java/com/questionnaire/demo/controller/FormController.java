package com.questionnaire.demo.controller;

import com.alibaba.fastjson.JSON;
import com.questionnaire.demo.model.formtemplate.Form;
import com.questionnaire.demo.service.FormService;
import com.questionnaire.demo.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@Controller
public class FormController {
    private FormService formService = new FormService();
    Form f = null;
    @CrossOrigin
    @RequestMapping(value = "api/form", method= RequestMethod.POST)
    @ResponseBody
    public HashMap<String, Object> create(@RequestBody HashMap<String, Object> request) {
        HashMap<String, Object> response = new HashMap<String, Object>();
        try {
            Form form = JSON.parseObject(JSON.toJSONString(request.get("form")) , Form.class);
            f = form;
            Boolean isRelease = (Boolean) request.get("isRelease");
            response.put("code", 20000);
            response.put("message", "问卷创建成功");
            String formURL = formService.addForm(form);
            response.put("url", formURL);
        } catch (Exception e) {
            response.put("code", 70225);
            response.put("message", "问卷创建失败");
        }
        return response;
    }

    @CrossOrigin
    @RequestMapping(value = "api/f", method= RequestMethod.GET)
    @ResponseBody
    public HashMap<String, Object> query(@RequestParam("id") String formID)
    {
        HashMap<String, Object> response = new HashMap<String, Object>();
        response.put("form", f);
        response.put("code", 20000);
        return response;
    }

}
