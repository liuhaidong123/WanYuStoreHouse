package com.storehouse.wanyu.activity.PanDian;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.DialogInterface;
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
import com.scwang.smartrefresh.header.CircleHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.storehouse.wanyu.Bean.AssetQrCode;
import com.storehouse.wanyu.Bean.ErwermaRoot;
import com.storehouse.wanyu.Bean.PanDianErWeiMaInventory;
import com.storehouse.wanyu.Bean.PanDianErWeiMaRoot;
import com.storehouse.wanyu.Bean.PanDianLocationRoot;
import com.storehouse.wanyu.Bean.PanDianLocationRows;
import com.storehouse.wanyu.Bean.PanDianMesInventory;
import com.storehouse.wanyu.Bean.PanDianMesRoot;
import com.storehouse.wanyu.Bean.PanDianMesRows;
import com.storehouse.wanyu.Bean.PanDianStatusRoot;
import com.storehouse.wanyu.Bean.PanDianStatusRows;
import com.storehouse.wanyu.Bean.PanDianSubmitBean;
import com.storehouse.wanyu.IPAddress.URLTools;
import com.storehouse.wanyu.MyUtils.BallProgressUtils;
import com.storehouse.wanyu.MyUtils.ToastUtils;
import com.storehouse.wanyu.OkHttpUtils.OkHttpManager;
import com.storehouse.wanyu.R;
import com.storehouse.wanyu.activity.PropertyManage.PropertyMessageActivity;
import com.xys.libzxing.activity.CaptureActivity;

import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


