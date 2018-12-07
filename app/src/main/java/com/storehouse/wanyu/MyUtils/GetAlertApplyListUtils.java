package com.storehouse.wanyu.MyUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 获取弹框申请类别
 */

public class GetAlertApplyListUtils {

    private static List<String> mAlertList = new ArrayList<>();

    public static List<String> getAlertList() {
        mAlertList.clear();
        mAlertList.add("采购申请");
        mAlertList.add("领用申请");
        mAlertList.add("借用申请");
        mAlertList.add("维修申请");
        mAlertList.add("以旧换新");
        mAlertList.add("报废申请");
        //mAlertList.add("归还申请");
        mAlertList.add("退库申请");
        return mAlertList;
    }
}
