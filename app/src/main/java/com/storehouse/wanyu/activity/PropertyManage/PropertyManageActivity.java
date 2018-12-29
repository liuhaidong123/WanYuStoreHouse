package com.storehouse.wanyu.activity.PropertyManage;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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
import com.storehouse.wanyu.Bean.AssetQrCode;
import com.storehouse.wanyu.Bean.DepartmentRoot;
import com.storehouse.wanyu.Bean.DepartmentRows;
import com.storehouse.wanyu.Bean.ErwermaRoot;
import com.storehouse.wanyu.Bean.PropertyClassRoot;
import com.storehouse.wanyu.Bean.PropertyClassRows;
import com.storehouse.wanyu.Bean.PropertyLocationRoot;
import com.storehouse.wanyu.Bean.PropertyLocationRows;
import com.storehouse.wanyu.Bean.PropertyManageRoot;
import com.storehouse.wanyu.Bean.PropertyManageRows;
import com.storehouse.wanyu.Bean.PropertyManagerRoot;
import com.storehouse.wanyu.Bean.PropertyManagerRows;
import com.storehouse.wanyu.IPAddress.URLTools;
import com.storehouse.wanyu.MyUtils.BallProgressUtils;
import com.storehouse.wanyu.OkHttpUtils.OkHttpManager;
import com.storehouse.wanyu.R;
import com.xys.libzxing.activity.CaptureActivity;

import java.util.ArrayList;
import java.util.List;

