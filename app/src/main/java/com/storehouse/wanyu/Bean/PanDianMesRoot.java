package com.storehouse.wanyu.Bean;

import java.util.List;

/**
 * Created by liuhaidong on 2018/9/14.
 */

public class PanDianMesRoot {
    private String code;

    private String message;

    private PanDianMesInventory inventory;

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

    public PanDianMesInventory getInventory() {
        return inventory;
    }

    public void setInventory(PanDianMesInventory inventory) {
        this.inventory = inventory;
    }
}
