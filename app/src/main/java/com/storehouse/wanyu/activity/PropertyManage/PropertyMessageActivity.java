package com.storehouse.wanyu.activity.PropertyManage;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Property;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.storehouse.wanyu.Bean.PropertyMessage;
import com.storehouse.wanyu.Bean.PropertyMessageRoot;
import com.storehouse.wanyu.IPAddress.URLTools;
import com.storehouse.wanyu.MyUtils.GetAlertApplyListUtils;
import com.storehouse.wanyu.MyUtils.SharedPrefrenceTools;
import com.storehouse.wanyu.OkHttpUtils.OkHttpManager;
import com.storehouse.wanyu.R;
import com.storehouse.wanyu.activity.ApplyActivity.ApplyBaoFeiActivity;
import com.storehouse.wanyu.activity.ApplyActivity.ApplyCaiGouActivity;
import com.storehouse.wanyu.activity.ApplyActivity.ApplyJieYongActivity;
import com.storehouse.wanyu.activity.ApplyActivity.ApplyLingYongActivity;
import com.storehouse.wanyu.activity.ApplyActivity.ApplyTuiKuActivity;
import com.storehouse.wanyu.activity.ApplyActivity.ApplyWeiXiuActivity;

import java.util.ArrayList;
import java.util.List;

