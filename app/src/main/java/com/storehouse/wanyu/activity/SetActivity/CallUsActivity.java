package com.storehouse.wanyu.activity.SetActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.storehouse.wanyu.Bean.CallUsRoot;
import com.storehouse.wanyu.IPAddress.URLTools;
import com.storehouse.wanyu.MyUtils.CheckPhone;
import com.storehouse.wanyu.OkHttpUtils.OkHttpManager;
import com.storehouse.wanyu.R;

public class CallUsActivity extends AppCompatActivity {
    private ImageView mback;
    private TextView phone, call_btn;
    private OkHttpManager okHttpManager;
    private Gson gson = new Gson();
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                String str = (String) msg.obj;
                Object o = gson.fromJson(str, CallUsRoot.class);
                if (o != null && o instanceof CallUsRoot) {
                    CallUsRoot callUsRoot = (CallUsRoot) o;
                    if (callUsRoot != null && "0".equals(callUsRoot.getCode())) {

                        if (callUsRoot.getRows() != null && callUsRoot.getRows().size() != 0) {
                            phone.setText(callUsRoot.getRows().get(0).getValue() + "");
                        }

                    }else {
                        Toast.makeText(CallUsActivity.this, "登录过期，请重新登录", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(CallUsActivity.this, "获取联系方式失败", Toast.LENGTH_SHORT).show();

                }


            } else {
                Toast.makeText(CallUsActivity.this, "获取联系方式失败", Toast.LENGTH_SHORT).show();

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_us);
        okHttpManager = OkHttpManager.getInstance();
        okHttpManager.getMethod(false, URLTools.urlBase + URLTools.call_us_url, "联系我们", handler, 1);
        mback = (ImageView) findViewById(R.id.back_img);
        mback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        phone = (TextView) findViewById(R.id.call_phone);
        call_btn = (TextView) findViewById(R.id.call_btn);
        call_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!"".equals(phone.getText().toString().trim())) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    Uri data = Uri.parse("tel:" + phone.getText().toString().trim());
                    intent.setData(data);
                    startActivity(intent);
                } else {
                    Toast.makeText(CallUsActivity.this, "电话不正确", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}
