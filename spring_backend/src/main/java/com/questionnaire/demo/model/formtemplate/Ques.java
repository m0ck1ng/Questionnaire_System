package com.questionnaire.demo.model.formtemplate;

import java.util.ArrayList;

public class Ques {
    public String desc;
    public String type;
    public boolean required;
    public Options options;
    int key;
    class pre {
        String key;
        String type;
        String val;
        ArrayList<Integer> options;
    };
    String next;
}
