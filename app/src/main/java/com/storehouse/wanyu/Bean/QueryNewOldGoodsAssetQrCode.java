package com.storehouse.wanyu.Bean;

/**
 * Created by liuhaidong on 2019/1/7.
 */

public class QueryNewOldGoodsAssetQrCode {
    private int isUse;

    private String useTimeString;

    private String createTimeString;

    private String updateTimeString;

    private int isPrint;

    private long assetId;

    private String assetName;

    private long id;

    private String printTimeString;

    private String barcode;

    private int status;

    public int getIsUse() {
        return isUse;
    }

    public void setIsUse(int isUse) {
        this.isUse = isUse;
    }

    public String getUseTimeString() {
        return useTimeString;
    }

    public void setUseTimeString(String useTimeString) {
        this.useTimeString = useTimeString;
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

    public int getIsPrint() {
        return isPrint;
    }

    public void setIsPrint(int isPrint) {
        this.isPrint = isPrint;
    }

    public long getAssetId() {
        return assetId;
    }

    public void setAssetId(long assetId) {
        this.assetId = assetId;
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

    public String getPrintTimeString() {
        return printTimeString;
    }

    public void setPrintTimeString(String printTimeString) {
        this.printTimeString = printTimeString;
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
