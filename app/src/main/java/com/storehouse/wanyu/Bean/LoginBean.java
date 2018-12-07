package com.storehouse.wanyu.Bean;

/**
 * Created by liuhaidong on 2018/8/2.
 */

public class LoginBean {

    private String code;

    private User User;

    private String message;

    public void setCode(String code){
        this.code = code;
    }
    public String getCode(){
        return this.code;
    }
    public void setUser(User User){
        this.User = User;
    }
    public User getUser(){
        return this.User;
    }
    public void setMessage(String message){
        this.message = message;
    }
    public String getMessage(){
        return this.message;
    }
}
