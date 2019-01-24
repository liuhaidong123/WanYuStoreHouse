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
import com.storehouse.wanyu.Bean.BaoFeiDetailsRoot;
import com.storehouse.wanyu.Bean.BaoFeiDetailsRows;
import com.storehouse.wanyu.IPAddress.URLTools;
import com.storehouse.wanyu.MyUtils.BallProgressUtils;
import com.storehouse.wanyu.OkHttpUtils.OkHttpManager;
import com.storehouse.wanyu.R;

import java.util.HashMap;
import java.util.Map;

//审批中报废申请详情
public class SPZBaoFeiApplyDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView mBack_img;
    private TextView mBumen_Tv, mPerson_Tv, mName_Tv, mNum_Tv, mApplyTime_tv, mLeixing_Tv, mDate_Tv, mReason_TV, mAgree_Btn, mDisagree_Btn, mSPZ_Status_Tv;
    private RelativeLayout mAgreeAndDisagree_Rl, mStatus_Rl;
    private OkHttpManager okHttpManager;
    private Gson gson = new Gson();
    private String BF_Url;//报废详情url
    private long mReferId;//请求详情需要的id
    private int flag;//判断是申请跳转过来的还审批跳转过来的，2代表申请，3代表审批
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            BallProgressUtils.dismisLoading();
            if (msg.what == 15) {
                try {
                    String mes = (String) msg.obj;
                    Object o = gson.fromJson(mes, BaoFeiDetailsRoot.class);
                    if (o != null && o instanceof BaoFeiDetailsRoot) {
                        BaoFeiDetailsRoot baoFeiDetailsRoot = (BaoFeiDetailsRoot) o;
                        if (baoFeiDetailsRoot != null && "0".equals(baoFeiDetailsRoot.getCode())) {
                            BaoFeiDetailsRows baoFeiDetailsRows = baoFeiDetailsRoot.getAssetScrap();
                            if (baoFeiDetailsRows != null) {
                                mNoData_rl.setVisibility(View.GONE);
                                no_mess_tv.setText("");
                                mBumen_Tv.setText(baoFeiDetailsRows.getDepartmentName() + "");
                                mPerson_Tv.setText(baoFeiDetailsRows.getUserName() + "");
                                mName_Tv.setText(baoFeiDetailsRows.getAssetsName() + "");
                                mLeixing_Tv.setText(baoFeiDetailsRows.getScrapModeName() + "");
                                mDate_Tv.setText(baoFeiDetailsRows.getScrapDateString() + "");
                                mNum_Tv.setText(baoFeiDetailsRows.getTotalNum() + "");
                                mApplyTime_tv.setText(baoFeiDetailsRows.getScrapDateString());
                                if ("".equals(baoFeiDetailsRows.getComment())) {
                                    mReason_TV.setText("---");
                                } else {
                                    mReason_TV.setText(baoFeiDetailsRows.getComment() + "");
                                }

                                if (baoFeiDetailsRows.getApplyStatus() == 0) {
                                    mSPZ_Status_Tv.setText("审批中");
                                    mSPZ_Status_Tv.setTextColor(ContextCompat.getColor(SPZBaoFeiApplyDetailsActivity.this, R.color.color_dc8268));
                                } else if (baoFeiDetailsRows.getApplyStatus() == 1) {
                                    mBh_rl.setVisibility(View.VISIBLE);
                                    mBh_reason.setText(baoFeiDetailsRows.getRejectReason() + "");
                                    mSPZ_Status_Tv.setText("被驳回");
                                    mSPZ_Status_Tv.setTextColor(ContextCompat.getColor(SPZBaoFeiApplyDetailsActivity.this, R.color.color_dc8268));
                                } else if (baoFeiDetailsRows.getApplyStatus() == 2) {
                                    mSPZ_Status_Tv.setText("已完成");
                                    mSPZ_Status_Tv.setBackgroundResource(R.drawable.tongyi);
                                    mSPZ_Status_Tv.setTextColor(ContextCompat.getColor(SPZBaoFeiApplyDetailsActivity.this, R.color.color_23b880));
                                } else {
                                    mSPZ_Status_Tv.setText("已失效");
                                    mSPZ_Status_Tv.setTextColor(ContextCompat.getColor(SPZBaoFeiApplyDetailsActivity.this, R.color.color_dc8268));
                                }
                            }


                        } else {
                            Toast.makeText(SPZBaoFeiApplyDetailsActivity.this, "登录过期，请重新登录", Toast.LENGTH_SHORT).show();
                            mNoData_rl.setVisibility(View.VISIBLE);
                            no_mess_tv.setText("登录过期，请重新登录");
                        }


                    } else {
                        Toast.makeText(SPZBaoFeiApplyDetailsActivity.this, "获取详情失败", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    mNoData_rl.setVisibility(View.VISIBLE);
                    no_mess_tv.setText("数据解析错误，请重新尝试");
                    Toast.makeText(SPZBaoFeiApplyDetailsActivity.this, "数据解析错误，请重新尝试", Toast.LENGTH_SHORT).show();
                }

            } else if (msg.what == 1010) {
                mNoData_rl.setVisibility(View.VISIBLE);
                no_mess_tv.setText("连接服务器失败，请检查网络");
                Toast.makeText(SPZBaoFeiApplyDetailsActivity.this, "连接服务器失败，请检查网络", Toast.LENGTH_SHORT).show();
            } else if (msg.what == 6) {//同意驳回
                try {
                    BallProgressUtils.dismisLoading();
                    String mes = (String) msg.obj;
                    Object o = gson.fromJson(mes, AgreeAnddiagreeRoot.class);
                    if (o != null && o instanceof AgreeAnddiagreeRoot) {
                        AgreeAnddiagreeRoot ag = (AgreeAnddiagreeRoot) o;
                        if (ag != null && "0".equals(ag.getCode())) {
                            Toast.makeText(SPZBaoFeiApplyDetailsActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK, intent);
                            finish();
                        } else if (ag != null && "-1".equals(ag.getCode())) {
                            Toast.makeText(SPZBaoFeiApplyDetailsActivity.this, "登录过期，请重新登录", Toast.LENGTH_SHORT).show();
                            mNoData_rl.setVisibility(View.VISIBLE);
                            no_mess_tv.setText("登录过期，请重新登录");
                        } else {
                            Toast.makeText(SPZBaoFeiApplyDetailsActivity.this, "提交错误", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(SPZBaoFeiApplyDetailsActivity.this, "提交失败", Toast.LENGTH_SHORT).show();

                    }
                } catch (Exception e) {
                    Toast.makeText(SPZBaoFeiApplyDetailsActivity.this, "数据解析错误,请重新尝试", Toast.LENGTH_SHORT).show();
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
    private TextView mBh_reason, no_mess_tv;//驳回理由
    private int status;//判断是已审批跳转过来的，还是待审批跳转过来的 0，待审批，1已审批
    private RelativeLayout mAll_rl, mNoData_rl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spzbao_fei_apply_details);
        mAll_rl = (RelativeLayout) findViewById(R.id.activity_spzbao_fei_apply_details);
        mNoData_rl = (RelativeLayout) findViewById(R.id.no_data_rl);
        mNoData_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mReferId != -1) {
                    //请求详情
                    BallProgressUtils.showLoading(SPZBaoFeiApplyDetailsActivity.this, mNoData_rl);
                    BF_Url = URLTools.urlBase + URLTools.baofei_details_url + "id=" + mReferId;
                    okHttpManager.getMethod(false, BF_Url, "申请详情", handler, 15);
                } else {
                    Toast.makeText(SPZBaoFeiApplyDetailsActivity.this, "获取详情ID错误", Toast.LENGTH_SHORT).show();
                }
            }
        });
        no_mess_tv = (TextView) findViewById(R.id.no_mess_tv);
        initUI();
        intent = getIntent();
        mReferId = intent.getLongExtra("referId", -1);
        flag = intent.getIntExtra("flag", -1);
        okHttpManager = OkHttpManager.getInstance();
        if (mReferId != -1) {
            //请求详情
            BF_Url = URLTools.urlBase + URLTools.baofei_details_url + "id=" + mReferId;
            okHttpManager.getMethod(false, BF_Url, "申请详情", handler, 15);
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
        mLeixing_Tv = (TextView) findViewById(R.id.cg_xinghao_msg);
        mDate_Tv = (TextView) findViewById(R.id.cg_location_msg);
        mReason_TV = (TextView) findViewById(R.id.cg_reason_msg);
        mNum_Tv = (TextView) findViewById(R.id.cg_num_msg);
        mApplyTime_tv = (TextView) findViewById(R.id.cg_apply_time_msg);


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
        agree_and_disagree_url = URLTools.urlBase + URLTools.baofei_agree_and_disagree;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == mBack_img.getId()) {
            finish();
        } else if (id == mAgree_Btn.getId()) {//同意
            BallProgressUtils.showLoading(SPZBaoFeiApplyDetailsActivity.this, mAll_rl);
            Map<Object, Object> map = new HashMap<>();
            map.put("id", mReferId);
            map.put("isAdopt", 1);
            okHttpManager.postMethod(false, agree_and_disagree_url, "同意接口", map, handler, 6);
        } else if (id == mDisagree_Btn.getId()) {//驳回
            alertDialog.show();
        } else if (id == cancle.getId()) {//弹框中的取消
            alertDialog.dismiss();
        } else if (id == sure.getId()) {//弹框中的确定
            BallProgressUtils.showLoading(SPZBaoFeiApplyDetailsActivity.this, mAll_rl);
            Map<Object, Object> map = new HashMap<>();
            map.put("id", mReferId);
            map.put("isAdopt", 0);
            map.put("rejectReason", reason.getText().toString().trim());
            okHttpManager.postMethod(false, agree_and_disagree_url, "驳回接口", map, handler, 6);
            alertDialog.dismiss();
        }
    }
}
