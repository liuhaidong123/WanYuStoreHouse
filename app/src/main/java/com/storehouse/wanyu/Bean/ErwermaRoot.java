package com.storehouse.wanyu.Bean;

/**
 * Created by liuhaidong on 2018/8/9.
 */

public class ErwermaRoot {
    private String code;

    private AssetQrCode AssetQrCode;

    private String message;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public AssetQrCode getAssetQrCode() {
        return AssetQrCode;
    }

    public void setAssetQrCode(AssetQrCode assetQrCode) {
        AssetQrCode = assetQrCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
