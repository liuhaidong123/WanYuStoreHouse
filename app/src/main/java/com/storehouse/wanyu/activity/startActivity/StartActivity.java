package com.storehouse.wanyu.activity.startActivity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.PrintStreamPrinter;
import android.widget.Toast;

import com.google.gson.Gson;
import com.storehouse.wanyu.Bean.CheckCookieRoot;
import com.storehouse.wanyu.Bean.Permission;
import com.storehouse.wanyu.IPAddress.URLTools;
import com.storehouse.wanyu.MyUtils.SharedPrefrenceTools;
import com.storehouse.wanyu.OkHttpUtils.OkHttpManager;
import com.storehouse.wanyu.R;
import com.storehouse.wanyu.activity.LoginActivity.LoginActivity;
import com.storehouse.wanyu.activity.MainActivity;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Response;

public class StartActivity extends AppCompatActivity {
    private SharedPrefrenceTools sharedPrefrenceTools;
    private String check_cookie_url;
    private OkHttpManager mOkHttpManager;
    private Gson mGson = new Gson();
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 4) {
                String mes = (String) msg.obj;
                Log.e("检查cookie=", mes);
                Object o = mGson.fromJson(mes, CheckCookieRoot.class);
                if (o != null && o instanceof CheckCookieRoot) {
                    CheckCookieRoot checkCookieRoot = (CheckCookieRoot) o;
                    if ("0".equals(checkCookieRoot.getCode())) {//0表示cookie没有过期
                        Intent intent = new Intent(StartActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else if ("-1".equals(checkCookieRoot.getCode())) {
                        ;//-1表示cookie已经过期，需要重新登录
                        SharedPrefrenceTools.clear();
                        Intent intent2 = new Intent(StartActivity.this, LoginActivity.class);
                        startActivity(intent2);
                        finish();
                    }

                }


            } else if (msg.what == 1010) {
                ;//-1表示cookie已经过期，需要重新登录
                SharedPrefrenceTools.clear();
                Intent intent2 = new Intent(StartActivity.this, LoginActivity.class);
                startActivity(intent2);
                finish();
                //Toast.makeText(StartActivity.this, "检查cookie错误，无法连接服务器", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        mOkHttpManager = OkHttpManager.getInstance();
        sharedPrefrenceTools = SharedPrefrenceTools.getSharedPrefrenceToolsInstance(this);
        check_cookie_url = URLTools.urlBase + URLTools.check_cookie;//检查本地cookie是否过期


        for (int i = 0; i < (int) SharedPrefrenceTools.getValueByKey("PermissionNum", 0); i++) {
            Permission permission = (Permission) SharedPrefrenceTools.getObject("Permission" + i);
            if (permission != null) {
                Log.e("权限" + i, "Target=" + permission.getTarget() + "--Operaton=" + permission.getOperaton());
            } else {
                Toast.makeText(StartActivity.this, "存储权限对象为空", Toast.LENGTH_SHORT).show();
            }

        }
        //Activity启动页：
        final Timer timer = new Timer(true);
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {

                if (SharedPrefrenceTools.contain("cookie")) {
                    mOkHttpManager.getMethod(false, check_cookie_url, "检查本地cookie是否过期", mHandler, 4);

                } else {
                    Intent intent = new Intent(StartActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        };
        timer.schedule(timerTask, 2000);


    }
}
