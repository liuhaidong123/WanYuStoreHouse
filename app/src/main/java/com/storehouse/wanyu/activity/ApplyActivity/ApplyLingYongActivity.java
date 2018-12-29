package com.storehouse.wanyu.activity.ApplyActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
import com.storehouse.wanyu.Bean.PropertyMessage;
import com.storehouse.wanyu.Bean.ZiChanRoot;
import com.storehouse.wanyu.Bean.ZiChanRow;
import com.storehouse.wanyu.IPAddress.URLTools;
import com.storehouse.wanyu.MyUtils.BallProgressUtils;
import com.storehouse.wanyu.MyUtils.MilliSecondToDate;
import com.storehouse.wanyu.MyUtils.SharedPrefrenceTools;
import com.storehouse.wanyu.OkHttpUtils.OkHttpManager;
import com.storehouse.wanyu.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//领用申请
public class ApplyLingYongActivity extends AppCompatActivity implements View.OnClickListener {
    private RelativeLayout mAll_RL;
    private ImageView mBack;
    private TextView mSubmit, mAdd_Btn, mDelete_Btn, mBumen_Tv, mPerson_Edit;
    private EditText mBeiZhu_Edit;
    private AlertDialog.Builder mAddAlertBuilder, mDeleteAlertBuilder;
    private AlertDialog mAddAlertDialog, mDeleteAlertDialog;
    private View mAddAlertView, mAddHeaderView, mDeleteAlertView, mDeleteHeaderView;
    private SmartRefreshLayout smart_Refresh;//弹框中的刷新
    private ListView mAddAlertLisview, mDeleteAlertLisview;//弹框中的listview
    private TextView mAddAlertSureBtn, mDeleteAlertSureBtn;//弹框中的确认,删除
    private EditText search_Edit;//弹框中的搜索
    private ZiChanAdapter ziChanAdapter;//弹框中的适配器
    private DeleteAdapter deleteAdapter;//删除弹框中的适配器
    private ListView mLingYongListView;//领用明细ListView
    private LingYongAdapter mLingYongAda;
    private List<ZiChanRow> mZiChanList = new ArrayList<>();
    private String mZiChanList_Url, mSubmit_lingyong_url;//资产列表接口，提交接口
    private int start = 0, limit = 30;
    private boolean flag = true;//true表示刷新，false表示加载更多
    private OkHttpManager mOkHttpManager;
    private Gson mGson = new Gson();
    private List<ZiChanRow> idList = new ArrayList<>();//存放选择后资产列表
    private List<Long> idLongList = new ArrayList<>();//存放选择后资产列表中每个资产的id
    //private List<EditText> editList = new ArrayList<>();
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 5) {
                try {
                    String mes = (String) msg.obj;
                    Object o = mGson.fromJson(mes, ZiChanRoot.class);
                    Log.e("资产列表信息=", mes);
                    if (o != null && o instanceof ZiChanRoot) {
                        ZiChanRoot ziChanRoot = (ZiChanRoot) o;
                        if (ziChanRoot != null && "0".equals(ziChanRoot.getCode())) {
                            if (ziChanRoot.getRows() != null) {

                                if (flag) {//刷新
                                    mZiChanList = ziChanRoot.getRows();
                                    //刷新时不会改变用户编辑过的数量
//                                    for (int i = 0; i < mZiChanList.size(); i++) {
//                                        for (int k = 0; k < idList.size(); k++) {
//                                            if (mZiChanList.get(i).getId() == idList.get(k).getId()) {
//                                                mZiChanList.get(i).setNum(idList.get(k).getNum());
//                                                break;
//                                            }
//                                        }
//                                    }
                                    if (ziChanRoot.getRows().size() == 0) {
                                        Toast.makeText(ApplyLingYongActivity.this, "暂无资产", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    for (int i = 0; i < ziChanRoot.getRows().size(); i++) {
                                        mZiChanList.add(ziChanRoot.getRows().get(i));
                                    }
                                    if (ziChanRoot.getRows().size() == 0) {
                                        Toast.makeText(ApplyLingYongActivity.this, "已加载全部资产", Toast.LENGTH_SHORT).show();
                                    }

                                }

                                ziChanAdapter.notifyDataSetChanged();
                            }

                        } else if (ziChanRoot != null && "-1".equals(ziChanRoot.getCode())) {
                            Toast.makeText(ApplyLingYongActivity.this, "您的账号已过期,请重新登录", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ApplyLingYongActivity.this, "获取资产列表失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (msg.what == 1010) {
                mSubmit.setClickable(true);
                Toast.makeText(ApplyLingYongActivity.this, "连接服务器失败，请重新尝试", Toast.LENGTH_SHORT).show();
            } else if (msg.what == 7) {//提交lingyong申请接口
                mSubmit.setClickable(true);
                BallProgressUtils.dismisLoading();
                String mes = (String) msg.obj;
                Log.e("提交领用=", mes);
                Object o = mGson.fromJson(mes, CaiGouApplySubmit.class);
                if (o != null && o instanceof CaiGouApplySubmit) {
                    CaiGouApplySubmit caiGouApplySubmit = (CaiGouApplySubmit) o;
                    if ("0".equals(caiGouApplySubmit.getCode())) {
                        Toast.makeText(ApplyLingYongActivity.this, "提交领用申请成功", Toast.LENGTH_SHORT).show();
                        finish();
                    } else if ("-1".equals(caiGouApplySubmit.getCode())) {
                        Toast.makeText(ApplyLingYongActivity.this, "您的账号已过期请重新登录，请重新登录", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ApplyLingYongActivity.this, "提交领用申请失败", Toast.LENGTH_SHORT).show();
                    }

                }

            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_ling_yong);
        mAll_RL = (RelativeLayout) findViewById(R.id.activity_apply_ling_yong);
        initUI();
    }

    private void initUI() {
        String bumen = (String) SharedPrefrenceTools.getValueByKey("departmentName", "");
        String name = (String) SharedPrefrenceTools.getValueByKey("truename", "");

        mZiChanList_Url = URLTools.urlBase + URLTools.lingyong_property_list;//资产列表接口
        mOkHttpManager = OkHttpManager.getInstance();
        Map<Object, Object> map = new HashMap<>();
        map.put("name", "");
        map.put("start", start);
        map.put("limit", limit);
        mOkHttpManager.postMethod(false, mZiChanList_Url, "获取资产列表接口", map, mHandler, 5);//资产列表接口
        mBack = (ImageView) findViewById(R.id.back_img);
        mBack.setOnClickListener(this);
        mSubmit = (TextView) findViewById(R.id.submit_btn);
        mSubmit.setOnClickListener(this);
        mAdd_Btn = (TextView) findViewById(R.id.add_tv);
        mDelete_Btn = (TextView) findViewById(R.id.delete_tv);
        mAdd_Btn.setOnClickListener(this);//添加
        mDelete_Btn.setOnClickListener(this); //删除
        mBumen_Tv = (TextView) findViewById(R.id.bumen_name);//部门
        mBumen_Tv.setText(bumen);
        mPerson_Edit = (TextView) findViewById(R.id.person_edit);//申请人
        mPerson_Edit.setText(name);
        mBeiZhu_Edit = (EditText) findViewById(R.id.beizhu_edit);//备注说明

        mLingYongListView = (ListView) findViewById(R.id.mingxi_listview_id);
        mLingYongListView.addHeaderView(LayoutInflater.from(ApplyLingYongActivity.this).inflate(R.layout.add_item_table_head, null));
        mLingYongAda = new LingYongAdapter();
        mLingYongListView.setAdapter(mLingYongAda);

        //增加按钮弹框
        mAddAlertBuilder = new AlertDialog.Builder(this, R.style.dialogNoBg);
        mAddAlertBuilder.setCancelable(false);
        mAddAlertDialog = mAddAlertBuilder.create();
        mAddAlertView = LayoutInflater.from(this).inflate(R.layout.add_alertbox, null);
        mAddAlertDialog.setView(mAddAlertView);
        mAddHeaderView = LayoutInflater.from(this).inflate(R.layout.add_item_table_head, null);

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
                mOkHttpManager.postMethod(false, mZiChanList_Url, "获取资产列表接口", map, mHandler, 5);//资产列表接口
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
                mOkHttpManager.postMethod(false, mZiChanList_Url, "获取资产列表接口", map, mHandler, 5);//资产列表接口
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
                            Toast.makeText(ApplyLingYongActivity.this, "请输入资产名称", Toast.LENGTH_SHORT).show();
                        } else {
                            flag = true;
                            start = 0;
                            limit = 30;
                            Map<Object, Object> map = new HashMap<>();
                            map.put("name", search_Edit.getText().toString().trim());
                            map.put("start", start);
                            map.put("limit", limit);
                            mOkHttpManager.postMethod(false, mZiChanList_Url, "获取资产列表接口", map, mHandler, 5);//资产列表接口
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
                mOkHttpManager.postMethod(false, mZiChanList_Url, "获取资产列表接口", map, mHandler, 5);//资产列表接口
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        mAddAlertLisview = mAddAlertView.findViewById(R.id.add_listview);//弹框中的listview
        mAddAlertLisview.addHeaderView(mAddHeaderView);//添加头
        ziChanAdapter = new ZiChanAdapter();
        mAddAlertLisview.setAdapter(ziChanAdapter);
        //点击选择资产注意：当ListView添加header或者footer后setOnItemClickListener会响应头和尾，在adapter中使用点击事件不会响应
        mAddAlertLisview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView textView = view.findViewById(R.id.select_state);
                if (i != 0) {
                    if (idLongList.contains(mZiChanList.get(i - 1).getId())) {
                        Toast.makeText(ApplyLingYongActivity.this, "此资产已经选择", Toast.LENGTH_SHORT).show();
                        //集合中如果没有这个物品，点击的时候变为黑色状态，并且增加这个物品到集合中
                    } else {
                        textView.setBackgroundResource(R.drawable.bumen_box_select);
                        idList.add(mZiChanList.get(i - 1));
                        idLongList.add(mZiChanList.get(i - 1).getId());
                    }
                }
            }
        });
        mAddAlertSureBtn = mAddAlertView.findViewById(R.id.add_sure_btn);//弹框中的确认
        //点击确定以后更新领用明细适配器
        mAddAlertSureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLingYongAda.notifyDataSetChanged();
                mAddAlertDialog.dismiss();

            }
        });


        //删除弹框
        mDeleteAlertBuilder = new AlertDialog.Builder(this, R.style.dialogNoBg);
        mDeleteAlertBuilder.setCancelable(false);
        mDeleteAlertDialog = mDeleteAlertBuilder.create();
        mDeleteAlertView = LayoutInflater.from(this).inflate(R.layout.delete_alert_box, null);
        mDeleteAlertDialog.setView(mDeleteAlertView);
        mDeleteHeaderView = LayoutInflater.from(this).inflate(R.layout.add_item_table_head, null);
        mDeleteAlertLisview = mDeleteAlertView.findViewById(R.id.delete_listview);
        mDeleteAlertLisview.addHeaderView(mDeleteHeaderView);
        deleteAdapter = new DeleteAdapter();
        mDeleteAlertLisview.setAdapter(deleteAdapter);
        mDeleteAlertSureBtn = mDeleteAlertView.findViewById(R.id.delete_sure_btn);
        mDeleteAlertSureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //点击删除的时候需要将idList中的数据更新

                for (int i = 0; i < statusList.size(); i++) {
                    idList.remove(statusList.get(i));
                    idLongList.remove(statusList.get(i).getId());
                }
                statusList.clear();

                mLingYongAda.notifyDataSetChanged();
                mDeleteAlertDialog.dismiss();
            }
        });
        mDeleteAlertLisview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView textView = view.findViewById(R.id.select_state);

                if (i != 0) {

                    if (statusList.contains(idList.get(i - 1))) {
                        statusList.remove(idList.get(i - 1));
                        textView.setBackgroundResource(R.drawable.bumen_box);
                    } else {
                        statusList.add(idList.get(i - 1));
                        textView.setBackgroundResource(R.drawable.bumen_box_select);
                    }

                }

            }
        });

    }

    private List<ZiChanRow> statusList = new ArrayList<>();

    private boolean checkContent() {
        if ("".equals(mBumen_Tv.getText().toString().trim())) {
            Toast.makeText(this, "无法获取申请部门，请重新尝试登录", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            if ("".equals(mPerson_Edit.getText().toString().trim())) {
                Toast.makeText(this, "无法获取申请人，请重新尝试登录", Toast.LENGTH_SHORT).show();
                return false;
            } else {
                return true;

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
                    StringBuilder id = new StringBuilder();
                    for (int i = 0; i < idList.size(); i++) {
                        id.append(idList.get(i).getId() + "," + idList.get(i).getNum() + ";");//这里需要添加数量id,num;id,num;
                    }
                    Log.e("提交领用的id集合", id.toString());

                    if ("".equals(id.toString())) {
                        Toast.makeText(this, "请选择物品", Toast.LENGTH_SHORT).show();
                    } else {
                        mSubmit.setClickable(false);
                        BallProgressUtils.showLoading(this, mAll_RL);
                        Map<Object, Object> map = new HashMap<>();
                        map.put("recipientsDate", MilliSecondToDate.ms2DateOnlyDay());
                        map.put("assetsIds", id.toString());
                        map.put("comment", mBeiZhu_Edit.getText().toString().trim());

                        mSubmit_lingyong_url = URLTools.urlBase + URLTools.submit_lingyong_apply;//提交领用申请接口
                        mOkHttpManager.postMethod(false, mSubmit_lingyong_url, "提交领用申请接口", map, mHandler, 7);
                        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                    }

                }

                break;

            //增加
            case R.id.add_tv:
                if (mZiChanList.size() != 0) {
                    ziChanAdapter.notifyDataSetChanged();
                    mAddAlertDialog.show();
                } else {
                    mOkHttpManager.getMethod(false, mZiChanList_Url, "获取资产列表接口", mHandler, 5);//资产列表接口
                    Toast.makeText(this, "正在获取资产列表,请稍等", Toast.LENGTH_SHORT).show();
                }
                break;
            //删除
            case R.id.delete_tv:
                if (idList.size() != 0) {
                    mDeleteAlertDialog.show();
                } else {
                    Toast.makeText(this, "暂无需要删除的物品", Toast.LENGTH_SHORT).show();
                }
                break;

        }
    }

    //领用用明细适配器
    class LingYongAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return idList.size() == 0 ? 0 : idList.size();
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
//            if (view == null) {
            view = LayoutInflater.from(ApplyLingYongActivity.this).inflate(R.layout.add_item_table_edit, null);
            lyHolder = new LYHolder();
            lyHolder.status = view.findViewById(R.id.select_state);
            lyHolder.bianhao = view.findViewById(R.id.bianhao_tv);
            lyHolder.leibie = view.findViewById(R.id.leibie_tv);
            lyHolder.mingcheng = view.findViewById(R.id.mingcheng_tv);

            //  view.setTag(lyHolder);
//            } else {
//                lyHolder = (LYHolder) view.getTag();
//
//            }

            //设置数据
            lyHolder.status.setBackgroundResource(R.drawable.bumen_box_select);
            lyHolder.bianhao.setText(idList.get(i).getNum() + "");//数量
            lyHolder.leibie.setText(idList.get(i).getCategoryName() + "");
            lyHolder.mingcheng.setText(idList.get(i).getAssetName() + "");
            final int position = i;
            Log.e("position=", position + "");
            final LYHolder finalLyHolder = lyHolder;
            lyHolder.bianhao.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    // Log.e("editable=",editable+"");
                    String s = editable + "";

                    if (!"".equals(s)) {
                        if (s.startsWith("0")) {
                            Toast.makeText(ApplyLingYongActivity.this, "请填写正确的数量", Toast.LENGTH_SHORT).show();
                            finalLyHolder.bianhao.setText("1");
                        } else {
                            Integer num = Integer.valueOf(s);
                            idList.get(position).setNum(num);
                            Log.e("edit -position=", position + "");
                            Log.e("数量=", idList.get(position).getNum() + "");
                        }

                    } else {
                        //若不填数量，将以原始数量为准
                        Toast.makeText(ApplyLingYongActivity.this, "请填写数量", Toast.LENGTH_SHORT).show();
                    }

                }
            });

            return view;
        }

        class LYHolder {
            TextView status, leibie, mingcheng;//状态,类别,物品名称，
            EditText bianhao;
        }
    }

    //增加弹框资产列表适配器
    class ZiChanAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mZiChanList.size() == 0 ? 0 : mZiChanList.size();
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
        public View getView(final int i, View view, ViewGroup viewGroup) {
            LYHolder lyHolder = null;
            if (view == null) {
                view = LayoutInflater.from(ApplyLingYongActivity.this).inflate(R.layout.add_item_table, null);
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
            lyHolder.bianhao.setText(mZiChanList.get(i).getNum() + "");//数量
            lyHolder.leibie.setText(mZiChanList.get(i).getCategoryName() + "");
            lyHolder.mingcheng.setText(mZiChanList.get(i).getAssetName() + "");
            Log.e(" i=", i + "");

            if (idLongList.contains(mZiChanList.get(i).getId())) {
                lyHolder.status.setBackgroundResource(R.drawable.bumen_box_select);
            } else {
                lyHolder.status.setBackgroundResource(R.drawable.bumen_box);
            }
            return view;
        }

        class LYHolder {
            TextView status, bianhao, leibie, mingcheng;//状态，序号，物品名称，数量，类别
        }
    }

    //删除弹框适配器
    class DeleteAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return idList.size();
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
            DeleteHolder deleteHolder = null;
            if (view == null) {
                deleteHolder = new DeleteHolder();
                view = LayoutInflater.from(ApplyLingYongActivity.this).inflate(R.layout.add_item_table, null);
                deleteHolder.status = view.findViewById(R.id.select_state);
                deleteHolder.leibie = view.findViewById(R.id.leibie_tv);
                deleteHolder.mingcheng = view.findViewById(R.id.mingcheng_tv);
                deleteHolder.shuliang = view.findViewById(R.id.bianhao_tv);
                view.setTag(deleteHolder);
            } else {
                deleteHolder = (DeleteHolder) view.getTag();
            }
            deleteHolder.status.setBackgroundResource(R.drawable.bumen_box);
            deleteHolder.shuliang.setText(idList.get(i).getNum() + "");//数量
            deleteHolder.leibie.setText(idList.get(i).getCategoryName() + "");
            deleteHolder.mingcheng.setText(idList.get(i).getAssetName() + "");


            return view;
        }

        class DeleteHolder {
            TextView status, leibie, mingcheng, shuliang;//状态,类别,物品名称，数量
        }
    }

}
