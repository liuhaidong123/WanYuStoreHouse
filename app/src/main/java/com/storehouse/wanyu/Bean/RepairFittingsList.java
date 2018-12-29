package com.storehouse.wanyu.Bean;

/**
 * Created by liuhaidong on 2018/12/19.
 */

public class RepairFittingsList {
    private String createTimeString;

    private String updateTimeString;

    private int num;

    private String maintenanceLogName;

    private long maintenanceLogId;

    private long assetId;

    private String specTyp;

    private String assetName;

    private long id;

    private String barcode;

    private int status;

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

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getMaintenanceLogName() {
        return maintenanceLogName;
    }

    public void setMaintenanceLogName(String maintenanceLogName) {
        this.maintenanceLogName = maintenanceLogName;
    }

    public long getMaintenanceLogId() {
        return maintenanceLogId;
    }

    public void setMaintenanceLogId(long maintenanceLogId) {
        this.maintenanceLogId = maintenanceLogId;
    }

    public long getAssetId() {
        return assetId;
    }

    public void setAssetId(long assetId) {
        this.assetId = assetId;
    }

    public String getSpecTyp() {
        return specTyp;
    }

    public void setSpecTyp(String specTyp) {
        this.specTyp = specTyp;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
