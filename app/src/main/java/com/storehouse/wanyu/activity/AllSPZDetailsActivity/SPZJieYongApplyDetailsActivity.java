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
import com.storehouse.wanyu.Bean.JieYongDetailsListBean;
import com.storehouse.wanyu.Bean.JieYongDetailsRoot;
import com.storehouse.wanyu.Bean.JieYongDetailsRows;
import com.storehouse.wanyu.Bean.LingYongDetailsRoot;
import com.storehouse.wanyu.Bean.LingYongDetailsRows;
import com.storehouse.wanyu.IPAddress.URLTools;
import com.storehouse.wanyu.MyUtils.BallProgressUtils;
import com.storehouse.wanyu.OkHttpUtils.OkHttpManager;
import com.storehouse.wanyu.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//审批中借用申请详情
public class SPZJieYongApplyDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView mBack_img;
    private ListView mListview;
    private TextView mAgree_Btn, mDisAgree_btn, mBumen_tv, mpersion_tv, mBeizhu, mStart_time_tv, mEnd_time_tv, mSPZ_Status_Tv,mTime_tv;
    private RelativeLayout mAgreeAndDisagree_Rl, mSPZStatus_Rl;//同意反驳布局，审批中布局，根据权限不同分别显示和隐藏
    private JieYongAdapter mAdapter;
    private OkHttpManager okHttpManager;
    private List<JieYongDetailsListBean> mList = new ArrayList<>();
    private Gson gson = new Gson();
    private String JY_Url;//借用详情url
    private long mReferId;//请求详情需要的id
    private int flag;//判断是申请跳转过来的还审批跳转过来的，2代表申请，3代表审批
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 15) {
                String mes = (String) msg.obj;
                Object o = gson.fromJson(mes, JieYongDetailsRoot.class);
                if (o != null && o instanceof JieYongDetailsRoot) {
                    JieYongDetailsRoot jieYongDetailsRoot = (JieYongDetailsRoot) o;
                    if (jieYongDetailsRoot != null && "0".equals(jieYongDetailsRoot.getCode())) {
                        JieYongDetailsRows jieYongDetailsRows = jieYongDetailsRoot.getAssetBorrow();
                        if (jieYongDetailsRows != null) {
                            mBumen_tv.setText(jieYongDetailsRows.getDepartmentName() + "");
                            mpersion_tv.setText(jieYongDetailsRows.getUserName() + "");
                            if ("".equals(jieYongDetailsRows.getComment())){
                                mBeizhu.setText("----");
                            }else {
                                mBeizhu.setText(jieYongDetailsRows.getComment() + "");
                            }

                            mStart_time_tv.setText(jieYongDetailsRows.getBorrowDateString() + "");
                            mEnd_time_tv.setText(jieYongDetailsRows.getWillReturnDateString() + "");
                            mTime_tv.setText(jieYongDetailsRows.getBorrowDateString()+"");
                            if (jieYongDetailsRows.getAssetList() != null) {
                                mList = jieYongDetailsRows.getAssetList();
                                mAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(SPZJieYongApplyDetailsActivity.this, "获取详情列表失败", Toast.LENGTH_SHORT).show();

                            }
                            if (jieYongDetailsRows.getApplyStatus() == 0) {
                                mSPZ_Status_Tv.setText("审批中");
                                mSPZ_Status_Tv.setTextColor(ContextCompat.getColor(SPZJieYongApplyDetailsActivity.this, R.color.color_dc8268));
                            } else if (jieYongDetailsRows.getApplyStatus() == 1) {
                                mBh_rl.setVisibility(View.VISIBLE);
                                mBh_reason.setText(jieYongDetailsRows.getRejectReason() + "");
                                mSPZ_Status_Tv.setText("被驳回");
                                mSPZ_Status_Tv.setTextColor(ContextCompat.getColor(SPZJieYongApplyDetailsActivity.this, R.color.color_dc8268));
                            } else if (jieYongDetailsRows.getApplyStatus() == 2) {
                                mSPZ_Status_Tv.setText("已完成");
                                mSPZ_Status_Tv.setBackgroundResource(R.drawable.tongyi);
                                mSPZ_Status_Tv.setTextColor(ContextCompat.getColor(SPZJieYongApplyDetailsActivity.this, R.color.color_23b880));
                            } else {
                                mSPZ_Status_Tv.setText("已失效");
                                mSPZ_Status_Tv.setTextColor(ContextCompat.getColor(SPZJieYongApplyDetailsActivity.this, R.color.color_dc8268));
                            }
                        }


                    } else {
                        Toast.makeText(SPZJieYongApplyDetailsActivity.this, "登录过期，请重新登录", Toast.LENGTH_SHORT).show();

                    }


                } else {
                    Toast.makeText(SPZJieYongApplyDetailsActivity.this, "获取详情失败", Toast.LENGTH_SHORT).show();
                }


            } else if (msg.what == 1010) {
                Toast.makeText(SPZJieYongApplyDetailsActivity.this, "连接服务器失败，请重新尝试", Toast.LENGTH_SHORT).show();
            } else if (msg.what == 6) {//同意驳回
                BallProgressUtils.dismisLoading();
                String mes = (String) msg.obj;
                Object o = gson.fromJson(mes, AgreeAnddiagreeRoot.class);
                if (o != null && o instanceof AgreeAnddiagreeRoot) {
                    AgreeAnddiagreeRoot ag = (AgreeAnddiagreeRoot) o;
                    if (ag != null && "0".equals(ag.getCode())) {
                        Toast.makeText(SPZJieYongApplyDetailsActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK, intent);
                        finish();
                    } else if (ag != null && "-1".equals(ag.getCode())) {
                        Toast.makeText(SPZJieYongApplyDetailsActivity.this, "登录过期，请重新登录", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(SPZJieYongApplyDetailsActivity.this, "提交失败", Toast.LENGTH_SHORT).show();

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
        setContentView(R.layout.activity_spzjie_yong_apply_details);
        mAll_rl = (RelativeLayout) findViewById(R.id.activity_spzjie_yong_apply_details);
        initUI();
        intent = getIntent();
        mReferId = intent.getLongExtra("referId", -1);
        flag = intent.getIntExtra("flag", -1);
        okHttpManager = OkHttpManager.getInstance();
        if (mReferId != -1) {
            //请求详情
            JY_Url = URLTools.urlBase + URLTools.jieyong_details_url + "id=" + mReferId;
            okHttpManager.getMethod(false, JY_Url, "申请详情", handler, 15);
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
        mAgree_Btn = (TextView) findViewById(R.id.agree_btn);
        mAgree_Btn.setOnClickListener(this);
        mDisAgree_btn = (TextView) findViewById(R.id.disagree_btn);
        mDisAgree_btn.setOnClickListener(this);
        mBumen_tv = (TextView) findViewById(R.id.bumen_name);
        mpersion_tv = (TextView) findViewById(R.id.person_edit);
        mBeizhu = (TextView) findViewById(R.id.beizhu_edit);
        mTime_tv=  (TextView) findViewById(R.id.time_msg);
        mStart_time_tv = (TextView) findViewById(R.id.jie_time_content);
        mEnd_time_tv = (TextView) findViewById(R.id.return_time_content);
        mSPZ_Status_Tv = (TextView) findViewById(R.id.spz_status_tv);
        mAgreeAndDisagree_Rl = (RelativeLayout) findViewById(R.id.disagree_rl);
        mSPZStatus_Rl = (RelativeLayout) findViewById(R.id.status_rl);
        mBh_reason = (TextView) findViewById(R.id.bh_reason_msg);//驳回理由
        mBh_rl = (RelativeLayout) findViewById(R.id.bh_reason_rl);//
        View header=  LayoutInflater.from(this).inflate(R.layout.add_item_table_head, null);
        mAdapter = new JieYongAdapter();
        mListview = (ListView) findViewById(R.id.spz_jieyong_listview_id);
        mListview.setAdapter(mAdapter);
        mListview.addHeaderView(header);
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
        agree_and_disagree_url = URLTools.urlBase + URLTools.jieyong_agree_and_disagree;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == mBack_img.getId()) {
            finish();
        } else if (id == mAgree_Btn.getId()) {//同意
            BallProgressUtils.showLoading(SPZJieYongApplyDetailsActivity.this, mAll_rl);
            Map<Object, Object> map = new HashMap<>();
            map.put("id", mReferId);
            map.put("isAdopt", 1);
            okHttpManager.postMethod(false, agree_and_disagree_url, "同意接口", map, handler, 6);
        } else if (id == mDisAgree_btn.getId()) {//驳回
            alertDialog.show();
        } else if (id == cancle.getId()) {//弹框中的取消
            alertDialog.dismiss();
        } else if (id == sure.getId()) {//弹框中的确定
            BallProgressUtils.showLoading(SPZJieYongApplyDetailsActivity.this, mAll_rl);
            Map<Object, Object> map = new HashMap<>();
            map.put("id", mReferId);
            map.put("isAdopt", 0);
            map.put("rejectReason", reason.getText().toString().trim());
            okHttpManager.postMethod(false, agree_and_disagree_url, "驳回接口", map, handler, 6);
            alertDialog.dismiss();
        }
    }

    //领用用明细适配器
    class JieYongAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mList.size() == 0 ? 0 : mList.size();
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
            JYHolder jyHolder = null;
            if (view == null) {
                view = LayoutInflater.from(SPZJieYongApplyDetailsActivity.this).inflate(R.layout.add_item_table, null);
                jyHolder = new JYHolder();
                jyHolder.status = view.findViewById(R.id.select_state);
                jyHolder.bianhao = view.findViewById(R.id.bianhao_tv);
                jyHolder.leibie = view.findViewById(R.id.leibie_tv);
                jyHolder.mingcheng = view.findViewById(R.id.mingcheng_tv);
                view.setTag(jyHolder);
            } else {
                jyHolder = (JYHolder) view.getTag();

            }
            //设置数据
            jyHolder.status.setBackgroundResource(R.drawable.bumen_box_select);
            jyHolder.bianhao.setText(mList.get(i).getTotalNum() + "");
            jyHolder.leibie.setText(mList.get(i).getCategoryName() + "");
            jyHolder.mingcheng.setText(mList.get(i).getAssetsName() + "");
            return view;
        }

        class JYHolder {
            TextView status, bianhao, leibie, mingcheng;//状态，序号，物品名称，编号，类别
        }
    }
}
