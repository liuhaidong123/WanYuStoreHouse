package com.storehouse.wanyu.MyUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by liuhaidong on 2018/8/21.
 */

public class MilliSecondToDate {

    public static String ms2DateOnlyDay(){
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return format.format(date);
    }

}
