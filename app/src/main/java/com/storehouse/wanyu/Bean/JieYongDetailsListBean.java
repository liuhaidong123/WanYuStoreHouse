package com.storehouse.wanyu.Bean;

/**
 * Created by liuhaidong on 2018/9/5.
 */

public class JieYongDetailsListBean {

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
    private String borrowDateString;

    private String documentDate;

    private String createTimeString;

    private String willReturnDateString;

    private String categoryName;

    private String returnUserName;

    private String borrowName;

    private long documentMaker;

    private String assetsName;

    private long id;

    private String returnDateString;

    private String updateTimeString;

    private String documentDateString;

    private String categoryCode;

    private long returnUserId;

    private long borrowId;

    private long assetsId;

    private long status;

    public String getBorrowDateString() {
        return borrowDateString;
    }

    public void setBorrowDateString(String borrowDateString) {
        this.borrowDateString = borrowDateString;
    }

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

    public String getWillReturnDateString() {
        return willReturnDateString;
    }

    public void setWillReturnDateString(String willReturnDateString) {
        this.willReturnDateString = willReturnDateString;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getReturnUserName() {
        return returnUserName;
    }

    public void setReturnUserName(String returnUserName) {
        this.returnUserName = returnUserName;
    }

    public String getBorrowName() {
        return borrowName;
    }

    public void setBorrowName(String borrowName) {
        this.borrowName = borrowName;
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getReturnDateString() {
        return returnDateString;
    }

    public void setReturnDateString(String returnDateString) {
        this.returnDateString = returnDateString;
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

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public long getReturnUserId() {
        return returnUserId;
    }

    public void setReturnUserId(long returnUserId) {
        this.returnUserId = returnUserId;
    }

    public long getBorrowId() {
        return borrowId;
    }

    public void setBorrowId(long borrowId) {
        this.borrowId = borrowId;
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