//盘点详情
public class PDMessageActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView mBack;
    private TextView mSubmit_btn, mSaomiao_btn, mTitle_Tv, mMes_tv;
    private ListView mListview;
    private MyAdapter myAdapter;
    private List<PanDianMesRows> mList = new ArrayList<>();
    private long submitID;//每一个盘点提交id
    private OkHttpManager okHttpManager;
    private String mes_Url, mSaoYiSao_Url, submit_pandan_url, jiean_url;
    private long mID;//请求详情id
    private Gson gson = new Gson();
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            BallProgressUtils.dismisLoading();
            if (msg.what == 2) {
                String mes = (String) msg.obj;
                try {
                    Object o = gson.fromJson(mes, PanDianMesRoot.class);
                    if (o != null && o instanceof PanDianMesRoot) {
                        PanDianMesRoot panDianMesRoot = (PanDianMesRoot) o;
                        if (panDianMesRoot != null && !"".equals(panDianMesRoot.getCode())) {
                            PanDianMesInventory panDianMesInventory = panDianMesRoot.getInventory();
                            if (panDianMesInventory != null) {
                                mNodata_rl.setVisibility(View.GONE);
                                no_mess_tv.setText("");
                                mTitle_Tv.setText(panDianMesInventory.getSubject() + "");
                                mMes_tv.setText(panDianMesInventory.getDescription() + "");
                                if (panDianMesInventory.getClosedMaker() != null && panDianMesInventory.getClosedMaker() != 0) {
                                    mSubmit_btn.setText("已结案");
                                    mSubmit_btn.setClickable(false);
                                    mSaomiao_btn.setVisibility(View.GONE);

                                } else {
                                    mSubmit_btn.setText("结案");
                                    mSubmit_btn.setClickable(true);
                                    mSaomiao_btn.setVisibility(View.VISIBLE);
                                    mSaomiao_btn.setText("扫码盘点");

                                }
                                if (panDianMesInventory.getAssetList() != null) {
                                    mList = panDianMesInventory.getAssetList();
                                    myAdapter.notifyDataSetChanged();
                                }

                            }
                        }


                    } else {
                        Toast.makeText(PDMessageActivity.this, "获取盘点详情失败", Toast.LENGTH_SHORT).show();
                        mNodata_rl.setVisibility(View.VISIBLE);
                        no_mess_tv.setText("网络异常，请检查网络");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ToastUtils.show(PDMessageActivity.this, "数据解析错误，联系后台");
                    mNodata_rl.setVisibility(View.VISIBLE);
                    no_mess_tv.setText("数据解析错误，联系后台");
                }


            } else if (msg.what == 1010) {
                mNodata_rl.setVisibility(View.VISIBLE);
                no_mess_tv.setText("网络异常，请检查网络");
                BallProgressUtils.dismisLoading();
                Toast.makeText(PDMessageActivity.this, "网络异常，请检查网络", Toast.LENGTH_SHORT).show();
            } else if (msg.what == 11) {//盘点二维码
                String mes = (String) msg.obj;
                try{
                    Object o = gson.fromJson(mes, PanDianErWeiMaRoot.class);
                    Log.e("扫一扫接口信息=", mes);
                    if (o != null && o instanceof PanDianErWeiMaRoot) {
                        PanDianErWeiMaRoot panDianErWeiMaRoot = (PanDianErWeiMaRoot) o;
                        if ("0".equals(panDianErWeiMaRoot.getCode())) {
                            PanDianErWeiMaInventory panDianErWeiMaInventory = panDianErWeiMaRoot.getInventoryItem();
                            if (panDianErWeiMaInventory != null) {
                                submitID = panDianErWeiMaInventory.getId();
                                //弹框
                                //待盘数量大于1，并且待盘数量大于已盘数量，需要弹框
                                if (panDianErWeiMaInventory.getNum() > 1) {
                                    alertDialog.show();
                                    barCode.setText(panDianErWeiMaInventory.getBarcode());
                                    name.setText(panDianErWeiMaInventory.getAssetsName());
                                    manager.setText(panDianErWeiMaInventory.getSaveUserName());
                                    location.setText(panDianErWeiMaInventory.getOrgAddressName());
                                    num.setText(panDianErWeiMaInventory.getNum() + "");


                                } else {
                                    Map<Object, Object> map = new HashMap<>();
                                    map.put("id", submitID);
                                    map.put("inventoryNum", panDianErWeiMaInventory.getNum());
                                    okHttpManager.postMethod(false, submit_pandan_url, "提交扫码盘点接口", map, handler, 40);


                                }


                            } else {
                                Toast.makeText(PDMessageActivity.this, "抱歉，盘点中无此物品", Toast.LENGTH_SHORT).show();
                            }


                        } else if ("-1".equals(panDianErWeiMaRoot.getCode())) {
                            Toast.makeText(PDMessageActivity.this, "账号过期，请重新登录", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(PDMessageActivity.this, "二维码扫数据错误", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(PDMessageActivity.this, "此二维码无效", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    ToastUtils.show(PDMessageActivity.this, "数据解析错误，联系后台");

                }


            } else if (msg.what == 40) {//提交扫码盘点接口
                BallProgressUtils.dismisLoading();
                String mes = (String) msg.obj;
                try{
                    Object o = gson.fromJson(mes, PanDianSubmitBean.class);
                    if (o != null && o instanceof PanDianSubmitBean) {
                        PanDianSubmitBean panDianSubmitBean = (PanDianSubmitBean) o;
                        if (panDianSubmitBean != null && "0".equals(panDianSubmitBean.getCode())) {
                            Toast.makeText(PDMessageActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
                            okHttpManager.getMethod(false, mes_Url, "盘点详情", handler, 2);//重新刷新物品信息
                        } else {
                            Toast.makeText(PDMessageActivity.this, "提交失败", Toast.LENGTH_SHORT).show();

                        }

                    }
                }catch (Exception e){
                    e.printStackTrace();
                    ToastUtils.show(PDMessageActivity.this, "数据解析错误，联系后台");
                }

            } else if (msg.what == 50) {//提交结案
                BallProgressUtils.dismisLoading();
                String mes = (String) msg.obj;
                try{
                    Object o = gson.fromJson(mes, PanDianSubmitBean.class);
                    if (o != null && o instanceof PanDianSubmitBean) {
                        PanDianSubmitBean panDianSubmitBean = (PanDianSubmitBean) o;
                        if (panDianSubmitBean != null && "0".equals(panDianSubmitBean.getCode())) {
                            Toast.makeText(PDMessageActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK, mIntent);
                            finish();
                        } else {
                            Toast.makeText(PDMessageActivity.this, "提交失败", Toast.LENGTH_SHORT).show();

                        }

                    }
                }catch (Exception e){
                    e.printStackTrace();
                    ToastUtils.show(PDMessageActivity.this, "数据解析错误，联系后台");
                }

            }
        }
    };
    private RelativeLayout mAll, mNodata_rl;
    private Intent mIntent;
    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;
    private View alertview;
    private TextView barCode, name, manager, location, num, sure_btn, no_mess_tv;
    private EditText edit_num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdmessage);
        mAll = (RelativeLayout) findViewById(R.id.activity_pdmessage);
        initUI();
    }

    private void initUI() {

        mIntent = getIntent();
        mID = mIntent.getLongExtra("id", -1);
        okHttpManager = OkHttpManager.getInstance();
        if (mID != -1) {
            mes_Url = URLTools.urlBase + URLTools.pandian_mes_url + "id=" + mID;//请求详情
            okHttpManager.getMethod(false, mes_Url, "盘点详情", handler, 2);
        } else {
            Toast.makeText(this, "无法获取盘点详情", Toast.LENGTH_SHORT).show();
        }

        mNodata_rl = (RelativeLayout) findViewById(R.id.nodata_rl);
        no_mess_tv = (TextView) findViewById(R.id.no_mess);
        mNodata_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mID != -1) {
                    BallProgressUtils.showLoading(PDMessageActivity.this, mNodata_rl);
                    mes_Url = URLTools.urlBase + URLTools.pandian_mes_url + "id=" + mID;//请求详情
                    okHttpManager.getMethod(false, mes_Url, "盘点详情", handler, 2);
                } else {
                    Toast.makeText(PDMessageActivity.this, "无法获取盘点详情", Toast.LENGTH_SHORT).show();
                }
            }
        });
        submit_pandan_url = URLTools.urlBase + URLTools.pandian_sure_url;//提交盘点接口
        //返回
        mBack = (ImageView) findViewById(R.id.back_img);
        mBack.setOnClickListener(this);
        //结案
        mSubmit_btn = (TextView) findViewById(R.id.submit);
        mSubmit_btn.setOnClickListener(this);
        //扫描
        mSaomiao_btn = (TextView) findViewById(R.id.pd_saoma);
        mSaomiao_btn.setOnClickListener(this);
        mTitle_Tv = (TextView) findViewById(R.id.title_tv);
        mMes_tv = (TextView) findViewById(R.id.explain_tv);
        //盘点列表
        mListview = (ListView) findViewById(R.id.pd_mes_listview);
        myAdapter = new MyAdapter();
        mListview.addHeaderView(LayoutInflater.from(this).inflate(R.layout.new_pandian_header, null));
        mListview.setAdapter(myAdapter);
        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    //跳转资产详情
                    Intent intent = new Intent(PDMessageActivity.this, PropertyMessageActivity.class);
                    intent.putExtra("assetID", mList.get(i - 1).getAssetsId());
                    startActivity(intent);

                }
            }
        });

        builder = new AlertDialog.Builder(this);
        alertDialog = builder.create();
        alertview = LayoutInflater.from(PDMessageActivity.this).inflate(R.layout.pandian_alert, null);
        alertDialog.setView(alertview);
        barCode = alertview.findViewById(R.id.bianhao);
        name = alertview.findViewById(R.id.mingcheng);
        manager = alertview.findViewById(R.id.manager);
        location = alertview.findViewById(R.id.location);
        num = alertview.findViewById(R.id.daipan_mes);
        sure_btn = alertview.findViewById(R.id.sure_btn);
        edit_num = alertview.findViewById(R.id.shipan_mes);
        sure_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String num = edit_num.getText().toString().trim();
                if (!"".equals(num)) {
                    int i = Integer.valueOf(num);
                    if (i > 0) {
                        alertDialog.dismiss();
                        BallProgressUtils.showLoading(PDMessageActivity.this, mAll);
                        Map<Object, Object> map = new HashMap<>();
                        map.put("id", submitID);
                        map.put("inventoryNum", i);
                        okHttpManager.postMethod(false, submit_pandan_url, "提交扫码盘点接口", map, handler, 40);
                        edit_num.setText("");
                    } else {
                        Toast.makeText(PDMessageActivity.this, "实盘数量不能为0", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(PDMessageActivity.this, "请填写实盘数量", Toast.LENGTH_SHORT).show();

                }

            }
        });

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == mBack.getId()) {
            finish();
            //结案
        } else if (id == mSubmit_btn.getId()) {
            BallProgressUtils.showLoading(this, mAll);
            jiean_url = URLTools.urlBase + URLTools.pandian_jiean_url + "id=" + mID + "&isClosed=isClosed";
            okHttpManager.getMethod(false, jiean_url, "提交结案", handler, 50);
            //二维码扫描盘点
        } else if (id == mSaomiao_btn.getId()) {
            checkCameraPermission();
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
                //requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_CODE);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                String scanResult = bundle.getString("result");
                mSaoYiSao_Url = URLTools.urlBase + URLTools.erweima_pandian_url + "barcode=" + scanResult + "&inventoryId=" + mID;
                okHttpManager.getMethod(false, mSaoYiSao_Url, "扫一扫接口", handler, 11);
                Log.e("scanResult=", scanResult);
            } else {
                Toast.makeText(this, "扫描信息错误", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //资产列表adapter
    class MyAdapter extends BaseAdapter {

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
            MyHolder myHolder = null;
            myHolder = new MyHolder();
            view = LayoutInflater.from(PDMessageActivity.this).inflate(R.layout.new_pandian_item, null);
            myHolder.daipannum = view.findViewById(R.id.daipan_tv);
            myHolder.yipannum = view.findViewById(R.id.yipan_tv);
            myHolder.name = view.findViewById(R.id.name_tv);
            myHolder.manager = view.findViewById(R.id.manager_tv);
            myHolder.location = view.findViewById(R.id.location_tv);
            view.setTag(myHolder);

            myHolder.daipannum.setText(mList.get(i).getNum() + "");
            myHolder.yipannum.setText(mList.get(i).getInventoryNum() + "");
            myHolder.name.setText(mList.get(i).getAssetsName() + "");
            myHolder.manager.setText(mList.get(i).getSaveUserName() + "");
            myHolder.location.setText(mList.get(i).getOrgAddressName() + "");
            //待盘数量等于已盘数量时，变为绿色
            if (mList.get(i).getNum() == mList.get(i).getInventoryNum()) {
                myHolder.daipannum.setBackgroundResource(R.color.color_23b880);
                myHolder.yipannum.setBackgroundResource(R.color.color_23b880);
                myHolder.name.setBackgroundResource(R.color.color_23b880);
                myHolder.manager.setBackgroundResource(R.color.color_23b880);
                myHolder.location.setBackgroundResource(R.color.color_23b880);
                //待盘数量大于已盘数量时
            } else if (mList.get(i).getNum() > mList.get(i).getInventoryNum()) {
                //如果已盘数量为0,那就是没有盘点，标记为红色
                if (mList.get(i).getInventoryNum() == 0) {
                    myHolder.daipannum.setBackgroundResource(R.color.red);
                    myHolder.yipannum.setBackgroundResource(R.color.red);
                    myHolder.name.setBackgroundResource(R.color.red);
                    myHolder.manager.setBackgroundResource(R.color.red);
                    myHolder.location.setBackgroundResource(R.color.red);
                    //如果已盘数量不为0,并且已盘数量小于待盘数量，那就说明没有盘点完毕，标记为黄色
                } else {
                    myHolder.daipannum.setBackgroundResource(R.color.color_dc8268);
                    myHolder.yipannum.setBackgroundResource(R.color.color_dc8268);
                    myHolder.name.setBackgroundResource(R.color.color_dc8268);
                    myHolder.manager.setBackgroundResource(R.color.color_dc8268);
                    myHolder.location.setBackgroundResource(R.color.color_dc8268);
                }

            } else {
                myHolder.daipannum.setBackgroundResource(R.color.color_23b880);
                myHolder.yipannum.setBackgroundResource(R.color.color_23b880);
                myHolder.name.setBackgroundResource(R.color.color_23b880);
                myHolder.manager.setBackgroundResource(R.color.color_23b880);
                myHolder.location.setBackgroundResource(R.color.color_23b880);
            }
            return view;
        }

        class MyHolder {
            TextView daipannum, yipannum, name, manager, location;
        }
    }
}
