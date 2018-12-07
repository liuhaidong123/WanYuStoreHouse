package com.storehouse.wanyu.Bean;

/**
 * Created by liuhaidong on 2018/9/5.
 */

public class BaoFeiDetailsRoot {
    private BaoFeiDetailsRows assetScrap;

    private String code;

    private String message;

    public BaoFeiDetailsRows getAssetScrap() {
        return assetScrap;
    }

    public void setAssetScrap(BaoFeiDetailsRows assetScrap) {
        this.assetScrap = assetScrap;
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
