package com.questionnaire.demo.model;

public class User {
    private String id;
    private String username;
    private String password;
    private String name;

    public void setID(String id){
        this.id = id;
    }
    public void setUsername(String username)
    {
        this.username = username;
    }
    public void setPassword(String password)
    {
        this.password = password;
    }
    public void setName(String name) {this.name = name;}
    
    public String getID()
    {
        return id;
    }
    public String getUserName()
    {
        return username;
    }
    public String getPassswd()
    {
        return password;
    }
    public String getName() {return name;}
}