//资产管理
public class PropertyManageActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView mBack, mSearch_btn;
    private EditText mEdit;
    private TextView mAdd_btn;
    TextView department_mes, leibie_mes, manager_mes, start_time_mes, end_time_mes, location_mes;
    private AlertDialog.Builder departmentBuilder, leibieBuilder, managerBuilder, time_builder, location_builder;
    private AlertDialog department_alertdialog, leibie_alertdialog, manager_alertdialog, time_alertdialog, location_alertdialog;
    private View department_view, leibie_view, manager_view, time_view, location_view;
    private ListView department_listview, leibie_listview, manager_listview, location_listview;
    private DatePicker datePickerStart;
    private DepartmentAdapter departmentAdapter;
    private ManagerAdapter managerAdapter;
    private LocationAdapter locationAdapter;
    private LeibieAdapter leibieAdapter;
    private List<DepartmentRows> mDepartmentList = new ArrayList<>();
    private List<PropertyManagerRows> mManagerList = new ArrayList<>();
    private List<PropertyLocationRows> mLocationList = new ArrayList<>();
    private List<PropertyClassRows> mLeiBieList = new ArrayList<>();
    private int mDepartmentPosition = -1, mLocationPosition = -1, mLeibiePosition = -1;
    private String saveUserID = "";//保管人id
    private String addressCode = "";//存放地code
    private String departmentCode = "";//科室code
    private String categoryCode = "";//资产类别code
    private String starttime = "", endtime = "";//时间段
    private int timeFlag = -1;//0 代表点击的是开始时间，1代表的是结束时间
    private String name = "";//搜索名称
    private SmartRefreshLayout mSmartRefreshLayout;
    private ListView mListView;
    private MyAdapter myAdapter;
    private View mHead;
    private String property_list_url, mSaoYiSao_Url, department_url, manager_url, location_url, leibie_url;
    ;
    private int start = 0, limit = 20;
    private boolean flag = true;//true表示刷新，false表示加载更多
    private OkHttpManager okHttpManager;
    private List<PropertyManageRows> propertyList = new ArrayList();
    private Gson gson = new Gson();
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            BallProgressUtils.dismisLoading();
            if (msg.what == 1) {//资产列表
                String s = (String) msg.obj;
                Object o = gson.fromJson(s, PropertyManageRoot.class);
                if (o != null && o instanceof PropertyManageRoot) {
                    PropertyManageRoot propertyManageRoot = (PropertyManageRoot) o;
                    if (propertyManageRoot != null && propertyManageRoot.getRows() != null) {

                        if (flag) {
                            propertyList = propertyManageRoot.getRows();
                            myAdapter.notifyDataSetChanged();
                        } else {
                            for (int i = 0; i < propertyManageRoot.getRows().size(); i++) {
                                propertyList.add(propertyManageRoot.getRows().get(i));
                            }
                            if (propertyManageRoot.getRows().size() == 0) {
                                Toast.makeText(PropertyManageActivity.this, "已加载全部数据", Toast.LENGTH_SHORT).show();

                            }
                            myAdapter.notifyDataSetChanged();
                        }


                    } else {
                        Toast.makeText(PropertyManageActivity.this, "请求资产列表错误,请检查服务器", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(PropertyManageActivity.this, "请求资产列表错误", Toast.LENGTH_SHORT).show();
                }
            } else if (msg.what == 1010) {
                Toast.makeText(PropertyManageActivity.this, "数据错误，请检查后台服务器", Toast.LENGTH_SHORT).show();
            } else if (msg.what == 11) {
                String mes = (String) msg.obj;

                Object o = gson.fromJson(mes, ErwermaRoot.class);
                Log.e("扫一扫接口信息=", mes);
                if (o != null && o instanceof ErwermaRoot) {
                    ErwermaRoot erwermaRoot = (ErwermaRoot) o;
                    if ("0".equals(erwermaRoot.getCode())) {
                        AssetQrCode assetQrCode = erwermaRoot.getAssetQrCode();
                        if (assetQrCode != null) {
                            if (assetQrCode.getIsUse() == 0) {//这是一个新的二维码，可以保存资产信息，跳转到资产入库页面
                                // Toast.makeText(getActivity(), "这是一个新的二维码，可以保存资产信息，跳转到资产入库页面", Toast.LENGTH_SHORT).show();
                                //将资产编码传过去
                                Intent intent = new Intent(PropertyManageActivity.this, AddPropertyActivity.class);
                                intent.putExtra("barcode", assetQrCode.getBarcode());
                                startActivity(intent);
                            } else if (assetQrCode.getIsUse() != 0 && assetQrCode.getAssetId() != 0) {//这是一个已经使用过的二维码，已经保存过资产信息，跳转到资产详情页面
                                // 接这个接口  http://192.168.1.168:8085/mobileapi/asset/get.do?id=assetQrCode.getAssetId()
                                // Toast.makeText(getActivity(), "这是一个已经使用过的二维码，已经保存过资产信息，跳转到资产详情页面", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(PropertyManageActivity.this, PropertyMessageActivity.class);
                                i.putExtra("assetID", assetQrCode.getAssetId());
                                startActivity(i);
                            } else {
                                Toast.makeText(PropertyManageActivity.this, "此二维码无效", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(PropertyManageActivity.this, "此二维码不存在", Toast.LENGTH_SHORT).show();

                        }


                    } else if ("-1".equals(erwermaRoot.getCode())) {
                        Toast.makeText(PropertyManageActivity.this, "账号过期，请重新登录", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(PropertyManageActivity.this, "二维码扫数据错误", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(PropertyManageActivity.this, "此二维码无效", Toast.LENGTH_SHORT).show();
                }


            } else if (msg.what == 3) {//科室接口
                String s = (String) msg.obj;
                Object o = gson.fromJson(s, DepartmentRoot.class);
                if (o != null && o instanceof DepartmentRoot) {
                    DepartmentRoot departmentRoot = (DepartmentRoot) o;
                    if (departmentRoot != null && "0".equals(departmentRoot.getCode())) {

                        if (departmentRoot.getRows() != null) {
                            if (departmentRoot.getRows().size() != 0) {
                                mDepartmentList = departmentRoot.getRows();
                                departmentAdapter.notifyDataSetChanged();

                            } else {

                                //根据对应科室数据请求资产数据
                                if (mDepartmentPosition != -1) {

                                    department_mes.setText(mDepartmentList.get(mDepartmentPosition).getDepartmentName() + "");
                                    departmentCode = mDepartmentList.get(mDepartmentPosition).getDepartmentCode();
                                    department_alertdialog.dismiss();
                                    //选择科室后，请求资产列表
                                    property_list_url = URLTools.urlBase + URLTools.property_new_list + "start=" + start + "&limit=" + limit + "&name=" + name + "&departmentCode=" + departmentCode + "&saveUserId=" + saveUserID + "&passEntryTimeBegin=" + starttime + "&passEntryTimeEnd=" + endtime + "&addressCode=" + addressCode + "&categoryCode=" + categoryCode;
                                    okHttpManager.getMethod(false, property_list_url, "请求资产列表", mHandler, 1);
                                } else {
                                    department_alertdialog.dismiss();
                                    Toast.makeText(PropertyManageActivity.this, "科室错误", Toast.LENGTH_SHORT).show();
                                }
                                //code = mDepartmentList.get(mPosition).getDepartmentCode();

                            }
                        } else {
                            Toast.makeText(PropertyManageActivity.this, "科室数据集合为null", Toast.LENGTH_SHORT).show();

                        }
                    } else if (departmentRoot != null && "-1".equals(departmentRoot.getCode())) {
                        Toast.makeText(PropertyManageActivity.this, "登录过期,请重新登录", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(PropertyManageActivity.this, "科室数据错误", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(PropertyManageActivity.this, "科室数据错误", Toast.LENGTH_SHORT).show();

                }

            } else if (msg.what == 2) {//请求保管人接口
                String s = (String) msg.obj;
                Object o = gson.fromJson(s, PropertyManagerRoot.class);
                if (o != null && o instanceof PropertyManagerRoot) {

                    PropertyManagerRoot propertyManagerRoot = (PropertyManagerRoot) o;

                    if (propertyManagerRoot != null && "0".equals(propertyManagerRoot.getCode())) {

                        if (propertyManagerRoot.getRows() != null) {

                            if (propertyManagerRoot.getRows().size() != 0) {
                                mManagerList = propertyManagerRoot.getRows();
                                managerAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(PropertyManageActivity.this, "抱歉，暂无保管人", Toast.LENGTH_SHORT).show();

                            }


                        } else {
                            Toast.makeText(PropertyManageActivity.this, "请求保管人数据集合为null", Toast.LENGTH_SHORT).show();

                        }

                    } else {
                        Toast.makeText(PropertyManageActivity.this, "登录过期,请重新登录", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(PropertyManageActivity.this, "保管人数据错误", Toast.LENGTH_SHORT).show();
                }
            } else if (msg.what == 4) {//请求资产存放地接口
                String s = (String) msg.obj;
                Object o = gson.fromJson(s, PropertyLocationRoot.class);
                if (o != null && o instanceof PropertyLocationRoot) {
                    PropertyLocationRoot propertyLocationRoot = (PropertyLocationRoot) o;
                    if (propertyLocationRoot != null && "0".equals(propertyLocationRoot.getCode())) {

                        if (propertyLocationRoot.getRows() != null) {
                            if (propertyLocationRoot.getRows().size() != 0) {
                                mLocationList = propertyLocationRoot.getRows();
                                locationAdapter.notifyDataSetChanged();

                            } else {
                                if (mLocationPosition != -1) {
                                    location_mes.setText(mLocationList.get(mLocationPosition).getAddressName() + "");
                                    addressCode = mLocationList.get(mLocationPosition).getAddressCode();
                                    location_alertdialog.dismiss();
                                    //请求资产列表
                                    property_list_url = URLTools.urlBase + URLTools.property_new_list + "start=" + start + "&limit=" + limit + "&name=" + name + "&departmentCode=" + departmentCode + "&saveUserId=" + saveUserID + "&passEntryTimeBegin=" + starttime + "&passEntryTimeEnd=" + endtime + "&addressCode=" + addressCode + "&categoryCode=" + categoryCode;
                                    okHttpManager.getMethod(false, property_list_url, "请求资产列表", mHandler, 1);
                                } else {
                                    location_alertdialog.dismiss();
                                }
                            }
                        } else {
                            Toast.makeText(PropertyManageActivity.this, "资产存放地数据集合为null", Toast.LENGTH_SHORT).show();

                        }
                    } else if (propertyLocationRoot != null && "-1".equals(propertyLocationRoot.getCode())) {
                        Toast.makeText(PropertyManageActivity.this, "登录过期,请重新登录", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(PropertyManageActivity.this, "存放地数据错误", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(PropertyManageActivity.this, "资产存放地数据错误", Toast.LENGTH_SHORT).show();

                }

            } else if (msg.what == 5) {//请求资产类别接口
                String s = (String) msg.obj;
                Object o = gson.fromJson(s, PropertyClassRoot.class);
                if (o != null && o instanceof PropertyClassRoot) {
                    PropertyClassRoot propertyClassRoot = (PropertyClassRoot) o;
                    if (propertyClassRoot != null && "0".equals(propertyClassRoot.getCode())) {

                        if (propertyClassRoot.getRows() != null) {
                            if (propertyClassRoot.getRows().size() != 0) {
                                mLeiBieList = propertyClassRoot.getRows();
                                leibieAdapter.notifyDataSetChanged();

                            } else {
                                if (mLeibiePosition != -1) {
                                    //Toast.makeText(AddPropertyActivity.this, "抱歉，没有此类别的资产", Toast.LENGTH_SHORT).show();
                                    leibie_mes.setText(mLeiBieList.get(mLeibiePosition).getCategoryName() + "");
                                    categoryCode = mLeiBieList.get(mLeibiePosition).getCategoryCode();
                                    leibie_alertdialog.dismiss();
                                    //，请求资产列表
                                    property_list_url = URLTools.urlBase + URLTools.property_new_list + "start=" + start + "&limit=" + limit + "&name=" + name + "&departmentCode=" + departmentCode + "&saveUserId=" + saveUserID + "&passEntryTimeBegin=" + starttime + "&passEntryTimeEnd=" + endtime + "&addressCode=" + addressCode + "&categoryCode=" + categoryCode;
                                    okHttpManager.getMethod(false, property_list_url, "请求资产列表", mHandler, 1);
                                }
                            }
                        } else {
                            Toast.makeText(PropertyManageActivity.this, "资产类别数据集合为null", Toast.LENGTH_SHORT).show();

                        }
                    } else {
                        Toast.makeText(PropertyManageActivity.this, "登录过期,请重新登录", Toast.LENGTH_SHORT).show();
                    }


                } else {
                    Toast.makeText(PropertyManageActivity.this, "资产类别数据错误", Toast.LENGTH_SHORT).show();

                }

            }
        }
    };
    private RelativeLayout mAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_manage);
        mAll = (RelativeLayout) findViewById(R.id.activity_property_manage);
        BallProgressUtils.showLoading(this, mAll);
        initUI();
    }


    private void initUI() {
        //请求资产列表
        property_list_url = URLTools.urlBase + URLTools.property_new_list + "start=" + start + "&limit=" + limit + "&name=" + name + "&departmentCode=" + departmentCode + "&saveUserId=" + saveUserID + "&passEntryTimeBegin=" + starttime + "&passEntryTimeEnd=" + endtime + "&addressCode=" + addressCode + "&categoryCode=" + categoryCode;
        okHttpManager = OkHttpManager.getInstance();
        okHttpManager.getMethod(false, property_list_url, "请求资产列表", mHandler, 1);
        //科室类别
        department_url = URLTools.urlBase + URLTools.department_list + "parentId=0";
        okHttpManager.getMethod(false, department_url, "科室类别接口", mHandler, 3);
        //资产保管人
        manager_url = URLTools.urlBase + URLTools.property_manager;
        okHttpManager.getMethod(false, manager_url, "请求资产保管人", mHandler, 2);

        //资产存放
        location_url = URLTools.urlBase + URLTools.property_location_list + "parentId=0";
        okHttpManager.getMethod(false, location_url, "资产存放地接口", mHandler, 4);
        //资产类别
        leibie_url = URLTools.urlBase + URLTools.property_leibie + "parentId=0";
        okHttpManager.getMethod(false, leibie_url, "资产类别接口", mHandler, 5);

        mBack = (ImageView) findViewById(R.id.back_img);
        mBack.setOnClickListener(this);
        //搜索
        mSearch_btn = (ImageView) findViewById(R.id.property_search_btn);
        mSearch_btn.setOnClickListener(this);
        //输入框
        mEdit = (EditText) findViewById(R.id.input_edit);
        mEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                                            @Override
                                            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                                                if (!"".equals(mEdit.getText().toString().trim())) {
                                                    if (i == EditorInfo.IME_ACTION_SEND || (keyEvent != null && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                                                        //按下的时候会会执行：手指按下和手指松开俩个过程
                                                        if (keyEvent.getAction() == KeyEvent.ACTION_UP) {
                                                            //search_url = URLTools.urlBase + URLTools.property_list + "name=" + mEdit.getText().toString().trim();
                                                            start = 0;
                                                            name = mEdit.getText().toString().trim();
                                                            property_list_url = URLTools.urlBase + URLTools.property_new_list + "start=" + start + "&limit=" + limit + "&name=" + name + "&departmentCode=" + departmentCode + "&saveUserId=" + saveUserID + "&passEntryTimeBegin=" + starttime + "&passEntryTimeEnd=" + endtime + "&addressCode=" + addressCode + "&categoryCode=" + categoryCode;
                                                            flag = true;
                                                            okHttpManager.getMethod(false, property_list_url, "请求搜索资产列表", mHandler, 1);
                                                        }

                                                        return true;
                                                    }
                                                } else {
                                                    Toast.makeText(PropertyManageActivity.this, "请输入搜索内容", Toast.LENGTH_SHORT).show();
                                                }

                                                return false;
                                            }


                                        }

        );
        //增加
        mAdd_btn = (TextView) findViewById(R.id.property_add_btn);
        mAdd_btn.setOnClickListener(this);
        //刷新
        mSmartRefreshLayout = (SmartRefreshLayout) findViewById(R.id.property_refresh);
        mListView = (ListView) findViewById(R.id.property_listview);
        myAdapter = new MyAdapter();
        mHead = LayoutInflater.from(this).inflate(R.layout.property_head, null);
        mListView.addHeaderView(mHead);
        mListView.setAdapter(myAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //跳转详情
                Intent intent = new Intent(PropertyManageActivity.this, PropertyMessageActivity.class);
                intent.putExtra("assetID", propertyList.get(i).getId());
                startActivity(intent);
            }
        });

        mSmartRefreshLayout.setRefreshHeader(new CircleHeader(this));
        mSmartRefreshLayout.setRefreshFooter(new BallPulseFooter(this).setSpinnerStyle(SpinnerStyle.Scale).setAnimatingColor(ContextCompat.getColor(this, R.color.color_1c82d4)));
        mSmartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
                                                     @Override
                                                     public void onRefresh(RefreshLayout refreshlayout) {
                                                         refreshlayout.finishRefresh(2000/*,false*/);//传入false表示刷新失败
                                                         flag = true;
                                                         start = 0;
                                                         //刷新时候所有数据变为默认值
                                                         mEdit.setText("");
                                                         name = "";
                                                         departmentCode = "";
                                                         saveUserID = "";
                                                         starttime = "";
                                                         endtime = "";
                                                         addressCode = "";
                                                         categoryCode = "";
                                                         department_mes.setText("未选择");
                                                         leibie_mes.setText("未选择");
                                                         manager_mes.setText("未选择");
                                                         start_time_mes.setText("开始时间");
                                                         end_time_mes.setText("结束时间");
                                                         location_mes.setText("未选择");
                                                         //property_list_url = URLTools.urlBase + URLTools.property_list + "start=" + start + "&limit=" + limit;
                                                         property_list_url = URLTools.urlBase + URLTools.property_new_list + "start=" + start + "&limit=" + limit + "&name=" + name + "&departmentCode=" + departmentCode + "&saveUserId=" + saveUserID + "&passEntryTimeBegin=" + starttime + "&passEntryTimeEnd=" + endtime + "&addressCode=" + addressCode + "&categoryCode=" + categoryCode;
                                                         okHttpManager.getMethod(false, property_list_url, "请求资产列表", mHandler, 1);
                                                     }
                                                 }
        );
        mSmartRefreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
                                                      @Override
                                                      public void onLoadmore(RefreshLayout refreshlayout) {
                                                          refreshlayout.finishLoadmore(2000/*,false*/);//传入false表示加载失败
                                                          flag = false;
                                                          start += 20;
                                                          name = mEdit.getText().toString().trim();
                                                          //mEdit.setText("");
                                                          //property_list_url = URLTools.urlBase + URLTools.property_list + "start=" + start + "&limit=" + limit;
                                                          property_list_url = URLTools.urlBase + URLTools.property_new_list + "start=" + start + "&limit=" + limit + "&name=" + name + "&departmentCode=" + departmentCode + "&saveUserId=" + saveUserID + "&passEntryTimeBegin=" + starttime + "&passEntryTimeEnd=" + endtime + "&addressCode=" + addressCode + "&categoryCode=" + categoryCode;
                                                          okHttpManager.getMethod(false, property_list_url, "请求资产列表", mHandler, 1);
                                                      }
                                                  }

        );


        department_mes = (TextView) findViewById(R.id.department_mess);
        department_mes.setOnClickListener(this);
        leibie_mes = (TextView) findViewById(R.id.leibie_mess);
        leibie_mes.setOnClickListener(this);
        manager_mes = (TextView) findViewById(R.id.manager_mess);
        manager_mes.setOnClickListener(this);
        start_time_mes = (TextView) findViewById(R.id.start_time_mess);
        start_time_mes.setOnClickListener(this);
        end_time_mes = (TextView) findViewById(R.id.end_time_mess);
        end_time_mes.setOnClickListener(this);
        location_mes = (TextView) findViewById(R.id.location_mess);
        location_mes.setOnClickListener(this);
        //科室
        departmentBuilder = new AlertDialog.Builder(this);
        department_alertdialog = departmentBuilder.create();
        department_view = LayoutInflater.from(this).inflate(R.layout.department_alert, null);
        department_alertdialog.setView(department_view);
        departmentAdapter = new DepartmentAdapter();
        department_listview = department_view.findViewById(R.id.department_listview);
        department_listview.setAdapter(departmentAdapter);
        department_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                flag = true;
                start=0;
                mDepartmentPosition = i;
                department_url = URLTools.urlBase + URLTools.department_list + "parentId=" + mDepartmentList.get(i).getId();
                okHttpManager.getMethod(false, department_url, "科室类别接口", mHandler, 3);
            }
        });

        //类别
        leibieBuilder = new AlertDialog.Builder(this);
        leibie_alertdialog = leibieBuilder.create();
        leibie_view = LayoutInflater.from(this).inflate(R.layout.leibie_alert, null);
        leibie_alertdialog.setView(leibie_view);
        leibieAdapter = new LeibieAdapter();
        leibie_listview = leibie_view.findViewById(R.id.leibie_listview);
        leibie_listview.setAdapter(leibieAdapter);
        leibie_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                flag = true;
                start=0;
                mLeibiePosition = i;
                leibie_url = URLTools.urlBase + URLTools.property_leibie + "parentId=" + mLeiBieList.get(i).getId();
                okHttpManager.getMethod(false, leibie_url, "资产类别接口", mHandler, 5);
            }
        });
        //保管人
        managerBuilder = new AlertDialog.Builder(this);
        manager_alertdialog = managerBuilder.create();
        manager_view = LayoutInflater.from(this).inflate(R.layout.manager_alert, null);
        manager_alertdialog.setView(manager_view);
        manager_listview = manager_view.findViewById(R.id.manager_listview);
        managerAdapter = new ManagerAdapter();
        manager_listview.setAdapter(managerAdapter);
        manager_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                flag = true;
                start=0;
                manager_mes.setText(mManagerList.get(i).getTrueName() + "");
                saveUserID = mManagerList.get(i).getId() + "";
                manager_alertdialog.dismiss();
                //，请求资产列表
                property_list_url = URLTools.urlBase + URLTools.property_new_list + "start=" + start + "&limit=" + limit + "&name=" + name + "&departmentCode=" + departmentCode + "&saveUserId=" + saveUserID + "&passEntryTimeBegin=" + starttime + "&passEntryTimeEnd=" + endtime + "&addressCode=" + addressCode + "&categoryCode=" + categoryCode;
                okHttpManager.getMethod(false, property_list_url, "请求资产列表", mHandler, 1);
            }
        });

        time_builder = new AlertDialog.Builder(this);
        time_alertdialog = time_builder.create();
        time_view = LayoutInflater.from(this).inflate(R.layout.select_start_time, null);
        time_alertdialog.setView(time_view);
        datePickerStart = time_view.findViewById(R.id.start_datePicker);
        TextView cancleStartTimeBtn = time_view.findViewById(R.id.cancle_btn);
        TextView sureStartTimeBtn = time_view.findViewById(R.id.sure_btn);
        cancleStartTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                time_alertdialog.dismiss();
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
                if (timeFlag == 0) {//开始时间
                    starttime = year + "-" + month2 + "-" + day2;
                    start_time_mes.setText(year + "-" + month2 + "-" + day2);
                }
                if (timeFlag == 1) {//结束时间
                    endtime = year + "-" + month2 + "-" + day2;
                    end_time_mes.setText(year + "-" + month2 + "-" + day2);
                }
                flag = true;
                start=0;
                //，请求资产列表
                property_list_url = URLTools.urlBase + URLTools.property_new_list + "start=" + start + "&limit=" + limit + "&name=" + name + "&departmentCode=" + departmentCode + "&saveUserId=" + saveUserID + "&passEntryTimeBegin=" + starttime + "&passEntryTimeEnd=" + endtime + "&addressCode=" + addressCode + "&categoryCode=" + categoryCode;
                okHttpManager.getMethod(false, property_list_url, "请求资产列表", mHandler, 1);
                time_alertdialog.dismiss();
            }
        });

        //存放地
        location_builder = new AlertDialog.Builder(this);
        location_alertdialog = location_builder.create();
        location_view = LayoutInflater.from(this).inflate(R.layout.location_alert, null);
        location_alertdialog.setView(location_view);
        location_listview = location_view.findViewById(R.id.location_listview);
        locationAdapter = new LocationAdapter();
        location_listview.setAdapter(locationAdapter);
        location_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                flag = true;
                start=0;
                mLocationPosition = i;
                location_url = URLTools.urlBase + URLTools.property_location_list + "parentId=" + mLocationList.get(i).getId();
                okHttpManager.getMethod(false, location_url, "资产存放地接口", mHandler, 4);

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            flag = true;
            start = 0;
            property_list_url = URLTools.urlBase + URLTools.property_list + "start=" + start + "&limit=" + limit;
            okHttpManager.getMethod(false, property_list_url, "请求资产列表", mHandler, 1);
        } else if (requestCode == 0 && resultCode == RESULT_OK) {
            //扫一扫
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                String scanResult = bundle.getString("result");
                mSaoYiSao_Url = URLTools.urlBase + URLTools.saoyisao + "barcode=" + scanResult;
                okHttpManager.getMethod(false, mSaoYiSao_Url, "扫一扫接口", mHandler, 11);
                Log.e("scanResult=", scanResult);
            } else {
                Toast.makeText(this, "扫描信息错误", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == mBack.getId()) {
            finish();
        } else if (id == mAdd_btn.getId()) {
            checkCameraPermission();
            //Intent intent5 = new Intent(this, AddPropertyActivity.class);
            //startActivityForResult(intent5,1);
            //startActivity(intent5);
        } else if (id == mSearch_btn.getId()) {
            if (!"".equals(mEdit.getText().toString().trim())) {
                //search_url = URLTools.urlBase + URLTools.property_list + "name=" + mEdit.getText().toString().trim();
                start = 0;
                start=0;
                property_list_url = URLTools.urlBase + URLTools.property_new_list + "start=" + start + "&limit=" + limit + "&name=" + name + "&departmentCode=" + departmentCode + "&saveUserId=" + saveUserID + "&passEntryTimeBegin=" + starttime + "&passEntryTimeEnd=" + endtime + "&addressCode=" + addressCode + "&categoryCode=" + categoryCode;
                flag = true;
                okHttpManager.getMethod(false, property_list_url, "请求搜索资产列表", mHandler, 1);
            } else {
                Toast.makeText(PropertyManageActivity.this, "请输入搜索内容", Toast.LENGTH_SHORT).show();

            }

        } else if (id == department_mes.getId()) {//选择科室

            //科室类别
            department_url = URLTools.urlBase + URLTools.department_list + "parentId=0";
            okHttpManager.getMethod(false, department_url, "科室类别接口", mHandler, 3);
            department_alertdialog.show();


        } else if (id == leibie_mes.getId()) {//选择类别

            //选择类别
            leibie_url = URLTools.urlBase + URLTools.property_leibie + "parentId=0";
            okHttpManager.getMethod(false, leibie_url, "资产类别接口", mHandler, 5);
            leibie_alertdialog.show();

        } else if (id == manager_mes.getId()) {//选择保管人
            if (mManagerList.size() == 0) {

                Toast.makeText(PropertyManageActivity.this, "正在保管人列表，请稍后。。。", Toast.LENGTH_SHORT).show();
                okHttpManager.getMethod(false, manager_url, "请求资产保管人", mHandler, 2);
            } else {
                manager_alertdialog.show();
            }
        } else if (id == start_time_mes.getId()) {//选择开始时间时间段
            timeFlag = 0;
            time_alertdialog.show();

        } else if (id == end_time_mes.getId()) {//选择结束时间时间段
            timeFlag = 1;
            time_alertdialog.show();

        } else if (id == location_mes.getId()) {//选择存放地

            //每次弹框之前，重新设置数据
            location_url = URLTools.urlBase + URLTools.property_location_list + "parentId=0";
            okHttpManager.getMethod(false, location_url, "资产存放地接口", mHandler, 4);
            location_alertdialog.show();

        }
    }

    /**
     * 照相机权限
     */
    public static final int CAMERA_CODE = 1;

    private void checkCameraPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            int checkTag = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
            //如果相机权限没有授权
            if (checkTag != PackageManager.PERMISSION_GRANTED) {
                //在这个数组中可以添加很多权限
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_CODE);
                return;
            } else {
                //如果已经授权，执行业务逻辑
                Intent intent = new Intent(this, CaptureActivity.class);
                startActivityForResult(intent, 0);
            }
        } else {
            //版本小于23时，不需要判断敏感权限，执行业务逻辑
            Intent intent = new Intent(this, CaptureActivity.class);
            startActivityForResult(intent, 0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_CODE) {
            //点击了允许，授权成功
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(this, CaptureActivity.class);
                startActivityForResult(intent, 0);
            } else {
                //点击了拒绝，授权失败
                Toast.makeText(this, "相机授权失败，请授权", Toast.LENGTH_SHORT).show();

            }


        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }

    //资产列表适配器
    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return propertyList.size();
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
            MyHolder myHolder = null;
            if (view == null) {
                myHolder = new MyHolder();
                view = LayoutInflater.from(PropertyManageActivity.this).inflate(R.layout.property_item, null);
                myHolder.number = view.findViewById(R.id.select_state);
                myHolder.mbianhao = view.findViewById(R.id.bianhao_tv);
                myHolder.name = view.findViewById(R.id.name_tv);
                myHolder.manager = view.findViewById(R.id.manager_tv);
                myHolder.location = view.findViewById(R.id.location_tv);
                view.setTag(myHolder);
            } else {
                myHolder = (MyHolder) view.getTag();
            }

            myHolder.number.setText(i + 1 + "");
            myHolder.mbianhao.setText(propertyList.get(i).getNum() + "");
            myHolder.name.setText(propertyList.get(i).getAssetName() + "");
            myHolder.manager.setText(propertyList.get(i).getSaveUserName() + "");
            myHolder.location.setText(propertyList.get(i).getAddressName() + "");

            return view;
        }

        class MyHolder {
            TextView number, mbianhao, name, manager, location;
        }
    }

    //    科室下拉适配器
    class DepartmentAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mDepartmentList.size();
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
            DepartmentHolder departmentHolder = null;
            if (view == null) {
                view = LayoutInflater.from(PropertyManageActivity.this).inflate(R.layout.bumen_alert_item, null);
                departmentHolder = new DepartmentHolder();
                departmentHolder.tv = view.findViewById(R.id.bumen_item_tv);
                view.setTag(departmentHolder);
            } else {
                departmentHolder = (DepartmentHolder) view.getTag();
            }
            departmentHolder.tv.setText(mDepartmentList.get(i).getDepartmentName() + "");
            return view;
        }

        class DepartmentHolder {
            TextView tv;
        }
    }

    //    保管人下拉适配器
    class ManagerAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mManagerList.size();
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
            ManagerHolder managerHolder = null;
            if (view == null) {
                view = LayoutInflater.from(PropertyManageActivity.this).inflate(R.layout.bumen_alert_item, null);
                managerHolder = new ManagerHolder();
                managerHolder.tv = view.findViewById(R.id.bumen_item_tv);
                view.setTag(managerHolder);
            } else {
                managerHolder = (ManagerHolder) view.getTag();
            }
            managerHolder.tv.setText(mManagerList.get(i).getTrueName() + "");
            return view;
        }

        class ManagerHolder {
            TextView tv;
        }
    }

    //    时间段下拉适配器
    class LeibieAdapter extends BaseAdapter {

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
            LeibieHolder leibieHolder = null;
            if (view == null) {
                view = LayoutInflater.from(PropertyManageActivity.this).inflate(R.layout.bumen_alert_item, null);
                leibieHolder = new LeibieHolder();
                leibieHolder.tv = view.findViewById(R.id.bumen_item_tv);
                view.setTag(leibieHolder);
            } else {
                leibieHolder = (LeibieHolder) view.getTag();
            }
            leibieHolder.tv.setText(mLeiBieList.get(i).getCategoryName()+"");
            return view;
        }

        class LeibieHolder {
            TextView tv;
        }
    }

    //    存放地段下拉适配器
    class LocationAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mLocationList.size();
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
            LibraryHolder libraryHolder = null;
            if (view == null) {
                view = LayoutInflater.from(PropertyManageActivity.this).inflate(R.layout.bumen_alert_item, null);
                libraryHolder = new LibraryHolder();
                libraryHolder.tv = view.findViewById(R.id.bumen_item_tv);
                view.setTag(libraryHolder);
            } else {
                libraryHolder = (LibraryHolder) view.getTag();
            }
            libraryHolder.tv.setText(mLocationList.get(i).getAddressName() + "");
            return view;
        }

        class LibraryHolder {
            TextView tv;
        }
    }
}
