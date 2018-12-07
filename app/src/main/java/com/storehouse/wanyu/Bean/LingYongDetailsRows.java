package com.storehouse.wanyu.Bean;

import java.util.List;

/**
 * Created by liuhaidong on 2018/9/5.
 */

public class LingYongDetailsRows {
    private String departmentName;
    private String documentDate;

    private String createTimeString;

    private String gmtModifiedString;

    private String auditorDateString;

    private String departmentCode;

    private List<LingYongDetailsList> assetList;

    private long documentMaker;

    private String rejectReason;

    private long id;

    private String updateTimeString;

    private long auditor;

    private String documentDateString;

    private String userName;

    private long userId;

    private String gmtCreateString;

    private String recipientsDateString;

    private String comment;

    private long applyStatus;

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

    public String getGmtModifiedString() {
        return gmtModifiedString;
    }

    public void setGmtModifiedString(String gmtModifiedString) {
        this.gmtModifiedString = gmtModifiedString;
    }

    public String getAuditorDateString() {
        return auditorDateString;
    }

    public void setAuditorDateString(String auditorDateString) {
        this.auditorDateString = auditorDateString;
    }

    public String getDepartmentCode() {
        return departmentCode;
    }

    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode;
    }

    public List<LingYongDetailsList> getAssetList() {
        return assetList;
    }

    public void setAssetList(List<LingYongDetailsList> assetList) {
        this.assetList = assetList;
    }

    public long getDocumentMaker() {
        return documentMaker;
    }

    public void setDocumentMaker(long documentMaker) {
        this.documentMaker = documentMaker;
    }

    public String getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUpdateTimeString() {
        return updateTimeString;
    }

    public void setUpdateTimeString(String updateTimeString) {
        this.updateTimeString = updateTimeString;
    }

    public long getAuditor() {
        return auditor;
    }

    public void setAuditor(long auditor) {
        this.auditor = auditor;
    }

    public String getDocumentDateString() {
        return documentDateString;
    }

    public void setDocumentDateString(String documentDateString) {
        this.documentDateString = documentDateString;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getGmtCreateString() {
        return gmtCreateString;
    }

    public void setGmtCreateString(String gmtCreateString) {
        this.gmtCreateString = gmtCreateString;
    }

    public String getRecipientsDateString() {
        return recipientsDateString;
    }

    public void setRecipientsDateString(String recipientsDateString) {
        this.recipientsDateString = recipientsDateString;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public long getApplyStatus() {
        return applyStatus;
    }

    public void setApplyStatus(long applyStatus) {
        this.applyStatus = applyStatus;
    }

    public long getStatus() {
        return status;
    }

    public void setStatus(long status) {
        this.status = status;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }
}
