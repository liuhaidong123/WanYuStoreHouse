package com.storehouse.wanyu.Bean;

import java.util.List;

/**
 * Created by liuhaidong on 2018/9/18.
 */

public class PanDianStatusRoot {
    private String code;

    private List<PanDianStatusRows> profitLoss;

    private String message;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<PanDianStatusRows> getProfitLoss() {
        return profitLoss;
    }

    public void setProfitLoss(List<PanDianStatusRows> profitLoss) {
        this.profitLoss = profitLoss;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
