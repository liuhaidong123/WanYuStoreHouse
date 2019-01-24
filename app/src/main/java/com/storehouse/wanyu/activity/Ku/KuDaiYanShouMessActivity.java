package com.storehouse.wanyu.activity.Ku;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
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
import com.storehouse.wanyu.activity.PurchaseOrder.PurchaseOrderMsgActivity;

import java.util.HashMap;
import java.util.Map;

//待验收
public class KuDaiYanShouMessActivity extends AppCompatActivity implements View.OnClickListener {

    private RelativeLayout mAll_rl;
    private ImageView mBack_img;
    //申请部门，物品名称，规格型号，计量单位，采购数量，生产厂家，采购类别，采购理由，同意按钮，驳回按钮，审批中状态
    private TextView mBumen_Tv, mName_Tv, mXingHao_Tv, mDanWei_Tv, mNum_Tv, mChangJia_Tv, mLeiBie_Tv, mReason_Tv, mTime_msg, mBeiZhu_msg_tv, mSubmit_btn;
    private RadioButton yesRadioButton, noRadioButton;
    private EditText mPrice_edit, mLocation_edit, mSuggestion_edit;//采购单价，采购地点，验收评价
    private String url, submit_url;
    private int suggestionFlag = -1;
    private long myID;
    private Intent intent;
    private OkHttpManager okHttpManager;
    private Gson gson = new Gson();
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            BallProgressUtils.dismisLoading();

