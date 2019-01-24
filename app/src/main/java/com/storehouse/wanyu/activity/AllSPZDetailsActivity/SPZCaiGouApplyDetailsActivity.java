package com.storehouse.wanyu.activity.AllSPZDetailsActivity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.internal.ObjectConstructor;
import com.storehouse.wanyu.Bean.AgreeAnddiagreeRoot;
import com.storehouse.wanyu.Bean.CaiGouDetailsRoot;
import com.storehouse.wanyu.Bean.CaiGouDetailsRows;
import com.storehouse.wanyu.IPAddress.URLTools;
import com.storehouse.wanyu.MyUtils.BallProgressUtils;
import com.storehouse.wanyu.OkHttpUtils.OkHttpManager;
import com.storehouse.wanyu.R;

import java.util.HashMap;
import java.util.Map;

//审批中采购申请详情
public class SPZCaiGouApplyDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    private Intent intent;
    private ImageView mBack_img;
    //申请部门，物品名称，规格型号，计量单位，采购数量，生产厂家，采购类别，采购理由，同意按钮，驳回按钮，审批中状态
    private TextView mBumen_Tv, mName_Tv, mXingHao_Tv, mDanWei_Tv, mNum_Tv, mChangJia_Tv, mLeiBie_Tv, mReason_Tv, mAgree_Btn, mDisagree_Btn, mSPZ_Status_Tv, mTime_msg;
    private RelativeLayout mAgreeAndDisagree_Rl, mSPZStatus_Rl;//同意反驳布局，审批中布局，根据权限不同分别显示和隐藏
    private OkHttpManager okHttpManager;
    private Gson gson = new Gson();
    private String CG_Url;//采购详情url
    private long mReferId;//请求详情需要的id
    private int flag;//判断是申请跳转过来的还审批跳转过来的，2代表申请，3代表审批
    private int status;//判断是已审批跳转过来的，还是待审批跳转过来的 0，待审批，1已审批
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            BallProgressUtils.dismisLoading();
            if (msg.what == 15) {
                try{
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
                            //mPrice_Tv.setText(caiGouDetailsRows.getWorth() + "元");
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
                            if (caiGouDetailsRows.getApplyStatus() == 0) {
                                mSPZ_Status_Tv.setText("审批中");
                                mSPZ_Status_Tv.setTextColor(ContextCompat.getColor(SPZCaiGouApplyDetailsActivity.this, R.color.color_dc8268));
                            } else if (caiGouDetailsRows.getApplyStatus() == 1) {
                                mBh_rl.setVisibility(View.VISIBLE);
                                mBh_reason.setText(caiGouDetailsRows.getRejectReason() + "");
                                mSPZ_Status_Tv.setText("被驳回");
                                mSPZ_Status_Tv.setTextColor(ContextCompat.getColor(SPZCaiGouApplyDetailsActivity.this, R.color.color_dc8268));
                            } else if (caiGouDetailsRows.getApplyStatus() == 2) {
                                mSPZ_Status_Tv.setText("已完成");
                                mSPZ_Status_Tv.setBackgroundResource(R.drawable.tongyi);
                                mSPZ_Status_Tv.setTextColor(ContextCompat.getColor(SPZCaiGouApplyDetailsActivity.this, R.color.color_23b880));
                            } else {
                                mSPZ_Status_Tv.setText("已失效");
                                mSPZ_Status_Tv.setTextColor(ContextCompat.getColor(SPZCaiGouApplyDetailsActivity.this, R.color.color_dc8268));
                            }

                        } else {
                            Toast.makeText(SPZCaiGouApplyDetailsActivity.this, "登录过期，请重新登录", Toast.LENGTH_SHORT).show();
                            mNoData_rl.setVisibility(View.VISIBLE);
                            no_mess_tv.setText("登录过期，请重新登录");
                        }


                    } else {
                        Toast.makeText(SPZCaiGouApplyDetailsActivity.this, "获取详情失败", Toast.LENGTH_SHORT).show();
                    }

                }catch (Exception e){
                    Toast.makeText(SPZCaiGouApplyDetailsActivity.this, "数据解析错误", Toast.LENGTH_SHORT).show();
                    mNoData_rl.setVisibility(View.VISIBLE);
                    no_mess_tv.setText("数据解析错误，请重新尝试");
                }

            } else if (msg.what == 1010) {
                mNoData_rl.setVisibility(View.VISIBLE);
                no_mess_tv.setText("连接服务器失败，请检查网络");
                Toast.makeText(SPZCaiGouApplyDetailsActivity.this, "连接服务器失败，请检查网络", Toast.LENGTH_SHORT).show();
            } else if (msg.what == 6) {//同意驳回
                try{
                    BallProgressUtils.dismisLoading();
                    String mes = (String) msg.obj;
                    Object o = gson.fromJson(mes, AgreeAnddiagreeRoot.class);
                    if (o != null && o instanceof AgreeAnddiagreeRoot) {
                        AgreeAnddiagreeRoot ag = (AgreeAnddiagreeRoot) o;
                        if (ag != null && "0".equals(ag.getCode())) {
                            Toast.makeText(SPZCaiGouApplyDetailsActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK, intent);
                            finish();
                        } else if (ag != null && "-1".equals(ag.getCode())) {
                            Toast.makeText(SPZCaiGouApplyDetailsActivity.this, "登录过期，请重新登录", Toast.LENGTH_SHORT).show();
                            mNoData_rl.setVisibility(View.VISIBLE);
                            no_mess_tv.setText("登录过期，请重新登录");
                        }else {
                            Toast.makeText(SPZCaiGouApplyDetailsActivity.this, "提交失败", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(SPZCaiGouApplyDetailsActivity.this, "提交失败", Toast.LENGTH_SHORT).show();

                    }
                }catch (Exception e){
                    Toast.makeText(SPZCaiGouApplyDetailsActivity.this, "数据解析错误，请重新尝试", Toast.LENGTH_SHORT).show();
                }


            }
        }
    };

    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;
    private View view;
    private EditText reason;
    private TextView cancle, sure;
    private String agree_and_disagree_url;
    private RelativeLayout mBh_rl;
    private TextView mBh_reason,no_mess_tv;//驳回理由
    private RelativeLayout mAll_rl,mNoData_rl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spzcai_gou_apply_details);
        mAll_rl = (RelativeLayout) findViewById(R.id.activity_spzcai_gou_apply_details);
        mNoData_rl = (RelativeLayout) findViewById(R.id.no_data_rl);
        mNoData_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mReferId != -1) {
                    //请求详情
                    BallProgressUtils.showLoading(SPZCaiGouApplyDetailsActivity.this,mNoData_rl);
                    CG_Url = URLTools.urlBase + URLTools.caigou_details_url + "id=" + mReferId;
                    okHttpManager.getMethod(false, CG_Url, "申请详情", handler, 15);
                } else {
                    Toast.makeText(SPZCaiGouApplyDetailsActivity.this, "获取详情ID错误", Toast.LENGTH_SHORT).show();
                }
            }
        });
        no_mess_tv= (TextView) findViewById(R.id.no_mess_tv);
        initUI();
        intent = getIntent();
        mReferId = intent.getLongExtra("referId", -1);
        flag = intent.getIntExtra("flag", -1);

        okHttpManager = OkHttpManager.getInstance();
        if (mReferId != -1) {
            //请求详情
            CG_Url = URLTools.urlBase + URLTools.caigou_details_url + "id=" + mReferId;
            okHttpManager.getMethod(false, CG_Url, "申请详情", handler, 15);
        } else {
            Toast.makeText(this, "获取详情ID错误", Toast.LENGTH_SHORT).show();
        }
        if (flag != -1) {
            if (flag == 2) {//2代表申请
                mAgreeAndDisagree_Rl.setVisibility(View.GONE);
                mSPZStatus_Rl.setVisibility(View.VISIBLE);
            }
            if (flag == 3) {//3代表审批
                status = intent.getIntExtra("status", -1);

                if (status == 0) {//待审批
                    mAgreeAndDisagree_Rl.setVisibility(View.VISIBLE);
                    mSPZStatus_Rl.setVisibility(View.GONE);
                } else {//已审批
                    mAgreeAndDisagree_Rl.setVisibility(View.GONE);
                    mSPZStatus_Rl.setVisibility(View.VISIBLE);
                }

            }

        } else {
            Toast.makeText(this, "获取申请,审批种类错误", Toast.LENGTH_SHORT).show();
        }
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
        mBh_reason = (TextView) findViewById(R.id.bh_reason_msg);//驳回理由
        mBh_rl = (RelativeLayout) findViewById(R.id.bh_reason_rl);//
        mAgree_Btn = (TextView) findViewById(R.id.agree_btn);
        mAgree_Btn.setOnClickListener(this);
        mDisagree_Btn = (TextView) findViewById(R.id.disagree_btn);
        mDisagree_Btn.setOnClickListener(this);
        mSPZ_Status_Tv = (TextView) findViewById(R.id.spz_status_tv);
        mTime_msg = (TextView) findViewById(R.id.time_msg);
        //需要根据权限显示和隐藏的布局
        mAgreeAndDisagree_Rl = (RelativeLayout) findViewById(R.id.disagree_rl);
        mSPZStatus_Rl = (RelativeLayout) findViewById(R.id.status_rl);
