package com.questionnaire.demo.controller;

import com.alibaba.fastjson.JSON;
import com.questionnaire.demo.mapper.FormMapper;
import com.questionnaire.demo.mapper.ReplyMapper;
import com.questionnaire.demo.model.Token;
import com.questionnaire.demo.model.formtemplate.Form;
import com.questionnaire.demo.service.ReplyService;
import org.apache.poi.hssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Controller
public class ReplyController {
    @Autowired
    private FormMapper formMapper;
    @Autowired
    private ReplyMapper replyMapper;
    private ReplyService replyService = new ReplyService();

    @CrossOrigin
    @RequestMapping(value = "api/open/replys", method = RequestMethod.POST)
    @ResponseBody
    public HashMap<String, Object> addReply(HttpServletRequest request, @RequestBody HashMap<String, Object> body) {
        HashMap<String, Object> response = new HashMap<String, Object>();
        String qid = body.get("qid").toString();
        String state = formMapper.getStateByQID(qid);
        if (state == null) {
            response.put("code", 71225);
            response.put("message", "问卷不存在");
            return response;
        }
        String token = request.getHeader("X-Token");
        if (state.equals("REONLY") && ((token == null) || !Token.verify(token))) {
            response.put("code", 71215);
            response.put("message", "请在登录后回答");
            return response;
        }
        String due_time = formMapper.getDuetimeByQID(qid);
        String submit_time = body.get("time").toString();
        if (submit_time.compareTo(due_time) > 0) {
            response.put("code", "78215");
            response.put("message", "Questionnaire Expired");
            return response;
        }
        String ip = request.getRemoteAddr();
        if (state.startsWith("DMOST") || state.startsWith("NMOST")) {
            int max_time = Integer.parseInt(state.substring(6));
            int submit;
            if (state.startsWith("DMOST")) {
                submit = replyMapper.countByDay(ip, qid, submit_time);
            } else {
                submit = replyMapper.countByAll(ip, qid);
            }
            if (submit >= max_time) {
                response.put("code", "77215");
                response.put("message", "You submission reach limit");
                return response;
            }
        }

        String answers = body.get("ans").toString();

        if (replyService.addReply(replyMapper, qid, ip, answers, submit_time)) {
            response.put("code", 20000);
            response.put("message", "问卷提交成功");
        } else {
            response.put("code", 75215);
            response.put("message", "问卷提交失败");
        }
        return response;
    }

    @CrossOrigin
    @RequestMapping(value = "api/replys", method = RequestMethod.GET)
    @ResponseBody
    public  HashMap<String, Object> getReplys(HttpServletRequest request,
                                              @RequestParam("token") String token,
                                              @RequestParam("qid") String qid) {
        HashMap<String, Object> response = new HashMap<>();
        String id = Token.decode(token);
        if (id == null || !formMapper.isQuesExist(id, qid)) {
            response.put("code", 79215);
            response.put("message", "Permission Forbidden");
            return response;
        }
        ArrayList<HashMap<String,Object>> reply = replyMapper.getInfoByQid(qid);
        String formBody_str = formMapper.getBodyByQID(qid);
        Form form = JSON.parseObject(formBody_str, Form.class);
        HashMap<Integer, HashMap<String, Integer>> statis = replyService.countResults(form, reply);
        ArrayList<ArrayList<String>> datalist = replyService.formatReply(form, reply);
        ArrayList<String> descs = new ArrayList<>();
        ArrayList<String> types = new ArrayList<>();
        for (int i = 0; i < form.Qslist.size();i++) {
            descs.add(form.Qslist.get(i).desc);
            types.add(form.Qslist.get(i).type);
        }
        response.put("code",20000);
        response.put("desc", descs);
        response.put("title", form.title);
        response.put("types", types);
        response.put("statis", statis);
        response.put("datatable", datalist);
        return  response;
    }

    @CrossOrigin
    @RequestMapping(value = "api/replys/download", method = RequestMethod.GET)
    public void downloadReply(HttpServletRequest request,
                              HttpServletResponse response,
                              @RequestParam("token") String token,
                              @RequestParam("qid") String qid) throws IOException {
        String id = Token.decode(token);
        if (id == null || !formMapper.isQuesExist(id, qid)) {
            HashMap<String, Object> data = new HashMap<>();
            data.put("code", 79215);
            data.put("message", "Permission Forbidden");
            response.getWriter().write(data.toString());
            return;
        }
        ArrayList<HashMap<String,Object>> reply = replyMapper.getInfoByQid(qid);
        String formBody_str = formMapper.getBodyByQID(qid);
        Form form = JSON.parseObject(formBody_str, Form.class);
        ArrayList<ArrayList<String>> datalist = replyService.formatReply(form, reply);

        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet();
        sheet.setDefaultColumnWidth((short) 18);

        ArrayList<String> headers = new ArrayList<>();
        for (int i = 0;i < form.Qslist.size();i++)
            headers.add("问题" + (i + 1) + ":" + form.Qslist.get(i).desc);
        headers.add("提交时间");
        headers.add("IP");

        HSSFRow row = sheet.createRow(0);
        for (int i = 0;i < headers.size();i++) {
            HSSFCell cell = row.createCell(i);
            HSSFRichTextString text = new HSSFRichTextString(headers.get(i));
            cell.setCellValue(text);
        }

        for (int i = 0;i < datalist.size();i++) {
            row = sheet.createRow(i+1);
            ArrayList<String> item = datalist.get(i);
            for (int j = 0;j < item.size();j++) {
                HSSFCell cell = row.createCell(j);
                HSSFRichTextString text = new HSSFRichTextString(item.get(j));
                cell.setCellValue(text);
            }
        }

        response.setContentType("application/octet-stream");
        response.setHeader("Content-disposition", "attachment;filename="+"statistics.xls");
        try {
            response.flushBuffer();
        } catch (IOException e) {
            HashMap<String, Object> data = new HashMap<>();
            data.put("code","78125");
            data.put("message","Report Export Failure");
            response.getWriter().write(data.toString());
            e.printStackTrace();
            return;
        }
        workbook.write(response.getOutputStream());
    }
}
