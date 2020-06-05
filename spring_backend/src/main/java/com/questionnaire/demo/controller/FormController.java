package com.questionnaire.demo.controller;

import com.alibaba.fastjson.JSON;
import com.questionnaire.demo.mapper.FormMapper;
import com.questionnaire.demo.mapper.ReplyMapper;
import com.questionnaire.demo.model.Token;
import com.questionnaire.demo.model.formtemplate.Form;
import com.questionnaire.demo.service.FormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class FormController {
    @Autowired
    private FormMapper formMapper;
    @Autowired
    private ReplyMapper replyMapper;
    private FormService formService = new FormService();

    @CrossOrigin
    @RequestMapping(value = "api/forms", method = RequestMethod.POST)
    @ResponseBody
    public HashMap<String, Object> create(@RequestBody HashMap<String, Object> request) {
        HashMap<String, Object> response = new HashMap<String, Object>();
        try {
            String token = request.get("token").toString();
            String id = Token.decode(token);
            if (id == null) {
                response.put("code", 50008);
                response.put("message", "Illegal Token");
            }
            String formBody = JSON.toJSONString(request.get("form"));
            String due_time = request.get("due_time").toString();
            String state = request.get("state").toString();
            if (!state.equals("SAVED") && !state.equals("REONLY")) {
                Integer max_time = Integer.parseInt(request.get("max_time").toString()) ;
                state = state+','+max_time.toString();
            }
            String qid = formService.createUUID();
            formMapper.insert(qid, id, due_time, state, formBody);

            response.put("code", 20000);
            response.put("message", "问卷创建成功");
            response.put("url", formService.getFormURL(qid));
        } catch (Exception e) {
            e.printStackTrace();
            response.put("code", 70225);
            response.put("message", "问卷创建失败");
        }
        return response;
    }

    @CrossOrigin
    @RequestMapping(value = "api/forms", method = RequestMethod.GET)
    @ResponseBody
    public HashMap<String, Object> getAllFormsInfo(@RequestParam("token") String token) {
        HashMap<String, Object> response = new HashMap<String, Object>();
        String id = Token.decode(token);
        if (id == null) {
            response.put("code", 72225);
            response.put("message", "Illegal Token");
            return response;
        }
        List<Map<String, Object>> forms = formMapper.getFormsByID(id);
        ArrayList<HashMap<String, Object>> data = new ArrayList<>();
        for (int i = 0; i < forms.size(); i++) {
            HashMap<String, Object> item = new HashMap<>();
            DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            item.put("due_time", sdf.format(forms.get(i).get("due_time")));
            String state = forms.get(i).get("state").toString();
            if (state.equals("SAVED")) state = "未发布";
            else if (state.equals("EXPIRED")) state = "已过期";
            else state = "已发布";
            item.put("state", state);
            item.put("qid", forms.get(i).get("qid"));
            Form one = JSON.parseObject(forms.get(i).get("body").toString(), Form.class);
            item.put("title", one.title);
            item.put("desc", one.desc);
            data.add(item);
        }
        response.put("code", 20000);
        response.put("data", data);
        return response;
    }

    @CrossOrigin
    @RequestMapping(value = "api/forms", method = RequestMethod.DELETE)
    @ResponseBody
    public HashMap<String, Object> deleteForm(@RequestParam("qid") String qid, @RequestParam("token") String token) {
        HashMap<String, Object> response = new HashMap<String, Object>();
        String id = Token.decode(token);
        try {
            formMapper.deleteForm(qid, id);
            replyMapper.deleteByQid(qid);
            response.put("code", 20000);
            response.put("message", "删除成功");
        } catch (Exception e) {
            response.put("code", 73225);
            response.put("message", "问卷删除失败");
        }
        return response;
    }

    @CrossOrigin
    @RequestMapping(value = "api/forms", method = RequestMethod.PATCH)
    @ResponseBody
    public HashMap<String, Object> releaseForm(@RequestParam("qid") String qid,
                                               @RequestParam("token") String token,
                                               @RequestParam("state") String state,
                                               @RequestParam("max_time") String max_time) {
        HashMap<String, Object> response = new HashMap<String, Object>();
        String id = Token.decode(token);
        if (state.equals("NMOST") || state.equals("DMOST"))
            state = state+","+max_time;
        try {
            formMapper.updateState(qid, state, id);
            response.put("code", 20000);
            response.put("message", "发布成功");
        } catch (Exception e) {
            response.put("code", 74225);
            response.put("message", "问卷发布失败");
        }
        return response;
    }

    @CrossOrigin
    @RequestMapping(value = "api/open/forms", method = RequestMethod.GET)
    @ResponseBody
    public HashMap<String, Object> getForm(HttpServletRequest request, @RequestParam("id") String formID) {
        HashMap<String, Object> response = new HashMap<String, Object>();
        String state = formMapper.getStateByQID(formID);
        if (state == null) {
            response.put("code", 71225);
            response.put("message", "问卷不存在");
            return response;
        }
        String token = request.getHeader("X-Token");
        if (state.equals("REONLY") && ((token == null ) || !Token.verify(token)))
        {
            response.put("code",71215);
            response.put("message", "请登录后回答");
            return response;
        }
        String form_in_Json = formMapper.getBodyByQID(formID);
        if (form_in_Json != null && !form_in_Json.equals("")) {
            response.put("form", JSON.parseObject(form_in_Json, Form.class));
            response.put("code", 20000);
        }
        else {
            response.put("code",73215);
            response.put("message", "问卷出现错误");
        }
        return response;
    }
}
