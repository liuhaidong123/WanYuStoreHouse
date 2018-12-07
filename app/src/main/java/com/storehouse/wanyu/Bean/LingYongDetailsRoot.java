package com.storehouse.wanyu.Bean;

/**
 * Created by liuhaidong on 2018/9/5.
 */

public class LingYongDetailsRoot {
    private String code;

    private String message;

    private LingYongDetailsRows assetRecipients;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LingYongDetailsRows getAssetRecipients() {
        return assetRecipients;
    }

    public void setAssetRecipients(LingYongDetailsRows assetRecipients) {
        this.assetRecipients = assetRecipients;
    }
}

