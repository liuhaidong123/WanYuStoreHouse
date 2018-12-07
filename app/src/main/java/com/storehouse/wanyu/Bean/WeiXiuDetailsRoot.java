package com.storehouse.wanyu.Bean;

/**
 * Created by liuhaidong on 2018/9/5.
 */

public class WeiXiuDetailsRoot {
    private WeiIXiuDetailsRows maintenanceLog;

    private String code;

    private String message;

    public WeiIXiuDetailsRows getMaintenanceLog() {
        return maintenanceLog;
    }

    public void setMaintenanceLog(WeiIXiuDetailsRows maintenanceLog) {
        this.maintenanceLog = maintenanceLog;
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
