package com.storehouse.wanyu.activity.Ku;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.storehouse.wanyu.Bean.CaiGouApplySubmit;
import com.storehouse.wanyu.Bean.LingYongDetailsList;
import com.storehouse.wanyu.Bean.LingYongDetailsRoot;
import com.storehouse.wanyu.Bean.LingYongDetailsRows;
import com.storehouse.wanyu.IPAddress.URLTools;
import com.storehouse.wanyu.MyUtils.BallProgressUtils;
import com.storehouse.wanyu.MyUtils.MilliSecondToDate;
import com.storehouse.wanyu.OkHttpUtils.OkHttpManager;
import com.storehouse.wanyu.R;
import com.storehouse.wanyu.activity.AllSPZDetailsActivity.SPZLingYongApplyDetailsActivity;
import com.storehouse.wanyu.activity.ApplyActivity.ApplyLingYongActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//出库入库中领用详情
public class KuLingYongMessActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView mBack_img;
    private ListView mListview;
    private TextView mtitle_tv, mAgree_Btn, mBumenTV, mPersonTv, mBeizhuTv, mTime_tv;
    private KuLingYongAdapter mAdapter;
    private List<LingYongDetailsList> mList = new ArrayList<>();
    private OkHttpManager okHttpManager;
    private Gson gson = new Gson();
    private String LY_Url, agree_url;//详情url,出口接口
    private long mReferId;//请求详情需要的id
    private boolean flag = true;//true表示已领用，false表示未领用
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
                        LingYongDetailsRows lingYongDetailsRows = lingYongDetailsRoot.getAssetRecipients();
                        if (lingYongDetailsRows != null) {
                            mBumenTV.setText(lingYongDetailsRows.getDepartmentName() + "");
                            mPersonTv.setText(lingYongDetailsRows.getUserName() + "");
                            mTime_tv.setText(lingYongDetailsRows.getRecipientsDateString() + "");
                            if ("".equals(lingYongDetailsRows.getComment())) {
                                mBeizhuTv.setText("---");
                            } else {
                                mBeizhuTv.setText(lingYongDetailsRows.getComment() + "");
                            }
                            if ("".equals(lingYongDetailsRows.getOutboundDateString())) {
                                flag = false;
                                mAgree_Btn.setVisibility(View.VISIBLE);
                                mtitle_tv.setText("未领用");
                                mtitle_tv.setTextColor(ContextCompat.getColor(KuLingYongMessActivity.this, R.color.color_dc8268));
                            } else {
                                flag = true;
                                mAgree_Btn.setVisibility(View.GONE);
                                mtitle_tv.setText("已领用");
                                mtitle_tv.setTextColor(ContextCompat.getColor(KuLingYongMessActivity.this, R.color.color_23b880));
                            }

                            if (lingYongDetailsRows.getAssetList() != null) {
                                mList = lingYongDetailsRows.getAssetList();
                                mAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(KuLingYongMessActivity.this, "获取详情列表失败", Toast.LENGTH_SHORT).show();

                            }

                        }


                    } else {
                        Toast.makeText(KuLingYongMessActivity.this, "登录过期，请重新登录", Toast.LENGTH_SHORT).show();

                    }


                } else {
                    Toast.makeText(KuLingYongMessActivity.this, "获取详情失败", Toast.LENGTH_SHORT).show();
                }


            } else if (msg.what == 1010) {
                BallProgressUtils.dismisLoading();
                mAgree_Btn.setClickable(true);
                Toast.makeText(KuLingYongMessActivity.this, "连接服务器失败，请重新尝试", Toast.LENGTH_SHORT).show();
            } else if (msg.what == 7) {//提交lingyong申请接口
                mAgree_Btn.setClickable(true);
                BallProgressUtils.dismisLoading();
                String mes = (String) msg.obj;
                Log.e("提交领用=", mes);
                Object o = gson.fromJson(mes, CaiGouApplySubmit.class);
                if (o != null && o instanceof CaiGouApplySubmit) {
                    CaiGouApplySubmit caiGouApplySubmit = (CaiGouApplySubmit) o;
                    if ("0".equals(caiGouApplySubmit.getCode())) {
                        Toast.makeText(KuLingYongMessActivity.this, "领用出库成功", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK, intent);
                        finish();
                    } else if ("-1".equals(caiGouApplySubmit.getCode())) {
                        Toast.makeText(KuLingYongMessActivity.this, "您的账号已过期请重新登录，请重新登录", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(KuLingYongMessActivity.this, "资产数量不能全为0，请填写数量", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        }
    };
    private Intent intent;
    private RelativeLayout mAll_RL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ku_ling_yong_mess);
        mAll_RL = (RelativeLayout) findViewById(R.id.activity_ku_ling_yong_mess);
        intent = getIntent();
        mReferId = intent.getLongExtra("referId", -1);
        okHttpManager = OkHttpManager.getInstance();
        if (mReferId != -1) {
            LY_Url = URLTools.urlBase + URLTools.lingyong_details_url + "id=" + mReferId;
            okHttpManager.getMethod(false, LY_Url, "申请详情", handler, 15);
        } else {
            Toast.makeText(this, "获取详情ID错误", Toast.LENGTH_SHORT).show();
        }
        agree_url = URLTools.urlBase + URLTools.ku_lingyong_getout;//出库接口
        initUI();
    }

    private void initUI() {
        mBack_img = (ImageView) findViewById(R.id.back_img);
        mBack_img.setOnClickListener(this);

        mtitle_tv = (TextView) findViewById(R.id.lingyong_status);


        mBumenTV = (TextView) findViewById(R.id.bumen_name);
        mPersonTv = (TextView) findViewById(R.id.person_edit);
        mBeizhuTv = (TextView) findViewById(R.id.beizhu_edit);
        mTime_tv = (TextView) findViewById(R.id.time_msg);

        mAgree_Btn = (TextView) findViewById(R.id.agree_btn);
        mAgree_Btn.setOnClickListener(this);
        View header = LayoutInflater.from(this).inflate(R.layout.add_item_table_head, null);
        mListview = (ListView) findViewById(R.id.spz_Lingxi_listview_id);
        mListview.addHeaderView(header);
        mAdapter = new KuLingYongAdapter();
        mListview.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == mBack_img.getId()) {
            finish();
        } else if (id == mAgree_Btn.getId()) {
            if (mReferId != -1) {
                StringBuilder strId = new StringBuilder();
                for (int i = 0; i < mList.size(); i++) {
                    strId.append(mList.get(i).getId() + "," + mList.get(i).getTotalNum() + ";");//这里需要添加数量id,num;id,num;
                }
                Log.e("提交领用的id集合", strId.toString());

                mAgree_Btn.setClickable(false);
                BallProgressUtils.showLoading(this, mAll_RL);
                Map<Object, Object> map = new HashMap<>();
                map.put("id", mReferId);
                map.put("assetsIds", strId.toString());
                okHttpManager.postMethod(false, agree_url, "出库提交领用接口", map, handler, 7);

            } else {
                Toast.makeText(this, "获取详情ID错误,请重新获取", Toast.LENGTH_SHORT).show();
            }

        }
    }

    //领用用明细适配器
    class KuLingYongAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mList.size();
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
            KuLYHolder lyHolder = null;

            view = LayoutInflater.from(KuLingYongMessActivity.this).inflate(R.layout.add_item_table_edit, null);
            lyHolder = new KuLYHolder();
            lyHolder.status = view.findViewById(R.id.select_state);
            lyHolder.bianhao = view.findViewById(R.id.bianhao_tv);
            lyHolder.leibie = view.findViewById(R.id.leibie_tv);
            lyHolder.mingcheng = view.findViewById(R.id.mingcheng_tv);


            //设置数据
            lyHolder.status.setBackgroundResource(R.drawable.bumen_box_select);

            lyHolder.leibie.setText(mList.get(i).getCategoryName() + "");
            lyHolder.mingcheng.setText(mList.get(i).getAssetsName() + "");

            if (flag) {//已领用状态下，edittext不可编辑
                lyHolder.bianhao.setEnabled(false);
                lyHolder.bianhao.setText(mList.get(i).getNum() + "");//数量
            } else {
                lyHolder.bianhao.setEnabled(true);
                lyHolder.bianhao.setText(mList.get(i).getTotalNum() + "");//数量
            }

            final int position = i;
            Log.e("position=", position + "");
            final KuLYHolder finalLyHolder = lyHolder;
            lyHolder.bianhao.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {

                    String s = editable + "";

                    if (!"".equals(s)) {
                        if (s.startsWith("0") && s.length() > 1) {
                            Toast.makeText(KuLingYongMessActivity.this, "请填写正确的数量", Toast.LENGTH_SHORT).show();
                            finalLyHolder.bianhao.setText("0");
                        } else {
                            Integer num = Integer.valueOf(s);
                            mList.get(position).setTotalNum(num);
                            Log.e("edit -position=", position + "");
                            Log.e("数量=", mList.get(position).getTotalNum() + "");
                        }

                    } else {
                        //若不填数量，将以原始数量为准
                        Toast.makeText(KuLingYongMessActivity.this, "请填写数量", Toast.LENGTH_SHORT).show();
                    }

                }
            });

            return view;
        }

        class KuLYHolder {
            TextView status, leibie, mingcheng;//状态,类别,物品名称，
            EditText bianhao;
        }
    }
}
