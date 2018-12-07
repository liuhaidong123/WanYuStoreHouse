package com.storehouse.wanyu.activity.AllBBHDetailsActivity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.storehouse.wanyu.R;

//被驳回退库申请详情
public class BBHTuiKuApplyDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView mBack_Img;
    private TextView mBumen_Tv, mName_Tv, mXinghao_tv, mDanwei_Tv, mPrice_Tv, nNum_tv, mChangjia_tv, mReason_tv, mBeizhu_tv, mStatus_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bbhtui_ku_apply_details);
        initUI();
    }

    private void initUI() {
        mBack_Img = (ImageView) findViewById(R.id.back_img);
        mBack_Img.setOnClickListener(this);
        mBumen_Tv = (TextView) findViewById(R.id.cg_bumen_msg);
        mName_Tv = (TextView) findViewById(R.id.cg_name_msg);
        mXinghao_tv = (TextView) findViewById(R.id.cg_xinghao_msg);
        mDanwei_Tv = (TextView) findViewById(R.id.cg_danwei_msg);
        mPrice_Tv = (TextView) findViewById(R.id.cg_price_msg);
        nNum_tv = (TextView) findViewById(R.id.cg_num_msg);
        mChangjia_tv = (TextView) findViewById(R.id.cg_changjia_msg);
        mReason_tv = (TextView) findViewById(R.id.cg_reason_msg);
        mBeizhu_tv = (TextView) findViewById(R.id.cg_beizhu_edit_msg);
        mStatus_tv = (TextView) findViewById(R.id.spz_status_tv);


    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == mBack_Img.getId()) {
            finish();
        }
    }
}
