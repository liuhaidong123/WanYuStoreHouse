package com.storehouse.wanyu.MyApplication;

import android.app.Application;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by liuhaidong on 2018/8/8.
 */

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        JPushInterface.init(this);
    }
}
