package com.storehouse.wanyu.Bean;

/**
 * Created by liuhaidong on 2018/9/6.
 */

public class TuiKuDetailsRoot {
    private TuiKuDetailsRows assetReturn;

    private String code;

    private String message;

    public TuiKuDetailsRows getAssetReturn() {
        return assetReturn;
    }

    public void setAssetReturn(TuiKuDetailsRows assetReturn) {
        this.assetReturn = assetReturn;
    }

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
}
