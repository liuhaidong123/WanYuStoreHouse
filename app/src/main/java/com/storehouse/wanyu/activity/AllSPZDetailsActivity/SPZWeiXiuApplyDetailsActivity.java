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
import com.storehouse.wanyu.Bean.AgreeAnddiagreeRoot;
import com.storehouse.wanyu.Bean.WeiIXiuDetailsRows;
import com.storehouse.wanyu.Bean.WeiXiuDetailsRoot;
import com.storehouse.wanyu.IPAddress.URLTools;
import com.storehouse.wanyu.MyUtils.BallProgressUtils;
import com.storehouse.wanyu.OkHttpUtils.OkHttpManager;
import com.storehouse.wanyu.R;

import java.util.HashMap;
import java.util.Map;

//审批中维修申请详情
public class SPZWeiXiuApplyDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView mBack_img;
    private TextView mBumen_Tv, mPerson_Tv, mName_Tv, mNum_tv,mBeizhuMsg_Tv, mAgree_Btn, mDisagree_Btn, mSPZ_Status_Tv, mTime_tv, mBianMa_tv, mLeiXing_tv;
    private RelativeLayout mAgreeAndDisagree_Rl, mStatus_Rl;
    private OkHttpManager okHttpManager;
    private Gson gson = new Gson();
    private String WX_Url;//借用详情url
    private long mReferId;//请求详情需要的id
    private int flag;//判断是申请跳转过来的还审批跳转过来的，2代表申请，3代表审批
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 15) {
                String mes = (String) msg.obj;
                Object o = gson.fromJson(mes, WeiXiuDetailsRoot.class);
                if (o != null && o instanceof WeiXiuDetailsRoot) {
                    WeiXiuDetailsRoot weiXiuDetailsRoot = (WeiXiuDetailsRoot) o;
                    if (weiXiuDetailsRoot != null && "0".equals(weiXiuDetailsRoot.getCode())) {
                        WeiIXiuDetailsRows weiIXiuDetailsRows = weiXiuDetailsRoot.getMaintenanceLog();
                        if (weiIXiuDetailsRows != null) {
                            mBumen_Tv.setText(weiIXiuDetailsRows.getDepartmentName() + "");
                            mPerson_Tv.setText(weiIXiuDetailsRows.getUserName() + "");
                            mName_Tv.setText(weiIXiuDetailsRows.getAssetName() + "");
                            mTime_tv.setText(weiIXiuDetailsRows.getRepairDateString() + "");
                            mBianMa_tv.setText(weiIXiuDetailsRows.getBarcode()+"");
                            mNum_tv.setText(weiIXiuDetailsRows.getTotalNum()+"");
                            if (weiIXiuDetailsRows.getMainType() == 0) {
                                mLeiXing_tv.setText("日常维修");
                            } else if (weiIXiuDetailsRows.getMainType() == 1) {
                                mLeiXing_tv.setText("重大维修");
                            } else {
                                mLeiXing_tv.setText("---");
                            }

                            if ("".equals(weiIXiuDetailsRows.getComment())) {
                                mBeizhuMsg_Tv.setText("---");
                            } else {
                                mBeizhuMsg_Tv.setText(weiIXiuDetailsRows.getComment() + "");
                            }

                            if (weiIXiuDetailsRows.getApplyStatus() == 0) {
                                mSPZ_Status_Tv.setText("审批中");
                                mSPZ_Status_Tv.setTextColor(ContextCompat.getColor(SPZWeiXiuApplyDetailsActivity.this, R.color.color_dc8268));
                            } else if (weiIXiuDetailsRows.getApplyStatus() == 1) {
                                mBh_rl.setVisibility(View.VISIBLE);
                                mBh_reason.setText(weiIXiuDetailsRows.getRejectReason() + "");
                                mSPZ_Status_Tv.setText("被驳回");
                                mSPZ_Status_Tv.setTextColor(ContextCompat.getColor(SPZWeiXiuApplyDetailsActivity.this, R.color.color_dc8268));
                            } else if (weiIXiuDetailsRows.getApplyStatus() == 2) {
                                mSPZ_Status_Tv.setText("已完成");
                                mSPZ_Status_Tv.setBackgroundResource(R.drawable.tongyi);
                                mSPZ_Status_Tv.setTextColor(ContextCompat.getColor(SPZWeiXiuApplyDetailsActivity.this, R.color.color_23b880));
                            } else {
                                mSPZ_Status_Tv.setText("已失效");
                                mSPZ_Status_Tv.setTextColor(ContextCompat.getColor(SPZWeiXiuApplyDetailsActivity.this, R.color.color_dc8268));
                            }
                        }


                    } else {
                        Toast.makeText(SPZWeiXiuApplyDetailsActivity.this, "登录过期，请重新登录", Toast.LENGTH_SHORT).show();

                    }


                } else {
                    Toast.makeText(SPZWeiXiuApplyDetailsActivity.this, "获取详情失败", Toast.LENGTH_SHORT).show();
                }


            } else if (msg.what == 1010) {
                Toast.makeText(SPZWeiXiuApplyDetailsActivity.this, "连接服务器失败，请重新尝试", Toast.LENGTH_SHORT).show();
            } else if (msg.what == 6) {//同意驳回
                BallProgressUtils.dismisLoading();
                String mes = (String) msg.obj;
                Object o = gson.fromJson(mes, AgreeAnddiagreeRoot.class);
                if (o != null && o instanceof AgreeAnddiagreeRoot) {
                    AgreeAnddiagreeRoot ag = (AgreeAnddiagreeRoot) o;
                    if (ag != null && "0".equals(ag.getCode())) {
                        Toast.makeText(SPZWeiXiuApplyDetailsActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK, intent);
                        finish();
                    } else if (ag != null && "-1".equals(ag.getCode())) {
                        Toast.makeText(SPZWeiXiuApplyDetailsActivity.this, "登录过期", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(SPZWeiXiuApplyDetailsActivity.this, "提交失败", Toast.LENGTH_SHORT).show();

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
    private Intent intent;
    private RelativeLayout mBh_rl;
    private TextView mBh_reason;//驳回理由
    private int status;//判断是已审批跳转过来的，还是待审批跳转过来的 0，待审批，1已审批
    private RelativeLayout mAll_rl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spzwei_xiu_apply_details);
        mAll_rl = (RelativeLayout) findViewById(R.id.activity_spzwei_xiu_apply_details);
        initUI();
        intent = getIntent();
        mReferId = intent.getLongExtra("referId", -1);
        flag = intent.getIntExtra("flag", -1);
        okHttpManager = OkHttpManager.getInstance();
        if (mReferId != -1) {
            //请求详情
            WX_Url = URLTools.urlBase + URLTools.weixiu_details_url + "id=" + mReferId;
            okHttpManager.getMethod(false, WX_Url, "申请详情", handler, 15);
        } else {
            Toast.makeText(this, "获取详情ID错误", Toast.LENGTH_SHORT).show();
        }
        if (flag != -1) {
            if (flag == 2) {//2代表申请
                mAgreeAndDisagree_Rl.setVisibility(View.GONE);
                mStatus_Rl.setVisibility(View.VISIBLE);
            }
            if (flag == 3) {//3代表审批
                status = intent.getIntExtra("status", -1);

                if (status == 0) {//待审批
                    mAgreeAndDisagree_Rl.setVisibility(View.VISIBLE);
                    mStatus_Rl.setVisibility(View.GONE);
                } else {//已审批
                    mAgreeAndDisagree_Rl.setVisibility(View.GONE);
                    mStatus_Rl.setVisibility(View.VISIBLE);
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
        mPerson_Tv = (TextView) findViewById(R.id.cg_person_msg);
        mName_Tv = (TextView) findViewById(R.id.cg_name_msg);
        mNum_tv = (TextView) findViewById(R.id.cg_num_msg);

        mBeizhuMsg_Tv = (TextView) findViewById(R.id.cg_beizhu_msg);
        mTime_tv = (TextView) findViewById(R.id.time_msg);
        mBianMa_tv = (TextView) findViewById(R.id.bianma_edit);
        mLeiXing_tv = (TextView) findViewById(R.id.weixiu_edit);

        mAgree_Btn = (TextView) findViewById(R.id.agree_btn);
        mAgree_Btn.setOnClickListener(this);
        mDisagree_Btn = (TextView) findViewById(R.id.disagree_btn);
        mDisagree_Btn.setOnClickListener(this);
        mSPZ_Status_Tv = (TextView) findViewById(R.id.spz_status_tv);
        mAgreeAndDisagree_Rl = (RelativeLayout) findViewById(R.id.disagree_rl);
        mStatus_Rl = (RelativeLayout) findViewById(R.id.status_rl);
        mBh_reason = (TextView) findViewById(R.id.bh_reason_msg);//驳回理由
        mBh_rl = (RelativeLayout) findViewById(R.id.bh_reason_rl);//
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
        agree_and_disagree_url = URLTools.urlBase + URLTools.weixiu_agree_and_disagree;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == mBack_img.getId()) {
            finish();
        } else if (id == mAgree_Btn.getId()) {//同意
            BallProgressUtils.showLoading(SPZWeiXiuApplyDetailsActivity.this, mAll_rl);
            Map<Object, Object> map = new HashMap<>();
            map.put("id", mReferId);
            map.put("isAdopt", 1);
            okHttpManager.postMethod(false, agree_and_disagree_url, "同意接口", map, handler, 6);
        } else if (id == mDisagree_Btn.getId()) {//驳回
            alertDialog.show();
        } else if (id == cancle.getId()) {//弹框中的取消
            alertDialog.dismiss();
        } else if (id == sure.getId()) {//弹框中的确定
            BallProgressUtils.showLoading(SPZWeiXiuApplyDetailsActivity.this, mAll_rl);
            Map<Object, Object> map = new HashMap<>();
            map.put("id", mReferId);
            map.put("isAdopt", 0);
            map.put("rejectReason", reason.getText().toString().trim());
            okHttpManager.postMethod(false, agree_and_disagree_url, "驳回接口", map, handler, 6);
            alertDialog.dismiss();
        }
    }
}
