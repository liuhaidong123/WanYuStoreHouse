package com.storehouse.wanyu.activity.AllBBHDetailsActivity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.storehouse.wanyu.R;

//被驳回报废申请详情
public class BBHBaoFeiApplyDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView mBack_img;
    private TextView mBumen_Tv, mPerson_Tv, mName_Tv, mXingHao_Tv, mLocation_Tv, mReason_TV, mBeizhu_Tv, mStatus_Tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bbhbao_fei_apply_details);
        initUI();
    }

    private void initUI() {
        mBack_img = (ImageView) findViewById(R.id.back_img);
        mBack_img.setOnClickListener(this);

        mBumen_Tv = (TextView) findViewById(R.id.cg_bumen_msg);
        mPerson_Tv = (TextView) findViewById(R.id.cg_person_msg);
        mName_Tv = (TextView) findViewById(R.id.cg_name_msg);
        mXingHao_Tv = (TextView) findViewById(R.id.cg_xinghao_msg);
        mLocation_Tv = (TextView) findViewById(R.id.cg_location_msg);
        mReason_TV = (TextView) findViewById(R.id.cg_reason_msg);
        mBeizhu_Tv = (TextView) findViewById(R.id.cg_beizhu_edit_msg);
        mStatus_Tv = (TextView) findViewById(R.id.spz_status_tv);


    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == mBack_img.getId()) {
            finish();
        }
    }
}
