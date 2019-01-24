package com.storehouse.wanyu.MyUtils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by liuhaidong on 2019/1/15.
 */

public class ToastUtils {

    public static void show(Context context, String s) {
        Toast.makeText(context, s, Toast.LENGTH_LONG).show();
    }
}
