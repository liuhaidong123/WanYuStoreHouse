package com.storehouse.wanyu.Bean;

/**
 * Created by liuhaidong on 2018/9/5.
 */

public class LingYongDetailsList {
    private int  totalNum;     //总数量
    private int  num  ; //库存数量

    public int getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(int totalNum) {
        this.totalNum = totalNum;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    private String categoryName;
    private String documentDate;

    private String createTimeString;

    private String updateTimeString;

    private String documentDateString;

    private long recipientsId;

    private long documentMaker;

    private String assetsName;

    private String recipientsDateString;

    private long id;

    private String recipientsName;

    private long assetsId;

    private long status;


    public String getDocumentDate() {
        return documentDate;
    }

    public void setDocumentDate(String documentDate) {
        this.documentDate = documentDate;
    }

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

    public long getRecipientsId() {
        return recipientsId;
    }

    public void setRecipientsId(long recipientsId) {
        this.recipientsId = recipientsId;
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

    public String getRecipientsName() {
        return recipientsName;
    }

    public void setRecipientsName(String recipientsName) {
        this.recipientsName = recipientsName;
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

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