            if (msg.what == 15) {//详情接口
                try {
                    String mes = (String) msg.obj;
                    Object o = gson.fromJson(mes, CaiGouDetailsRoot.class);
                    if (o != null && o instanceof CaiGouDetailsRoot) {
                        CaiGouDetailsRoot caiGouDetailsRoot = (CaiGouDetailsRoot) o;
                        if (caiGouDetailsRoot != null && "0".equals(caiGouDetailsRoot.getCode())) {
                            mNoData_rl.setVisibility(View.GONE);
                            no_mess_tv.setText("");

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


                        } else {
                            Toast.makeText(KuDaiYanShouMessActivity.this, "登录过期，请重新登录", Toast.LENGTH_SHORT).show();
                            mNoData_rl.setVisibility(View.VISIBLE);
                            no_mess_tv.setText("登录过期，请重新登录");
                        }


                    } else {
                        Toast.makeText(KuDaiYanShouMessActivity.this, "获取详情失败", Toast.LENGTH_SHORT).show();
                    }


                }catch (Exception e){
                    e.printStackTrace();
                    mNoData_rl.setVisibility(View.VISIBLE);
                    no_mess_tv.setText("数据解析错误，请重新尝试");
                    Toast.makeText(KuDaiYanShouMessActivity.this, "数据解析错误，请重新尝试", Toast.LENGTH_SHORT).show();
                }

            } else if (msg.what == 1010) {
                BallProgressUtils.dismisLoading();
                mSubmit_btn.setClickable(true);
                mNoData_rl.setVisibility(View.VISIBLE);
                no_mess_tv.setText("连接服务器失败，请检查网络");
                Toast.makeText(KuDaiYanShouMessActivity.this, "连接服务器失败，请检查网络", Toast.LENGTH_SHORT).show();
            } else if (msg.what == 2) {//开始采购
                try {
                    BallProgressUtils.dismisLoading();
                    mSubmit_btn.setClickable(true);
                    String mes = (String) msg.obj;
                    Object o = gson.fromJson(mes, SetPasswordRoot.class);
                    if (o != null && o instanceof SetPasswordRoot) {
                        SetPasswordRoot setPasswordRoot = (SetPasswordRoot) o;
                        if (setPasswordRoot != null && "0".equals(setPasswordRoot.getCode())) {
                            Toast.makeText(KuDaiYanShouMessActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK, intent);
                            finish();
                        } else if (setPasswordRoot != null && "-1".equals(setPasswordRoot.getCode())) {
                            Toast.makeText(KuDaiYanShouMessActivity.this, "登录过期,请重新登录", Toast.LENGTH_SHORT).show();
                            mNoData_rl.setVisibility(View.VISIBLE);
                            no_mess_tv.setText("登录过期,请重新登录");
                        } else {
                            Toast.makeText(KuDaiYanShouMessActivity.this, "提交失败", Toast.LENGTH_SHORT).show();
                        }


                    } else {
                        Toast.makeText(KuDaiYanShouMessActivity.this, "连接服务器失败，请重新尝试", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    mNoData_rl.setVisibility(View.VISIBLE);
                    no_mess_tv.setText("数据解析错误，请重新尝试");
                    Toast.makeText(KuDaiYanShouMessActivity.this, "数据解析错误，请重新尝试", Toast.LENGTH_SHORT).show();
                }

            }
        }
    };
    private RelativeLayout mNoData_rl;
    private TextView no_mess_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ku_dai_yan_shou_mess);
        mNoData_rl = (RelativeLayout) findViewById(R.id.no_data_rl);
        mNoData_rl.setOnClickListener(this);
        no_mess_tv = (TextView) findViewById(R.id.no_mess_tv);
        mAll_rl = (RelativeLayout) findViewById(R.id.activity_ku_dai_yan_shou_mess);
        okHttpManager = OkHttpManager.getInstance();
        url = URLTools.urlBase + URLTools.caigou_details_url;//详情接口
        submit_url = URLTools.urlBase + URLTools.ku_yanshou_submit;//提交采购验收
        initUI();
    }

    private void initUI() {
        mBack_img = (ImageView) findViewById(R.id.back_img);
        mBack_img.setOnClickListener(this);

        mBumen_Tv = (TextView) findViewById(R.id.cg_bumen_msg);
        mName_Tv = (TextView) findViewById(R.id.cg_name_msg);
        mXingHao_Tv = (TextView) findViewById(R.id.cg_xinghao_msg);
        mDanWei_Tv = (TextView) findViewById(R.id.cg_danwei_msg);
        mNum_Tv = (TextView) findViewById(R.id.cg_num_msg);
        mChangJia_Tv = (TextView) findViewById(R.id.cg_changjia_msg);
        mLeiBie_Tv = (TextView) findViewById(R.id.cg_leibie_msg);
        mReason_Tv = (TextView) findViewById(R.id.cg_reason_msg);
        mTime_msg = (TextView) findViewById(R.id.time_msg);
        mBeiZhu_msg_tv = (TextView) findViewById(R.id.caigou_beihu_msg);

        yesRadioButton = (RadioButton) findViewById(R.id.radiobutton_yes);
        noRadioButton = (RadioButton) findViewById(R.id.radiobutton_no);
        yesRadioButton.setOnClickListener(this);
        noRadioButton.setOnClickListener(this);
        mSubmit_btn = (TextView) findViewById(R.id.yanshou_sure);
        mSubmit_btn.setOnClickListener(this);

        mPrice_edit = (EditText) findViewById(R.id.danjia_edit);
        mLocation_edit = (EditText) findViewById(R.id.didian_edit);
        mSuggestion_edit = (EditText) findViewById(R.id.pingjia_edit);


        intent = getIntent();
        myID = intent.getLongExtra("id", -1);
        if (myID != -1) {
            okHttpManager.getMethod(false, url + "id=" + myID, "采购详情", handler, 15);
        } else {
            Toast.makeText(this, "详情信息错误", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == mBack_img.getId()) {
            finish();
        } else if (id == yesRadioButton.getId()) {
            suggestionFlag = 1;
        } else if (id == noRadioButton.getId()) {
            suggestionFlag = 2;
        } else if (id == mSubmit_btn.getId()) {//确定

            if (!"".equals(mPrice_edit.getText().toString())) {
                //以小数点开头不行
                if (mPrice_edit.getText().toString().indexOf(".") == 0) {
                    //填写正确价格
                    Toast.makeText(KuDaiYanShouMessActivity.this, "请填写正确的价格", Toast.LENGTH_SHORT).show();
                } else {
                    //以0开头并且小数点没有紧跟在0的后面，也不行
                    if (mPrice_edit.getText().toString().indexOf(".") > 1 && mPrice_edit.getText().toString().startsWith("0")) {
                        Toast.makeText(KuDaiYanShouMessActivity.this, "请填写正确的价格", Toast.LENGTH_SHORT).show();
                    } else {
                        //以0开头并且没有小数点，也不行
                        if (mPrice_edit.getText().toString().indexOf(".") == -1 && mPrice_edit.getText().toString().startsWith("0")) {
                            Toast.makeText(KuDaiYanShouMessActivity.this, "请填写正确的价格", Toast.LENGTH_SHORT).show();

                        } else {
                            if (suggestionFlag != -1) {
                                if (myID != -1) {
                                    mSubmit_btn.setClickable(false);
                                    BallProgressUtils.showLoading(KuDaiYanShouMessActivity.this, mAll_rl);
                                    Map<Object, Object> map = new HashMap<>();
                                    map.put("id", myID);
                                    map.put("buyWorth", mPrice_edit.getText().toString().trim());
                                    map.put("buyAddress", mLocation_edit.getText().toString().trim());
                                    map.put("acceptanceEvaluation", mSuggestion_edit.getText().toString().trim());
                                    map.put("acceptanceOpinion", suggestionFlag);
                                    okHttpManager.postMethod(false, submit_url, "提交验收接口", map, handler, 2);
                                } else {
                                    Toast.makeText(KuDaiYanShouMessActivity.this, "详情信息错误,请重新尝试", Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                Toast.makeText(KuDaiYanShouMessActivity.this, "请选择验收意见", Toast.LENGTH_SHORT).show();

                            }

                        }

                    }

                }

            } else {
                Toast.makeText(KuDaiYanShouMessActivity.this, "请填写价格", Toast.LENGTH_SHORT).show();
            }

        }else if (id==mNoData_rl.getId()){
            if (myID != -1) {
                BallProgressUtils.showLoading(KuDaiYanShouMessActivity.this,mNoData_rl);
                okHttpManager.getMethod(false, url + "id=" + myID, "采购详情", handler, 15);
            } else {
                Toast.makeText(this, "详情信息错误", Toast.LENGTH_SHORT).show();
            }

        }
    }
}
