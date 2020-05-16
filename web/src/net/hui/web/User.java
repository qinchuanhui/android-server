package net.hui.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {


    public Map<String, Double> info= new HashMap<>();


    public List<Map<String,String>> bs =new ArrayList<>();

    //用户姓名
    private String userName;

    //用户密码
    private String password;

    private String adr_id;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAdr_id(String adr_id){
        this.adr_id=adr_id;
    }

    public String getAdr_id(){
        return adr_id;
    }


}

