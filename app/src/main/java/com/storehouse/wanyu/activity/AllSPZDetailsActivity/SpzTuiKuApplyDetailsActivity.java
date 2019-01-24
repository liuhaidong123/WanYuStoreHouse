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
import com.storehouse.wanyu.Bean.TuiKuDetailsListBean;
import com.storehouse.wanyu.Bean.TuiKuDetailsRoot;
import com.storehouse.wanyu.Bean.TuiKuDetailsRows;
import com.storehouse.wanyu.IPAddress.URLTools;
import com.storehouse.wanyu.MyUtils.BallProgressUtils;
import com.storehouse.wanyu.OkHttpUtils.OkHttpManager;
import com.storehouse.wanyu.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//审批中退库申请详情
public class SpzTuiKuApplyDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView mBack_Img;
    private TextView mBumen_Tv, mName_Tv, mReason_tv, mAgree_Btn, mDisagree_btn, mSPZ_Status_Tv;
    private RelativeLayout mAgreeAndDisagree_rl, mStatus_rl;
    private TKAdapter mAdapter;
    private ListView mListView;
    private List<TuiKuDetailsListBean> mList = new ArrayList<>();
    private OkHttpManager okHttpManager;
    private Gson gson = new Gson();
    private String TK_Url;//详情url
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
                    Object o = gson.fromJson(mes, TuiKuDetailsRoot.class);
                    if (o != null && o instanceof TuiKuDetailsRoot) {
                        TuiKuDetailsRoot tuiKuDetailsRoot = (TuiKuDetailsRoot) o;
                        if (tuiKuDetailsRoot != null && "0".equals(tuiKuDetailsRoot.getCode())) {
                            TuiKuDetailsRows tuiKuDetailsRows = tuiKuDetailsRoot.getAssetReturn();
                            if (tuiKuDetailsRows != null) {
                                mNoData_rl.setVisibility(View.GONE);
                                no_mess_tv.setText("");
                                mBumen_Tv.setText(tuiKuDetailsRows.getDepartmentName() + "");
                                mName_Tv.setText(tuiKuDetailsRows.getUserName() + "");

                                if ("".equals(tuiKuDetailsRows.getComment())) {
                                    mReason_tv.setText("---");
                                } else {
                                    mReason_tv.setText(tuiKuDetailsRows.getComment() + "");
                                }
                                if (tuiKuDetailsRows.getAssetList() != null) {
                                    mList = tuiKuDetailsRows.getAssetList();
                                    mAdapter.notifyDataSetChanged();
                                } else {
                                    Toast.makeText(SpzTuiKuApplyDetailsActivity.this, "获取详情列表失败", Toast.LENGTH_SHORT).show();
                                }
                                if (tuiKuDetailsRows.getApplyStatus() == 0) {
                                    mSPZ_Status_Tv.setText("审批中");
                                    mSPZ_Status_Tv.setTextColor(ContextCompat.getColor(SpzTuiKuApplyDetailsActivity.this, R.color.color_dc8268));
                                } else if (tuiKuDetailsRows.getApplyStatus() == 1) {
                                    mBh_rl.setVisibility(View.VISIBLE);
                                    mBh_reason.setText(tuiKuDetailsRows.getRejectReason() + "");
                                    mSPZ_Status_Tv.setText("被驳回");
                                    mSPZ_Status_Tv.setTextColor(ContextCompat.getColor(SpzTuiKuApplyDetailsActivity.this, R.color.color_dc8268));
                                } else if (tuiKuDetailsRows.getApplyStatus() == 2) {
                                    mSPZ_Status_Tv.setText("已完成");
                                    mSPZ_Status_Tv.setBackgroundResource(R.drawable.tongyi);
                                    mSPZ_Status_Tv.setTextColor(ContextCompat.getColor(SpzTuiKuApplyDetailsActivity.this, R.color.color_23b880));
                                } else {
                                    mSPZ_Status_Tv.setText("已失效");
                                    mSPZ_Status_Tv.setTextColor(ContextCompat.getColor(SpzTuiKuApplyDetailsActivity.this, R.color.color_dc8268));
                                }
                            }


                        } else {
                            Toast.makeText(SpzTuiKuApplyDetailsActivity.this, "登录过期，请重新登录", Toast.LENGTH_SHORT).show();
                            mNoData_rl.setVisibility(View.VISIBLE);
                            no_mess_tv.setText("登录过期，请重新登录");
                        }


                    } else {
                        Toast.makeText(SpzTuiKuApplyDetailsActivity.this, "获取详情失败", Toast.LENGTH_SHORT).show();
                    }


                } catch (Exception e) {
                    Toast.makeText(SpzTuiKuApplyDetailsActivity.this, "数据解析错误，请重新尝试", Toast.LENGTH_SHORT).show();
                    mNoData_rl.setVisibility(View.VISIBLE);
                    no_mess_tv.setText("数据解析错误");
                }

            } else if (msg.what == 1010) {
                Toast.makeText(SpzTuiKuApplyDetailsActivity.this, "连接服务器失败,请检查网络", Toast.LENGTH_SHORT).show();
                mNoData_rl.setVisibility(View.VISIBLE);
                no_mess_tv.setText("连接服务器失败,请检查网络");
            } else if (msg.what == 6) {//同意驳回
                try {
                    BallProgressUtils.dismisLoading();
                    String mes = (String) msg.obj;
                    Object o = gson.fromJson(mes, AgreeAnddiagreeRoot.class);
                    if (o != null && o instanceof AgreeAnddiagreeRoot) {
                        AgreeAnddiagreeRoot ag = (AgreeAnddiagreeRoot) o;
                        if (ag != null && "0".equals(ag.getCode())) {
                            Toast.makeText(SpzTuiKuApplyDetailsActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK, intent);
                            finish();
                        } else if (ag != null && "-1".equals(ag.getCode())) {
                            Toast.makeText(SpzTuiKuApplyDetailsActivity.this, "登录过期，请重新登录", Toast.LENGTH_SHORT).show();
                            mNoData_rl.setVisibility(View.VISIBLE);
                            no_mess_tv.setText("登录过期，请重新登录");
                        }

                    } else {
                        Toast.makeText(SpzTuiKuApplyDetailsActivity.this, "提交失败", Toast.LENGTH_SHORT).show();

                    }

                } catch (Exception e) {
                    Toast.makeText(SpzTuiKuApplyDetailsActivity.this, "数据解析错误，请重新尝试", Toast.LENGTH_SHORT).show();
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
        setContentView(R.layout.activity_spz_tui_ku_apply_details);
        mAll_rl = (RelativeLayout) findViewById(R.id.activity_spz_tui_ku_apply_details);
        mNoData_rl = (RelativeLayout) findViewById(R.id.no_data_rl);
        mNoData_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mReferId != -1) {
                    //请求详情
                    BallProgressUtils.showLoading(SpzTuiKuApplyDetailsActivity.this, mNoData_rl);
                    TK_Url = URLTools.urlBase + URLTools.tuiku_details_url + "id=" + mReferId;
                    okHttpManager.getMethod(false, TK_Url, "申请详情", handler, 15);
                } else {
                    Toast.makeText(SpzTuiKuApplyDetailsActivity.this, "获取详情ID错误", Toast.LENGTH_SHORT).show();
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
            TK_Url = URLTools.urlBase + URLTools.tuiku_details_url + "id=" + mReferId;
            okHttpManager.getMethod(false, TK_Url, "申请详情", handler, 15);
        } else {
            Toast.makeText(this, "获取详情ID错误", Toast.LENGTH_SHORT).show();
        }
        if (flag != -1) {
            if (flag == 2) {//2代表申请
                mAgreeAndDisagree_rl.setVisibility(View.GONE);
                mStatus_rl.setVisibility(View.VISIBLE);
            }
            if (flag == 3) {//3代表审批
                status = intent.getIntExtra("status", -1);

                if (status == 0) {//待审批
                    mAgreeAndDisagree_rl.setVisibility(View.VISIBLE);
                    mStatus_rl.setVisibility(View.GONE);
                } else {//已审批
                    mAgreeAndDisagree_rl.setVisibility(View.GONE);
                    mStatus_rl.setVisibility(View.VISIBLE);
                }

            }

        } else {
            Toast.makeText(this, "获取申请,审批种类错误", Toast.LENGTH_SHORT).show();
        }
    }

    private void initUI() {
        mBack_Img = (ImageView) findViewById(R.id.back_img);
        mBack_Img.setOnClickListener(this);
        mBumen_Tv = (TextView) findViewById(R.id.cg_bumen_msg);
        mName_Tv = (TextView) findViewById(R.id.cg_name_msg);
        mReason_tv = (TextView) findViewById(R.id.cg_reason_msg);
        mBh_reason = (TextView) findViewById(R.id.bh_reason_msg);//驳回理由
        mBh_rl = (RelativeLayout) findViewById(R.id.bh_reason_rl);//
        mAgree_Btn = (TextView) findViewById(R.id.agree_btn);
        mAgree_Btn.setOnClickListener(this);
        mDisagree_btn = (TextView) findViewById(R.id.disagree_btn);
        mDisagree_btn.setOnClickListener(this);
        mSPZ_Status_Tv = (TextView) findViewById(R.id.spz_status_tv);
        mAgreeAndDisagree_rl = (RelativeLayout) findViewById(R.id.disagree_rl);
        mStatus_rl = (RelativeLayout) findViewById(R.id.status_rl);
        View header = LayoutInflater.from(this).inflate(R.layout.add_item_table_head, null);
        mListView = (ListView) findViewById(R.id.spz_Lingxi_listview_id);
        mListView.addHeaderView(header);
        mAdapter = new TKAdapter();
        mListView.setAdapter(mAdapter);
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
        agree_and_disagree_url = URLTools.urlBase + URLTools.tuiku_agree_and_disagree;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == mBack_Img.getId()) {
            finish();
        } else if (id == mAgree_Btn.getId()) {//同意
            BallProgressUtils.showLoading(SpzTuiKuApplyDetailsActivity.this, mAll_rl);
            Map<Object, Object> map = new HashMap<>();
            map.put("id", mReferId);
            map.put("isAdopt", 1);
            okHttpManager.postMethod(false, agree_and_disagree_url, "同意接口", map, handler, 6);
        } else if (id == mDisagree_btn.getId()) {//驳回
            alertDialog.show();
        } else if (id == cancle.getId()) {//弹框中的取消
            alertDialog.dismiss();
        } else if (id == sure.getId()) {//弹框中的确定
            BallProgressUtils.showLoading(SpzTuiKuApplyDetailsActivity.this, mAll_rl);
            Map<Object, Object> map = new HashMap<>();
            map.put("id", mReferId);
            map.put("isAdopt", 0);
            map.put("rejectReason", reason.getText().toString().trim());
            okHttpManager.postMethod(false, agree_and_disagree_url, "驳回接口", map, handler, 6);
            alertDialog.dismiss();
        }
    }

    //适配器
    class TKAdapter extends BaseAdapter {

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
            TKHolder tkHolder = null;
            if (view == null) {
                view = LayoutInflater.from(SpzTuiKuApplyDetailsActivity.this).inflate(R.layout.add_item_table, null);
                tkHolder = new TKHolder();
                tkHolder.status = view.findViewById(R.id.select_state);
                tkHolder.bianhao = view.findViewById(R.id.bianhao_tv);
                tkHolder.leibie = view.findViewById(R.id.leibie_tv);
                tkHolder.mingcheng = view.findViewById(R.id.mingcheng_tv);

                view.setTag(tkHolder);
            } else {
                tkHolder = (TKHolder) view.getTag();

            }
            //设置数据
            tkHolder.status.setBackgroundResource(R.drawable.bumen_box_select);
            tkHolder.bianhao.setText(mList.get(i).getTotalNum() + "");
            tkHolder.leibie.setText(mList.get(i).getCategoryName() + "");
            tkHolder.mingcheng.setText(mList.get(i).getAssetsName() + "");
            return view;
        }

        class TKHolder {
            TextView status, bianhao, leibie, mingcheng;//状态，序号，物品名称，编号，类别
        }
    }

}
