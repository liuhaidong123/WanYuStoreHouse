package com.storehouse.wanyu.activity.PanDian;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.storehouse.wanyu.R;

//盘点
public class  PDActivity extends AppCompatActivity {
    private  FragmentManager fragmentManager;
    private  TextView mNew_btn, mHistory_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pd);
        fragmentManager = getSupportFragmentManager();
       // showNewFragment();

        mNew_btn = (TextView) findViewById(R.id.new_pamdian);
        mHistory_btn = (TextView) findViewById(R.id.history_pamdian);
//        新建盘点
        mNew_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNew_btn.setBackgroundResource(R.color.color_30a2d4);
                mNew_btn.setTextColor(ContextCompat.getColor(PDActivity.this,R.color.color_ffffff));
                mHistory_btn.setBackgroundResource(R.color.color_ffffff);
                mHistory_btn.setTextColor(ContextCompat.getColor(PDActivity.this,R.color.color_30a2d4));
                showNewFragment();
            }
        });
        //盘点汇总
        mHistory_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNew_btn.setBackgroundResource(R.color.color_ffffff);
                mNew_btn.setTextColor(ContextCompat.getColor(PDActivity.this,R.color.color_30a2d4));
                mHistory_btn.setBackgroundResource(R.color.color_30a2d4);
                mHistory_btn.setTextColor(ContextCompat.getColor(PDActivity.this,R.color.color_ffffff));
                showHistoryFragment();
            }
        });
        showHistoryFragment();
    }

    //显示新建盘点
    public void showNewFragment() {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Fragment newFragment = fragmentManager.findFragmentByTag("newFragment");
        Fragment historyFragment = fragmentManager.findFragmentByTag("historyFragment");


        if (newFragment != null) {
            fragmentTransaction.show(newFragment);
        } else {
            New2Fragment newFragment2 = new New2Fragment();
            fragmentTransaction.add(R.id.new_history_rl, newFragment2, "newFragment");
        }
        if (historyFragment != null) {
            fragmentTransaction.hide(historyFragment);
        }
        fragmentTransaction.commit();
    }

    //显示盘点汇总
    public  void showHistoryFragment() {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Fragment newFragment = fragmentManager.findFragmentByTag("newFragment");
        Fragment historyFragment = fragmentManager.findFragmentByTag("historyFragment");


        if (historyFragment != null) {
            fragmentTransaction.show(historyFragment);

        } else {
            HistoryFragment historyFragment1 = new HistoryFragment();
            fragmentTransaction.add(R.id.new_history_rl, historyFragment1, "historyFragment");

        }
        if (newFragment != null) {
            fragmentTransaction.hide(newFragment);
        }
        fragmentTransaction.commit();
        mNew_btn.setBackgroundResource(R.color.color_ffffff);
        mNew_btn.setTextColor(ContextCompat.getColor(PDActivity.this,R.color.color_30a2d4));
        mHistory_btn.setBackgroundResource(R.color.color_30a2d4);
        mHistory_btn.setTextColor(ContextCompat.getColor(PDActivity.this,R.color.color_ffffff));

    }
}