//资产详情页面
public class PropertyMessageActivity extends AppCompatActivity {
    SharedPrefrenceTools sharedPrefrenceTools;
    private Intent intent;
    private long saveUserID = -1;
    private long loginUserID = -1;
    private AlertDialog.Builder mBuilder;
    private AlertDialog mAlertDialog;
    private List<String> mAlertList = new ArrayList<>();//弹框list
    private ListView mAlertDialogListView;
    private AlertAdapter alertAdapter;
    private ImageView mBack;
    private TextView mleiBie_Tv, mManager_Tv, mName_tv, mXingHao_tv, mLocation_tv, mBianHao_tv;//资产类别，保管人,资产名称，型号，所在位置，资产编号
    private TextView mYear_tv, mPrice_tv, mQuantity_tv, mUnit_Tv, mRemark_tv, mMana_Btn;//管理
    private Gson gson = new Gson();
    private PropertyMessage propertyMessage;
    private OkHttpManager okHttpManager;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {

                String s = (String) msg.obj;
                Object o = gson.fromJson(s, PropertyMessageRoot.class);

                if (o != null && o instanceof PropertyMessageRoot) {
                    PropertyMessageRoot propertyMessageRoot = (PropertyMessageRoot) o;
                    if (propertyMessageRoot != null && "0".equals(propertyMessageRoot.getCode())) {
                        try {
                            propertyMessage = propertyMessageRoot.getMessage();
                            if (propertyMessage != null) {
                                mleiBie_Tv.setText(propertyMessage.getCategoryName() + "");
                                mManager_Tv.setText(propertyMessage.getSaveUserName() + "");
                                mName_tv.setText(propertyMessage.getAssetName() + "");
                                mXingHao_tv.setText(propertyMessage.getSpecTyp() + "");
                                mLocation_tv.setText(propertyMessage.getAddressName() + "");
                                mBianHao_tv.setText(propertyMessage.getBarcode() + "");
                                mYear_tv.setText(propertyMessage.getUseTimes() + "");
                                mPrice_tv.setText(propertyMessage.getWorth() + "");
                                mQuantity_tv.setText(propertyMessage.getNum() + "");
                                mUnit_Tv.setText(propertyMessage.getUnit() + "");
                                mRemark_tv.setText(propertyMessage.getComment() + "");
                                saveUserID = propertyMessage.getSaveUserId();//保管人的id,用于查询是否属于当前扫描人的资产
                                Log.e("saveUserID=",saveUserID+"");
                            }

                        } catch (Exception e) {
                            Toast.makeText(PropertyMessageActivity.this, "登录过期,请重新登录", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();

                        }

                    } else {
                        Toast.makeText(PropertyMessageActivity.this, "登录过期,请重新登录", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(PropertyMessageActivity.this, "登录过期,请重新登录", Toast.LENGTH_SHORT).show();

                }

            } else {
                Toast.makeText(PropertyMessageActivity.this, "连接服务器失败，请重新尝试", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_message);
        mBack = (ImageView) findViewById(R.id.back_img);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        sharedPrefrenceTools = SharedPrefrenceTools.getSharedPrefrenceToolsInstance(this);
        loginUserID = (long) SharedPrefrenceTools.getValueByKey("userID", -2L);
        Log.e("loginUserID=",loginUserID+"");
        mBuilder = new AlertDialog.Builder(this);
        mAlertDialog = mBuilder.create();
        View v = LayoutInflater.from(this).inflate(R.layout.bumen_alert, null);
        mAlertDialog.setView(v);
        mAlertDialogListView = v.findViewById(R.id.bumen_listview);
        alertAdapter = new AlertAdapter();
        mAlertDialogListView.setAdapter(alertAdapter);
        mAlertDialogListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                switch (i) {
                    case 0://采购
                        Intent intent=new Intent(PropertyMessageActivity.this, ApplyCaiGouActivity.class);
                       // intent.putExtra("bean",propertyMessage);
                        startActivity(intent);
                        break;
                    case 1://领用
                        Intent intent1=new Intent(PropertyMessageActivity.this, ApplyLingYongActivity.class);
                        //intent1.putExtra("bean",propertyMessage);
                        startActivity(intent1);
                        break;
                    case 2://借用
                        Intent intent2=new Intent(PropertyMessageActivity.this, ApplyJieYongActivity.class);
                       // intent2.putExtra("bean",propertyMessage);
                        startActivity(intent2);
                        break;
                    case 3://维修
                        Intent intent3=new Intent(PropertyMessageActivity.this, ApplyWeiXiuActivity.class);
                       // intent3.putExtra("bean",propertyMessage);
                        startActivity(intent3);
                        break;
                    case 4://以旧换新
                        Intent intent4=new Intent(PropertyMessageActivity.this, ApplyWeiXiuActivity.class);
                        //intent4.putExtra("bean",propertyMessage);
                        startActivity(intent4);
                        break;
                    case 5://报废
                        Intent intent5=new Intent(PropertyMessageActivity.this, ApplyBaoFeiActivity.class);
                        //intent5.putExtra("bean",propertyMessage);
                        startActivity(intent5);
                        break;
                    case 6://退库
                        Intent intent6=new Intent(PropertyMessageActivity.this, ApplyTuiKuActivity.class);
                       // intent6.putExtra("bean",propertyMessage);
                        startActivity(intent6);
                        break;

                }


                mAlertDialog.dismiss();


            }
        });
        intent = getIntent();
        long id = intent.getLongExtra("assetID", -1);
        Log.e("资产id=", id + "");
        String url = URLTools.urlBase + URLTools.property_message + "id=" + id;
        okHttpManager = OkHttpManager.getInstance();
        okHttpManager.getMethod(false, url, "资产详情接口", handler, 1);

        mleiBie_Tv = (TextView) findViewById(R.id.leibie);
        mName_tv = (TextView) findViewById(R.id.name_mes);
        mManager_Tv = (TextView) findViewById(R.id.manager_mes);
        mXingHao_tv = (TextView) findViewById(R.id.xinghao_mes);
        mLocation_tv = (TextView) findViewById(R.id.location_mes);
        mBianHao_tv = (TextView) findViewById(R.id.number_mes);
        mYear_tv = (TextView) findViewById(R.id.year_mes);
        mPrice_tv = (TextView) findViewById(R.id.price_mes);
        mQuantity_tv = (TextView) findViewById(R.id.quantity_mes);
        mUnit_Tv = (TextView) findViewById(R.id.unit_mes);
        mRemark_tv = (TextView) findViewById(R.id.remark_mes);
        mMana_Btn = (TextView) findViewById(R.id.my_mana_btn);
        mMana_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (saveUserID != -1) {
                    if (loginUserID != -2) {

                        if (saveUserID==loginUserID){
                            mAlertList=GetAlertApplyListUtils.getAlertList();
                            alertAdapter.notifyDataSetChanged();
                        }else {
                            mAlertList.add("采购申请");
                            mAlertList.add("领用申请");
                            mAlertList.add("借用申请");
                            alertAdapter.notifyDataSetChanged();
                        }

                    } else {
                        Toast.makeText(PropertyMessageActivity.this, "登录过期，请请重新登录", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(PropertyMessageActivity.this, "资产详情错误,请检查网络", Toast.LENGTH_SHORT).show();
                }

                mAlertDialog.show();
            }
        });
    }

    class AlertAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mAlertList.size() == 0 ? 0 : mAlertList.size();
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
            AlertHolder alertHolder = null;
            if (view == null) {
                view = LayoutInflater.from(PropertyMessageActivity.this).inflate(R.layout.sq_alert_item, null);
                alertHolder = new AlertHolder();
                alertHolder.sqName = view.findViewById(R.id.alert_item_tv);
                view.setTag(alertHolder);
            } else {
                alertHolder = (AlertHolder) view.getTag();
            }
            //设置数据
            alertHolder.sqName.setText(mAlertList.get(i));
            return view;
        }

        class AlertHolder {
            TextView sqName;//申请名称
        }
    }

}