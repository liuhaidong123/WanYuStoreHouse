package com.storehouse.wanyu.activity.PurchaseOrder;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.storehouse.wanyu.Bean.CaiGouDetailsRoot;
import com.storehouse.wanyu.Bean.CaiGouDetailsRows;
import com.storehouse.wanyu.Bean.SetPasswordRoot;
import com.storehouse.wanyu.IPAddress.URLTools;
import com.storehouse.wanyu.MyUtils.BallProgressUtils;
import com.storehouse.wanyu.OkHttpUtils.OkHttpManager;
import com.storehouse.wanyu.R;

import java.util.HashMap;
import java.util.Map;

//采购订单详情 （根据不同的参数隐藏显示不同的字段）
public class PurchaseOrderMsgActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView mBack_img;
    private TextView purchase_status_tv;//
    //申请部门，物品名称，规格型号，计量单位，采购数量，生产厂家，采购类别，采购理由，同意按钮，驳回按钮，审批中状态
    private TextView mBumen_Tv, mName_Tv, mXingHao_Tv, mDanWei_Tv, mNum_Tv, mChangJia_Tv, mLeiBie_Tv, mReason_Tv, mTime_msg;
    private EditText mBeiZhu_Edit;//待采购中备注输入框
    //备注信息，单价信息，采购地点，验收评价，验收意见，开始采购按钮
    private TextView mBeiZhu_msg_tv, mDanjia_mes_tv, mLocation_msg_tv, mComment_msg_tv, mSuggestion_msg_tv, mStart_Purchase_Btn;
    //备注信息布局，备注输入框布局，采购单价布局，采购地点布局，验收评价布局，验收意见布局
    private RelativeLayout mBeiZhuTv_Rl, mBeiZhuEdit_Rl, mDanJia_Rl, mLocation_Rl, mComment_Rl, mSuggestion_Rl;
    private Gson gson = new Gson();
    private String url, submit_url;
    private long myID;
    private Intent intent;
    private OkHttpManager okHttpManager;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 15) {//详情接口
                String mes = (String) msg.obj;
                Object o = gson.fromJson(mes, CaiGouDetailsRoot.class);
                if (o != null && o instanceof CaiGouDetailsRoot) {
                    CaiGouDetailsRoot caiGouDetailsRoot = (CaiGouDetailsRoot) o;
                    if (caiGouDetailsRoot != null && "0".equals(caiGouDetailsRoot.getCode())) {

                        CaiGouDetailsRows caiGouDetailsRows = caiGouDetailsRoot.getBuyApply();

                        mBumen_Tv.setText(caiGouDetailsRows.getDepartmentName() + "");
                        mName_Tv.setText(caiGouDetailsRows.getAssetName() + "");
                        mTime_msg.setText(caiGouDetailsRows.getApplyTimeString() + "");
                        if (!"".equals(caiGouDetailsRows.getSpecTyp())) {
                            mXingHao_Tv.setText(caiGouDetailsRows.getSpecTyp() + "");
                        } else {
                            mXingHao_Tv.setText("---");
                        }


                        mDanWei_Tv.setText(caiGouDetailsRows.getUnit() + "");
                        mNum_Tv.setText(caiGouDetailsRows.getBuyCount() + "");

                        if (!"".equals(caiGouDetailsRows.getProducerName())) {
                            mChangJia_Tv.setText(caiGouDetailsRows.getProducerName() + "");
                        } else {
                            mChangJia_Tv.setText("---");
                        }

                        if (caiGouDetailsRows.getBuyCate() == 1) {
                            mLeiBie_Tv.setText("计划内采购");
                        }
                        if (caiGouDetailsRows.getBuyCate() == 2) {
                            mLeiBie_Tv.setText("计划外采购");
                        }

                        if (!"".equals(caiGouDetailsRows.getBuyReason())) {
                            mReason_Tv.setText(caiGouDetailsRows.getBuyReason() + "");
                        } else {
                            mReason_Tv.setText("---");
                        }
                        if (!"".equals(caiGouDetailsRows.getComment())) {
                            mBeiZhu_msg_tv.setText(caiGouDetailsRows.getComment() + "");
                        } else {
                            mBeiZhu_msg_tv.setText("---");
                        }

                        if (!"".equals(caiGouDetailsRows.getBuyWorth())) {
                            mDanjia_mes_tv.setText(caiGouDetailsRows.getBuyWorth() + "元");
                        } else {
                            mDanjia_mes_tv.setText("---");
                        }

                        if (!"".equals(caiGouDetailsRows.getBuyAddress())) {
                            mLocation_msg_tv.setText(caiGouDetailsRows.getBuyAddress() + "");
                        } else {
                            mLocation_msg_tv.setText("---");
                        }
                        if (!"".equals(caiGouDetailsRows.getAcceptanceEvaluation())) {
                            mComment_msg_tv.setText(caiGouDetailsRows.getAcceptanceEvaluation() + "");
                        } else {
                            mComment_msg_tv.setText("---");
                        }


                        if (caiGouDetailsRows.getAcceptanceOpinion() == 1) {
                            mSuggestion_msg_tv.setText("同意");
                        }
                        if (caiGouDetailsRows.getAcceptanceOpinion() == 2) {
                            mSuggestion_msg_tv.setText("退货");
                        }


                    } else {
                        Toast.makeText(PurchaseOrderMsgActivity.this, "登录过期，请重新登录", Toast.LENGTH_SHORT).show();

                    }


                } else {
                    Toast.makeText(PurchaseOrderMsgActivity.this, "获取详情失败", Toast.LENGTH_SHORT).show();
                }


            } else if (msg.what == 1010) {
                BallProgressUtils.dismisLoading();
                mStart_Purchase_Btn.setClickable(true);
                Toast.makeText(PurchaseOrderMsgActivity.this, "连接服务器失败，请重新尝试", Toast.LENGTH_SHORT).show();
            } else if (msg.what == 1) {//开始采购
                BallProgressUtils.dismisLoading();
                mStart_Purchase_Btn.setClickable(true);
                String mes = (String) msg.obj;
                Object o = gson.fromJson(mes, SetPasswordRoot.class);
                if (o != null && o instanceof SetPasswordRoot) {
                    SetPasswordRoot setPasswordRoot = (SetPasswordRoot) o;
                    if (setPasswordRoot != null && "0".equals(setPasswordRoot.getCode())) {
                        Toast.makeText(PurchaseOrderMsgActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK, intent);
                        finish();
                    } else if (setPasswordRoot != null && "-1".equals(setPasswordRoot.getCode())) {
                        Toast.makeText(PurchaseOrderMsgActivity.this, "登录过期,请重新登录", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(PurchaseOrderMsgActivity.this, "提交失败", Toast.LENGTH_SHORT).show();
                    }


                } else {
                    Toast.makeText(PurchaseOrderMsgActivity.this, "连接服务器失败，请重新尝试", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };
    private RelativeLayout mAll_rl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_order_msg);
        mAll_rl= (RelativeLayout) findViewById(R.id.activity_purchase_order_msg);
        okHttpManager = OkHttpManager.getInstance();
        url = URLTools.urlBase + URLTools.caigou_details_url;//详情接口
        submit_url = URLTools.urlBase + URLTools.start_purchase_url;//开始采购接口
        initUI();
    }

    private void initUI() {
        mBack_img = (ImageView) findViewById(R.id.back_img);
        mBack_img.setOnClickListener(this);
        purchase_status_tv = (TextView) findViewById(R.id.purchase_status_tv);
        mBumen_Tv = (TextView) findViewById(R.id.cg_bumen_msg);
        mName_Tv = (TextView) findViewById(R.id.cg_name_msg);
        mXingHao_Tv = (TextView) findViewById(R.id.cg_xinghao_msg);
        mDanWei_Tv = (TextView) findViewById(R.id.cg_danwei_msg);
        mNum_Tv = (TextView) findViewById(R.id.cg_num_msg);
        mChangJia_Tv = (TextView) findViewById(R.id.cg_changjia_msg);
        mLeiBie_Tv = (TextView) findViewById(R.id.cg_leibie_msg);
        mReason_Tv = (TextView) findViewById(R.id.cg_reason_msg);
        mTime_msg = (TextView) findViewById(R.id.time_msg);

        mBeiZhu_Edit = (EditText) findViewById(R.id.remark_edit);

        mBeiZhu_msg_tv = (TextView) findViewById(R.id.caigou_beihu_msg);
        mDanjia_mes_tv = (TextView) findViewById(R.id.caigou_danjia_msg);
        mLocation_msg_tv = (TextView) findViewById(R.id.caigou_location_msg);
        mComment_msg_tv = (TextView) findViewById(R.id.caigou_comment_msg);
        mSuggestion_msg_tv = (TextView) findViewById(R.id.caigou_suggestion_msg);
        //开始采购
        mStart_Purchase_Btn = (TextView) findViewById(R.id.start_purchase_btn);
        mStart_Purchase_Btn.setOnClickListener(this);

        mBeiZhuTv_Rl = (RelativeLayout) findViewById(R.id.caigou_beihu_rl);
        mBeiZhuEdit_Rl = (RelativeLayout) findViewById(R.id.remark_rl);
        mDanJia_Rl = (RelativeLayout) findViewById(R.id.caigou_danjia_rl);
        mLocation_Rl = (RelativeLayout) findViewById(R.id.caigou_location_rl);
        mComment_Rl = (RelativeLayout) findViewById(R.id.caigou_comment_rl);
        mSuggestion_Rl = (RelativeLayout) findViewById(R.id.caigou_suggestion_rl);

        intent = getIntent();
        int flag = intent.getIntExtra("flag", -1);
        myID = intent.getLongExtra("id", -1);
        if (myID != -1) {
            okHttpManager.getMethod(false, url + "id=" + myID, "采购详情", handler, 15);
        } else {
            Toast.makeText(this, "详情信息错误", Toast.LENGTH_SHORT).show();
        }

        if (flag == 1) {//待采购跳过来的，隐藏备注信息，采购单价，采购地点，验收评价，验收意见；显示备注输入框，开始采购按钮
            purchase_status_tv.setText("待采购");
            mBeiZhuTv_Rl.setVisibility(View.GONE);
            mDanJia_Rl.setVisibility(View.GONE);
            mLocation_Rl.setVisibility(View.GONE);
            mComment_Rl.setVisibility(View.GONE);
            mSuggestion_Rl.setVisibility(View.GONE);
            mBeiZhuEdit_Rl.setVisibility(View.VISIBLE);
            mStart_Purchase_Btn.setVisibility(View.VISIBLE);

        } else if (flag == 2) {//采购中跳过来的，隐藏采购单价，采购地点，验收评价，验收意见，备注输入框，开始采购按钮；显示备注信息
            purchase_status_tv.setText("采购中");
            mDanJia_Rl.setVisibility(View.GONE);
            mLocation_Rl.setVisibility(View.GONE);
            mComment_Rl.setVisibility(View.GONE);
            mSuggestion_Rl.setVisibility(View.GONE);
            mBeiZhuEdit_Rl.setVisibility(View.GONE);
            mStart_Purchase_Btn.setVisibility(View.GONE);
            mBeiZhuTv_Rl.setVisibility(View.VISIBLE);

        } else if (flag == 3) {//已入库跳过来的，隐藏 备注输入框，开始采购按钮；显示备注信息，采购单价，采购地点，验收评价，验收意见，
            purchase_status_tv.setText("已入库");
            mBeiZhuEdit_Rl.setVisibility(View.GONE);
            mStart_Purchase_Btn.setVisibility(View.GONE);
            mDanJia_Rl.setVisibility(View.VISIBLE);
            mLocation_Rl.setVisibility(View.VISIBLE);
            mComment_Rl.setVisibility(View.VISIBLE);
            mSuggestion_Rl.setVisibility(View.VISIBLE);
            mBeiZhuTv_Rl.setVisibility(View.VISIBLE);

        } else if (flag == 4) {//已退货跳过来的，隐藏 备注输入框，开始采购按钮；显示备注信息，采购单价，采购地点，验收评价，验收意见，
            purchase_status_tv.setText("已退货");
            mBeiZhuEdit_Rl.setVisibility(View.GONE);
            mStart_Purchase_Btn.setVisibility(View.GONE);
            mDanJia_Rl.setVisibility(View.VISIBLE);
            mLocation_Rl.setVisibility(View.VISIBLE);
            mComment_Rl.setVisibility(View.VISIBLE);
            mSuggestion_Rl.setVisibility(View.VISIBLE);
            mBeiZhuTv_Rl.setVisibility(View.VISIBLE);

        } else {
            Toast.makeText(this, "详情信息错误", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == mBack_img.getId()) {
            finish();
        } else if (id == mStart_Purchase_Btn.getId()) {//开始采购
            if (!"".equals(mBumen_Tv.getText().toString().trim())) {//如果没有获取详情信息，那么无法提交采购
                mStart_Purchase_Btn.setClickable(false);
                BallProgressUtils.showLoading(PurchaseOrderMsgActivity.this,mAll_rl);
                Map<Object, Object> map = new HashMap<>();
                map.put("comment", mBeiZhu_Edit.getText().toString().trim());
                map.put("id", myID);
                okHttpManager.postMethod(false, submit_url, "开始采购接口", map, handler, 1);
            } else {
                Toast.makeText(this, "详情信息错误", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
