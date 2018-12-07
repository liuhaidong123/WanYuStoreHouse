package com.storehouse.wanyu.Bean;

import java.util.List;

/**
 * Created by liuhaidong on 2018/8/31.
 */

public class OneSelfRoot {
    private int total;

    private String code;

    private String message;

    private List<OneSelfRows> rows;

    public void setTotal(int total){
        this.total = total;
    }
    public int getTotal(){
        return this.total;
    }
    public void setCode(String code){
        this.code = code;
    }
    public String getCode(){
        return this.code;
    }
    public void setMessage(String message){
        this.message = message;
    }
    public String getMessage(){
        return this.message;
    }
    public void setRows(List<OneSelfRows> rows){
        this.rows = rows;
    }
    public List<OneSelfRows> getRows(){
        return this.rows;
    }


}
