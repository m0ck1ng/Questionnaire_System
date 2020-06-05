package com.questionnaire.demo.service;

import com.questionnaire.demo.model.formtemplate.Form;

import java.util.HashMap;
import java.util.UUID;

public class FormService {
    public String createUUID() {
        String formID = UUID.randomUUID().toString().replace("-", "").toLowerCase();
        return formID;
    }

    public String getFormURL(String form_id)
    {
        String  baseURL = "http://localhost:9526/#/shared/";
        String formURL = baseURL + form_id;
        return formURL;
    }
}
