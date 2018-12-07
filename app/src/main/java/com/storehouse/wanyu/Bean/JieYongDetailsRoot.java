package com.storehouse.wanyu.Bean;

/**
 * Created by liuhaidong on 2018/9/5.
 */

public class JieYongDetailsRoot {
    private String code;

    private JieYongDetailsRows assetBorrow;

    private String message;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public JieYongDetailsRows getAssetBorrow() {
        return assetBorrow;
    }

    public void setAssetBorrow(JieYongDetailsRows assetBorrow) {
        this.assetBorrow = assetBorrow;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
