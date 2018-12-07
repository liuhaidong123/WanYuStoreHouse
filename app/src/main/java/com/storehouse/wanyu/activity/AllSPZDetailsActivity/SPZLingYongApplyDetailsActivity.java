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
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.storehouse.wanyu.Bean.AgreeAnddiagreeRoot;
import com.storehouse.wanyu.Bean.LingYongDetailsList;
import com.storehouse.wanyu.Bean.LingYongDetailsRoot;
import com.storehouse.wanyu.Bean.LingYongDetailsRows;
import com.storehouse.wanyu.IPAddress.URLTools;
import com.storehouse.wanyu.MyUtils.BallProgressUtils;
import com.storehouse.wanyu.OkHttpUtils.OkHttpManager;
import com.storehouse.wanyu.R;
import com.storehouse.wanyu.activity.ApplyActivity.ApplyLingYongActivity;

import java.lang.annotation.ElementType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//审批中领用申请详情
public class SPZLingYongApplyDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView mBack_img;
    private ListView mListview;
    private TextView mAgree_Btn, mDisAgree_btn, mBumenTV, mPersonTv, mBeizhuTv,mSPZ_Status_Tv,mTme_tv;
    private RelativeLayout mAgreeAndDisagree_RL, mStatus_RL;
    private LingYongAdapter mAdapter;
    private List<LingYongDetailsList> mList = new ArrayList<>();
    private OkHttpManager okHttpManager;
    private Gson gson = new Gson();
    private String LY_Url;//采购详情url
    private long mReferId;//请求详情需要的id
    private int flag;//判断是申请跳转过来的还审批跳转过来的，2代表申请，3代表审批
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 15) {
                String mes = (String) msg.obj;
                Object o = gson.fromJson(mes, LingYongDetailsRoot.class);
                if (o != null && o instanceof LingYongDetailsRoot) {
                    LingYongDetailsRoot lingYongDetailsRoot = (LingYongDetailsRoot) o;
                    if (lingYongDetailsRoot != null && "0".equals(lingYongDetailsRoot.getCode())) {
                        LingYongDetailsRows lingYongDetailsRows=lingYongDetailsRoot.getAssetRecipients();
                        if (lingYongDetailsRows!=null){
                            mBumenTV.setText(lingYongDetailsRows.getDepartmentName()+"");
                            mPersonTv.setText(lingYongDetailsRows.getUserName()+"");
                            mTme_tv.setText(lingYongDetailsRows.getRecipientsDateString()+"");
                            if ("".equals(lingYongDetailsRows.getComment())){
                                mBeizhuTv.setText("---");
                            }else {
                                mBeizhuTv.setText(lingYongDetailsRows.getComment()+"");
                            }


                            if (lingYongDetailsRows.getAssetList()!=null){
                                mList=lingYongDetailsRows.getAssetList();
                                mAdapter.notifyDataSetChanged();
                            }else {
                                Toast.makeText(SPZLingYongApplyDetailsActivity.this, "获取详情列表失败", Toast.LENGTH_SHORT).show();

                            }
                            if (lingYongDetailsRows.getApplyStatus()==0){
                                mSPZ_Status_Tv.setText("审批中");
                                mSPZ_Status_Tv.setTextColor(ContextCompat.getColor(SPZLingYongApplyDetailsActivity.this,R.color.color_dc8268));
                            }else if (lingYongDetailsRows.getApplyStatus()==1){
                                mBh_rl.setVisibility(View.VISIBLE);
                                mBh_reason.setText(lingYongDetailsRows.getRejectReason()+"");
                                mSPZ_Status_Tv.setText("被驳回");
                                mSPZ_Status_Tv.setTextColor(ContextCompat.getColor(SPZLingYongApplyDetailsActivity.this,R.color.color_dc8268));
                            }else if (lingYongDetailsRows.getApplyStatus()==2){
                                mSPZ_Status_Tv.setText("已完成");
                                mSPZ_Status_Tv.setBackgroundResource(R.drawable.tongyi);
                                mSPZ_Status_Tv.setTextColor(ContextCompat.getColor(SPZLingYongApplyDetailsActivity.this,R.color.color_23b880));
                            }else {
                                mSPZ_Status_Tv.setText("已失效");
                                mSPZ_Status_Tv.setTextColor(ContextCompat.getColor(SPZLingYongApplyDetailsActivity.this,R.color.color_dc8268));
                            }
                        }


                    } else {
                        Toast.makeText(SPZLingYongApplyDetailsActivity.this, "登录过期，请重新登录", Toast.LENGTH_SHORT).show();

                    }


                } else {
                    Toast.makeText(SPZLingYongApplyDetailsActivity.this, "获取详情失败", Toast.LENGTH_SHORT).show();
                }


            } else if (msg.what == 1010) {
                Toast.makeText(SPZLingYongApplyDetailsActivity.this, "连接服务器失败，请重新尝试", Toast.LENGTH_SHORT).show();
            }else if (msg.what == 6) {//同意驳回
                BallProgressUtils.dismisLoading();
                String mes = (String) msg.obj;
                Object o = gson.fromJson(mes, AgreeAnddiagreeRoot.class);
                if (o != null && o instanceof AgreeAnddiagreeRoot) {
                    AgreeAnddiagreeRoot ag = (AgreeAnddiagreeRoot) o;
                    if (ag != null&&"0".equals(ag.getCode())) {
                        Toast.makeText(SPZLingYongApplyDetailsActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK,intent);
                        finish();
                    } else if (ag != null&&"-1".equals(ag.getCode())){
                        Toast.makeText(SPZLingYongApplyDetailsActivity.this, "登录过期，请重新登录", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(SPZLingYongApplyDetailsActivity.this, "提交失败", Toast.LENGTH_SHORT).show();

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
    private  TextView mBh_reason;//驳回理由
    private int status;//判断是已审批跳转过来的，还是待审批跳转过来的 0，待审批，1已审批
    private RelativeLayout mAll_rl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spzling_yong_apply_details);
        mAll_rl= (RelativeLayout) findViewById(R.id.activity_spzling_yong_apply_details);
        initUI();
        intent = getIntent();
        mReferId = intent.getLongExtra("referId", -1);
        flag = intent.getIntExtra("flag", -1);
        okHttpManager = OkHttpManager.getInstance();
        if (mReferId != -1) {
            //请求详情
            LY_Url = URLTools.urlBase + URLTools.lingyong_details_url + "id=" + mReferId;
            okHttpManager.getMethod(false, LY_Url, "申请详情", handler, 15);
        } else {
            Toast.makeText(this, "获取详情ID错误", Toast.LENGTH_SHORT).show();
        }
        if (flag != -1) {
            if (flag == 2) {//2代表申请
                mAgreeAndDisagree_RL.setVisibility(View.GONE);
                mStatus_RL.setVisibility(View.VISIBLE);
            }
            if (flag == 3) {//3代表审批
                status=intent.getIntExtra("status",-1);

                if (status==0){//待审批
                    mAgreeAndDisagree_RL.setVisibility(View.VISIBLE);
                    mStatus_RL.setVisibility(View.GONE);
                }else {//已审批
                    mAgreeAndDisagree_RL.setVisibility(View.GONE);
                    mStatus_RL.setVisibility(View.VISIBLE);
                }

            }

        } else {
            Toast.makeText(this, "获取申请,审批种类错误", Toast.LENGTH_SHORT).show();
        }
    }

    private void initUI() {
        mBack_img = (ImageView) findViewById(R.id.back_img);
        mBack_img.setOnClickListener(this);

        mBumenTV = (TextView) findViewById(R.id.bumen_name);
        mPersonTv = (TextView) findViewById(R.id.person_edit);
        mBeizhuTv = (TextView) findViewById(R.id.beizhu_edit);
        mTme_tv= (TextView) findViewById(R.id.time_msg);

        mAgree_Btn = (TextView) findViewById(R.id.agree_btn);
        mAgree_Btn.setOnClickListener(this);
        mDisAgree_btn = (TextView) findViewById(R.id.disagree_btn);
        mDisAgree_btn.setOnClickListener(this);
        mSPZ_Status_Tv= (TextView) findViewById(R.id.spz_status_tv);

        mAgreeAndDisagree_RL = (RelativeLayout) findViewById(R.id.disagree_rl);
        mStatus_RL = (RelativeLayout) findViewById(R.id.status_rl);
        mAdapter = new LingYongAdapter();
      View header=  LayoutInflater.from(this).inflate(R.layout.add_item_table_head, null);
        mListview = (ListView) findViewById(R.id.spz_Lingxi_listview_id);
        mListview.setAdapter(mAdapter);
        mListview.addHeaderView(header);
        mBh_reason=(TextView) findViewById(R.id.bh_reason_msg);//驳回理由
        mBh_rl= (RelativeLayout) findViewById(R.id.bh_reason_rl);//
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
        agree_and_disagree_url = URLTools.urlBase +URLTools.lingyong_agree_and_disagree;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == mBack_img.getId()) {
            finish();
        }  else if (id == mAgree_Btn.getId()) {//同意
            BallProgressUtils.showLoading(SPZLingYongApplyDetailsActivity.this,mAll_rl);
            Map<Object, Object> map = new HashMap<>();
            map.put("id", mReferId);
            map.put("isAdopt", 1);
            okHttpManager.postMethod(false, agree_and_disagree_url, "同意接口", map, handler, 6);
        } else if (id == mDisAgree_btn.getId()) {//驳回
            alertDialog.show();
        } else if (id == cancle.getId()) {//弹框中的取消
            alertDialog.dismiss();
        } else if (id == sure.getId()) {//弹框中的确定
            BallProgressUtils.showLoading(SPZLingYongApplyDetailsActivity.this,mAll_rl);
            Map<Object, Object> map = new HashMap<>();
            map.put("id", mReferId);
            map.put("isAdopt", 0);
            map.put("rejectReason", reason.getText().toString().trim());
            okHttpManager.postMethod(false, agree_and_disagree_url, "驳回接口", map, handler, 6);
            alertDialog.dismiss();
        }
    }

    //领用用明细适配器
    class LingYongAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mList.size()==0?0:mList.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            LYHolder lyHolder = null;
            if (view == null) {
                view = LayoutInflater.from(SPZLingYongApplyDetailsActivity.this).inflate(R.layout.add_item_table, null);
                lyHolder = new LYHolder();
                lyHolder.status = view.findViewById(R.id.select_state);
                lyHolder.bianhao = view.findViewById(R.id.bianhao_tv);
                lyHolder.leibie = view.findViewById(R.id.leibie_tv);
                lyHolder.mingcheng = view.findViewById(R.id.mingcheng_tv);

                view.setTag(lyHolder);
            } else {
                lyHolder = (LYHolder) view.getTag();

            }
            //设置数据
            lyHolder.status.setBackgroundResource(R.drawable.bumen_box_select);
            lyHolder.bianhao.setText(mList.get(i).getTotalNum() + "");
            lyHolder.leibie.setText(mList.get(i).getCategoryName()+ "");
            lyHolder.mingcheng.setText(mList.get(i).getAssetsName() + "");
            return view;
        }

        class LYHolder {
            TextView status, bianhao, leibie, mingcheng;//状态，序号，物品名称，编号，类别
        }
    }


}
