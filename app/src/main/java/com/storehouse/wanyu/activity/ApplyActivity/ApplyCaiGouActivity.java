package com.storehouse.wanyu.activity.ApplyActivity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.storehouse.wanyu.Bean.BuMenRoot;
import com.storehouse.wanyu.Bean.CaiGouApplySubmit;
import com.storehouse.wanyu.Bean.PropertyMessage;
import com.storehouse.wanyu.Bean.Rows;
import com.storehouse.wanyu.IPAddress.URLTools;
import com.storehouse.wanyu.MyUtils.BallProgressUtils;
import com.storehouse.wanyu.MyUtils.SharedPrefrenceTools;
import com.storehouse.wanyu.MyUtils.TwoBallRotationProgressBar;
import com.storehouse.wanyu.OkHttpUtils.OkHttpManager;
import com.storehouse.wanyu.R;
import com.storehouse.wanyu.activity.LoginActivity.LoginActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Response;

//采购申请
public class ApplyCaiGouActivity extends AppCompatActivity implements View.OnClickListener {
    private RelativeLayout mAll_RL;
    private ImageView mBack;
    private TextView mSubmitBtn, mBumen_Tv, mLeiBie_Tv;//提交，部门，单位，类别
    private EditText mName_Edit, mGuiGe_Edit, mJiaGe_Edit, mDanwei_TV, mShuLiang_Edit, mChangJia_Edit, mLiYou_Edit;
    private RelativeLayout mBuMen_Rl, mDanwei_Rl, mLeiBie_Rl;//部门，单位，类别
    private AlertDialog.Builder mBuilderDanWei, mBuilderLeiBie;
    private AlertDialog mAlertDialogDanWei, mAlertDialogLeiBie;
    private ListView mDanwei_ListView, mLeibie_ListView;
    private List<Rows> mBuMenList = new ArrayList<>();
    private List<String> mDanWeiList = new ArrayList<>();
    private List<String> mLeiBieList = new ArrayList<>();
    private int flag = 0;
    private OkHttpManager mOkHttpManager;
    private String submitUrl;

