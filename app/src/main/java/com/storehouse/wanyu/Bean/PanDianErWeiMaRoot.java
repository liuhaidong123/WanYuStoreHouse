package com.storehouse.wanyu.Bean;

/**
 * Created by liuhaidong on 2018/9/17.
 */

public class PanDianErWeiMaRoot {
    private PanDianErWeiMaInventory inventoryItem;

    private String code;

    private String message;

    public PanDianErWeiMaInventory getInventoryItem() {
        return inventoryItem;
    }

    public void setInventoryItem(PanDianErWeiMaInventory inventoryItem) {
        this.inventoryItem = inventoryItem;
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
