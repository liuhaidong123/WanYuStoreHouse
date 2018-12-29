package com.storehouse.wanyu.activity.Ku;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.storehouse.wanyu.Bean.CaiGouApplySubmit;
import com.storehouse.wanyu.Bean.NewOldDetailsRoot;
import com.storehouse.wanyu.Bean.NewOldDetailsRows;
import com.storehouse.wanyu.IPAddress.URLTools;
import com.storehouse.wanyu.MyUtils.BallProgressUtils;
import com.storehouse.wanyu.OkHttpUtils.OkHttpManager;
import com.storehouse.wanyu.R;
import com.storehouse.wanyu.activity.AllSPZDetailsActivity.SPZNewOldApplyDetailsActivity;

import java.util.HashMap;
import java.util.Map;

public class KuNewOldMessActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView mBack_img;
    private TextView mBumen_Tv, mPerson_Tv, mName_Tv, mTime_tv, mBeizhuMsg_Tv, mAgree_Btn, newold_status_Tv;
    private EditText mNum_edit;
    private int num;
    private RelativeLayout mAll_rl;
    private OkHttpManager okHttpManager;
    private Gson gson = new Gson();
    private String NO_Url,submit_agree_url;//借用详情url
    private Intent intent;
    private long mReferId;//请求详情需要的id
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 15) {
                String mes = (String) msg.obj;
                Object o = gson.fromJson(mes, NewOldDetailsRoot.class);
                if (o != null && o instanceof NewOldDetailsRoot) {
                    NewOldDetailsRoot newOldDetailsRoot = (NewOldDetailsRoot) o;
                    if (newOldDetailsRoot != null && "0".equals(newOldDetailsRoot.getCode())) {
                        NewOldDetailsRows weiIXiuDetailsRows = newOldDetailsRoot.getAssetOldfornew();
                        if (weiIXiuDetailsRows != null) {
                            mBumen_Tv.setText(weiIXiuDetailsRows.getDepartmentName() + "");
                            mPerson_Tv.setText(weiIXiuDetailsRows.getUserName() + "");
                            mName_Tv.setText(weiIXiuDetailsRows.getAssetName() + "");

                            mTime_tv.setText(weiIXiuDetailsRows.getChangeDateString() + "");
                            if ("".equals(weiIXiuDetailsRows.getComment())) {
                                mBeizhuMsg_Tv.setText("---");
                            } else {
                                mBeizhuMsg_Tv.setText(weiIXiuDetailsRows.getComment() + "");
                            }

                            if ("".equals(weiIXiuDetailsRows.getOutboundDateString())) {
                                mAgree_Btn.setVisibility(View.VISIBLE);
                                mNum_edit.setEnabled(true);
                                newold_status_Tv.setText("未换新");
                                mNum_edit.setText(weiIXiuDetailsRows.getTotalNum() + "");
                                num=weiIXiuDetailsRows.getTotalNum();
                                newold_status_Tv.setTextColor(ContextCompat.getColor(KuNewOldMessActivity.this, R.color.color_dc8268));
                            } else {
                                mAgree_Btn.setVisibility(View.GONE);
                                mNum_edit.setEnabled(false);
                                newold_status_Tv.setText("已换新");
                                mNum_edit.setText(weiIXiuDetailsRows.getNum() + "");
                                newold_status_Tv.setTextColor(ContextCompat.getColor(KuNewOldMessActivity.this, R.color.color_23b880));

                            }

                        }


                    } else {
                        Toast.makeText(KuNewOldMessActivity.this, "登录过期,请重新登录", Toast.LENGTH_SHORT).show();

                    }


                } else {
                    Toast.makeText(KuNewOldMessActivity.this, "获取详情失败", Toast.LENGTH_SHORT).show();
                }


            } else if (msg.what == 1010) {
                mAgree_Btn.setClickable(true);
                BallProgressUtils.dismisLoading();
                Toast.makeText(KuNewOldMessActivity.this, "连接服务器失败,请重新尝试", Toast.LENGTH_SHORT).show();
            }else if (msg.what == 1) {//提交以旧换新接口
                mAgree_Btn.setClickable(true);
                BallProgressUtils.dismisLoading();
                String mes = (String) msg.obj;
                Log.e("提交以旧换新=", mes);
                Object o = gson.fromJson(mes, CaiGouApplySubmit.class);
                if (o != null && o instanceof CaiGouApplySubmit) {
                    CaiGouApplySubmit caiGouApplySubmit = (CaiGouApplySubmit) o;
                    if ("0".equals(caiGouApplySubmit.getCode())) {
                        Toast.makeText(KuNewOldMessActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK, intent);
                        finish();
                    } else if ("-1".equals(caiGouApplySubmit.getCode())) {
                        Toast.makeText(KuNewOldMessActivity.this, "您的账号已过期请重新登录，请重新登录", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(KuNewOldMessActivity.this, "提交失败", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ku_new_old_mess);
        mAll_rl = (RelativeLayout) findViewById(R.id.activity_ku_new_old_mess);
        okHttpManager = OkHttpManager.getInstance();
        intent = getIntent();
        mReferId = intent.getLongExtra("referId", -1);
        if (mReferId != -1) {
            //请求详情
            NO_Url = URLTools.urlBase + URLTools.newold_details_url + "id=" + mReferId;
            okHttpManager.getMethod(false, NO_Url, "以旧换新详情", handler, 15);
        } else {
            Toast.makeText(this, "获取详情ID错误", Toast.LENGTH_SHORT).show();
        }
        submit_agree_url=URLTools.urlBase + URLTools.ku_newold_agree;//确认换新
        initUI();
    }

    private void initUI() {
        mBack_img = (ImageView) findViewById(R.id.back_img);
        mBack_img.setOnClickListener(this);

        mBumen_Tv = (TextView) findViewById(R.id.cg_bumen_msg);
        mPerson_Tv = (TextView) findViewById(R.id.cg_person_msg);
        mName_Tv = (TextView) findViewById(R.id.cg_name_msg);
        mTime_tv = (TextView) findViewById(R.id.cg_time_msg);
        mBeizhuMsg_Tv = (TextView) findViewById(R.id.cg_beizhu_msg);
        mAgree_Btn = (TextView) findViewById(R.id.agree_btn);
        mAgree_Btn.setOnClickListener(this);


        mNum_edit = (EditText) findViewById(R.id.cg_num_msg);
        newold_status_Tv = (TextView) findViewById(R.id.newold_status);


    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == mBack_img.getId()) {
            finish();
        } else if (id == mAgree_Btn.getId()) {//确认换新
            if (mReferId != -1) {
               if (checkNum()){
                   mAgree_Btn.setClickable(false);
                   BallProgressUtils.showLoading(KuNewOldMessActivity.this,mAll_rl);
                   Map<Object,Object> map=new HashMap<>();
                   map.put("id",mReferId);
                   map.put("num",mNum_edit.getText().toString());
                   map.put("newAssetId","");
                   okHttpManager.postMethod(false,submit_agree_url,"同意换新接口",map,handler,1);
               }

            } else {
                Toast.makeText(this, "获取详情ID错误", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean checkNum(){
        String s = mNum_edit.getText().toString();
        if (s.length()==0){
            Toast.makeText(this, "请填写换新的数量", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (Integer.valueOf(s)==0){
            Toast.makeText(this, "换新数量不能为0", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (s.startsWith("0")){
            Toast.makeText(this, "请填写正确的换新数量", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (Integer.valueOf(s) > num) {
            Toast.makeText(this, "换新数量大于使用数量", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
