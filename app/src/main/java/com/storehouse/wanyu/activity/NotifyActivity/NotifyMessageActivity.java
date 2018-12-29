package com.storehouse.wanyu.activity.NotifyActivity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.storehouse.wanyu.IPAddress.URLTools;
import com.storehouse.wanyu.OkHttpUtils.OkHttpManager;
import com.storehouse.wanyu.R;

public class NotifyMessageActivity extends AppCompatActivity {

    private ImageView mBack_btn;
    private TextView mTitle_tv, mContent_tv, mDate_tv;
    private OkHttpManager okHttpManager;
    private Gson gson=new Gson();
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==1){
                //不需要处理
            }
        }
    };
    private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify_message);
        mBack_btn = (ImageView) findViewById(R.id.back_img);
        mBack_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_OK,intent);
                finish();
            }
        });
        mTitle_tv = (TextView) findViewById(R.id.title_message);
        mContent_tv = (TextView) findViewById(R.id.content_message);
        mDate_tv = (TextView) findViewById(R.id.notify_time);
        intent = getIntent();
        String title = intent.getStringExtra("title");
        String content = intent.getStringExtra("content");
        String date = intent.getStringExtra("date");
        long id=intent.getLongExtra("id",-1);
        boolean read=intent.getBooleanExtra("read",true);
        String url= URLTools.urlBase+URLTools.message_isread;//标记消息已读
        okHttpManager=OkHttpManager.getInstance();
        if (id!=-1){
            if (read){

            }else {
                okHttpManager.getMethod(false,url+"id="+id,"标记消息已读",handler,1);
            }

        }else {
            Toast.makeText(NotifyMessageActivity.this,"详情错误",Toast.LENGTH_SHORT).show();
        }

        mTitle_tv.setText(title);
        mContent_tv.setText(content);
        mDate_tv.setText(date);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        setResult(RESULT_OK,intent);
        finish();
    }
}
