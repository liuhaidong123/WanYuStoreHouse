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
import com.storehouse.wanyu.Bean.TuiKuDetailsListBean;
import com.storehouse.wanyu.Bean.TuiKuDetailsRoot;
import com.storehouse.wanyu.Bean.TuiKuDetailsRows;
import com.storehouse.wanyu.IPAddress.URLTools;
import com.storehouse.wanyu.MyUtils.BallProgressUtils;
import com.storehouse.wanyu.OkHttpUtils.OkHttpManager;
import com.storehouse.wanyu.R;
import com.storehouse.wanyu.activity.AllSPZDetailsActivity.SpzTuiKuApplyDetailsActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//出库入库中退库详情
public class KuTuiKuMessActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView mBack_Img;
    private TextView mBumen_Tv, mName_Tv, mReason_tv, mAgree_Btn, status_Tv;
    private RelativeLayout mAll_rl;
    private TKAdapter mAdapter;
    private ListView mListView;
    private List<TuiKuDetailsListBean> mList = new ArrayList<>();
    private OkHttpManager okHttpManager;
    private Gson gson = new Gson();
    private String TK_Url,submit_url;//详情url
    private long mReferId;//请求详情需要的id
    private boolean flag = true;//true表示已入库，false表示未入库
    private Intent intent;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 15) {
                String mes = (String) msg.obj;
                Object o = gson.fromJson(mes, TuiKuDetailsRoot.class);
                if (o != null && o instanceof TuiKuDetailsRoot) {
                    TuiKuDetailsRoot tuiKuDetailsRoot = (TuiKuDetailsRoot) o;
                    if (tuiKuDetailsRoot != null && "0".equals(tuiKuDetailsRoot.getCode())) {
                        TuiKuDetailsRows tuiKuDetailsRows = tuiKuDetailsRoot.getAssetReturn();
                        if (tuiKuDetailsRows != null) {
                            mBumen_Tv.setText(tuiKuDetailsRows.getDepartmentName() + "");
                            mName_Tv.setText(tuiKuDetailsRows.getUserName() + "");

                            if ("".equals(tuiKuDetailsRows.getComment())){
                                mReason_tv.setText("---");
                            }else {
                                mReason_tv.setText(tuiKuDetailsRows.getComment() + "");
                            }

                            if ("".equals(tuiKuDetailsRows.getInboundDateString())) {
                                flag=false;
                                mAgree_Btn.setVisibility(View.VISIBLE);
                                status_Tv.setText("未入库");
                                status_Tv.setTextColor(ContextCompat.getColor(KuTuiKuMessActivity.this, R.color.color_dc8268));
                            } else {
                                flag=true;
                                mAgree_Btn.setVisibility(View.GONE);
                                status_Tv.setText("已入库");
                                status_Tv.setTextColor(ContextCompat.getColor(KuTuiKuMessActivity.this, R.color.color_23b880));
                            }


                            if (tuiKuDetailsRows.getAssetList() != null) {
                                mList = tuiKuDetailsRows.getAssetList();
                                mAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(KuTuiKuMessActivity.this, "获取详情列表失败", Toast.LENGTH_SHORT).show();
                            }

                        }


                    } else {
                        Toast.makeText(KuTuiKuMessActivity.this, "登录过期，请重新登录", Toast.LENGTH_SHORT).show();

                    }


                } else {
                    Toast.makeText(KuTuiKuMessActivity.this, "获取详情失败", Toast.LENGTH_SHORT).show();
                }


            } else if (msg.what == 1010) {
                mAgree_Btn.setClickable(true);
                BallProgressUtils.dismisLoading();
                Toast.makeText(KuTuiKuMessActivity.this, "连接服务器失败，请重新尝试", Toast.LENGTH_SHORT).show();
            }else if (msg.what == 7) {//提交lingyong申请接口
                mAgree_Btn.setClickable(true);
                BallProgressUtils.dismisLoading();
                String mes = (String) msg.obj;
                Log.e("入库接口=", mes);
                Object o = gson.fromJson(mes, CaiGouApplySubmit.class);
                if (o != null && o instanceof CaiGouApplySubmit) {
                    CaiGouApplySubmit caiGouApplySubmit = (CaiGouApplySubmit) o;
                    if ("0".equals(caiGouApplySubmit.getCode())) {
                        Toast.makeText(KuTuiKuMessActivity.this, "入库成功", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK, intent);
                        finish();
                    } else if ("-1".equals(caiGouApplySubmit.getCode())) {
                        Toast.makeText(KuTuiKuMessActivity.this, "您的账号已过期请重新登录，请重新登录", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(KuTuiKuMessActivity.this, "资产数量不能全为0，请填写数量", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ku_tui_ku_mess);
        mAll_rl= (RelativeLayout) findViewById(R.id.activity_ku_tui_ku_mess);
        intent = getIntent();
        mReferId = intent.getLongExtra("referId", -1);
        okHttpManager = OkHttpManager.getInstance();
        if (mReferId != -1) {
            //请求详情
            TK_Url = URLTools.urlBase + URLTools.tuiku_details_url + "id=" + mReferId;
            okHttpManager.getMethod(false, TK_Url, "退库详情", handler, 15);
        } else {
            Toast.makeText(this, "获取详情ID错误", Toast.LENGTH_SHORT).show();
        }
        submit_url=URLTools.urlBase + URLTools.ku_tuiku_agree;//同意入库
        initUI();
    }


    private void initUI(){
        mBack_Img = (ImageView) findViewById(R.id.back_img);
        mBack_Img.setOnClickListener(this);
        status_Tv= (TextView) findViewById(R.id.tuiku_status_tv);

        mBumen_Tv = (TextView) findViewById(R.id.cg_bumen_msg);
        mName_Tv = (TextView) findViewById(R.id.cg_name_msg);
        mReason_tv = (TextView) findViewById(R.id.cg_reason_msg);

        mAgree_Btn = (TextView) findViewById(R.id.agree_btn);
        mAgree_Btn.setOnClickListener(this);

        View header=  LayoutInflater.from(this).inflate(R.layout.add_item_table_head, null);
        mListView= (ListView) findViewById(R.id.spz_Lingxi_listview_id);
        mListView.addHeaderView(header);
        mAdapter=new TKAdapter();
        mListView.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View view) {
            int id=view.getId();
        if (id==mBack_Img.getId()){
            finish();
        }else if (id==mAgree_Btn.getId()){
            if (mReferId != -1) {
                StringBuilder strId = new StringBuilder();
                for (int i = 0; i < mList.size(); i++) {
                    strId.append(mList.get(i).getId() + "," + mList.get(i).getTotalNum() + ";");//这里需要添加数量id,num;id,num;
                }
                Log.e("提交退库的id集合", strId.toString());

                mAgree_Btn.setClickable(false);
                BallProgressUtils.showLoading(this, mAll_rl);
                Map<Object, Object> map = new HashMap<>();
                map.put("id", mReferId);
                map.put("assetsIds", strId.toString());
                okHttpManager.postMethod(false, submit_url, "同意入库接口", map, handler, 7);

            } else {
                Toast.makeText(this, "获取详情ID错误,请重新获取", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //领用用明细适配器
    class TKAdapter extends BaseAdapter {

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
            TuiKuHolder tuiKuHolder = null;

            view = LayoutInflater.from(KuTuiKuMessActivity.this).inflate(R.layout.add_item_table_edit, null);
            tuiKuHolder = new TuiKuHolder();
            tuiKuHolder.status = view.findViewById(R.id.select_state);
            tuiKuHolder.bianhao = view.findViewById(R.id.bianhao_tv);
            tuiKuHolder.leibie = view.findViewById(R.id.leibie_tv);
            tuiKuHolder.mingcheng = view.findViewById(R.id.mingcheng_tv);


            //设置数据
            tuiKuHolder.status.setBackgroundResource(R.drawable.bumen_box_select);

            tuiKuHolder.leibie.setText(mList.get(i).getCategoryName() + "");
            tuiKuHolder.mingcheng.setText(mList.get(i).getAssetsName() + "");

            if (flag) {//已入库状态下，edittext不可编辑
                tuiKuHolder.bianhao.setEnabled(false);
                tuiKuHolder.bianhao.setText(mList.get(i).getNum() + "");//数量
            } else {
                tuiKuHolder.bianhao.setEnabled(true);
                tuiKuHolder.bianhao.setText(mList.get(i).getTotalNum() + "");//数量
            }

            final int position = i;
            Log.e("position=", position + "");
            final TuiKuHolder finalLyHolder = tuiKuHolder;
            tuiKuHolder.bianhao.addTextChangedListener(new TextWatcher() {
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
                            Toast.makeText(KuTuiKuMessActivity.this, "请填写正确的数量", Toast.LENGTH_SHORT).show();
                            finalLyHolder.bianhao.setText("0");
                        } else {
                            Integer num = Integer.valueOf(s);
                            mList.get(position).setTotalNum(num);
                            Log.e("edit -position=", position + "");
                            Log.e("数量=", mList.get(position).getTotalNum() + "");
                        }

                    } else {
                        //若不填数量，将以原始数量为准
                        Toast.makeText(KuTuiKuMessActivity.this, "请填写数量", Toast.LENGTH_SHORT).show();
                    }

                }
            });

            return view;
        }

        class TuiKuHolder {
            TextView status, leibie, mingcheng;//状态,类别,物品名称，
            EditText bianhao;
        }
    }

}
