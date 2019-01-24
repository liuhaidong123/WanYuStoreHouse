package com.storehouse.wanyu.activity.PropertyManage;

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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.storehouse.wanyu.Bean.CaiGouDetailsRoot;
import com.storehouse.wanyu.Bean.CaiGouDetailsRows;
import com.storehouse.wanyu.Bean.PropertyClassRoot;
import com.storehouse.wanyu.Bean.PropertyClassRows;
import com.storehouse.wanyu.Bean.PropertyLocationRoot;
import com.storehouse.wanyu.Bean.PropertyLocationRows;
import com.storehouse.wanyu.Bean.PropertyManagerRoot;
import com.storehouse.wanyu.Bean.PropertyManagerRows;
import com.storehouse.wanyu.Bean.PropertySubmitBean;
import com.storehouse.wanyu.Bean.Rows;
import com.storehouse.wanyu.IPAddress.URLTools;
import com.storehouse.wanyu.MyUtils.BallProgressUtils;
import com.storehouse.wanyu.OkHttpUtils.OkHttpManager;
import com.storehouse.wanyu.R;
import com.storehouse.wanyu.activity.ApplyActivity.ApplyBaoFeiActivity;
import com.storehouse.wanyu.activity.ApplyActivity.ApplyCaiGouActivity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//资产入库
public class AddPropertyActivity extends AppCompatActivity {
    private RelativeLayout mAll;
    private Intent intent;
    private CaiGouDetailsRows caiGouDetailsRows;
    private ImageView mBack;
    private TextView mleiBie_Tv, mManager_Tv, mLocation_Edit, mSubmit_btn;//资产类别，保管人
    private EditText mYear_edit, mPrice_edit, mQuantity_edit, mUnit_edit, mRemark_edit;//年限，价格，数量，计量单位，备注
    private EditText mName_Edit, mXingHao_Edit, mBianHao_Edit;//资产名称，型号，所在位置，资产编号
    private RelativeLayout mLocation_RL_Btn, mLeiBie_RL_Btn, mManager_RL_Btn, mUnit_Rl_btn;//存放地点弹框，选择资产类别弹框，保管人弹框,计量单位
    private AlertDialog.Builder mBuilderLeiBie, mBuilderManager, mBuilderLocation;//资产类别，保管人弹框,存放地弹框
    private AlertDialog mAlertDialogLeiBie, mAlertDialogManager, mAlertDialogLocation;//
    private View mLeiBieView, mManagerView, mLocationView;
    private ListView mLeiBiewListview, mManagerListView, mLocationListView;
    private LeiBieAdapter mLeiBieAdapter;
    private ManagerAdapter mManagerAdapter;
    private LocationAdapter mLocationAdapter;
    private List<PropertyClassRows> mLeiBieList = new ArrayList<>();
    private List<PropertyManagerRows> mManagerList = new ArrayList<>();
    private List<PropertyLocationRows> mLocationList = new ArrayList<>();
    //计量单位
    private AlertDialog.Builder mBuilderDanWei;
    private AlertDialog mAlertDialogDanWei;
    private ListView mDanwei_ListView;
    private List<String> mDanWeiList = new ArrayList<>();

