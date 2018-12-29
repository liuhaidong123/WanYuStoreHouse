package com.storehouse.wanyu.activity.ApplyActivity;

import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
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
import com.scwang.smartrefresh.header.CircleHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.storehouse.wanyu.Bean.CaiGouApplySubmit;
import com.storehouse.wanyu.Bean.OneSelfRoot;
import com.storehouse.wanyu.Bean.OneSelfRows;
import com.storehouse.wanyu.IPAddress.URLTools;
import com.storehouse.wanyu.MyUtils.BallProgressUtils;
import com.storehouse.wanyu.MyUtils.SharedPrefrenceTools;
import com.storehouse.wanyu.OkHttpUtils.OkHttpManager;
import com.storehouse.wanyu.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//以旧换新
public class ApplyNewOldActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView mBack;
    private TextView mSubmit_Btn, mBumen_Tv, mPerson_tv, mWuPing_Name, mSureBtn;//申请部门，申请人，物品名称，
    private EditText mBeizhu_Edit, mNum_edit;//备注说明,物品数量
    private SmartRefreshLayout smart_Refresh;//弹框中的刷新
    private EditText search_Edit;//弹框中的搜索
    private int num = -1;//标记点击后的数量
    private int start = 0, limit = 30;
    private boolean flag = true;//true表示刷新，false表示加载更多
    private RelativeLayout mSelctBumen_Rl, mAll_Rl;
    private AlertDialog.Builder mBuilderBuMen;//个人物品名称弹框
    private AlertDialog mAlertDialogBuMen;
    private View mAddAlertView, mAddHeaderView;
    private ListView mBumen_ListView;
    private BumenAdapter bumenAdapter;
    private List<OneSelfRows> mBuMenList = new ArrayList<>();
    private long assetId = -1;
    private String mOneSelfUrl, mSubmitUrl;
    private OkHttpManager okHttpManager;
    private Gson mgson = new Gson();
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 11) {//个人资产列表
                String mes = (String) msg.obj;
                Object o = mgson.fromJson(mes, OneSelfRoot.class);
                if (o != null && o instanceof OneSelfRoot) {
                    OneSelfRoot oneSelfRoot = (OneSelfRoot) o;

                    if (oneSelfRoot != null && "0".equals(oneSelfRoot.getCode())) {
                        if (oneSelfRoot.getRows() != null) {

                            if (flag) {
                                mBuMenList = oneSelfRoot.getRows();
                                if (oneSelfRoot.getRows().size() == 0) {
                                    Toast.makeText(ApplyNewOldActivity.this, "暂无资产", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                for (int i = 0; i < oneSelfRoot.getRows().size(); i++) {
                                    mBuMenList.add(oneSelfRoot.getRows().get(i));
                                }
                                if (oneSelfRoot.getRows().size() == 0) {
                                    Toast.makeText(ApplyNewOldActivity.this, "已加载全部资产", Toast.LENGTH_SHORT).show();
                                }
                            }

                            bumenAdapter.notifyDataSetChanged();
                        }


                    } else {
                        Toast.makeText(ApplyNewOldActivity.this, "登录过期，请重新登录", Toast.LENGTH_SHORT).show();
                    }


                } else {
                    Toast.makeText(ApplyNewOldActivity.this, "获取个人资产列表数据错误", Toast.LENGTH_SHORT).show();

                }


            } else if (msg.what == 1010) {
                mSubmit_Btn.setClickable(false);
                Toast.makeText(ApplyNewOldActivity.this, "连接服务器失败,请重新尝试", Toast.LENGTH_SHORT).show();
            } else if (msg.what == 12) {//提交依旧换新申请接口
                mSubmit_Btn.setClickable(false);
                BallProgressUtils.dismisLoading();
                String mes = (String) msg.obj;
                Log.e("提交领用=", mes);
                Object o = mgson.fromJson(mes, CaiGouApplySubmit.class);
                if (o != null && o instanceof CaiGouApplySubmit) {
                    CaiGouApplySubmit caiGouApplySubmit = (CaiGouApplySubmit) o;
                    if ("0".equals(caiGouApplySubmit.getCode())) {
                        Toast.makeText(ApplyNewOldActivity.this, "提交申请成功", Toast.LENGTH_SHORT).show();
                        finish();
                    } else if ("-1".equals(caiGouApplySubmit.getCode())) {
                        Toast.makeText(ApplyNewOldActivity.this, "您的账号已过期请重新登录，请重新登录", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ApplyNewOldActivity.this, "提交申请失败", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_new_old);
        mAll_Rl = (RelativeLayout) findViewById(R.id.activity_apply_new_old);
        initUI();
    }

    private void initUI() {
        mBack = (ImageView) findViewById(R.id.back_img);
        mBack.setOnClickListener(this);
        //提交
        mSubmit_Btn = (TextView) findViewById(R.id.submit_btn);
        mSubmit_Btn.setOnClickListener(this);
        //选择部门
        mSelctBumen_Rl = (RelativeLayout) findViewById(R.id.bumen_btn);
        mSelctBumen_Rl.setOnClickListener(this);
        mBumen_Tv = (TextView) findViewById(R.id.bumen_name);//申请部门
        mPerson_tv = (TextView) findViewById(R.id.person_edit);//申请人
        mWuPing_Name = (TextView) findViewById(R.id.name_edit);//物品名称
        mBeizhu_Edit = (EditText) findViewById(R.id.beizhu_edit);
        mNum_edit = (EditText) findViewById(R.id.num_edit);
        okHttpManager = OkHttpManager.getInstance();
        mOneSelfUrl = URLTools.urlBase + URLTools.newold_property_list;
        Map<Object, Object> map = new HashMap<>();
        map.put("name", "");
        map.put("start", start);
        map.put("limit", limit);
        okHttpManager.postMethod(false, mOneSelfUrl, "个人资产列表", map, mHandler, 11);
        String bumen = (String) SharedPrefrenceTools.getValueByKey("departmentName", "");
        String name = (String) SharedPrefrenceTools.getValueByKey("truename", "");
        mBumen_Tv.setText(bumen);
        mPerson_tv.setText(name);
        //弹框中的个人资产列表
        mBuilderBuMen = new AlertDialog.Builder(this, R.style.dialogNoBg);
        mBuilderBuMen.setCancelable(false);
        mAlertDialogBuMen = mBuilderBuMen.create();
        mAddAlertView = LayoutInflater.from(this).inflate(R.layout.add_alertbox, null);
        mAlertDialogBuMen.setView(mAddAlertView);
        mAddHeaderView = LayoutInflater.from(this).inflate(R.layout.add_item_table_head, null);
        mBumen_ListView = mAddAlertView.findViewById(R.id.add_listview);//弹框中的listview
        mBumen_ListView.addHeaderView(mAddHeaderView);//添加头
        mSureBtn = mAddAlertView.findViewById(R.id.add_sure_btn);//弹框中的确认
        mSureBtn.setVisibility(View.GONE);
        bumenAdapter = new BumenAdapter();
        mBumen_ListView.setAdapter(bumenAdapter);
        mBack = (ImageView) findViewById(R.id.back_img);
        mBack.setOnClickListener(this);
        //点击选择个人资产
        mBumen_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView textView = view.findViewById(R.id.select_state);
                if (i != 0) {
                    mWuPing_Name.setText(mBuMenList.get(i - 1).getAssetName() + "");
                    mNum_edit.setText(mBuMenList.get(i - 1).getNum() + "");
                    num = mBuMenList.get(i - 1).getNum();
                    assetId = mBuMenList.get(i - 1).getId();
                    mAlertDialogBuMen.dismiss();
                    Log.e("物品id=", assetId + "");
                }

            }
        });


        smart_Refresh = mAddAlertView.findViewById(R.id.smart_refresh);//弹框中的刷新
        smart_Refresh.setRefreshHeader(new CircleHeader(this));
        smart_Refresh.setRefreshFooter(new BallPulseFooter(this).setSpinnerStyle(SpinnerStyle.Scale).setAnimatingColor(ContextCompat.getColor(this, R.color.color_1c82d4)));


        smart_Refresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshlayout.finishRefresh(2000/*,false*/);//传入false表示刷新失败
                flag = true;
                start = 0;
                limit = 30;
                Map<Object, Object> map = new HashMap<>();
                map.put("name", search_Edit.getText().toString().trim());
                map.put("start", start);
                map.put("limit", limit);
                okHttpManager.postMethod(false, mOneSelfUrl, "获取资产列表接口", map, mHandler, 11);//资产列表接口
            }
        });
        smart_Refresh.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                refreshlayout.finishLoadmore(2000/*,false*/);//传入false表示加载失败
                flag = false;
                start += 30;
                limit = 30;
                Map<Object, Object> map = new HashMap<>();
                map.put("name", search_Edit.getText().toString().trim());
                map.put("start", start);
                map.put("limit", limit);
                okHttpManager.postMethod(false, mOneSelfUrl, "获取资产列表接口", map, mHandler, 11);//资产列表接口
            }
        });


        search_Edit = mAddAlertView.findViewById(R.id.search_edit);//弹框中的搜索按钮
        //点击搜索按钮后，回调：
        search_Edit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    //按下的时候会会执行：手指按下和手指松开俩个过程
                    if (event.getAction() == KeyEvent.ACTION_UP) {
                        if ("".equals(search_Edit.getText().toString().trim())) {
                            Toast.makeText(ApplyNewOldActivity.this, "请输入资产名称", Toast.LENGTH_SHORT).show();
                        } else {
                            flag = true;
                            start = 0;
                            limit = 30;
                            Map<Object, Object> map = new HashMap<>();
                            map.put("name", search_Edit.getText().toString().trim());
                            map.put("start", start);
                            map.put("limit", limit);
                            okHttpManager.postMethod(false, mOneSelfUrl, "获取资产列表接口", map, mHandler, 11);//资产列表接口
                            ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                        }

                    }
                    return true;
                }
                return false;
            }
        });
        search_Edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                start = 0;
                limit = 30;
                flag = true;
                Map<Object, Object> map = new HashMap<>();
                map.put("name", charSequence);
                map.put("start", start);
                map.put("limit", limit);
                okHttpManager.postMethod(false, mOneSelfUrl, "获取资产列表接口", map, mHandler, 11);//资产列表接口
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //提交
        mSubmit_Btn = (TextView) findViewById(R.id.submit_btn);
        mSubmit_Btn.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == mBack.getId()) {
            finish();
            //如果键盘显示，就隐藏
            ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        } else if (id == mSubmit_Btn.getId()) {//提交
            if (checkContent()) {
                mSubmit_Btn.setClickable(false);
                BallProgressUtils.showLoading(this, mAll_Rl);
                mSubmitUrl = URLTools.urlBase + URLTools.submit_newold_apply;
                Map<Object, Object> map = new HashMap<>();
                map.put("assetId", assetId);
                map.put("comment", mBeizhu_Edit.getText().toString().trim());
                map.put("totalNum",Integer.valueOf(mNum_edit.getText().toString()));
                okHttpManager.postMethod(false, mSubmitUrl, "提交以旧换新申请接口", map, mHandler, 12);
                ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

            }
        } else if (id == mSelctBumen_Rl.getId()) {//显示部门弹框
            if (mBuMenList.size() == 0) {
                okHttpManager.getMethod(false, mOneSelfUrl, "个人资产列表", mHandler, 11);
                Toast.makeText(this, "正在获取个人资产列表", Toast.LENGTH_SHORT).show();
            } else {
                mAlertDialogBuMen.show();
            }
        }
    }

    /**
     * 判断上传时的内容
     *
     * @return
     */
    private boolean checkContent() {
        if ("".equals(mBumen_Tv.getText().toString().trim())) {
            Toast.makeText(this, "无法获取申请部门，请重新尝试登录", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            if ("".equals(mPerson_tv.getText().toString().trim())) {
                Toast.makeText(this, "无法获取申请人，请重新尝试登录", Toast.LENGTH_SHORT).show();
                return false;
            } else {
                if ("".equals(mWuPing_Name.getText().toString().trim())) {
                    Toast.makeText(this, "请选择物品", Toast.LENGTH_SHORT).show();
                    return false;
                } else {
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

//                    if ("".equals(mBeizhu_Edit.getText().toString().trim())) {
//                        Toast.makeText(this, "请填写备注", Toast.LENGTH_SHORT).show();
//                        return false;
//                    } else {
//                        return true;
//                    }


                }


            }
        }
    }

    //物品名称适配器
    class BumenAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mBuMenList.size() == 0 ? 0 : mBuMenList.size();
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
            WeiXiuHolder lyHolder = null;
            if (view == null) {
                view = LayoutInflater.from(ApplyNewOldActivity.this).inflate(R.layout.add_item_table, null);
                lyHolder = new WeiXiuHolder();
                lyHolder.status = view.findViewById(R.id.select_state);
                lyHolder.bianhao = view.findViewById(R.id.bianhao_tv);
                lyHolder.leibie = view.findViewById(R.id.leibie_tv);
                lyHolder.mingcheng = view.findViewById(R.id.mingcheng_tv);
                view.setTag(lyHolder);
            } else {
                lyHolder = (WeiXiuHolder) view.getTag();

            }
            //设置数据
            lyHolder.bianhao.setText(mBuMenList.get(i).getNum() + "");
            lyHolder.leibie.setText(mBuMenList.get(i).getCategoryName() + "");
            lyHolder.mingcheng.setText(mBuMenList.get(i).getAssetName() + "");

            return view;
        }

        class WeiXiuHolder {
            TextView status, bianhao, leibie, mingcheng;//状态，序号，物品名称，编号，类别
        }
    }

}
