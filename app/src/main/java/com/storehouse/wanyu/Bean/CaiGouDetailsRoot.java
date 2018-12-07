package com.storehouse.wanyu.Bean;

/**
 * Created by liuhaidong on 2018/9/4.
 */

public class CaiGouDetailsRoot {
    private CaiGouDetailsRows buyApply;

    private String code;

    private String message;

    public void setBuyApply(CaiGouDetailsRows buyApply) {
        this.buyApply = buyApply;
    }

    public CaiGouDetailsRows getBuyApply() {
        return this.buyApply;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
