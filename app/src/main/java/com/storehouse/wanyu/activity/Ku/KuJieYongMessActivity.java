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
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.storehouse.wanyu.Bean.CaiGouApplySubmit;
import com.storehouse.wanyu.Bean.JieYongDetailsListBean;
import com.storehouse.wanyu.Bean.JieYongDetailsRoot;
import com.storehouse.wanyu.Bean.JieYongDetailsRows;
import com.storehouse.wanyu.IPAddress.URLTools;
import com.storehouse.wanyu.MyUtils.BallProgressUtils;
import com.storehouse.wanyu.OkHttpUtils.OkHttpManager;
import com.storehouse.wanyu.R;
import com.storehouse.wanyu.activity.AllSPZDetailsActivity.SPZJieYongApplyDetailsActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//出库入库中借用详情
public class KuJieYongMessActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView mBack_img;
    private ListView mListview;
    private TextView mAgree_Btn, mBumen_tv, mpersion_tv, mBeizhu, mStart_time_tv, mEnd_time_tv, mJie_Status_Tv, mTime_tv;
    private KuJieYongAdapter mAdapter;
    private OkHttpManager okHttpManager;
    private List<JieYongDetailsListBean> mList = new ArrayList<>();
    private Gson gson = new Gson();
    private String JY_Url,out_url,in_url;//借用详情url
    private long mReferId;//请求详情需要的id
    private int flag=-1;//1，表示未借用；2，表示未归还；3表示已归还
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
                            if ("".equals(jieYongDetailsRows.getComment())) {
                                mBeizhu.setText("----");
                            } else {
                                mBeizhu.setText(jieYongDetailsRows.getComment() + "");
                            }

                            mStart_time_tv.setText(jieYongDetailsRows.getBorrowDateString() + "");
                            mEnd_time_tv.setText(jieYongDetailsRows.getWillReturnDateString() + "");
                            mTime_tv.setText(jieYongDetailsRows.getBorrowDateString() + "");

                            if ("".equals(jieYongDetailsRows.getOutboundDateString())) {
                                mAgree_Btn.setVisibility(View.VISIBLE);
                                flag=1;
                                mAgree_Btn.setText("确认借用");
                                mJie_Status_Tv.setText("未借用");
                                mJie_Status_Tv.setTextColor(ContextCompat.getColor(KuJieYongMessActivity.this, R.color.color_dc8268));
                            } else {
                                if ("".equals(jieYongDetailsRows.getInboundDateString())){
                                    mAgree_Btn.setVisibility(View.VISIBLE);
                                    flag=2;
                                    mAgree_Btn.setText("确认归还");
                                    mJie_Status_Tv.setText("未归还");
                                    mJie_Status_Tv.setTextColor(ContextCompat.getColor(KuJieYongMessActivity.this, R.color.red));
                                }else {
                                    flag=3;
                                    mAgree_Btn.setVisibility(View.GONE);
                                    mJie_Status_Tv.setText("已归还");
                                    mJie_Status_Tv.setTextColor(ContextCompat.getColor(KuJieYongMessActivity.this, R.color.color_23b880));
                                }

                            }

                            if (jieYongDetailsRows.getAssetList() != null) {
                                mList = jieYongDetailsRows.getAssetList();
                                mAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(KuJieYongMessActivity.this, "获取详情列表失败", Toast.LENGTH_SHORT).show();

                            }

                        }


                    } else {
                        Toast.makeText(KuJieYongMessActivity.this, "登录过期，请重新登录", Toast.LENGTH_SHORT).show();

                    }


                } else {
                    Toast.makeText(KuJieYongMessActivity.this, "获取详情失败", Toast.LENGTH_SHORT).show();
                }


            } else if (msg.what == 1010) {
                mAgree_Btn.setClickable(true);
                BallProgressUtils.dismisLoading();
                Toast.makeText(KuJieYongMessActivity.this, "连接服务器失败，请重新尝试", Toast.LENGTH_SHORT).show();
            }else if (msg.what == 7) {//提交确认借用，确认归还接口
                mAgree_Btn.setClickable(true);
                BallProgressUtils.dismisLoading();
                String mes = (String) msg.obj;
                Log.e("提交领用=", mes);
                Object o = gson.fromJson(mes, CaiGouApplySubmit.class);
                if (o != null && o instanceof CaiGouApplySubmit) {
                    CaiGouApplySubmit caiGouApplySubmit = (CaiGouApplySubmit) o;
                    if ("0".equals(caiGouApplySubmit.getCode())) {
                        Toast.makeText(KuJieYongMessActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK, intent);
                        finish();
                    } else if ("-1".equals(caiGouApplySubmit.getCode())) {
                        Toast.makeText(KuJieYongMessActivity.this, "您的账号已过期请重新登录，请重新登录", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(KuJieYongMessActivity.this, "资产数量不能全为0，请填写数量", Toast.LENGTH_SHORT).show();
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
        setContentView(R.layout.activity_ku_jie_yong_mess);
        mAll_RL= (RelativeLayout) findViewById(R.id.activity_ku_jie_yong_mess);
        initUI();
        out_url=URLTools.urlBase+URLTools.ku_jieyong_getout;//确认借用
        in_url=URLTools.urlBase+URLTools.ku_jieyong_getin;//确认归还
        intent = getIntent();
        mReferId = intent.getLongExtra("referId", -1);
        okHttpManager = OkHttpManager.getInstance();
        if (mReferId != -1) {
            //请求详情
            JY_Url = URLTools.urlBase + URLTools.jieyong_details_url + "id=" + mReferId;
            okHttpManager.getMethod(false, JY_Url, "借用申请详情", handler, 15);
        } else {
            Toast.makeText(this, "获取详情ID错误", Toast.LENGTH_SHORT).show();
        }
    }

    private void initUI() {
        mBack_img = (ImageView) findViewById(R.id.back_img);
        mBack_img.setOnClickListener(this);

        mJie_Status_Tv = (TextView) findViewById(R.id.jieyong_status_tv);
        mBumen_tv = (TextView) findViewById(R.id.bumen_name);
        mpersion_tv = (TextView) findViewById(R.id.person_edit);
        mBeizhu = (TextView) findViewById(R.id.beizhu_edit);
        mTime_tv = (TextView) findViewById(R.id.time_msg);
        mStart_time_tv = (TextView) findViewById(R.id.jie_time_content);
        mEnd_time_tv = (TextView) findViewById(R.id.return_time_content);

        mAgree_Btn = (TextView) findViewById(R.id.agree_btn);
        mAgree_Btn.setOnClickListener(this);

        View header = LayoutInflater.from(this).inflate(R.layout.add_item_table_head, null);
        mAdapter = new KuJieYongAdapter();
        mListview = (ListView) findViewById(R.id.spz_jieyong_listview_id);
        mListview.setAdapter(mAdapter);
        mListview.addHeaderView(header);
    }

    @Override
    public void onClick(View view) {
        int id=view.getId();
        if (id==mBack_img.getId()){
            finish();
        }else if (id==mAgree_Btn.getId()){
            if (mReferId != -1) {

                mAgree_Btn.setClickable(false);
                BallProgressUtils.showLoading(this, mAll_RL);
                Map<Object, Object> map = new HashMap<>();

                if (flag==1){//确认借用
                    StringBuilder strId = new StringBuilder();
                    for (int i = 0; i < mList.size(); i++) {
                        strId.append(mList.get(i).getId() + "," + mList.get(i).getTotalNum() + ";");//这里需要添加数量id,num;id,num;
                    }
                    map.put("id", mReferId);
                    map.put("assetsIds", strId.toString());
                    Log.e("提交借用确认借用的id集合", strId.toString());
                    okHttpManager.postMethod(false, out_url, "提交借用确认借用", map, handler, 7);

                }else if (flag==2){//确认归还
                    StringBuilder strId = new StringBuilder();
                    for (int i = 0; i < mList.size(); i++) {
                        strId.append(mList.get(i).getId() + "," + mList.get(i).getNum() + ";");//这里需要添加数量id,num;id,num;
                    }
                    map.put("id", mReferId);
                    map.put("assetsIds", strId.toString());
                    Log.e("提交借用确认归还的id集合", strId.toString());
                    okHttpManager.postMethod(false, in_url, "提交借用确认归还", map, handler, 7);
                }



            } else {
                Toast.makeText(this, "获取详情ID错误,请重新获取", Toast.LENGTH_SHORT).show();
            }



        }
    }

    //借明细适配器
    class KuJieYongAdapter extends BaseAdapter {

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
            KuJYHolder kuJYHolder = null;

            view = LayoutInflater.from(KuJieYongMessActivity.this).inflate(R.layout.add_item_table_edit, null);
            kuJYHolder = new KuJYHolder();
            kuJYHolder.status = view.findViewById(R.id.select_state);
            kuJYHolder.bianhao = view.findViewById(R.id.bianhao_tv);
            kuJYHolder.leibie = view.findViewById(R.id.leibie_tv);
            kuJYHolder.mingcheng = view.findViewById(R.id.mingcheng_tv);


            //设置数据
            kuJYHolder.status.setBackgroundResource(R.drawable.bumen_box_select);
            kuJYHolder.leibie.setText(mList.get(i).getCategoryName() + "");
            kuJYHolder.mingcheng.setText(mList.get(i).getAssetsName() + "");

            if (flag==1) {//未借用，edittext可编辑
                kuJYHolder.bianhao.setEnabled(true);
                kuJYHolder.bianhao.setText(mList.get(i).getTotalNum() + "");//数量
            } else if (flag==2){//未归还，edittext不可编辑
                kuJYHolder.bianhao.setText(mList.get(i).getNum() + "");//数量
                kuJYHolder.bianhao.setEnabled(false);
            }else if(flag==3){//已归还，edittext不可编辑
                kuJYHolder.bianhao.setText(mList.get(i).getNum() + "");//数量
                kuJYHolder.bianhao.setEnabled(false);
            }

            final int position = i;
            Log.e("position=", position + "");
            final KuJYHolder kuJYHolder1 = kuJYHolder;
            kuJYHolder.bianhao.addTextChangedListener(new TextWatcher() {
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
                            Toast.makeText(KuJieYongMessActivity.this, "请填写正确的数量", Toast.LENGTH_SHORT).show();
                            kuJYHolder1.bianhao.setText("0");
                        } else {
                            Integer num = Integer.valueOf(s);
                            mList.get(position).setTotalNum(num);
                            Log.e("edit -position=", position + "");
                            Log.e("数量=", mList.get(position).getTotalNum() + "");
                        }

                    } else {
                        //若不填数量，将以原始数量为准
                        Toast.makeText(KuJieYongMessActivity.this, "请填写数量", Toast.LENGTH_SHORT).show();
                    }

                }
            });

            return view;
        }

        class KuJYHolder {
            TextView status, leibie, mingcheng;//状态,类别,物品名称，
            EditText bianhao;
        }
    }

}
