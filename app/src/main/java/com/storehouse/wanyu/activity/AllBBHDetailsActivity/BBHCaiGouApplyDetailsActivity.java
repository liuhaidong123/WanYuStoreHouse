package com.storehouse.wanyu.activity.AllBBHDetailsActivity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.storehouse.wanyu.R;
//被驳回采购申请详情
public class BBHCaiGouApplyDetailsActivity extends AppCompatActivity implements View.OnClickListener{
    private ImageView mBack_img;
    //申请部门，物品名称，规格型号，计量单位，预算价格，采购数量，生产厂家，采购类别，采购理由，备注输入框，同意按钮，反驳，审批中状态
    private TextView mBumen_Tv,mName_Tv,mXingHao_Tv,mDanWei_Tv,mPrice_Tv,mNum_Tv,mChangJia_Tv,mLeiBie_Tv,mReason_Tv,mBeizhu_TV,mStatus_Tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bbhcai_gou_apply_details);
        initUI();
    }

    private void initUI(){
        mBack_img= (ImageView) findViewById(R.id.back_img);
        mBack_img.setOnClickListener(this);
        mBumen_Tv= (TextView) findViewById(R.id.cg_bumen_msg);
        mName_Tv= (TextView) findViewById(R.id.cg_name_msg);
        mXingHao_Tv= (TextView) findViewById(R.id.cg_xinghao_msg);
        mDanWei_Tv= (TextView) findViewById(R.id.cg_danwei_msg);
        mPrice_Tv= (TextView) findViewById(R.id.cg_price_msg);
        mNum_Tv= (TextView) findViewById(R.id.cg_num_msg);
        mChangJia_Tv= (TextView) findViewById(R.id.cg_changjia_msg);
        mLeiBie_Tv= (TextView) findViewById(R.id.cg_leibie_msg);
        mReason_Tv= (TextView) findViewById(R.id.cg_reason_msg);
        mBeizhu_TV= (TextView) findViewById(R.id.cg_beizhu_msg);
        mStatus_Tv= (TextView) findViewById(R.id.spz_status_tv);
    }

    @Override
    public void onClick(View view) {
        int id= view.getId();
        if (id==mBack_img.getId()){
            finish();
        }
    }
}