    private Gson gson = new Gson();
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 3) {//提交采购申请接口
                mSubmitBtn.setClickable(true);
                BallProgressUtils.dismisLoading();
                String mes = (String) msg.obj;
                Log.e("提交采购申请=", mes);
                Object o = gson.fromJson(mes, CaiGouApplySubmit.class);
                if (o != null && o instanceof CaiGouApplySubmit) {
                    CaiGouApplySubmit caiGouApplySubmit = (CaiGouApplySubmit) o;
                    if ("0".equals(caiGouApplySubmit.getCode())) {
                        Toast.makeText(ApplyCaiGouActivity.this, "提交申请成功", Toast.LENGTH_SHORT).show();
                        finish();
                    } else if ("-1".equals(caiGouApplySubmit.getCode())) {
                        Toast.makeText(ApplyCaiGouActivity.this, "您的账号已过期请重新登录，请重新登录", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ApplyCaiGouActivity.this, "提交申请失败", Toast.LENGTH_SHORT).show();
                    }

                }


            } else if (msg.what == 1010) {
                mSubmitBtn.setClickable(true);
                BallProgressUtils.dismisLoading();
                Toast.makeText(ApplyCaiGouActivity.this, "连接服务器失败，请重新尝试", Toast.LENGTH_SHORT).show();
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_cai_gou);
        mAll_RL = (RelativeLayout) findViewById(R.id.activity_apply_cai_gou);
        initUI();
    }

    private void initUI() {

        submitUrl = URLTools.urlBase + URLTools.submit_caigou_apply;//提交采购申请
        mOkHttpManager = OkHttpManager.getInstance();
        String name = (String) SharedPrefrenceTools.getValueByKey("departmentName", "");
        mBack = (ImageView) findViewById(R.id.back_img);
        mBack.setOnClickListener(this);
        mSubmitBtn = (TextView) findViewById(R.id.submit_btn);
        mSubmitBtn.setOnClickListener(this);
        // 选择部门，单位，类别按钮
        mBumen_Tv = (TextView) findViewById(R.id.bumen_name);
        mBumen_Tv.setText(name);
        mLeiBie_Tv = (TextView) findViewById(R.id.leibie_name);
        mBuMen_Rl = (RelativeLayout) findViewById(R.id.bumen_btn);
        mBuMen_Rl.setOnClickListener(this);
        mDanwei_Rl = (RelativeLayout) findViewById(R.id.danwei_btn);
        mDanwei_Rl.setOnClickListener(this);
        mLeiBie_Rl = (RelativeLayout) findViewById(R.id.leibie_btn);
        mLeiBie_Rl.setOnClickListener(this);
        //输入框
        mName_Edit = (EditText) findViewById(R.id.mingcheng_edit);
        mGuiGe_Edit = (EditText) findViewById(R.id.guige_edit);
        mDanwei_TV = (EditText) findViewById(R.id.danwei_name);
        mJiaGe_Edit = (EditText) findViewById(R.id.jiage_edit);
        mShuLiang_Edit = (EditText) findViewById(R.id.shuliang_edit);
        mChangJia_Edit = (EditText) findViewById(R.id.changjia_edit);
        mLiYou_Edit = (EditText) findViewById(R.id.liyou_edit);

        //单位弹框
        mBuilderDanWei = new AlertDialog.Builder(this);
        mAlertDialogDanWei = mBuilderDanWei.create();
        View view2 = LayoutInflater.from(this).inflate(R.layout.danwei_alert, null);
        mDanwei_ListView = view2.findViewById(R.id.danwei_listview);
        mDanWeiList.add("个");
        mDanWeiList.add("台");
        mDanWeiList.add("只");
        mDanWeiList.add("箱");
        mDanWeiList.add("袋");
        mAlertDialogDanWei.setView(view2);
        mDanwei_ListView.setAdapter(new DanWeiAdapter());
        mDanwei_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mDanwei_TV.setText(mDanWeiList.get(i));
                mAlertDialogDanWei.dismiss();
            }
        });
        //类别弹框
        mBuilderLeiBie = new AlertDialog.Builder(this);
        mAlertDialogLeiBie = mBuilderLeiBie.create();
        View view3 = LayoutInflater.from(this).inflate(R.layout.leibie_alert, null);
        mLeibie_ListView = view3.findViewById(R.id.leibie_listview);
        mLeiBieList.add("计划内");
        mLeiBieList.add("计划外");
        mAlertDialogLeiBie.setView(view3);
        mLeibie_ListView.setAdapter(new LeiBieAdapter());
        mLeibie_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    flag = 1;//1计划内
                }
                if (i == 1) {
                    flag = 2;//2计划外
                }
                mLeiBie_Tv.setText(mLeiBieList.get(i));
                mAlertDialogLeiBie.dismiss();
            }
        });

