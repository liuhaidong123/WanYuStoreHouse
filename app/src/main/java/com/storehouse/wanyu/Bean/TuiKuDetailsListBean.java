package com.storehouse.wanyu.Bean;

/**
 * Created by liuhaidong on 2018/9/6.
 */

public class TuiKuDetailsListBean {

    //申请数量
    private Integer totalNum;
    //实际更换的数量
    private Integer num;

    public Integer getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(Integer totalNum) {
        this.totalNum = totalNum;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    private String createTimeString;

    private String updateTimeString;

    private String documentDateString;

    private String returnName;

    private String categoryCode;

    private String categoryName;

    private long documentMaker;

    private String assetsName;

    private long returnId;

    private String recipientsDateString;

    private long id;

    private long assetsId;

    private long status;


    public String getCreateTimeString() {
        return createTimeString;
    }

    public void setCreateTimeString(String createTimeString) {
        this.createTimeString = createTimeString;
    }

    public String getUpdateTimeString() {
        return updateTimeString;
    }

    public void setUpdateTimeString(String updateTimeString) {
        this.updateTimeString = updateTimeString;
    }

    public String getDocumentDateString() {
        return documentDateString;
    }

    public void setDocumentDateString(String documentDateString) {
        this.documentDateString = documentDateString;
    }

    public String getReturnName() {
        return returnName;
    }

    public void setReturnName(String returnName) {
        this.returnName = returnName;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public long getDocumentMaker() {
        return documentMaker;
    }

    public void setDocumentMaker(long documentMaker) {
        this.documentMaker = documentMaker;
    }

    public String getAssetsName() {
        return assetsName;
    }

    public void setAssetsName(String assetsName) {
        this.assetsName = assetsName;
    }

    public long getReturnId() {
        return returnId;
    }

    public void setReturnId(long returnId) {
        this.returnId = returnId;
    }

    public String getRecipientsDateString() {
        return recipientsDateString;
    }

    public void setRecipientsDateString(String recipientsDateString) {
        this.recipientsDateString = recipientsDateString;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getAssetsId() {
        return assetsId;
    }

    public void setAssetsId(long assetsId) {
        this.assetsId = assetsId;
    }

    public long getStatus() {
        return status;
    }

    public void setStatus(long status) {
        this.status = status;
    }
}
