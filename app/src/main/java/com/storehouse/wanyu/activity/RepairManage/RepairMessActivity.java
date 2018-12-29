package com.storehouse.wanyu.activity.RepairManage;

import android.content.Intent;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
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
import com.storehouse.wanyu.Bean.PartsRoot;
import com.storehouse.wanyu.Bean.PartsRows;
import com.storehouse.wanyu.Bean.PropertySubmitBean;
import com.storehouse.wanyu.Bean.RepairMaintenanceLog;
import com.storehouse.wanyu.Bean.RepairMessRoot;
import com.storehouse.wanyu.Bean.ZiChanRow;
import com.storehouse.wanyu.IPAddress.URLTools;
import com.storehouse.wanyu.MyUtils.BallProgressUtils;
import com.storehouse.wanyu.OkHttpUtils.OkHttpManager;
import com.storehouse.wanyu.R;
import com.storehouse.wanyu.activity.ApplyActivity.ApplyJieYongActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RepairMessActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView mBack_img;
    private TextView mBumen_Tv, mPerson_Tv, mName_Tv, mNum_tv, mBeizhuMsg_Tv, mTime_tv, mBianMa_tv, mLeiXing_tv;
    private TextView mParts_tv, mAdd_tv, mDelete_tv;
    private EditText mFault_edit;//故障说明
    private RadioButton yesRadioButton, noRadioButton;
    private int repairFlag = -1;//1表示已修复，0表示未修复
    private LinearLayout mll;
    private TextView mSure_submit_btn;
    private ListView mList;
    private PartsAdapter partsAdapter;
    private View headerView, footerView;

    private AlertDialog.Builder mAddAlertBuilder, mDeleteAlertBuilder;
    private AlertDialog mAddAlertDialog, mDeleteAlertDialog;
    private View mAddAlertView, mAddHeaderView, mDeleteAlertView, mDeleteHeaderView;
    private ListView mAddAlertLisview, mDeleteAlertLisview;//弹框中的listview
    private AddAlertAdapter addAlertAdapter;
    private DeleteAlertAdapter deleteAlertAdapter;
    private TextView mAddAlertSureBtn, mDeleteAlertSureBtn;//弹框中的确认
    private SmartRefreshLayout smart_Refresh;//弹框中的刷新
    private EditText search_Edit;//弹框中的搜索
    private List<PartsRows> mPartsAddList = new ArrayList<>();//增加弹框中的配件列表
    private List<PartsRows> mPartsDeleteList = new ArrayList<>();//配件明细中列表集合和删除配件弹框中的集合
    private List<Long> idLongList = new ArrayList<>();//配件明细中列表集合ID
    //在删除弹框中，存放选择后的将要删除的配件
    private List<PartsRows> statusList = new ArrayList<>();
    private int start = 0, limit = 30;
    private boolean flag = true;//true表示刷新，false表示加载更多
    private String PsrtsUrl, repairUrl, submitUrl;
    private String name = "";
    private OkHttpManager okHttpManager;
    private Gson mGson = new Gson();
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                String mes = (String) msg.obj;
                Object o = mGson.fromJson(mes, PartsRoot.class);
                if (o != null && o instanceof PartsRoot) {
                    PartsRoot partsRoot = (PartsRoot) o;
                    if (partsRoot != null && "0".equals(partsRoot.getCode())) {

                        if (partsRoot.getRows() != null) {

                            if (flag) {
                                mPartsAddList = partsRoot.getRows();
                                if (partsRoot.getRows().size() == 0) {
                                    Toast.makeText(RepairMessActivity.this, "无此配件", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                for (int i = 0; i < partsRoot.getRows().size(); i++) {
                                    mPartsAddList.add(partsRoot.getRows().get(i));
                                }
                                if (partsRoot.getRows().size() == 0) {
                                    Toast.makeText(RepairMessActivity.this, "已加载全部配件", Toast.LENGTH_SHORT).show();
                                }
                            }

                            addAlertAdapter.notifyDataSetChanged();

                        } else {
                            Toast.makeText(RepairMessActivity.this, "数据解析错误", Toast.LENGTH_SHORT).show();
                        }


                    } else if (partsRoot != null && "-1".equals(partsRoot.getCode())) {
                        Toast.makeText(RepairMessActivity.this, "登录过期，请重新登录", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(RepairMessActivity.this, "网络连接错误，请重新尝试", Toast.LENGTH_SHORT).show();
                    }


                } else {
                    Toast.makeText(RepairMessActivity.this, "列表数据错误", Toast.LENGTH_SHORT).show();
                }

            } else if (msg.what == 1010) {
                mSure_submit_btn.setClickable(true);
                BallProgressUtils.dismisLoading();
                Toast.makeText(RepairMessActivity.this, "连接服务器失败，请重新尝试", Toast.LENGTH_SHORT).show();
            } else if (msg.what == 2) {//维修详情
                String mes = (String) msg.obj;
                Object o = mGson.fromJson(mes, RepairMessRoot.class);
                if (o != null && o instanceof RepairMessRoot) {
                    RepairMessRoot repairMessRoot = (RepairMessRoot) o;
                    if (repairMessRoot != null && "0".equals(repairMessRoot.getCode())) {
                        if (repairMessRoot.getMaintenanceLog() != null) {
                            RepairMaintenanceLog repairMaintenanceLog = repairMessRoot.getMaintenanceLog();
                            mBumen_Tv.setText(repairMaintenanceLog.getDepartmentName() + "");
                            mPerson_Tv.setText(repairMaintenanceLog.getUserName() + "");
                            mName_Tv.setText(repairMaintenanceLog.getAssetName() + "");
                            mNum_tv.setText(repairMaintenanceLog.getTotalNum() + "");
                            mBianMa_tv.setText(repairMaintenanceLog.getBarcode() + "");
                            if (0 == repairMaintenanceLog.getMainType()) {
                                mLeiXing_tv.setText("日常维修");
                            } else {
                                mLeiXing_tv.setText("重大维修");
                            }
                            mBeizhuMsg_Tv.setText(repairMaintenanceLog.getComment() + "");
                            mTime_tv.setText(repairMaintenanceLog.getRepairDateString() + "");
                        } else {
                            Toast.makeText(RepairMessActivity.this, "详情数据错误", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(RepairMessActivity.this, "登录过期，请重新登录", Toast.LENGTH_SHORT).show();
                    }


                } else {
                    Toast.makeText(RepairMessActivity.this, "详情数据错误", Toast.LENGTH_SHORT).show();
                }
            } else if (msg.what == 3) {//提交维修
                mSure_submit_btn.setClickable(true);
                BallProgressUtils.dismisLoading();
                String mes = (String) msg.obj;
                Object o = mGson.fromJson(mes, PropertySubmitBean.class);
                if (o != null && o instanceof PropertySubmitBean) {
                    PropertySubmitBean property = (PropertySubmitBean) o;
                    if (property != null && "0".equals(property.getCode())) {
                        Toast.makeText(RepairMessActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK, intent);
                        finish();
                    } else if (property != null && "-1".equals(property.getCode())) {
                        Toast.makeText(RepairMessActivity.this, "登录过期，请重新登录", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(RepairMessActivity.this, "连接服务器失败，请重新尝试", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(RepairMessActivity.this, "提交失败", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };
    private long myid;//获取详情需要的id
    private Intent intent;
    private RelativeLayout mAll_rl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repair_mess);
        mAll_rl = (RelativeLayout) findViewById(R.id.activity_repair_mess);

        okHttpManager = OkHttpManager.getInstance();
        PsrtsUrl = URLTools.urlBase + URLTools.parts_list;//增加弹框中的配件列表
        repairUrl = URLTools.urlBase + URLTools.repair_mess;//维修详情
        submitUrl = URLTools.urlBase + URLTools.repair_submit;//确定提交维修
        Map<Object, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("start", start);
        map.put("limit", limit);
        intent = getIntent();
        myid = intent.getLongExtra("id", -1);
        okHttpManager.postMethod(false, PsrtsUrl, "弹框配件列表", map, handler, 1);
        okHttpManager.getMethod(false, repairUrl + "id=" + myid, "维修详情", handler, 2);
        initUI();
    }

    private void initUI() {
        mBack_img = (ImageView) findViewById(R.id.back_img);
        mBack_img.setOnClickListener(this);
        headerView = LayoutInflater.from(this).inflate(R.layout.repair_header_view, null);
        footerView = LayoutInflater.from(this).inflate(R.layout.repair_footer_view, null);
        mll = headerView.findViewById(R.id.ll_l);
        mBumen_Tv = (TextView) headerView.findViewById(R.id.cg_bumen_msg);
        mPerson_Tv = (TextView) headerView.findViewById(R.id.cg_person_msg);
        mName_Tv = (TextView) headerView.findViewById(R.id.cg_name_msg);
        mNum_tv = (TextView) headerView.findViewById(R.id.cg_num_msg);

        mBeizhuMsg_Tv = (TextView) headerView.findViewById(R.id.cg_beizhu_msg);
        mTime_tv = (TextView) headerView.findViewById(R.id.time_msg);
        mBianMa_tv = (TextView) headerView.findViewById(R.id.bianma_edit);
        mLeiXing_tv = (TextView) headerView.findViewById(R.id.weixiu_edit);

//        配件明细 增加 删除
        mParts_tv = (TextView) headerView.findViewById(R.id.parts_tv);
        mAdd_tv = (TextView) headerView.findViewById(R.id.add);
        mAdd_tv.setOnClickListener(this);
        mDelete_tv = (TextView) headerView.findViewById(R.id.delete);
        mDelete_tv.setOnClickListener(this);

        mFault_edit = footerView.findViewById(R.id.footer_edit);
        yesRadioButton = footerView.findViewById(R.id.footer_yes);
        noRadioButton = footerView.findViewById(R.id.footer_no);
        noRadioButton.setOnClickListener(this);
        yesRadioButton.setOnClickListener(this);
        mSure_submit_btn = footerView.findViewById(R.id.footer_sure);
        mSure_submit_btn.setOnClickListener(this);

        mList = (ListView) findViewById(R.id.parts_listview);
        partsAdapter = new PartsAdapter();
        mList.setAdapter(partsAdapter);

        mList.addHeaderView(headerView);
        mList.addFooterView(footerView);


        //增加按钮弹框
        mAddAlertBuilder = new AlertDialog.Builder(this, R.style.dialogNoBg);
        mAddAlertBuilder.setCancelable(false);
        mAddAlertDialog = mAddAlertBuilder.create();
        mAddAlertView = LayoutInflater.from(this).inflate(R.layout.add_alertbox, null);
        mAddAlertDialog.setView(mAddAlertView);
        mAddHeaderView = LayoutInflater.from(this).inflate(R.layout.repair_alert_header, null);
        mAddAlertLisview = mAddAlertView.findViewById(R.id.add_listview);//弹框中的listview
        mAddAlertLisview.addHeaderView(mAddHeaderView);//添加头
        addAlertAdapter = new AddAlertAdapter();
        mAddAlertLisview.setAdapter(addAlertAdapter);
        //点击选择资产注意：当ListView添加header或者footer后setOnItemClickListener会响应头和尾，在adapter中使用点击事件不会响应
        mAddAlertLisview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView textView = view.findViewById(R.id.select_state);
                if (i != 0) {
                    //集合中如果有这个物品，点击的时候变为白色状态，并且从集合中删除这个物品
                    if (idLongList.contains(mPartsAddList.get(i - 1).getId())) {
                        Toast.makeText(RepairMessActivity.this, "此配件已经选择", Toast.LENGTH_SHORT).show();
                        //集合中如果没有这个物品，点击的时候变为黑色状态，并且增加这个物品到集合中
                    } else {
                        textView.setBackgroundResource(R.drawable.bumen_box_select);
                        mPartsDeleteList.add(mPartsAddList.get(i - 1));
                        idLongList.add(mPartsAddList.get(i - 1).getId());
                    }
                }
            }
        });
        mAddAlertSureBtn = mAddAlertView.findViewById(R.id.add_sure_btn);//弹框中的确认
        //点击确定以后更新配件明细适配器
        mAddAlertSureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPartsDeleteList.size() != 0) {
                    mll.setVisibility(View.VISIBLE);
                } else {
                    mll.setVisibility(View.GONE);
                }
                partsAdapter.notifyDataSetChanged();
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
                okHttpManager.postMethod(false, PsrtsUrl, "刷新弹框配件列表", map, handler, 1);
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
                okHttpManager.postMethod(false, PsrtsUrl, "加载弹框配件列表", map, handler, 1);
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
                            Toast.makeText(RepairMessActivity.this, "请输入配件名称", Toast.LENGTH_SHORT).show();
                        } else {
                            flag = true;
                            start = 0;
                            limit = 30;
                            Map<Object, Object> map = new HashMap<>();
                            map.put("name", search_Edit.getText().toString().trim());
                            map.put("start", start);
                            map.put("limit", limit);
                            okHttpManager.postMethod(false, PsrtsUrl, "搜索弹框配件列表", map, handler, 1);
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
                okHttpManager.postMethod(false, PsrtsUrl, "搜索弹框配件列表", map, handler, 1);
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
        mDeleteAlertLisview = mDeleteAlertView.findViewById(R.id.delete_listview);
        mDeleteAlertLisview.addHeaderView(mAddHeaderView);
        deleteAlertAdapter = new DeleteAlertAdapter();
        mDeleteAlertLisview.setAdapter(deleteAlertAdapter);
        mDeleteAlertSureBtn = mDeleteAlertView.findViewById(R.id.delete_sure_btn);
        mDeleteAlertSureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //点击删除的时候需要将idList中的数据更新

                for (int i = 0; i < statusList.size(); i++) {
                    mPartsDeleteList.remove(statusList.get(i));
                    idLongList.remove(statusList.get(i).getId());
                }
                if (mPartsDeleteList.size() != 0) {
                    mll.setVisibility(View.VISIBLE);
                } else {
                    mll.setVisibility(View.GONE);
                }
                statusList.clear();
                partsAdapter.notifyDataSetChanged();
                mDeleteAlertDialog.dismiss();
            }
        });
        mDeleteAlertLisview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView textView = view.findViewById(R.id.select_state);

                if (i != 0) {

                    if (statusList.contains(mPartsDeleteList.get(i - 1))) {
                        statusList.remove(mPartsDeleteList.get(i - 1));
                        textView.setBackgroundResource(R.drawable.bumen_box);
                    } else {
                        statusList.add(mPartsDeleteList.get(i - 1));
                        textView.setBackgroundResource(R.drawable.bumen_box_select);
                    }


                }


            }
        });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == mBack_img.getId()) {
            finish();
        } else if (id == mAdd_tv.getId()) {//增加配件按钮
            if (mPartsAddList.size() != 0) {
                addAlertAdapter.notifyDataSetChanged();
                mAddAlertDialog.show();
            } else {
                Map<Object, Object> map = new HashMap<>();
                map.put("name", name);
                map.put("start", start);
                map.put("limit", limit);
                okHttpManager.postMethod(false, PsrtsUrl, "弹框配件列表", map, handler, 1);
                Toast.makeText(this, "正在获取资产列表,请稍等", Toast.LENGTH_SHORT).show();
            }
        } else if (id == mDelete_tv.getId()) {//删除配件按钮
            if (mPartsDeleteList.size() != 0) {
                mDeleteAlertDialog.show();
            } else {
                Toast.makeText(this, "暂无需要删除的物品", Toast.LENGTH_SHORT).show();
            }
        } else if (id == mSure_submit_btn.getId()) {//确定提交按钮

            if (myid != -1) {
                if (repairFlag != -1) {
                    mSure_submit_btn.setClickable(false);
                    BallProgressUtils.showLoading(RepairMessActivity.this, mAll_rl);
                    StringBuilder idL = new StringBuilder();
                    for (int i = 0; i < mPartsDeleteList.size(); i++) {
                        idL.append(mPartsDeleteList.get(i).getId() + "," + mPartsDeleteList.get(i).getNum() + ";");//这里需要添加数量id,num;id,num;
                    }
                    Log.e("提交配件的id集合", idL.toString());
                    Map<Object, Object> map = new HashMap<>();
                    map.put("id", myid);
                    map.put("isFixed", repairFlag);
                    map.put("assetsIds", idL.toString());
                    map.put("comment", mFault_edit.getText().toString().trim());
                    okHttpManager.postMethod(false, submitUrl, "提交维修", map, handler, 3);
                } else {
                    Toast.makeText(this, "请选择是否修复", Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(this, "详情错误,请重新尝试", Toast.LENGTH_SHORT).show();
            }
        } else if (id == yesRadioButton.getId()) {
            repairFlag = 1;
            Log.e("repairFlag=", repairFlag + "");
        } else if (id == noRadioButton.getId()) {
            repairFlag = 0;
            Log.e("repairFlag=", repairFlag + "");
        }
    }

    //配件明细适配器
    class PartsAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mPartsDeleteList.size();
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
            final PartsHolder partsHolder = new PartsHolder();
            view = LayoutInflater.from(RepairMessActivity.this).inflate(R.layout.repair_item_view, null);
            partsHolder.status = view.findViewById(R.id.select_state);
            partsHolder.name = view.findViewById(R.id.name_tv);
            partsHolder.xinghao = view.findViewById(R.id.xinghao_tv);
            partsHolder.num = view.findViewById(R.id.num_edit);

            partsHolder.name.setText(mPartsDeleteList.get(i).getAssetName() + "");
            partsHolder.xinghao.setText(mPartsDeleteList.get(i).getSpecTyp() + "");
            partsHolder.num.setText(mPartsDeleteList.get(i).getNum() + "");
            partsHolder.status.setVisibility(View.VISIBLE);
            partsHolder.status.setBackgroundResource(R.drawable.bumen_box_select);
            final int position = i;
            partsHolder.num.addTextChangedListener(new TextWatcher() {
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
                            Toast.makeText(RepairMessActivity.this, "请填写正确的数量", Toast.LENGTH_SHORT).show();
                            partsHolder.num.setText("1");
                        } else {
                            Integer num = Integer.valueOf(s);
                            mPartsDeleteList.get(position).setNum(num);

                        }

                    } else {
                        //若不填数量，将以原始数量为准
                        Toast.makeText(RepairMessActivity.this, "请填写数量", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            return view;
        }

        class PartsHolder {
            TextView status, name, xinghao;//状态 ,资产名称，型号
            EditText num;//数量
        }
    }

    //配件增加弹框适配器
    class AddAlertAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mPartsAddList.size();
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
            AddHolder addHolder = null;
            if (view == null) {
                addHolder = new AddHolder();
                view = LayoutInflater.from(RepairMessActivity.this).inflate(R.layout.repair_alert_header, null);
                addHolder.status = view.findViewById(R.id.select_state);
                addHolder.name = view.findViewById(R.id.name_tv);
                addHolder.xinghao = view.findViewById(R.id.xinghao_tv);
                addHolder.num = view.findViewById(R.id.num_tv);
                view.setTag(addHolder);
            } else {
                addHolder = (AddHolder) view.getTag();
            }
            addHolder.status.setVisibility(View.VISIBLE);
            addHolder.name.setText(mPartsAddList.get(i).getAssetName() + "");
            addHolder.xinghao.setText(mPartsAddList.get(i).getSpecTyp() + "");
            addHolder.num.setText(mPartsAddList.get(i).getNum() + "");
            //如果弹框配件列表中有配件明细中的配件，需要标记
            if (idLongList.contains(mPartsAddList.get(i).getId())) {
                addHolder.status.setBackgroundResource(R.drawable.bumen_box_select);
            } else {
                addHolder.status.setBackgroundResource(R.drawable.bumen_box);
            }
            return view;
        }

        class AddHolder {
            TextView status, name, xinghao, num;//状态 ,资产名称，型号    //数量

        }
    }

    //配件删除弹框适配器
    class DeleteAlertAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mPartsDeleteList.size();
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
                view = LayoutInflater.from(RepairMessActivity.this).inflate(R.layout.repair_alert_header, null);
                deleteHolder.status = view.findViewById(R.id.select_state);
                deleteHolder.name = view.findViewById(R.id.name_tv);
                deleteHolder.xinghao = view.findViewById(R.id.xinghao_tv);
                deleteHolder.num = view.findViewById(R.id.num_tv);
                view.setTag(deleteHolder);
            } else {
                deleteHolder = (DeleteHolder) view.getTag();
            }
            deleteHolder.name.setText(mPartsDeleteList.get(i).getAssetName() + "");
            deleteHolder.xinghao.setText(mPartsDeleteList.get(i).getSpecTyp() + "");
            deleteHolder.num.setText(mPartsDeleteList.get(i).getNum() + "");
            deleteHolder.status.setVisibility(View.VISIBLE);
            deleteHolder.status.setBackgroundResource(R.drawable.bumen_box);
            return view;
        }

        class DeleteHolder {
            TextView status, name, xinghao, num;//数量;//状态 ,资产名称，型号

        }
    }
}
