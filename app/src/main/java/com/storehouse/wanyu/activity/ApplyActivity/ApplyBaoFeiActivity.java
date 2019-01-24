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
import com.storehouse.wanyu.Bean.BFLeiXingRoot;
import com.storehouse.wanyu.Bean.BFLeiXingRows;
import com.storehouse.wanyu.Bean.CaiGouApplySubmit;
import com.storehouse.wanyu.Bean.OneSelfRoot;
import com.storehouse.wanyu.Bean.OneSelfRows;
import com.storehouse.wanyu.IPAddress.URLTools;
import com.storehouse.wanyu.MyUtils.BallProgressUtils;
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

//报废
public class ApplyBaoFeiActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView mBack;
    private TextView mSubmit_Btn, mBumen_Tv, mPerson_Tv, mWuPin_Name, mLeiXing_tv, mDate_tv, mSureBtn;
    private EditText mBeiZhu_Edit, mNum_Edit;
    private EditText search_Edit;//弹框中的搜索
    private SmartRefreshLayout smart_Refresh;//弹框中的刷新
    private long num = -1;//标记点击后的数量
    private int start = 0, limit = 30;
    private boolean flag = true;//true表示刷新，false表示加载更多
    private RelativeLayout mWuPing_Rl, mLeiXing_Rl, mDate_Rl, mAll_Rl;//物品名称按钮，报废类型按钮，报废日期按钮
    private AlertDialog.Builder mBuilderBuMen, mBuilderLeiXing, mBuilderDate;//个人物品名称,报废类型,报废日期弹框
    private AlertDialog mAlertDialogBuMen, mAlertDialogLeiXing, mAlertDialogDate;
    private View mAddAlertView, mAddHeaderView, mLeiXingView, mDateView;
    private ListView mBumen_ListView, mLeiXing_ListView;
    private DatePicker datePicker;
    private BumenAdapter bumenAdapter;//部门适配器
    private LeiXingAdapter leiXingAdapter;//类型适配器
    private List<OneSelfRows> mBuMenList = new ArrayList<>();
    private List<BFLeiXingRows> mLeiXingList = new ArrayList<>();
    private long assetsId = -1, scrapModeId = -1;
    private String mOneSelfUrl, mLeiXingUrl, mSubmitUrl;
    private OkHttpManager okHttpManager;
    private Gson mgson = new Gson();
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 11) {
                String mes = (String) msg.obj;
                Object o = mgson.fromJson(mes, OneSelfRoot.class);
                if (o != null && o instanceof OneSelfRoot) {
                    OneSelfRoot oneSelfRoot = (OneSelfRoot) o;

                    if (oneSelfRoot != null && "0".equals(oneSelfRoot.getCode())) {
                        if (oneSelfRoot.getRows() != null) {

                            if (flag) {
                                mBuMenList = oneSelfRoot.getRows();
                                if (oneSelfRoot.getRows().size() == 0) {
                                    Toast.makeText(ApplyBaoFeiActivity.this, "暂无资产", Toast.LENGTH_SHORT).show();
                                }
                            }else {
                                for (int i = 0; i < oneSelfRoot.getRows().size(); i++) {
                                    mBuMenList.add(oneSelfRoot.getRows().get(i));
                                }
                                if (oneSelfRoot.getRows().size() == 0) {
                                    Toast.makeText(ApplyBaoFeiActivity.this, "已加载全部资产", Toast.LENGTH_SHORT).show();
                                }
                            }

                            bumenAdapter.notifyDataSetChanged();
                        }


                    } else {
                        Toast.makeText(ApplyBaoFeiActivity.this, "登录过期，请重新登录", Toast.LENGTH_SHORT).show();
                    }


                } else {
                    Toast.makeText(ApplyBaoFeiActivity.this, "获取个人资产列表数据错误", Toast.LENGTH_SHORT).show();

                }


            } else if (msg.what == 1010) {
                mSubmit_Btn.setClickable(true);
                Toast.makeText(ApplyBaoFeiActivity.this, "连接服务器失败,请重新尝试", Toast.LENGTH_SHORT).show();
            } else if (msg.what == 12) {//提交baof申请接口
                mSubmit_Btn.setClickable(true);
                BallProgressUtils.dismisLoading();
                String mes = (String) msg.obj;
                Log.e("提交报废=", mes);
                Object o = mgson.fromJson(mes, CaiGouApplySubmit.class);
                if (o != null && o instanceof CaiGouApplySubmit) {
                    CaiGouApplySubmit caiGouApplySubmit = (CaiGouApplySubmit) o;
                    if ("0".equals(caiGouApplySubmit.getCode())) {
                        Toast.makeText(ApplyBaoFeiActivity.this, "提交申请成功", Toast.LENGTH_SHORT).show();
                        finish();
                    } else if ("-1".equals(caiGouApplySubmit.getCode())) {
                        Toast.makeText(ApplyBaoFeiActivity.this, "您的账号已过期请重新登录，请重新登录", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ApplyBaoFeiActivity.this, "提交申请失败", Toast.LENGTH_SHORT).show();
                    }

                }

            } else if (msg.what == 13) {//查询报废类型接口
                String mes = (String) msg.obj;
                Object o = mgson.fromJson(mes, BFLeiXingRoot.class);
                if (o != null && o instanceof BFLeiXingRoot) {
                    BFLeiXingRoot bfLeiXingRoot = (BFLeiXingRoot) o;

                    if (bfLeiXingRoot != null && "0".equals(bfLeiXingRoot.getCode())) {
                        if (bfLeiXingRoot.getRows() != null) {
                            mLeiXingList = bfLeiXingRoot.getRows();
                            leiXingAdapter.notifyDataSetChanged();
                        }


                    } else {
                        Toast.makeText(ApplyBaoFeiActivity.this, "获取报废方式列表数据失败", Toast.LENGTH_SHORT).show();
                    }


                } else {
                    Toast.makeText(ApplyBaoFeiActivity.this, "获取报废方式列表数据错误", Toast.LENGTH_SHORT).show();

                }


            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_bao_fei);
        mAll_Rl = (RelativeLayout) findViewById(R.id.activity_apply_bao_fei);
        initUI();
    }

    private void initUI() {
        mBack = (ImageView) findViewById(R.id.back_img);
        mBack.setOnClickListener(this);
        mBumen_Tv = (TextView) findViewById(R.id.bumen_name);
        mPerson_Tv = (TextView) findViewById(R.id.person_edit);
        mWuPin_Name = (TextView) findViewById(R.id.name_edit);
        mBeiZhu_Edit = (EditText) findViewById(R.id.beizhu_edit);
        mNum_Edit = (EditText) findViewById(R.id.num_edit);
        mLeiXing_tv = (TextView) findViewById(R.id.leixing_edit);
        mDate_tv = (TextView) findViewById(R.id.date_tv);
        //提交
        mSubmit_Btn = (TextView) findViewById(R.id.submit_btn);
        mSubmit_Btn.setOnClickListener(this);
        //物品名称按钮，报废类型按钮，报废日期按钮
        mWuPing_Rl = (RelativeLayout) findViewById(R.id.wuping_btn);
        mLeiXing_Rl = (RelativeLayout) findViewById(R.id.baofei_leixing_rl);
        mDate_Rl = (RelativeLayout) findViewById(R.id.baofei_date_rl);
        mWuPing_Rl.setOnClickListener(this);
        mLeiXing_Rl.setOnClickListener(this);
        mDate_Rl.setOnClickListener(this);


        okHttpManager = OkHttpManager.getInstance();
        mOneSelfUrl = URLTools.urlBase + URLTools.baofei_property_list;
        Map<Object, Object> map = new HashMap<>();
        map.put("name", "");
        map.put("start", start);
        map.put("limit", limit);
        okHttpManager.postMethod(false, mOneSelfUrl, "个人资产列表", map, mHandler, 11);
        mLeiXingUrl = URLTools.urlBase + URLTools.query_baofei_way;
        okHttpManager.getMethod(false, mLeiXingUrl, "报废方式", mHandler, 13);
        String bumen = (String) SharedPrefrenceTools.getValueByKey("departmentName", "");
        String name = (String) SharedPrefrenceTools.getValueByKey("truename", "");
        mBumen_Tv.setText(bumen);
        mPerson_Tv.setText(name);
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
                if (i != 0) {
                    mWuPin_Name.setText(mBuMenList.get(i - 1).getAssetName() + "");
                    assetsId = mBuMenList.get(i - 1).getId();
                    mNum_Edit.setText(mBuMenList.get(i - 1).getNum() + "");
                    num = mBuMenList.get(i - 1).getNum();
                    mAlertDialogBuMen.dismiss();
                    Log.e("物品id=", assetsId + "");
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
                            Toast.makeText(ApplyBaoFeiActivity.this, "请输入资产名称", Toast.LENGTH_SHORT).show();
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

        //报废类型弹框
        mBuilderLeiXing = new AlertDialog.Builder(this, R.style.dialogNoBg);
        mBuilderLeiXing.setCancelable(false);
        mAlertDialogLeiXing = mBuilderLeiXing.create();
        mLeiXingView = LayoutInflater.from(this).inflate(R.layout.danwei_alert, null);
        mAlertDialogLeiXing.setView(mLeiXingView);
        mLeiXing_ListView = mLeiXingView.findViewById(R.id.danwei_listview);//弹框中的listview
        leiXingAdapter = new LeiXingAdapter();
        mLeiXing_ListView.setAdapter(leiXingAdapter);
        mLeiXing_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mLeiXing_tv.setText(mLeiXingList.get(i).getModeName());
                scrapModeId = mLeiXingList.get(i).getId();
                mAlertDialogLeiXing.dismiss();
            }
        });
        //报废日期弹框
        mBuilderDate = new AlertDialog.Builder(this);
        mAlertDialogDate = mBuilderDate.create();
        View startView = LayoutInflater.from(this).inflate(R.layout.select_start_time, null);
        mAlertDialogDate.setView(startView);
        datePicker = startView.findViewById(R.id.start_datePicker);
        TextView cancleStartTimeBtn = startView.findViewById(R.id.cancle_btn);
        cancleStartTimeBtn.setVisibility(View.GONE);
        TextView sureStartTimeBtn = startView.findViewById(R.id.sure_btn);

        //确定，
        sureStartTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int year = datePicker.getYear();
                int month = datePicker.getMonth() + 1;
                String month2 = "";
                if (month < 10) {
                    month2 = "0" + month;
                } else {
                    month2 = month + "";
                }
                int day = datePicker.getDayOfMonth();
                String day2 = "";
                if (day < 10) {
                    day2 = "0" + day;
                } else {
                    day2 = day + "";
                }
                Log.e("报废日期", year + "-" + month2 + "-" + day2);
                mDate_tv.setText(year + "-" + month2 + "-" + day2);
                mAlertDialogDate.dismiss();
            }
        });


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
                mSubmitUrl = URLTools.urlBase + URLTools.submit_baofei_apply;
                Map<Object, Object> map = new HashMap<>();
                map.put("assetsId", assetsId);//物品id
                map.put("comment", mBeiZhu_Edit.getText().toString().trim());
                map.put("scrapModeId", scrapModeId);//报废类型id
                map.put("scrapDate", mDate_tv.getText().toString());//报废日期
                map.put("totalNum",Integer.valueOf(mNum_Edit.getText().toString()));
                okHttpManager.postMethod(false, mSubmitUrl, "提交报废申请接口", map, mHandler, 12);
                ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

            }
        } else if (id == mWuPing_Rl.getId()) {//物品按钮
            if (mBuMenList.size() == 0) {
                okHttpManager.getMethod(false, mOneSelfUrl, "个人资产列表", mHandler, 11);
                Toast.makeText(this, "正在获取个人资产列表", Toast.LENGTH_SHORT).show();
            } else {
                mAlertDialogBuMen.show();
            }
        } else if (id == mLeiXing_Rl.getId()) {//报废方式按钮
            if (mLeiXingList.size() == 0) {
                okHttpManager.getMethod(false, mLeiXingUrl, "报废方式", mHandler, 13);
                Toast.makeText(this, "正在获取报废方式列表", Toast.LENGTH_SHORT).show();
            } else {
                mAlertDialogLeiXing.show();
            }
        } else if (id == mDate_Rl.getId()) {//报废日期按钮
            mAlertDialogDate.show();
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
            if ("".equals(mPerson_Tv.getText().toString().trim())) {
                Toast.makeText(this, "无法获取申请人，请重新尝试登录", Toast.LENGTH_SHORT).show();
                return false;
            } else {
                if ("".equals(mWuPin_Name.getText().toString().trim())) {
                    Toast.makeText(this, "请选择报废的物品", Toast.LENGTH_SHORT).show();
                    return false;
                } else {
                    if (checkNum()){
                        if ("".equals(mLeiXing_tv.getText().toString().trim())) {
                            Toast.makeText(this, "请选择报废类型", Toast.LENGTH_SHORT).show();
                            return false;
                        } else {
                            if (checkTime()) {

                                return true;
                            } else {
                                Toast.makeText(this, "请选择报废时间", Toast.LENGTH_SHORT).show();
                                return false;
                            }
                        }
                    }else {
                        return false;
                    }
                }
            }
        }
    }

    //判断报废数量
    public boolean checkNum() {
        String s = mNum_Edit.getText().toString();

        if (s.length()==0){
            Toast.makeText(this, "请填写换新的数量", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (Integer.valueOf(s) == 0) {
            Toast.makeText(this, "报废数量不能为0", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (s.startsWith("0")) {
            Toast.makeText(this, "请填写正确的报废数量", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (Integer.valueOf(s) > num) {
            Toast.makeText(this, "报废数量大于使用数量", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


    /**
     * 判断报废时间是否大于当前时间
     *
     * @return
     */
    private boolean checkTime() {

        if ("".equals(mDate_tv.getText().toString().trim())) {
            Toast.makeText(this, "请选择报废时间", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {

                Date baoFeiDate = dateformat.parse(mDate_tv.getText().toString() + " 00:00:00");
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
                if (baoFeiDate.getTime() - currentDate.getTime() < 0) {//判断借用时间是否大于当前时间
                    Toast.makeText(ApplyBaoFeiActivity.this, "报废时间小于当前时间", Toast.LENGTH_SHORT).show();
                    return false;
                }
                return true;

            } catch (ParseException e) {
                e.printStackTrace();
                Toast.makeText(this, "时间解析错误", Toast.LENGTH_SHORT).show();
            }

            return false;

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
                view = LayoutInflater.from(ApplyBaoFeiActivity.this).inflate(R.layout.add_item_table, null);
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

    //报废类型适配器

    class LeiXingAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mLeiXingList.size() == 0 ? 0 : mLeiXingList.size();
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
            LXHolder lxHolder = null;
            if (view == null) {
                view = LayoutInflater.from(ApplyBaoFeiActivity.this).inflate(R.layout.danwei_alert_item, null);
                lxHolder = new LXHolder();
                lxHolder.textView = view.findViewById(R.id.danwei_item_tv);
                view.setTag(lxHolder);
            } else {
                lxHolder = (LXHolder) view.getTag();
            }
            lxHolder.textView.setText(mLeiXingList.get(i).getModeName() + "");
            return view;
        }

        class LXHolder {
            TextView textView;
        }
    }
}
