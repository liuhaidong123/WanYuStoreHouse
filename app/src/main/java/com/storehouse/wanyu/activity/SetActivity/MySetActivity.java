package com.storehouse.wanyu.activity.SetActivity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.storehouse.wanyu.MyUtils.ActivityListUtils;
import com.storehouse.wanyu.MyUtils.SharedPrefrenceTools;
import com.storehouse.wanyu.R;
import com.storehouse.wanyu.activity.LoginActivity.LoginActivity;
import com.storehouse.wanyu.activity.MainActivity;

import java.util.List;

//设置Activity
public class MySetActivity extends AppCompatActivity {
    private ImageView mback;
    private RelativeLayout mSetpassword_rl, mSetSuggesion_rl, mSetCallUs_rl;
    private TextView mExit_btn;
    MainActivity mainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_set);
        mainActivity = new MainActivity();
        mback = (ImageView) findViewById(R.id.back_img);
        mback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //修改密码
        mSetpassword_rl = (RelativeLayout) findViewById(R.id.password_rl);
        mSetpassword_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MySetActivity.this, SetPasswordActivity.class);
                startActivity(intent);
            }
        });
//        意见反馈
        mSetSuggesion_rl = (RelativeLayout) findViewById(R.id.suggestion_rl);
        mSetSuggesion_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MySetActivity.this, SuggestionActivity.class);
                startActivity(intent);
            }
        });
//        联系我们
        mSetCallUs_rl = (RelativeLayout) findViewById(R.id.callus_rl);
        mSetCallUs_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MySetActivity.this, CallUsActivity.class);
                startActivity(intent);
            }
        });
        //退出
        mExit_btn = (TextView) findViewById(R.id.set_exit_btn);
        mExit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPrefrenceTools.clear();
                List<Activity> list = ActivityListUtils.getActivityListUtilsInstance().getList();
                for (int i = 0; i < list.size(); i++) {
                    list.get(i).finish();
                }
                startActivity(new Intent(MySetActivity.this, LoginActivity.class));
                finish();


            }
        });
    }
}