//        //从资产详情跳过来
//        Intent intent = getIntent();
//        PropertyMessage propertyMessage = (PropertyMessage) intent.getSerializableExtra("bean");
//        if (propertyMessage != null) {
//            mName_Edit.setText(propertyMessage.getAssetName());
//            mGuiGe_Edit.setText(propertyMessage.getSpecTyp());
//            mDanwei_TV.setText(propertyMessage.getUnit());
//            mShuLiang_Edit.setText(propertyMessage.getNum()+"");
//
//        }

    }

    //判断每个内容是否填写
    private boolean checkContent() {
        if ("".equals(mBumen_Tv.getText().toString().trim())) {
            Toast.makeText(this, "无法获取申请部门,请重新登录", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            if ("".equals(mName_Edit.getText().toString().trim())) {
                Toast.makeText(this, "请填写物品名称", Toast.LENGTH_SHORT).show();
                return false;
            } else {
//                if ("".equals(mGuiGe_Edit.getText().toString().trim())) {
//                    Toast.makeText(this, "请填写规格型号", Toast.LENGTH_SHORT).show();
//                    return false;
//                } else {
                if ("".equals(mDanwei_TV.getText().toString().trim())) {
                    Toast.makeText(this, "请填写计量单位", Toast.LENGTH_SHORT).show();
                    return false;
                } else {
//                        if ("".equals(mJiaGe_Edit.getText().toString().trim())) {
//                            Toast.makeText(this, "请填写正确的价格", Toast.LENGTH_SHORT).show();
//                            return false;
//                        } else {
                    if ("".equals(mShuLiang_Edit.getText().toString().trim())) {
                        Toast.makeText(this, "请填写数量", Toast.LENGTH_SHORT).show();
                        return false;
                    } else {
//                                if ("".equals(mChangJia_Edit.getText().toString().trim())) {
//                                    Toast.makeText(this, "请填写生产厂家", Toast.LENGTH_SHORT).show();
//                                    return false;
//                                } else {
                        if ("".equals(mLeiBie_Tv.getText().toString().trim())) {
                            Toast.makeText(this, "请选择采购类别", Toast.LENGTH_SHORT).show();
                            return false;
                        } else {
//                                        if ("".equals(mLiYou_Edit.getText().toString().trim())) {
//                                            Toast.makeText(this, "请填写采购理由", Toast.LENGTH_SHORT).show();
//                                            return false;
//                                        } else {
//                                            return true;
//                                        }

                            return true;
                        }
                        //  }
                    }
                    //}
                }

                // }
            }
        }

    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            //返回
            case R.id.back_img:
                finish();
                //如果键盘显示，就隐藏
                ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                break;
            //提交
            case R.id.submit_btn:
                if (checkContent()) {
                    mSubmitBtn.setClickable(false);
                    BallProgressUtils.showLoading(this, mAll_RL);
                    Map<Object, Object> map = new HashMap<>();
                    map.put("departmentCode", mBumen_Tv.getText().toString().trim());
                    map.put("assetName", mName_Edit.getText().toString().trim());
                    map.put("specTyp", mGuiGe_Edit.getText().toString().trim());
                    map.put("unit", mDanwei_TV.getText().toString().trim());
                    // map.put("worth", mJiaGe_Edit.getText().toString().trim());
                    map.put("buyCount", mShuLiang_Edit.getText().toString().trim());
                    map.put("producerName", mChangJia_Edit.getText().toString().trim());
                    map.put("buyCate", flag);
                    map.put("buyReason", mLiYou_Edit.getText().toString().trim());
                    mOkHttpManager.postMethod(false, submitUrl, "采购申请提交接口", map, mHandler, 3);
                    ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                }

                break;

            //选择计量单位
            case R.id.danwei_btn:
                mAlertDialogDanWei.show();
                break;
            //选择采购类别
            case R.id.leibie_btn:
                mAlertDialogLeiBie.show();
                break;

        }
    }

    //申请部门适配器
    class BumenAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mBuMenList.size();
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

            BuMenHolder buMenHolder = null;
            if (view == null) {
                view = LayoutInflater.from(ApplyCaiGouActivity.this).inflate(R.layout.bumen_alert_item, null);
                buMenHolder = new BuMenHolder();
                buMenHolder.textView = view.findViewById(R.id.bumen_item_tv);
                view.setTag(buMenHolder);
            } else {
                buMenHolder = (BuMenHolder) view.getTag();
            }
            buMenHolder.textView.setText(mBuMenList.get(i).getDepartmentName());
            return view;
        }

        class BuMenHolder {
            TextView textView;
        }
    }

    //计量单位适配器
    class DanWeiAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mDanWeiList.size();
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

            DanWeiHolder danWeiHolder = null;
            if (view == null) {
                view = LayoutInflater.from(ApplyCaiGouActivity.this).inflate(R.layout.danwei_alert_item, null);
                danWeiHolder = new DanWeiHolder();
                danWeiHolder.textView = view.findViewById(R.id.danwei_item_tv);
                view.setTag(danWeiHolder);
            } else {
                danWeiHolder = (DanWeiHolder) view.getTag();
            }
            danWeiHolder.textView.setText(mDanWeiList.get(i));
            return view;
        }

        class DanWeiHolder {
            TextView textView;
        }
    }

    //采购类别适配器
    class LeiBieAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mLeiBieList.size();
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

            LeiBieHolder leiBieHolder = null;
            if (view == null) {
                view = LayoutInflater.from(ApplyCaiGouActivity.this).inflate(R.layout.leibie_alert_item, null);
                leiBieHolder = new LeiBieHolder();
                leiBieHolder.textView = view.findViewById(R.id.leibie_item_tv);
                view.setTag(leiBieHolder);
            } else {
                leiBieHolder = (LeiBieHolder) view.getTag();
            }
            leiBieHolder.textView.setText(mLeiBieList.get(i));
            return view;
        }

        class LeiBieHolder {
            TextView textView;
        }
    }
}