///驳回弹框
        builder = new AlertDialog.Builder(this);
        alertDialog = builder.create();
        view = LayoutInflater.from(this).inflate(R.layout.agree_and_disagree_box, null);
        alertDialog.setView(view);
        reason = view.findViewById(R.id.reason_edit);
        cancle = view.findViewById(R.id.cancle);
        cancle.setOnClickListener(this);
        sure = view.findViewById(R.id.sure);
        sure.setOnClickListener(this);
        agree_and_disagree_url = URLTools.urlBase + URLTools.caigou_agree_and_disagree;
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();
        if (id == mBack_img.getId()) {
            finish();
        } else if (id == mAgree_Btn.getId()) {//同意
            BallProgressUtils.showLoading(SPZCaiGouApplyDetailsActivity.this, mAll_rl);
            Map<Object, Object> map = new HashMap<>();
            map.put("id", mReferId);
            map.put("isAdopt", 1);
            okHttpManager.postMethod(false, agree_and_disagree_url, "同意接口", map, handler, 6);
        } else if (id == mDisagree_Btn.getId()) {//驳回
            alertDialog.show();
        } else if (id == cancle.getId()) {//弹框中的取消
            alertDialog.dismiss();
        } else if (id == sure.getId()) {//弹框中的确定
            BallProgressUtils.showLoading(SPZCaiGouApplyDetailsActivity.this, mAll_rl);
            Map<Object, Object> map = new HashMap<>();
            map.put("id", mReferId);
            map.put("isAdopt", 0);
            map.put("rejectReason", reason.getText().toString().trim());
            okHttpManager.postMethod(false, agree_and_disagree_url, "驳回接口", map, handler, 6);
            alertDialog.dismiss();
        }
    }
}
