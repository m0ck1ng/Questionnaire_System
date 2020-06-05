package com.questionnaire.demo.service;

import com.alibaba.fastjson.JSON;
import com.questionnaire.demo.mapper.ReplyMapper;
import com.questionnaire.demo.model.formtemplate.Form;
import com.questionnaire.demo.model.formtemplate.Ques;

import java.util.*;

public class ReplyService {
    public boolean addReply(ReplyMapper replyMapper, String qid, String ip, String answers, String submit_time) {
        try {
            String rid = createUUID();
            replyMapper.insert(rid, qid, ip, answers, submit_time);
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String createUUID() {
        String formID = UUID.randomUUID().toString().replace("-", "").toLowerCase();
        return formID;
    }

    public HashMap<Integer, HashMap<String, Integer>> countResults(Form form, ArrayList<HashMap<String,Object>> reply) {
        HashMap<Integer, String> key2txt = new HashMap<Integer, String>();
        for (int i = 0;i < form.Qslist.size();i++) {
            Ques q = form.Qslist.get(i);
            if (q.type.equals("radio") || q.type.equals("checkbox")) {
                for (int j = 0;j < q.options.values.size();j++)
                    key2txt.put(q.options.values.get(j).key, q.options.values.get(j).label);
            }
        }
        HashMap<Integer, HashMap<String, Integer>> result = new HashMap<>();
        for (int i = 0;i < form.Qslist.size();i++) {
            String type = form.Qslist.get(i).type;
            if (type.equals("radio") || type.equals("checkbox") || type.equals("rate"))
                result.put(i, new HashMap<String, Integer>());
        }
        for (int i = 0;i < reply.size();i++) {
            String body_str = reply.get(i).get("body").toString();
            body_str = body_str.substring(1, body_str.length()-1);
            List<String> answers = Arrays.asList(body_str.split(", (?![^\\[\\]]*\\])"));
            for (int j = 0;j < answers.size();j++) {
                String type = form.Qslist.get(j).type;
                String value = answers.get(j);
                if (!type.equals("rate") && !type.equals("radio") && !type.equals("checkbox"))
                    continue;
                if (value.equals("null"))
                    value = "[ 未填写 ]";
                HashMap<String, Integer> count = result.get(j);
                if (type.equals("rate")) {
                    if (count.containsKey(value))
                        count.put(value , 1 + count.get(value));
                    else
                        count.put(value, 1);
                }
                else if (type.equals("radio")) {
                    String label = key2txt.get(Integer.parseInt(value));
                    if (count.containsKey(label))
                        count.put(label , 1 + count.get(label));
                    else
                        count.put(label, 1);
                }
                else if (type.equals("checkbox")) {
                    String[] opts = value.substring(1, value.length()-1).split(", ");
                    StringBuilder label = new StringBuilder();
                    for (String opt: opts)
                        label.append(key2txt.get(Integer.parseInt(opt))).append(",");
                    label = new StringBuilder(label.substring(0, label.length() - 1));
                    if (count.containsKey(label.toString()))
                        count.put(label.toString(), 1 + count.get(label.toString()));
                    else
                        count.put(label.toString(), 1);
                }
            }
        }
        return result;
    }

    public ArrayList<ArrayList<String>> formatReply(Form form, ArrayList<HashMap<String,Object>> reply) {
        ArrayList<ArrayList<String>> ret = new ArrayList<>();
        HashMap<Integer, String> key2txt = new HashMap<Integer, String>();
        for (int i = 0;i < form.Qslist.size();i++) {
            Ques q = form.Qslist.get(i);
            if (q.type.equals("radio") || q.type.equals("checkbox")) {
                for (int j = 0;j < q.options.values.size();j++)
                    key2txt.put(q.options.values.get(j).key, q.options.values.get(j).label);
            }
        }
        for (int i = 0;i < reply.size();i++) {
            String body_str = reply.get(i).get("body").toString();
            body_str = body_str.substring(1, body_str.length()-1);
            List<String> answers = Arrays.asList(body_str.split(", (?![^\\[\\]]*])"));
            ArrayList<String> item = new ArrayList<>();
            for (int j = 0;j < answers.size();j++) {
                String type = form.Qslist.get(j).type;
                String value = answers.get(j);
                if (value.equals("null"))
                    value = "[ 未填写 ]";
                if (type.equals("radio")) {
                    String label = key2txt.get(Integer.parseInt(value));
                    item.add(label);
                }
                else if (type.equals("checkbox")) {
                    String[] opts = value.substring(1, value.length()-1).split(", ");
                    StringBuilder label = new StringBuilder();
                    for (String opt: opts)
                        label.append(key2txt.get(Integer.parseInt(opt))).append(",");
                    label = new StringBuilder(label.substring(0, label.length() - 1));
                    item.add(label.toString());
                }
                else
                    item.add(value);
            }
            item.add(reply.get(i).get("submit_time").toString());
            item.add(reply.get(i).get("ip").toString());
            ret.add(item);
        }
        return ret;
    }
}
