package com.questionnaire.demo.service;

import com.questionnaire.demo.model.formtemplate.Form;
import java.util.UUID;

public class FormService {
    public String addForm(Form form) {
        String formID = UUID.randomUUID().toString().replace("-", "").toLowerCase();
        String formURL = getFormURL(formID);
        return formURL;
    }

    public String getFormURL(String form_id)
    {
        String  baseURL = "http://localhost:9526/#/f/";
        String formURL = baseURL + form_id;
        return formURL;
    }
}
