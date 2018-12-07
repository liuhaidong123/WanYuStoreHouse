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
import android.widget.DatePicker;
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
import com.storehouse.wanyu.Bean.ZiChanRoot;
import com.storehouse.wanyu.Bean.ZiChanRow;
import com.storehouse.wanyu.IPAddress.URLTools;
import com.storehouse.wanyu.MyUtils.BallProgressUtils;
import com.storehouse.wanyu.MyUtils.MilliSecondToDate;
import com.storehouse.wanyu.MyUtils.SharedPrefrenceTools;
import com.storehouse.wanyu.OkHttpUtils.OkHttpManager;
import com.storehouse.wanyu.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//借用申请
public class ApplyJieYongActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView mBack;
    //提交，添加，删除，申请部门，申请人，开始时间，结束时间
    private TextView mSubmit, mAdd_Btn, mDelete_Btn, mBumen_Tv, mPerson_Tv, mStartTime_Tv, mEndTime_Tv;
    private EditText mBeiZhu_Edit;//备注
    private RelativeLayout mStartTime_Rl, mEndTime_Rl, mAll_RL;//开始时间按钮，归还时间按钮
    private AlertDialog.Builder mStartTimeBuilder, mEndTimeBuilder;//开始，归还时间builder
    private AlertDialog mStartTimeAlertDialog, mEndTimeAlertDialog;//开始，归还时间alertdialog
    private DatePicker datePickerEnd, datePickerStart;

    private ListView mJieYongListView;//借用明细ListView
    private JieYongAdapter jieYongAdapter;

    private AlertDialog.Builder mAddAlertBuilder, mDeleteAlertBuilder;
    private AlertDialog mAddAlertDialog, mDeleteAlertDialog;
    private View mAddAlertView, mAddHeaderView, mDeleteAlertView, mDeleteHeaderView;
    private ListView mAddAlertLisview, mDeleteAlertLisview;//弹框中的listview
    private TextView mAddAlertSureBtn, mDeleteAlertSureBtn;//弹框中的确认
    private ZiChanAdapter ziChanAdapter;//弹框中的适配器
    private DeleteAdapter deleteAdapter;//删除弹框中的适配器
    private SmartRefreshLayout smart_Refresh;//弹框中的刷新
    private EditText search_Edit;//弹框中的搜索
    private List<ZiChanRow> mZiChanList = new ArrayList<>();
    private List<ZiChanRow> idList = new ArrayList<>();//存放选择后资产列表
    private List<Long> idLongList = new ArrayList<>();//存放选择后资产列表中每个资产的id
    private String mZiChanList_Url, mSubmit_JieYong_Url;
    private int start = 0, limit = 30;
    private boolean flag = true;//true表示刷新，false表示加载更多
    //private List<EditText> editList = new ArrayList<>();
    private OkHttpManager okHttpManager;
    private Gson mGson = new Gson();
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
                                        Toast.makeText(ApplyJieYongActivity.this, "暂无此资产", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    for (int i = 0; i < ziChanRoot.getRows().size(); i++) {
                                        mZiChanList.add(ziChanRoot.getRows().get(i));
                                    }
                                    if (ziChanRoot.getRows().size() == 0) {
                                        Toast.makeText(ApplyJieYongActivity.this, "已加载全部资产", Toast.LENGTH_SHORT).show();
                                    }

                                }

                                ziChanAdapter.notifyDataSetChanged();
                            }

                        } else if (ziChanRoot != null && "-1".equals(ziChanRoot.getCode())) {
                            Toast.makeText(ApplyJieYongActivity.this, "您的账号已过期请重新登录，请重新登录", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ApplyJieYongActivity.this, "获取资产列表失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            } else if (msg.what == 1010) {
                mSubmit.setClickable(true);
                Toast.makeText(ApplyJieYongActivity.this, "连接服务器失败，请重新尝试", Toast.LENGTH_SHORT).show();
            } else if (msg.what == 7) {//提交采购申请接口
                mSubmit.setClickable(true);
                BallProgressUtils.dismisLoading();
                String mes = (String) msg.obj;
                Log.e("提交领用=", mes);
                Object o = mGson.fromJson(mes, CaiGouApplySubmit.class);
                if (o != null && o instanceof CaiGouApplySubmit) {
                    CaiGouApplySubmit caiGouApplySubmit = (CaiGouApplySubmit) o;
                    if ("0".equals(caiGouApplySubmit.getCode())) {
                        Toast.makeText(ApplyJieYongActivity.this, "提交借用申请成功", Toast.LENGTH_SHORT).show();
                        finish();
                    } else if ("-1".equals(caiGouApplySubmit.getCode())) {
                        Toast.makeText(ApplyJieYongActivity.this, "您的账号已过期请重新登录，请重新登录", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ApplyJieYongActivity.this, "提交借用申请失败", Toast.LENGTH_SHORT).show();
                    }

                }


            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_jie_yong);
        mAll_RL = (RelativeLayout) findViewById(R.id.activity_apply_jie_yong);
        initUI();
    }

    private void initUI() {
        okHttpManager = OkHttpManager.getInstance();
        mZiChanList_Url = URLTools.urlBase + URLTools.jieyong_property_list;//资产列表接口
        Map<Object, Object> map = new HashMap<>();
        map.put("name", "");
        map.put("start", start);
        map.put("limit", limit);
        okHttpManager.postMethod(false, mZiChanList_Url, "获取资产列表接口", map, mHandler, 5);//资产列表接口
        String bumen = (String) SharedPrefrenceTools.getValueByKey("departmentName", "");
        String name = (String) SharedPrefrenceTools.getValueByKey("truename", "");
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
        mPerson_Tv = (TextView) findViewById(R.id.person_edit);//申请人
        mPerson_Tv.setText(name);
        mBeiZhu_Edit = (EditText) findViewById(R.id.beizhu_edit);//备注
        mStartTime_Tv = (TextView) findViewById(R.id.jie_time_content);//开始时间
        mEndTime_Tv = (TextView) findViewById(R.id.return_time_content);//归还时间
        mStartTime_Rl = (RelativeLayout) findViewById(R.id.jie_time_btn);//借用开始时间按钮
        mStartTime_Rl.setOnClickListener(this);
        mEndTime_Rl = (RelativeLayout) findViewById(R.id.return_time_btn);//归还时间按钮
        mEndTime_Rl.setOnClickListener(this);
        //开始时间弹框
        mStartTimeBuilder = new AlertDialog.Builder(this);
        mStartTimeAlertDialog = mStartTimeBuilder.create();
        View startView = LayoutInflater.from(this).inflate(R.layout.select_start_time, null);
        mStartTimeAlertDialog.setView(startView);
        datePickerStart = startView.findViewById(R.id.start_datePicker);
        TextView cancleStartTimeBtn = startView.findViewById(R.id.cancle_btn);
        TextView sureStartTimeBtn = startView.findViewById(R.id.sure_btn);
        cancleStartTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mStartTimeAlertDialog.dismiss();
            }
        });
        //确定，获取借用开始时间
        sureStartTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int year = datePickerStart.getYear();
                int month = datePickerStart.getMonth() + 1;
                String month2 = "";
                if (month < 10) {
                    month2 = "0" + month;
                } else {
                    month2 = month + "";
                }
                int day = datePickerStart.getDayOfMonth();
                String day2 = "";
                if (day < 10) {
                    day2 = "0" + day;
                } else {
                    day2 = day + "";
                }
                Log.e("归还日期", year + "-" + month2 + "-" + day2);
                mStartTime_Tv.setText(year + "-" + month2 + "-" + day2);
                mStartTimeAlertDialog.dismiss();
            }
        });
        //归还时间弹框
        mEndTimeBuilder = new AlertDialog.Builder(this);
        mEndTimeAlertDialog = mEndTimeBuilder.create();
        View endView = LayoutInflater.from(this).inflate(R.layout.select_end_time, null);
        mEndTimeAlertDialog.setView(endView);
        datePickerEnd = endView.findViewById(R.id.end_datePicker);
        TextView cancleEndTimeBtn = endView.findViewById(R.id.cancle_btn);
        TextView sureEndTimeBtn = endView.findViewById(R.id.sure_btn);
        cancleEndTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mEndTimeAlertDialog.dismiss();
            }
        });
        //确定，获取借用归还时间
        sureEndTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int year = datePickerEnd.getYear();
                int month = datePickerEnd.getMonth() + 1;
                String month2 = "";
                if (month < 10) {
                    month2 = "0" + month;
                } else {
                    month2 = month + "";
                }
                int day = datePickerEnd.getDayOfMonth();
                String day2 = "";
                if (day < 10) {
                    day2 = "0" + day;
                } else {
                    day2 = day + "";
                }
                Log.e("归还日期", year + "-" + month2 + "-" + day2);
                mEndTime_Tv.setText(year + "-" + month2 + "-" + day2);
                mEndTimeAlertDialog.dismiss();
            }
        });
        //借用明细适配器
        mJieYongListView = (ListView) findViewById(R.id.mingxi_listview_id);
        mJieYongListView.addHeaderView(LayoutInflater.from(ApplyJieYongActivity.this).inflate(R.layout.add_item_table_head, null));
        jieYongAdapter = new JieYongAdapter();
        mJieYongListView.setAdapter(jieYongAdapter);

        //增加按钮弹框
        mAddAlertBuilder = new AlertDialog.Builder(this, R.style.dialogNoBg);
        mAddAlertBuilder.setCancelable(false);
        mAddAlertDialog = mAddAlertBuilder.create();
        mAddAlertView = LayoutInflater.from(this).inflate(R.layout.add_alertbox, null);
        mAddAlertDialog.setView(mAddAlertView);
        mAddHeaderView = LayoutInflater.from(this).inflate(R.layout.add_item_table_head, null);
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
//                    //集合中如果有这个物品，点击的时候变为白色状态，并且从集合中删除这个物品

                    if (idLongList.contains(mZiChanList.get(i - 1).getId())) {
//                        textView.setBackgroundResource(R.drawable.bumen_box);
//                        idList.remove(mZiChanList.get(i - 1));
//                        idLongList.remove(mZiChanList.get(i - 1).getId());
                        Toast.makeText(ApplyJieYongActivity.this, "此资产已经选择", Toast.LENGTH_SHORT).show();
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
                jieYongAdapter.notifyDataSetChanged();
                mAddAlertDialog.dismiss();
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
                okHttpManager.postMethod(false, mZiChanList_Url, "获取资产列表接口", map, mHandler, 5);//资产列表接口
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
                okHttpManager.postMethod(false, mZiChanList_Url, "获取资产列表接口", map, mHandler, 5);//资产列表接口
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
                            Toast.makeText(ApplyJieYongActivity.this, "请输入资产名称", Toast.LENGTH_SHORT).show();
                        } else {
                            flag = true;
                            start = 0;
                            limit = 30;
                            Map<Object, Object> map = new HashMap<>();
                            map.put("name", search_Edit.getText().toString().trim());
                            map.put("start", start);
                            map.put("limit", limit);
                            okHttpManager.postMethod(false, mZiChanList_Url, "获取资产列表接口", map, mHandler, 5);//资产列表接口
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
                okHttpManager.postMethod(false, mZiChanList_Url, "获取资产列表接口", map, mHandler, 5);//资产列表接口
            }

            @Override
            public void afterTextChanged(Editable editable) {

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
                jieYongAdapter.notifyDataSetChanged();
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

                    StringBuilder idL = new StringBuilder();
                    for (int i = 0; i < idList.size(); i++) {
                        idL.append(idList.get(i).getId() + "," + idList.get(i).getNum() + ";");//这里需要添加数量id,num;id,num;
                    }
                    Log.e("提交借用的id集合", idL.toString());
                    if ("".equals(idL.toString())) {
                        Toast.makeText(this, "请选择物品", Toast.LENGTH_SHORT).show();
                    } else {
                        mSubmit.setClickable(false);
                        BallProgressUtils.showLoading(this, mAll_RL);
                        Map<Object, Object> map = new HashMap<>();
                        map.put("borrowDate", mStartTime_Tv.getText().toString());
                        map.put("willReturnDate", mEndTime_Tv.getText().toString());
                        map.put("comment", mBeiZhu_Edit.getText().toString().trim());
                        map.put("assetsIds", idL.toString());

                        mSubmit_JieYong_Url = URLTools.urlBase + URLTools.submit_jieyong_apply;//提交借用申请接口
                        okHttpManager.postMethod(false, mSubmit_JieYong_Url, "提交领用申请接口", map, mHandler, 7);
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
                    okHttpManager.getMethod(false, mZiChanList_Url, "获取资产列表接口", mHandler, 5);//资产列表接口
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

            case R.id.jie_time_btn://借用开始时间
                mStartTimeAlertDialog.show();
                break;

            case R.id.return_time_btn://归还时间
                mEndTimeAlertDialog.show();
                break;
        }
    }

    /**
     * 提交时判断内容
     *
     * @return
     */
    private boolean checkContent() {
        if ("".equals(mBumen_Tv.getText().toString().trim())) {
            Toast.makeText(this, "无法获取申请部门，请重新尝试登录", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            if ("".equals(mPerson_Tv.getText().toString().trim())) {
                Toast.makeText(this, "无法获取申请人，请重新尝试登录", Toast.LENGTH_SHORT).show();
                return false;
            } else {
                return checkTime();

            }
        }
    }

    /**
     * 判断归还时间是否大于借用时间
     *
     * @return
     */
    private boolean checkTime() {
        if ("".equals(mStartTime_Tv.getText().toString())) {
            Toast.makeText(this, "请选择借用时间", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            if ("".equals(mEndTime_Tv.getText().toString())) {
                Toast.makeText(this, "请选择预计归还时间", Toast.LENGTH_SHORT).show();
                return false;
            } else {
                SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {

                    Date jieTimeDate = dateformat.parse(mStartTime_Tv.getText().toString() + " 00:00:00");
                    Date returnTimeDate = dateformat.parse(mEndTime_Tv.getText().toString() + " 00:00:00");
                    Calendar calendar = Calendar.getInstance();
                    int year = calendar.get(Calendar.YEAR);
                    int month = calendar.get(Calendar.MONTH) + 1;
                    int day = calendar.get(Calendar.DAY_OF_MONTH);
                    String month2 = "";
                    if (month < 10) {
                        month2 = "0" + month;
                    } else {
                        month2 = month + "";
                    }
                    String day2 = "";
                    if (day < 10) {
                        day2 = "0" + day;
                    } else {
                        day2 = day + "";
                    }

                    Date currentDate = dateformat.parse(year + "-" + month2 + "-" + day2 + " 00:00:00");
                    if (jieTimeDate.getTime() - currentDate.getTime() < 0) {//判断借用时间是否大于当前时间
                        Toast.makeText(ApplyJieYongActivity.this, "借用时间小于当前时间", Toast.LENGTH_SHORT).show();
                        return false;
                    }


                    if (returnTimeDate.getTime() - jieTimeDate.getTime() >= 0) {
                        return true;
                    }
                    Toast.makeText(this, "请选择正确的归还时间", Toast.LENGTH_SHORT).show();
                    return false;


                } catch (ParseException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "时间解析错误", Toast.LENGTH_SHORT).show();
                }

                return false;

            }
        }


    }


    //借用明细适配器
    class JieYongAdapter extends BaseAdapter {

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
            JYHolder lyHolder = null;
//            if (view == null) {
            view = LayoutInflater.from(ApplyJieYongActivity.this).inflate(R.layout.add_item_table_edit, null);
            lyHolder = new JYHolder();
            lyHolder.status = view.findViewById(R.id.select_state);
            lyHolder.bianhao = view.findViewById(R.id.bianhao_tv);
            lyHolder.leibie = view.findViewById(R.id.leibie_tv);
            lyHolder.mingcheng = view.findViewById(R.id.mingcheng_tv);
//                view.setTag(lyHolder);
//            } else {
//                lyHolder = (JYHolder) view.getTag();
//
//            }
            //设置数据
            lyHolder.status.setBackgroundResource(R.drawable.bumen_box_select);
            lyHolder.bianhao.setText(idList.get(i).getNum() + "");
            lyHolder.leibie.setText(idList.get(i).getCategoryName() + "");
            lyHolder.mingcheng.setText(idList.get(i).getAssetName() + "");

            final int position = i;
            final JYHolder finalLyHolder = lyHolder;
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
                        if (s.startsWith("0")) {
                            Toast.makeText(ApplyJieYongActivity.this, "请填写正确的数量", Toast.LENGTH_SHORT).show();
                            finalLyHolder.bianhao.setText("1");
                        } else {
                            Integer num = Integer.valueOf(s);
                            idList.get(position).setNum(num);
                            Log.e("edit -position=", position + "");
                            Log.e("数量=", idList.get(position).getNum() + "");
                        }

                    } else {
                        //若不填数量，将以原始数量为准
                        Toast.makeText(ApplyJieYongActivity.this, "请填写数量", Toast.LENGTH_SHORT).show();
                    }

                }
            });
            return view;
        }

        class JYHolder {
            TextView status, leibie, mingcheng;//状态 ，类别，物品名称
            EditText bianhao;//数量
        }
    }


    //弹框资产列表适配器
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
                view = LayoutInflater.from(ApplyJieYongActivity.this).inflate(R.layout.add_item_table, null);
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
            lyHolder.bianhao.setText(mZiChanList.get(i).getNum() + "");
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
            TextView status, bianhao, leibie, mingcheng;//状态，序号，物品名称，编号，类别
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
                view = LayoutInflater.from(ApplyJieYongActivity.this).inflate(R.layout.add_item_table, null);
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
