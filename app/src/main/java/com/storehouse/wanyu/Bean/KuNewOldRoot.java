package com.storehouse.wanyu.Bean;

import java.util.List;

/**
 * Created by liuhaidong on 2018/12/27.
 */

public class KuNewOldRoot {
    private int total;

    private String code;

    private String message;

    private List<KuNewOldRows> rows;

    private List<String> colmodel;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
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

    public List<KuNewOldRows> getRows() {
        return rows;
    }

    public void setRows(List<KuNewOldRows> rows) {
        this.rows = rows;
    }

    public List<String> getColmodel() {
        return colmodel;
    }

    public void setColmodel(List<String> colmodel) {
        this.colmodel = colmodel;
    }
}
