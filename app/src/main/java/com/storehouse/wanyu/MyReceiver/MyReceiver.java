package com.storehouse.wanyu.MyReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.storehouse.wanyu.activity.NotifyActivity.NotifyMessageActivity;

import cn.jpush.android.api.JPushInterface;

public class MyReceiver extends BroadcastReceiver {
    public MyReceiver() {


    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        Log.e("我的广播", "onReceive-" + intent.getAction());
        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Log.e("我的广播", "[MyReceiver] 接收 Registration Id : " + regId);
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            Log.e("我的广播", "收到了自定义消息。消息内容是：" + bundle.getString(JPushInterface.EXTRA_MESSAGE));
        }
        // 自定义消息不会展示在通知栏，完全要开发者写代码去处理
        else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Log.e("我的广播", "收到了通知");
            // 在这里可以做些统计，或者做些其他工作
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Log.e("我的广播", "用户点击打开了通知");
            // 在这里可以自己写代码去定义用户点击后的行为
            Intent intent1 = new Intent(context, NotifyMessageActivity.class);  //自定义打开的界面
            intent1.putExtra("title",bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE));
            intent1.putExtra("content",bundle.getString(JPushInterface.EXTRA_ALERT));
            Log.e("通知标题", bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE));
            Log.e("通知内容", bundle.getString(JPushInterface.EXTRA_ALERT));

           // i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent1);
        } else {
            Log.e("我的广播", "Unhandled intent - " + intent.getAction());
        }
    }
}
