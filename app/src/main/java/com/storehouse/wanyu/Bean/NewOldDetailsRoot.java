package com.storehouse.wanyu.Bean;

/**
 * Created by liuhaidong on 2018/9/5.
 */

public class NewOldDetailsRoot {
    private String code;

    private String message;

    private NewOldDetailsRows assetOldfornew;

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

    public NewOldDetailsRows getAssetOldfornew() {
        return assetOldfornew;
    }

    public void setAssetOldfornew(NewOldDetailsRows assetOldfornew) {
        this.assetOldfornew = assetOldfornew;
    }
}
