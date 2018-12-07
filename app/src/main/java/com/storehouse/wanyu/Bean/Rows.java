package com.storehouse.wanyu.Bean;

/**
 * Created by liuhaidong on 2018/8/3.
 */

public class Rows {
    private String departmentName;

    private String createTimeString;

    private String updateTimeString;

    private long level;

    private long orderId;

    private String departmentCode;

    private long parentId;

    private long id;

    private long status;

    public void setDepartmentName(String departmentName){
        this.departmentName = departmentName;
    }
    public String getDepartmentName(){
        return this.departmentName;
    }
    public void setCreateTimeString(String createTimeString){
        this.createTimeString = createTimeString;
    }
    public String getCreateTimeString(){
        return this.createTimeString;
    }
    public void setUpdateTimeString(String updateTimeString){
        this.updateTimeString = updateTimeString;
    }
    public String getUpdateTimeString(){
        return this.updateTimeString;
    }
    public void setLevel(long level){
        this.level = level;
    }
    public long getLevel(){
        return this.level;
    }
    public void setOrderId(long orderId){
        this.orderId = orderId;
    }
    public long getOrderId(){
        return this.orderId;
    }
    public void setDepartmentCode(String departmentCode){
        this.departmentCode = departmentCode;
    }
    public String getDepartmentCode(){
        return this.departmentCode;
    }
    public void setParentId(long parentId){
        this.parentId = parentId;
    }
    public long getParentId(){
        return this.parentId;
    }
    public void setId(long id){
        this.id = id;
    }
    public long getId(){
        return this.id;
    }
    public void setStatus(long status){
        this.status = status;
    }
    public long getStatus(){
        return this.status;
    }
}
