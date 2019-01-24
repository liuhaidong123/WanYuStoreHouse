package com.storehouse.wanyu.activity.Ku;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.storehouse.wanyu.Bean.CaiGouApplySubmit;
import com.storehouse.wanyu.Bean.NewOldDetailsRoot;
import com.storehouse.wanyu.Bean.NewOldDetailsRows;
import com.storehouse.wanyu.Bean.QueryNewOldGoodsRoot;
import com.storehouse.wanyu.IPAddress.URLTools;
import com.storehouse.wanyu.MyUtils.BallProgressUtils;
import com.storehouse.wanyu.OkHttpUtils.OkHttpManager;
import com.storehouse.wanyu.R;
import com.storehouse.wanyu.activity.AllSPZDetailsActivity.SPZNewOldApplyDetailsActivity;
import com.xys.libzxing.activity.CaptureActivity;

import java.util.HashMap;
import java.util.Map;

public class KuNewOldMessActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView mBack_img;
    private TextView mBumen_Tv, mPerson_Tv, mName_Tv, mTime_tv, mBeizhuMsg_Tv, mAgree_Btn, newold_status_Tv;
    private TextView mNum_edit, oldBarcode_tv, new_name_tv, new_num_tv, new_barcode_tv;
    private ImageView mSaoImg_btn;//扫一扫按钮
    private int num;
    private long assetId;//新资产的id
    private RelativeLayout mAll_rl;
    private OkHttpManager okHttpManager;
    private Gson gson = new Gson();
    private String NO_Url, submit_agree_url, query_new_old;//借用详情url
    private Intent intent;
    private long mReferId;//请求详情需要的id
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 15) {
                try{
                    String mes = (String) msg.obj;
                    Object o = gson.fromJson(mes, NewOldDetailsRoot.class);
                    if (o != null && o instanceof NewOldDetailsRoot) {
                        NewOldDetailsRoot newOldDetailsRoot = (NewOldDetailsRoot) o;
                        if (newOldDetailsRoot != null && "0".equals(newOldDetailsRoot.getCode())) {
                            NewOldDetailsRows weiIXiuDetailsRows = newOldDetailsRoot.getAssetOldfornew();
                            if (weiIXiuDetailsRows != null) {
                                mNoData_rl.setVisibility(View.GONE);
                                no_mess_tv.setText("");
                                mBumen_Tv.setText(weiIXiuDetailsRows.getDepartmentName() + "");
                                mPerson_Tv.setText(weiIXiuDetailsRows.getUserName() + "");
                                mName_Tv.setText(weiIXiuDetailsRows.getAssetName() + "");
                                oldBarcode_tv.setText(weiIXiuDetailsRows.getBarcode());
                                mTime_tv.setText(weiIXiuDetailsRows.getChangeDateString() + "");
                                mNum_edit.setText(weiIXiuDetailsRows.getTotalNum() + "");
                                if ("".equals(weiIXiuDetailsRows.getComment())) {
                                    mBeizhuMsg_Tv.setText("---");
                                } else {
                                    mBeizhuMsg_Tv.setText(weiIXiuDetailsRows.getComment() + "");
                                }

                                if ("".equals(weiIXiuDetailsRows.getOutboundDateString())) {
                                    mSaoImg_btn.setVisibility(View.VISIBLE);
                                    mAgree_Btn.setVisibility(View.VISIBLE);
                                    mNum_edit.setEnabled(true);
                                    newold_status_Tv.setText("未换新");
                                    num = weiIXiuDetailsRows.getTotalNum();
                                    newold_status_Tv.setTextColor(ContextCompat.getColor(KuNewOldMessActivity.this, R.color.color_dc8268));
                                } else {
                                    mSaoImg_btn.setVisibility(View.GONE);
                                    mAgree_Btn.setVisibility(View.GONE);
                                    newold_status_Tv.setText("已换新");
                                    newold_status_Tv.setTextColor(ContextCompat.getColor(KuNewOldMessActivity.this, R.color.color_23b880));
                                    new_name_tv.setText(weiIXiuDetailsRows.getNewAssetName() + "");
                                    new_num_tv.setEnabled(false);
                                    new_num_tv.setText(weiIXiuDetailsRows.getNum() + "");
                                    new_barcode_tv.setText(weiIXiuDetailsRows.getNewBarcode() + "");
                                }

                            }

                        } else {
                            newold_status_Tv.setText("换新详情");
                            Toast.makeText(KuNewOldMessActivity.this, "登录过期,请重新登录", Toast.LENGTH_SHORT).show();
                            mNoData_rl.setVisibility(View.VISIBLE);
                            no_mess_tv.setText("登录过期,请重新登录");
                        }
                    } else {
                        Toast.makeText(KuNewOldMessActivity.this, "获取详情失败", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    Toast.makeText(KuNewOldMessActivity.this, "数据解析错误,请重新尝试", Toast.LENGTH_SHORT).show();
                    mNoData_rl.setVisibility(View.VISIBLE);
                    no_mess_tv.setText("数据解析错误,请重新尝试");
                }

            } else if (msg.what == 1010) {
                newold_status_Tv.setText("换新详情");
                mAgree_Btn.setClickable(true);
                BallProgressUtils.dismisLoading();
                mNoData_rl.setVisibility(View.VISIBLE);
                no_mess_tv.setText("连接服务器失败，请检查网络");
                Toast.makeText(KuNewOldMessActivity.this, "连接服务器失败,请检查网络", Toast.LENGTH_SHORT).show();
            } else if (msg.what == 1) {//提交以旧换新接口
                try {
                    mAgree_Btn.setClickable(true);
                    BallProgressUtils.dismisLoading();
                    String mes = (String) msg.obj;
                    Log.e("提交以旧换新=", mes);
                    Object o = gson.fromJson(mes, CaiGouApplySubmit.class);
                    if (o != null && o instanceof CaiGouApplySubmit) {
                        CaiGouApplySubmit caiGouApplySubmit = (CaiGouApplySubmit) o;
                        if ("0".equals(caiGouApplySubmit.getCode())) {
                            Toast.makeText(KuNewOldMessActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK, intent);
                            finish();
                        } else if ("-1".equals(caiGouApplySubmit.getCode())) {
                            Toast.makeText(KuNewOldMessActivity.this, "您的账号已过期，请重新登录", Toast.LENGTH_SHORT).show();
                            mNoData_rl.setVisibility(View.VISIBLE);
                            no_mess_tv.setText("您的账号已过期，请重新登录");
                        } else {
                            Toast.makeText(KuNewOldMessActivity.this, "提交失败", Toast.LENGTH_SHORT).show();
                        }
                    }

                } catch (Exception e) {
                    Toast.makeText(KuNewOldMessActivity.this, "数据解析错误,请重新尝试", Toast.LENGTH_SHORT).show();

                }

            } else if (msg.what == 20) {//通过扫一扫获取换新资产实体类
                try {
                    String mes = (String) msg.obj;
                    Object o = gson.fromJson(mes, QueryNewOldGoodsRoot.class);
                    if (o != null && o instanceof QueryNewOldGoodsRoot) {
                        QueryNewOldGoodsRoot queryNewOldGoodsRoot = (QueryNewOldGoodsRoot) o;
                        if (queryNewOldGoodsRoot != null && "0".equals(queryNewOldGoodsRoot.getCode())) {

                            if (queryNewOldGoodsRoot.getAsset() == null) {
                                Toast.makeText(KuNewOldMessActivity.this, "暂无库存", Toast.LENGTH_SHORT).show();
                            } else {

                                new_name_tv.setText(queryNewOldGoodsRoot.getAsset().getAssetName() + "");
                                new_num_tv.setText(queryNewOldGoodsRoot.getAsset().getNum() + "");
                                new_barcode_tv.setText(queryNewOldGoodsRoot.getAsset().getBarcode() + "");
                                assetId = queryNewOldGoodsRoot.getAsset().getId();
                            }

                        } else {
                            Toast.makeText(KuNewOldMessActivity.this, "获取资产信息错误", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(KuNewOldMessActivity.this, "二维码解析失败", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(KuNewOldMessActivity.this, "数据解析错误,请重新尝试", Toast.LENGTH_SHORT).show();
                }

            }
        }
    };

    private RelativeLayout mNoData_rl;
    private TextView no_mess_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ku_new_old_mess);
        mAll_rl = (RelativeLayout) findViewById(R.id.activity_ku_new_old_mess);
        mNoData_rl = (RelativeLayout) findViewById(R.id.no_data_rl);
        mNoData_rl.setOnClickListener(this);
        no_mess_tv = (TextView) findViewById(R.id.no_mess_tv);
        okHttpManager = OkHttpManager.getInstance();
        intent = getIntent();
        mReferId = intent.getLongExtra("referId", -1);
        if (mReferId != -1) {
            //请求详情
            NO_Url = URLTools.urlBase + URLTools.newold_details_url + "id=" + mReferId;
            okHttpManager.getMethod(false, NO_Url, "以旧换新详情", handler, 15);
        } else {
            Toast.makeText(this, "获取详情ID错误", Toast.LENGTH_SHORT).show();
        }
        submit_agree_url = URLTools.urlBase + URLTools.ku_newold_agree;//确认换新
        query_new_old = URLTools.urlBase + URLTools.query_new_old_goods;//根据二维码查询以旧换新的物品
        initUI();
    }

    private void initUI() {
        mBack_img = (ImageView) findViewById(R.id.back_img);
        mBack_img.setOnClickListener(this);

        mBumen_Tv = (TextView) findViewById(R.id.cg_bumen_msg);
        mPerson_Tv = (TextView) findViewById(R.id.cg_person_msg);
        mName_Tv = (TextView) findViewById(R.id.cg_name_msg);
        mTime_tv = (TextView) findViewById(R.id.cg_time_msg);
        mBeizhuMsg_Tv = (TextView) findViewById(R.id.cg_beizhu_msg);
        mAgree_Btn = (TextView) findViewById(R.id.agree_btn);
        mAgree_Btn.setOnClickListener(this);


        mNum_edit = (TextView) findViewById(R.id.cg_num_msg);
        newold_status_Tv = (TextView) findViewById(R.id.newold_status);
        oldBarcode_tv = (TextView) findViewById(R.id.cg_barcode_msg);

        new_name_tv = (TextView) findViewById(R.id.new_name_msg);
        new_num_tv = (TextView) findViewById(R.id.new_num_msg);
        new_barcode_tv = (TextView) findViewById(R.id.new_barcode_msg);
        //扫一扫按钮
        mSaoImg_btn = (ImageView) findViewById(R.id.saoyisao_new_img);
        mSaoImg_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == mBack_img.getId()) {
            finish();
        } else if (id == mAgree_Btn.getId()) {//确认换新
            if (mReferId != -1) {
                if (checkNum()) {
                    if ("".equals(new_name_tv.getText().toString()) || "".equals(new_barcode_tv.getText().toString())) {
                        Toast.makeText(this, "请选择新物品", Toast.LENGTH_SHORT).show();
                    } else {
                        mAgree_Btn.setClickable(false);
                        BallProgressUtils.showLoading(KuNewOldMessActivity.this, mAll_rl);
                        Map<Object, Object> map = new HashMap<>();
                        map.put("id", mReferId);
                        map.put("num", mNum_edit.getText().toString());
                        map.put("newAssetId", assetId);
                        okHttpManager.postMethod(false, submit_agree_url, "同意换新接口", map, handler, 1);
                    }
                }

            } else {
                Toast.makeText(this, "获取详情ID错误", Toast.LENGTH_SHORT).show();
            }
        } else if (id == mSaoImg_btn.getId()) {
            checkCameraPermission();

        } else if (id == mNoData_rl.getId()) {
            if (mReferId != -1) {
                //请求详情
                BallProgressUtils.showLoading(KuNewOldMessActivity.this, mNoData_rl);
                NO_Url = URLTools.urlBase + URLTools.newold_details_url + "id=" + mReferId;
                okHttpManager.getMethod(false, NO_Url, "以旧换新详情", handler, 15);
            } else {
                Toast.makeText(this, "获取详情ID错误", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean checkNum() {
        String s = new_num_tv.getText().toString();
        if (!"".equals(s)) {
            if (s.length() == 0) {
                Toast.makeText(this, "暂无资产", Toast.LENGTH_SHORT).show();
                return false;
            }

            if (Integer.valueOf(s) == 0) {
                Toast.makeText(this, "暂无此资产", Toast.LENGTH_SHORT).show();
                return false;
            }

            return true;
        } else {
            Toast.makeText(this, "请选择资产", Toast.LENGTH_SHORT).show();
            return false;
        }


//        if (s.startsWith("0")) {
//            Toast.makeText(this, "请填写正确的换新数量", Toast.LENGTH_SHORT).show();
//            return false;
//        }

//        if (Integer.valueOf(s) > num) {
//            Toast.makeText(this, "换新数量大于使用数量", Toast.LENGTH_SHORT).show();
//            return false;
//        }


    }


    /**
     * 照相机权限
     */
    public static final int CAMERA_CODE = 1;

    private void checkCameraPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            int checkTag = ContextCompat.checkSelfPermission(KuNewOldMessActivity.this, Manifest.permission.CAMERA);
            //如果相机权限没有授权
            if (checkTag != PackageManager.PERMISSION_GRANTED) {
                //在这个数组中可以添加很多权限
                requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_CODE);
                return;
            } else {
                //如果已经授权，执行业务逻辑
                Intent intent = new Intent(KuNewOldMessActivity.this, CaptureActivity.class);
                startActivityForResult(intent, 0);
            }
        } else {
            //版本小于23时，不需要判断敏感权限，执行业务逻辑
            Intent intent = new Intent(KuNewOldMessActivity.this, CaptureActivity.class);
            startActivityForResult(intent, 0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_CODE) {
            //点击了允许，授权成功
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(KuNewOldMessActivity.this, CaptureActivity.class);
                startActivityForResult(intent, 0);
            } else {
                //点击了拒绝，授权失败
                Toast.makeText(KuNewOldMessActivity.this, "相机授权失败，请授权", Toast.LENGTH_SHORT).show();

            }


        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 0) {
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                String scanResult = bundle.getString("result");
                if (scanResult != null) {
                    okHttpManager.getMethod(false, query_new_old + "barcode=" + scanResult, "根据二维码查询换新物品", handler, 20);
                    Log.e("scanResult=", scanResult);
                } else {
                    Toast.makeText(KuNewOldMessActivity.this, "此二维码为空", Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(KuNewOldMessActivity.this, "扫描信息错误", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
