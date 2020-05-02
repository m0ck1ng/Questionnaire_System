package com.questionnaire.demo.controller;

import com.alibaba.fastjson.JSON;
import com.questionnaire.demo.mapper.FormMapper;
import com.questionnaire.demo.model.Token;
import com.questionnaire.demo.model.formtemplate.Form;
import com.questionnaire.demo.service.FormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class FormController {
    @Autowired
    private FormMapper formMappper;
    private FormService formService = new FormService();

    @CrossOrigin
    @RequestMapping(value = "api/form/new", method = RequestMethod.POST)
    @ResponseBody
    public HashMap<String, Object> create(@RequestBody HashMap<String, Object> request) {
        HashMap<String, Object> response = new HashMap<String, Object>();
        try {
            String token = request.get("token").toString();
            String id = Token.decode(token);
            if (id == null) {
                response.put("code",50008);
                response.put("message","Illegal Token");
            }
            String formBody = JSON.toJSONString(request.get("form"));
            String due_time = request.get("due_time").toString();
            Boolean isRelease = (Boolean) request.get("isRelease");
            String state = isRelease ? "released" : "saved";
            String uuid = formService.createUUID();
            formMappper.insert(uuid, id,due_time, state, formBody);

            response.put("code", 20000);
            response.put("message", "问卷创建成功");
            response.put("url", formService.getFormURL(uuid));
        } catch (Exception e) {
            e.printStackTrace();
            response.put("code", 70225);
            response.put("message", "问卷创建失败");
        }
        return response;
    }

    @CrossOrigin
    @RequestMapping(value = "api/f", method = RequestMethod.GET)
    @ResponseBody
    public HashMap<String, Object> getForm(@RequestParam("id") String formID) {
        HashMap<String, Object> response = new HashMap<String, Object>();
        String form_in_Json = formMappper.getBodyByQID(formID);
        if (form_in_Json != null && !form_in_Json.equals("")) {
            response.put("form", JSON.parseObject(form_in_Json, Form.class));
            response.put("code", 20000);
        } else {
            response.put("code", 71225);
            response.put("message", "问卷不存在");
        }
        return response;
    }

    @CrossOrigin
    @RequestMapping(value = "api/form/all", method = RequestMethod.GET)
    @ResponseBody
    public HashMap<String, Object> getAllFormsInfo(@RequestParam("token") String token) {
        HashMap<String, Object> response = new HashMap<String, Object>();
        String id = Token.decode(token);
        if (id == null)
        {
            response.put("code", 72225);
            response.put("message", "Illegal Token");
            return response;
        }
        List<Map<String, String>> forms = formMappper.getByID(id);
        ArrayList<HashMap<String, String>> data = new ArrayList<>();
        for (int i = 0; i< forms.size();i++) {
            HashMap<String, String> item = new HashMap<>();
            item.put("due_time", forms.get(i).get("due_time"));
            item.put("state", forms.get(i).get("state"));
            Form one = JSON.parseObject(forms.get(i).get("body"), Form.class);
            item.put("title", one.title);
            item.put("desc", one.desc);
            data.add(item);
        }
        response.put("code", 20000);
        response.put("data", data);
        return response;
    }
}