    private int mPosition = -1, mLocationPosition = -1;
    private String categoryCode, addressCode;//资产类别编号,存放地编号
    private long saveUserID;//保管人id
    private OkHttpManager okHttpManager;
    private Gson gson = new Gson();
    private String leibie_url, location_url, manager_url, submit_url;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {//请求资产类别接口
                try{
                    String s = (String) msg.obj;
                    Object o = gson.fromJson(s, PropertyClassRoot.class);
                    if (o != null && o instanceof PropertyClassRoot) {
                        PropertyClassRoot propertyClassRoot = (PropertyClassRoot) o;
                        if (propertyClassRoot != null && "0".equals(propertyClassRoot.getCode())) {

                            if (propertyClassRoot.getRows() != null) {
                                if (propertyClassRoot.getRows().size() != 0) {
                                    mLeiBieList = propertyClassRoot.getRows();
                                    mLeiBieAdapter.notifyDataSetChanged();

                                } else {
                                    if (mPosition != -1) {
                                        //Toast.makeText(AddPropertyActivity.this, "抱歉，没有此类别的资产", Toast.LENGTH_SHORT).show();
                                        mleiBie_Tv.setText(mLeiBieList.get(mPosition).getCategoryName() + "");
                                        categoryCode = mLeiBieList.get(mPosition).getCategoryCode();
                                        mAlertDialogLeiBie.dismiss();
                                    }
                                }
                            } else {
                                Toast.makeText(AddPropertyActivity.this, "资产类别数据集合为null", Toast.LENGTH_SHORT).show();

                            }
                        } else {
                            Toast.makeText(AddPropertyActivity.this, "登录过期,请重新登录", Toast.LENGTH_SHORT).show();
                        }


                    } else {
                        Toast.makeText(AddPropertyActivity.this, "资产类别数据错误", Toast.LENGTH_SHORT).show();

                    }
                }catch (Exception e){
                    Toast.makeText(AddPropertyActivity.this, "数据解析错误", Toast.LENGTH_SHORT).show();
                }


            } else if (msg.what == 1010) {
                mSubmit_btn.setClickable(true);
                Toast.makeText(AddPropertyActivity.this, "连接服务器失败,请检查网络", Toast.LENGTH_SHORT).show();
            } else if (msg.what == 2) {//请求保管人接口
                try{
                    String s = (String) msg.obj;
                    Object o = gson.fromJson(s, PropertyManagerRoot.class);
                    if (o != null && o instanceof PropertyManagerRoot) {

                        PropertyManagerRoot propertyManagerRoot = (PropertyManagerRoot) o;

                        if (propertyManagerRoot != null && "0".equals(propertyManagerRoot.getCode())) {

                            if (propertyManagerRoot.getRows() != null) {

                                if (propertyManagerRoot.getRows().size() != 0) {
                                    mManagerList = propertyManagerRoot.getRows();
                                    mManagerAdapter.notifyDataSetChanged();
                                } else {
                                    Toast.makeText(AddPropertyActivity.this, "抱歉，暂无保管人", Toast.LENGTH_SHORT).show();
                                    mAlertDialogManager.dismiss();
                                }


                            } else {
                                Toast.makeText(AddPropertyActivity.this, "请求保管人数据集合为null", Toast.LENGTH_SHORT).show();

                            }

                        } else {
                            Toast.makeText(AddPropertyActivity.this, "登录过期,请重新登录", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(AddPropertyActivity.this, "保管人数据错误", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    Toast.makeText(AddPropertyActivity.this, "数据解析错误", Toast.LENGTH_SHORT).show();
                }

            } else if (msg.what == 3) {//提交资产接口
                try{
                    mSubmit_btn.setClickable(true);
                    BallProgressUtils.dismisLoading();
                    String s = (String) msg.obj;
                    Object o = gson.fromJson(s, PropertySubmitBean.class);
                    if (o != null && o instanceof PropertySubmitBean) {
                        PropertySubmitBean propertySubmitBean = (PropertySubmitBean) o;
                        if (propertySubmitBean != null && "0".equals(propertySubmitBean.getCode())) {
                            if ("0".equals(propertySubmitBean.getCode())) {
                                Toast.makeText(AddPropertyActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
                                setResult(RESULT_OK, intent);//提交成功后，资产列表页面数据需要刷新或者刷新出库入库页面数据
                                finish();
                            } else {
                                Toast.makeText(AddPropertyActivity.this, "提交资产失败", Toast.LENGTH_SHORT).show();

                            }
                        } else if (propertySubmitBean != null && "-1".equals(propertySubmitBean.getCode())) {
                            Toast.makeText(AddPropertyActivity.this, "登录过期,请重新登录", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(AddPropertyActivity.this, "提交失败", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(AddPropertyActivity.this, "提交资产数据错误", Toast.LENGTH_SHORT).show();

                    }

                }catch (Exception e){
                    Toast.makeText(AddPropertyActivity.this, "数据解析错误", Toast.LENGTH_SHORT).show();
                }


            } else if (msg.what == 4) {//请求资产存放地接口
                try{
                    String s = (String) msg.obj;
                    Object o = gson.fromJson(s, PropertyLocationRoot.class);
                    if (o != null && o instanceof PropertyLocationRoot) {
                        PropertyLocationRoot propertyLocationRoot = (PropertyLocationRoot) o;
                        if (propertyLocationRoot != null && "0".equals(propertyLocationRoot.getCode())) {

                            if (propertyLocationRoot.getRows() != null) {
                                if (propertyLocationRoot.getRows().size() != 0) {
                                    mLocationList = propertyLocationRoot.getRows();
                                    mLocationAdapter.notifyDataSetChanged();

                                } else {
                                    if (mLocationPosition != -1) {
                                        mLocation_Edit.setText(mLocationList.get(mLocationPosition).getAddressName() + "");
                                        addressCode = mLocationList.get(mLocationPosition).getAddressCode();
                                        mAlertDialogLocation.dismiss();
                                    }

                                }
                            } else {
                                Toast.makeText(AddPropertyActivity.this, "资产存放地数据集合为null", Toast.LENGTH_SHORT).show();

                            }
                        } else if (propertyLocationRoot != null && "-1".equals(propertyLocationRoot.getCode())) {
                            Toast.makeText(AddPropertyActivity.this, "登录过期,请重新登录", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(AddPropertyActivity.this, "存放地数据错误", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(AddPropertyActivity.this, "资产存放地数据错误", Toast.LENGTH_SHORT).show();

                    }
                }catch (Exception e){
                    Toast.makeText(AddPropertyActivity.this, "数据解析错误", Toast.LENGTH_SHORT).show();
                }


            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_property);
        mAll = (RelativeLayout) findViewById(R.id.activity_add_property);
        intent = getIntent();
        initUI();
    }

    private void initUI() {

        okHttpManager = OkHttpManager.getInstance();
        leibie_url = URLTools.urlBase + URLTools.property_leibie + "parentId=0";
        okHttpManager.getMethod(false, leibie_url, "资产类别接口", mHandler, 1);

        //资产类别弹框
        mBuilderLeiBie = new AlertDialog.Builder(this);
        mAlertDialogLeiBie = mBuilderLeiBie.create();
        mLeiBieView = LayoutInflater.from(this).inflate(R.layout.danwei_alert, null);
        mAlertDialogLeiBie.setView(mLeiBieView);
        mLeiBiewListview = mLeiBieView.findViewById(R.id.danwei_listview);
        mLeiBieAdapter = new LeiBieAdapter();
        mLeiBiewListview.setAdapter(mLeiBieAdapter);
        mLeiBiewListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mPosition = i;
                leibie_url = URLTools.urlBase + URLTools.property_leibie + "parentId=" + mLeiBieList.get(i).getId();
                okHttpManager.getMethod(false, leibie_url, "资产类别接口", mHandler, 1);
            }
        });


        //资产类别
        mleiBie_Tv = (TextView) findViewById(R.id.leibie);
        mLeiBie_RL_Btn = (RelativeLayout) findViewById(R.id.select_btn);
        mLeiBie_RL_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //每次弹框之前，重新设置数据
                leibie_url = URLTools.urlBase + URLTools.property_leibie + "parentId=0";
                okHttpManager.getMethod(false, leibie_url, "资产类别接口", mHandler, 1);
                mAlertDialogLeiBie.show();
            }
        });


        //资产保管人弹框
        mBuilderManager = new AlertDialog.Builder(this);
        mAlertDialogManager = mBuilderManager.create();
        mManagerView = LayoutInflater.from(this).inflate(R.layout.danwei_alert2, null);
        mAlertDialogManager.setView(mManagerView);
        mManagerListView = mManagerView.findViewById(R.id.danwei_listview2);
        mManagerAdapter = new ManagerAdapter();
        mManagerListView.setAdapter(mManagerAdapter);
        mManagerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mManager_Tv.setText(mManagerList.get(i).getTrueName() + "");
                saveUserID = mManagerList.get(i).getId();
                mAlertDialogManager.dismiss();
            }
        });
        manager_url = URLTools.urlBase + URLTools.property_manager;
        okHttpManager.getMethod(false, manager_url, "请求资产保管人", mHandler, 2);
        //保管人
        mManager_Tv = (TextView) findViewById(R.id.manager);
        mManager_RL_Btn = (RelativeLayout) findViewById(R.id.manager_btn);
        mManager_RL_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                okHttpManager.getMethod(false, manager_url, "请求资产保管人", mHandler, 2);
                mAlertDialogManager.show();
            }
        });


        //资产存放地点
        location_url = URLTools.urlBase + URLTools.property_location_list + "parentId=0";
        okHttpManager.getMethod(false, location_url, "资产存放地接口", mHandler, 4);
        mLocation_RL_Btn = (RelativeLayout) findViewById(R.id.location_select_btn);
        mLocation_RL_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //每次弹框之前，重新设置数据
                location_url = URLTools.urlBase + URLTools.property_location_list + "parentId=0";
                okHttpManager.getMethod(false, location_url, "资产存放地接口", mHandler, 4);
                mAlertDialogLocation.show();

            }
        });

        //资产存放地点弹框
        mBuilderLocation = new AlertDialog.Builder(this);
        mAlertDialogLocation = mBuilderLocation.create();
        mLocationView = LayoutInflater.from(this).inflate(R.layout.danwei_alert3, null);
        mAlertDialogLocation.setView(mLocationView);
        mLocationListView = mLocationView.findViewById(R.id.danwei_listview3);
        mLocationAdapter = new LocationAdapter();
        mLocationListView.setAdapter(mLocationAdapter);
        mLocationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mLocationPosition = i;
                location_url = URLTools.urlBase + URLTools.property_location_list + "parentId=" + mLocationList.get(i).getId();
                okHttpManager.getMethod(false, location_url, "资产存放地接口", mHandler, 4);

            }
        });


        //资产名称，
        mName_Edit = (EditText) findViewById(R.id.name_edit);
        //资产型号
        mXingHao_Edit = (EditText) findViewById(R.id.xinghao_edit);
        //存放地
        mLocation_Edit = (TextView) findViewById(R.id.location_edit);
        //资产编ma
        mBianHao_Edit = (EditText) findViewById(R.id.number_edit);
        intent = getIntent();//这个是处理从扫一扫页面跳转过来后，获取资产编码
        String barcode = intent.getStringExtra("barcode");
        caiGouDetailsRows = (CaiGouDetailsRows) intent.getSerializableExtra("bean");//从出库入库页面传过来的采购实体类，用于入库
        mBianHao_Edit.setText(barcode);

        mYear_edit = (EditText) findViewById(R.id.year_edit);
        mPrice_edit = (EditText) findViewById(R.id.price_edit);
        mQuantity_edit = (EditText) findViewById(R.id.quantity_edit);
        mUnit_edit = (EditText) findViewById(R.id.unit_edit);
        mRemark_edit = (EditText) findViewById(R.id.remark_edit);

        //单位弹框
        mUnit_Rl_btn = (RelativeLayout) findViewById(R.id.unit_select_btn);
        mUnit_Rl_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAlertDialogDanWei.show();
            }
        });
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
                mUnit_edit.setText(mDanWeiList.get(i));
                mAlertDialogDanWei.dismiss();
            }
        });

        if (caiGouDetailsRows != null) {

            mName_Edit.setText(caiGouDetailsRows.getAssetName() + "");
            if (!"".equals(caiGouDetailsRows.getSpecTyp())) {
                mXingHao_Edit.setText(caiGouDetailsRows.getSpecTyp() + "");
            } else {
                mXingHao_Edit.setText("---");
            }
            mBianHao_Edit.setText(barcode);
            if (!"".equals(caiGouDetailsRows.getBuyWorth())) {
                DecimalFormat decimalFormat = new DecimalFormat("###0.00");//格式化设置
                String s2 = decimalFormat.format(caiGouDetailsRows.getBuyWorth());
                mPrice_edit.setText(s2 + "");
            } else {
                mPrice_edit.setText("---");
            }
            mQuantity_edit.setText(caiGouDetailsRows.getBuyCount() + "");
            mUnit_edit.setText(caiGouDetailsRows.getUnit() + "");
            if (!"".equals(caiGouDetailsRows.getComment())) {
                mRemark_edit.setText(caiGouDetailsRows.getComment() + "");
            } else {
                mRemark_edit.setText("---");
            }
        } else {
            //Toast.makeText(this, "资产错误", Toast.LENGTH_LONG).show();
        }


        mBack = (ImageView) findViewById(R.id.back_img);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        submit_url = URLTools.urlBase + URLTools.property_add;
        mSubmit_btn = (TextView) findViewById(R.id.submit_btn);
        mSubmit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (check()) {
                    //判断使用年限
                    if (!"".equals(mYear_edit.getText().toString()) && mYear_edit.getText().toString().startsWith("0")) {
                        if ("0".equals(mYear_edit.getText().toString())) {
                            Toast.makeText(AddPropertyActivity.this, "使用年限不能为0", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(AddPropertyActivity.this, "请填写正确的使用年限", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        //判断价格（价格为选填）
                        if (!"".equals(mPrice_edit.getText().toString())) {

                            if (mPrice_edit.getText().toString().indexOf(".") == 0) {
                                Toast.makeText(AddPropertyActivity.this, "请填写正确的价格", Toast.LENGTH_SHORT).show();
                            } else {
                                if (mPrice_edit.getText().toString().indexOf(".") > 1 && mPrice_edit.getText().toString().startsWith("0")) {
                                    Toast.makeText(AddPropertyActivity.this, "请填写正确的价格", Toast.LENGTH_SHORT).show();
                                } else {
                                    if (mPrice_edit.getText().toString().indexOf(".") == -1 && mPrice_edit.getText().toString().startsWith("0")) {
                                        Toast.makeText(AddPropertyActivity.this, "请填写正确的价格", Toast.LENGTH_SHORT).show();

                                    } else {
                                        if (!"".equals(mQuantity_edit.getText().toString().trim()) && mQuantity_edit.getText().toString().startsWith("0")) {
                                            Toast.makeText(AddPropertyActivity.this, "请填写正确的数量", Toast.LENGTH_SHORT).show();
                                        } else {
                                            mSubmit_btn.setClickable(false);
                                            BallProgressUtils.showLoading(AddPropertyActivity.this, mAll);
                                            Map<Object, Object> map = new HashMap<Object, Object>();
                                            map.put("categoryCode", categoryCode);
                                            map.put("saveUserId", saveUserID);
                                            map.put("assetName", mName_Edit.getText().toString().trim());
                                            map.put("specTyp", mXingHao_Edit.getText().toString().trim());
                                            map.put("addressCode", addressCode);
                                            map.put("barcode", mBianHao_Edit.getText().toString().trim());
                                            map.put("useTimes", mYear_edit.getText().toString().trim());
                                            map.put("worth", mPrice_edit.getText().toString().trim());
                                            map.put("num", mQuantity_edit.getText().toString().trim());
                                            map.put("unit", mUnit_edit.getText().toString().trim());
                                            map.put("comment", mRemark_edit.getText().toString().trim());
                                            if (caiGouDetailsRows != null) {
                                                map.put("buyNo", caiGouDetailsRows.getId());
                                            }

                                            okHttpManager.postMethod(false, submit_url, "提交添加资产接口", map, mHandler, 3);
                                        }

                                    }


                                }
                            }

                        } else {//价格为""的时候判断数量
                            if (!"".equals(mQuantity_edit.getText().toString().trim()) && mQuantity_edit.getText().toString().startsWith("0")) {
                                Toast.makeText(AddPropertyActivity.this, "请填写正确的数量", Toast.LENGTH_SHORT).show();
                            } else {
                                mSubmit_btn.setClickable(false);
                                BallProgressUtils.showLoading(AddPropertyActivity.this, mAll);
                                Map<Object, Object> map = new HashMap<Object, Object>();
                                map.put("categoryCode", categoryCode);
                                map.put("saveUserId", saveUserID);
                                map.put("assetName", mName_Edit.getText().toString().trim());
                                map.put("specTyp", mXingHao_Edit.getText().toString().trim());
                                map.put("addressCode", addressCode);
                                map.put("barcode", mBianHao_Edit.getText().toString().trim());
                                map.put("useTimes", mYear_edit.getText().toString().trim());
                                map.put("worth", mPrice_edit.getText().toString().trim());
                                map.put("num", mQuantity_edit.getText().toString().trim());
                                map.put("unit", mUnit_edit.getText().toString().trim());
                                map.put("comment", mRemark_edit.getText().toString().trim());
                                if (caiGouDetailsRows != null) {
                                    map.put("buyNo", caiGouDetailsRows.getId());
                                }
                                okHttpManager.postMethod(false, submit_url, "提交添加资产接口", map, mHandler, 3);
                            }
                        }
                    }
                }
            }
        });


    }

    private boolean check() {

        if (!"".equals(mleiBie_Tv.getText().toString().trim())) {

            if (!"".equals(mName_Edit.getText().toString().trim())) {

                if (!"".equals(mManager_Tv.getText().toString().trim())) {

                    if (!"".equals(mLocation_Edit.getText().toString().trim())) {

                        if (!"".equals(mQuantity_edit.getText().toString().trim())) {

                            if (!"".equals(mUnit_edit.getText().toString().trim())) {

                                return true;
                            } else {
                                Toast.makeText(this, "请填写计量单位", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "请填写资产数量", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(this, "请填写资产所在位置", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(this, "请选择保管人", Toast.LENGTH_SHORT).show();
                }


            } else {
                Toast.makeText(this, "请填写资产名称", Toast.LENGTH_SHORT).show();
            }


        } else {
            Toast.makeText(this, "请选择资产类别", Toast.LENGTH_SHORT).show();
        }

        return false;
    }

    //资产类别adapter
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
                leiBieHolder = new LeiBieHolder();
                view = LayoutInflater.from(AddPropertyActivity.this).inflate(R.layout.danwei_alert_item, null);
                leiBieHolder.tv = view.findViewById(R.id.danwei_item_tv);
                view.setTag(leiBieHolder);
            } else {
                leiBieHolder = (LeiBieHolder) view.getTag();
            }
            leiBieHolder.tv.setText(mLeiBieList.get(i).getCategoryName() + "");

            return view;
        }

        class LeiBieHolder {
            TextView tv;
        }
    }

    //保管人adapter
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
                managerHolder = new ManagerHolder();
                view = LayoutInflater.from(AddPropertyActivity.this).inflate(R.layout.danwei_alert_item, null);
                managerHolder.tv = view.findViewById(R.id.danwei_item_tv);
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

    //存放地adapter
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
            LocationHolder locationHolder = null;
            if (view == null) {
                locationHolder = new LocationHolder();
                view = LayoutInflater.from(AddPropertyActivity.this).inflate(R.layout.danwei_alert_item, null);
                locationHolder.tv = view.findViewById(R.id.danwei_item_tv);
                view.setTag(locationHolder);
            } else {
                locationHolder = (LocationHolder) view.getTag();
            }

            locationHolder.tv.setText(mLocationList.get(i).getAddressName());
            return view;
        }

        class LocationHolder {
            TextView tv;
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
                view = LayoutInflater.from(AddPropertyActivity.this).inflate(R.layout.danwei_alert_item, null);
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
}
