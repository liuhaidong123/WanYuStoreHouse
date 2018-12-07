package com.storehouse.wanyu.activity.NotifyActivity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.storehouse.wanyu.R;

public class NotifyMessageActivity extends AppCompatActivity {

    private ImageView mBack_btn;
    private TextView mTitle_tv, mContent_tv, mDate_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify_message);
        mBack_btn = (ImageView) findViewById(R.id.back_img);
        mBack_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mTitle_tv = (TextView) findViewById(R.id.title_message);
        mContent_tv = (TextView) findViewById(R.id.content_message);
        mDate_tv = (TextView) findViewById(R.id.notify_time);
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String content = intent.getStringExtra("content");
        String date = intent.getStringExtra("date");
        mTitle_tv.setText(title);
        mContent_tv.setText(content);
        mDate_tv.setText(date);
    }
}
