package com.storehouse.wanyu.Bean;

import java.util.List;

/**
 * Created by liuhaidong on 2018/12/19.
 */

public class RepairMessRoot {
    private RepairMaintenanceLog maintenanceLog;

    private List<RepairFittingsList> fittingsList;

    private String code;

    private String message;

    public RepairMaintenanceLog getMaintenanceLog() {
        return maintenanceLog;
    }

    public void setMaintenanceLog(RepairMaintenanceLog maintenanceLog) {
        this.maintenanceLog = maintenanceLog;
    }

    public List<RepairFittingsList> getFittingsList() {
        return fittingsList;
    }

    public void setFittingsList(List<RepairFittingsList> fittingsList) {
        this.fittingsList = fittingsList;
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
