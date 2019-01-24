package com.storehouse.wanyu.activity.PurchaseOrder;

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
import com.storehouse.wanyu.Bean.AssetQrCode;
import com.storehouse.wanyu.Bean.CaiGouDetailsRoot;
import com.storehouse.wanyu.Bean.CaiGouDetailsRows;
import com.storehouse.wanyu.Bean.ErwermaRoot;
import com.storehouse.wanyu.Bean.SetPasswordRoot;
import com.storehouse.wanyu.IPAddress.URLTools;
import com.storehouse.wanyu.MyUtils.BallProgressUtils;
import com.storehouse.wanyu.OkHttpUtils.OkHttpManager;
import com.storehouse.wanyu.R;
import com.storehouse.wanyu.activity.Ku.KuNewOldMessActivity;
import com.storehouse.wanyu.activity.PropertyManage.AddPropertyActivity;
import com.storehouse.wanyu.activity.PropertyManage.PropertyMessageActivity;
import com.xys.libzxing.activity.CaptureActivity;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

//采购订单详情 （根据不同的参数隐藏显示不同的字段）
public class PurchaseOrderMsgActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView mBack_img;
    private TextView purchase_status_tv;//
    //申请部门，物品名称，规格型号，计量单位，采购数量，生产厂家，采购类别，采购理由，同意按钮，驳回按钮，审批中状态
    private TextView mBumen_Tv, mName_Tv, mXingHao_Tv, mDanWei_Tv, mNum_Tv, mChangJia_Tv, mLeiBie_Tv, mReason_Tv, mTime_msg, ruku_btn;
    private EditText mBeiZhu_Edit;//待采购中备注输入框
    //备注信息，单价信息，采购地点，验收评价，验收意见，开始采购按钮
    private TextView mBeiZhu_msg_tv, mDanjia_mes_tv, mLocation_msg_tv, mComment_msg_tv, mSuggestion_msg_tv, mStart_Purchase_Btn;
    //备注信息布局，备注输入框布局，采购单价布局，采购地点布局，验收评价布局，验收意见布局
    private RelativeLayout mBeiZhuTv_Rl, mBeiZhuEdit_Rl, mDanJia_Rl, mLocation_Rl, mComment_Rl, mSuggestion_Rl;
    private Gson gson = new Gson();
    private String url, submit_url, mSaoYiSao_Url;
    private long myID;
    private Intent intent;
    private int flag;
    private OkHttpManager okHttpManager;
    private CaiGouDetailsRows caiGouDetailsRows;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 15) {//详情接口
                String mes = (String) msg.obj;
                try {
                    Object o = gson.fromJson(mes, CaiGouDetailsRoot.class);
                    if (o != null && o instanceof CaiGouDetailsRoot) {
                        CaiGouDetailsRoot caiGouDetailsRoot = (CaiGouDetailsRoot) o;
                        if (caiGouDetailsRoot != null && "0".equals(caiGouDetailsRoot.getCode())) {
                            mNoData_rl.setVisibility(View.GONE);
                            no_mess_tv.setText("");
                            caiGouDetailsRows = caiGouDetailsRoot.getBuyApply();

                            mBumen_Tv.setText(caiGouDetailsRows.getDepartmentName() + "");
                            mName_Tv.setText(caiGouDetailsRows.getAssetName() + "");
                            mTime_msg.setText(caiGouDetailsRows.getApplyTimeString() + "");
                            if (!"".equals(caiGouDetailsRows.getSpecTyp())) {
                                mXingHao_Tv.setText(caiGouDetailsRows.getSpecTyp() + "");
                            } else {
                                mXingHao_Tv.setText("---");
                            }


                            mDanWei_Tv.setText(caiGouDetailsRows.getUnit() + "");
                            mNum_Tv.setText(caiGouDetailsRows.getBuyCount() + "");

                            if (!"".equals(caiGouDetailsRows.getProducerName())) {
                                mChangJia_Tv.setText(caiGouDetailsRows.getProducerName() + "");
                            } else {
                                mChangJia_Tv.setText("---");
                            }

                            if (caiGouDetailsRows.getBuyCate() == 1) {
                                mLeiBie_Tv.setText("计划内采购");
                            }
                            if (caiGouDetailsRows.getBuyCate() == 2) {
                                mLeiBie_Tv.setText("计划外采购");
                            }

                            if (!"".equals(caiGouDetailsRows.getBuyReason())) {
                                mReason_Tv.setText(caiGouDetailsRows.getBuyReason() + "");
                            } else {
                                mReason_Tv.setText("---");
                            }
                            if (!"".equals(caiGouDetailsRows.getComment())) {
                                mBeiZhu_msg_tv.setText(caiGouDetailsRows.getComment() + "");
                            } else {
                                mBeiZhu_msg_tv.setText("---");
                            }

                            if (!"".equals(caiGouDetailsRows.getBuyWorth())) {
                                DecimalFormat decimalFormat = new DecimalFormat("###0.00");//格式化设置
                                String s2 = decimalFormat.format(caiGouDetailsRows.getBuyWorth());
                                mDanjia_mes_tv.setText(s2 + "元");
                            } else {
                                mDanjia_mes_tv.setText("---");
                            }

                            if (!"".equals(caiGouDetailsRows.getBuyAddress())) {
                                mLocation_msg_tv.setText(caiGouDetailsRows.getBuyAddress() + "");
                            } else {
                                mLocation_msg_tv.setText("---");
                            }
                            if (!"".equals(caiGouDetailsRows.getAcceptanceEvaluation())) {
                                mComment_msg_tv.setText(caiGouDetailsRows.getAcceptanceEvaluation() + "");
                            } else {
                                mComment_msg_tv.setText("---");
                            }


                            if (caiGouDetailsRows.getAcceptanceOpinion() == 1) {
                                mSuggestion_msg_tv.setText("同意");
                            }
                            if (caiGouDetailsRows.getAcceptanceOpinion() == 2) {
                                mSuggestion_msg_tv.setText("退货");
                            }


                        } else {
                            Toast.makeText(PurchaseOrderMsgActivity.this, "登录过期，请重新登录", Toast.LENGTH_SHORT).show();

                        }


                    } else {
                        Toast.makeText(PurchaseOrderMsgActivity.this, "获取详情失败", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(PurchaseOrderMsgActivity.this, "数据解析错误,请联系后台", Toast.LENGTH_SHORT).show();
                    mNoData_rl.setVisibility(View.VISIBLE);
                    no_mess_tv.setText("数据解析错误,请重新尝试");
                }


            } else if (msg.what == 1010) {
                mNoData_rl.setVisibility(View.VISIBLE);
                no_mess_tv.setText("连接服务器失败，请检查网络");
                BallProgressUtils.dismisLoading();
                mStart_Purchase_Btn.setClickable(true);
                Toast.makeText(PurchaseOrderMsgActivity.this, "连接服务器失败，请检查网络", Toast.LENGTH_SHORT).show();
            } else if (msg.what == 1) {//开始采购
                BallProgressUtils.dismisLoading();
                mStart_Purchase_Btn.setClickable(true);
                String mes = (String) msg.obj;
                try {
                    Object o = gson.fromJson(mes, SetPasswordRoot.class);
                    if (o != null && o instanceof SetPasswordRoot) {
                        SetPasswordRoot setPasswordRoot = (SetPasswordRoot) o;
                        if (setPasswordRoot != null && "0".equals(setPasswordRoot.getCode())) {
                            Toast.makeText(PurchaseOrderMsgActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK, intent);
                            finish();
                        } else if (setPasswordRoot != null && "-1".equals(setPasswordRoot.getCode())) {
                            Toast.makeText(PurchaseOrderMsgActivity.this, "登录过期,请重新登录", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(PurchaseOrderMsgActivity.this, "提交失败", Toast.LENGTH_SHORT).show();
                        }


                    } else {
                        Toast.makeText(PurchaseOrderMsgActivity.this, "连接服务器失败，请重新尝试", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(PurchaseOrderMsgActivity.this, "数据解析错误,请联系后台", Toast.LENGTH_SHORT).show();
                }

            } else if (msg.what == 11) {
                String mes = (String) msg.obj;
                try {
                    Object o = gson.fromJson(mes, ErwermaRoot.class);
                    Log.e("扫一扫接口信息=", mes);
                    if (o != null && o instanceof ErwermaRoot) {
                        ErwermaRoot erwermaRoot = (ErwermaRoot) o;
                        if ("0".equals(erwermaRoot.getCode())) {
                            AssetQrCode assetQrCode = erwermaRoot.getAssetQrCode();
                            if (assetQrCode != null) {
                                if (assetQrCode.getIsUse() == 0) {//这是一个新的二维码，可以保存资产信息，跳转到资产入库页面
//                                // Toast.makeText(getActivity(), "这是一个新的二维码，可以保存资产信息，跳转到资产入库页面", Toast.LENGTH_SHORT).show();
//                                //将资产编码传过去
                                    Intent intent = new Intent(PurchaseOrderMsgActivity.this, AddPropertyActivity.class);
                                    intent.putExtra("barcode", assetQrCode.getBarcode());
                                    intent.putExtra("bean", caiGouDetailsRows);//将采购订单信息传到资产入库页面
                                    startActivityForResult(intent, 23);
//                            } else if (assetQrCode.getIsUse() != 0 && assetQrCode.getAssetId() != 0) {//这是一个已经使用过的二维码，已经保存过资产信息，跳转到资产详情页面
//                                // 接这个接口  http://192.168.1.168:8085/mobileapi/asset/get.do?id=assetQrCode.getAssetId()
//                                // Toast.makeText(getActivity(), "这是一个已经使用过的二维码，已经保存过资产信息，跳转到资产详情页面", Toast.LENGTH_SHORT).show();
//                                Intent i = new Intent(PurchaseOrderMsgActivity.this, PropertyMessageActivity.class);
//                                i.putExtra("assetID", assetQrCode.getAssetId());
//                                startActivity(i);


                                } else {
                                    Intent intent = new Intent(PurchaseOrderMsgActivity.this, AddPropertyActivity.class);
                                    intent.putExtra("barcode", assetQrCode.getBarcode());
                                    intent.putExtra("bean", caiGouDetailsRows);//将采购订单信息传到资产入库页面
                                    startActivityForResult(intent, 23);
                                    Toast.makeText(PurchaseOrderMsgActivity.this, "此二维码已被使用，请谨慎使用！", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(PurchaseOrderMsgActivity.this, "此二维码不存在", Toast.LENGTH_SHORT).show();

                            }


                        } else if ("-1".equals(erwermaRoot.getCode())) {
                            Toast.makeText(PurchaseOrderMsgActivity.this, "账号过期，请重新登录", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(PurchaseOrderMsgActivity.this, "二维码扫数据错误", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(PurchaseOrderMsgActivity.this, "此二维码无效", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(PurchaseOrderMsgActivity.this, "数据解析错误,请联系后台", Toast.LENGTH_SHORT).show();
                }


            }
        }
    };
    private RelativeLayout mAll_rl, mNoData_rl;
    private TextView no_mess_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_order_msg);
        mAll_rl = (RelativeLayout) findViewById(R.id.activity_purchase_order_msg);
        mNoData_rl = (RelativeLayout) findViewById(R.id.no_data_rl);
        mNoData_rl.setOnClickListener(this);
        no_mess_tv = (TextView) findViewById(R.id.no_mess_tv);
        okHttpManager = OkHttpManager.getInstance();
        url = URLTools.urlBase + URLTools.caigou_details_url;//详情接口
        submit_url = URLTools.urlBase + URLTools.start_purchase_url;//开始采购接口
        initUI();
    }

    private void initUI() {
        mBack_img = (ImageView) findViewById(R.id.back_img);
        mBack_img.setOnClickListener(this);
        purchase_status_tv = (TextView) findViewById(R.id.purchase_status_tv);
        mBumen_Tv = (TextView) findViewById(R.id.cg_bumen_msg);
        mName_Tv = (TextView) findViewById(R.id.cg_name_msg);
        mXingHao_Tv = (TextView) findViewById(R.id.cg_xinghao_msg);
        mDanWei_Tv = (TextView) findViewById(R.id.cg_danwei_msg);
        mNum_Tv = (TextView) findViewById(R.id.cg_num_msg);
        mChangJia_Tv = (TextView) findViewById(R.id.cg_changjia_msg);
        mLeiBie_Tv = (TextView) findViewById(R.id.cg_leibie_msg);
        mReason_Tv = (TextView) findViewById(R.id.cg_reason_msg);
        mTime_msg = (TextView) findViewById(R.id.time_msg);

        mBeiZhu_Edit = (EditText) findViewById(R.id.remark_edit);

        mBeiZhu_msg_tv = (TextView) findViewById(R.id.caigou_beihu_msg);
        mDanjia_mes_tv = (TextView) findViewById(R.id.caigou_danjia_msg);
        mLocation_msg_tv = (TextView) findViewById(R.id.caigou_location_msg);
        mComment_msg_tv = (TextView) findViewById(R.id.caigou_comment_msg);
        mSuggestion_msg_tv = (TextView) findViewById(R.id.caigou_suggestion_msg);
        //开始采购
        mStart_Purchase_Btn = (TextView) findViewById(R.id.start_purchase_btn);
        mStart_Purchase_Btn.setOnClickListener(this);

        mBeiZhuTv_Rl = (RelativeLayout) findViewById(R.id.caigou_beihu_rl);
        mBeiZhuEdit_Rl = (RelativeLayout) findViewById(R.id.remark_rl);
        mDanJia_Rl = (RelativeLayout) findViewById(R.id.caigou_danjia_rl);
        mLocation_Rl = (RelativeLayout) findViewById(R.id.caigou_location_rl);
        mComment_Rl = (RelativeLayout) findViewById(R.id.caigou_comment_rl);
        mSuggestion_Rl = (RelativeLayout) findViewById(R.id.caigou_suggestion_rl);

        //确认入库
        ruku_btn = (TextView) findViewById(R.id.ruku_btn);
        ruku_btn.setOnClickListener(this);

        intent = getIntent();
        flag = intent.getIntExtra("flag", -1);
        myID = intent.getLongExtra("id", -1);
        if (myID != -1) {
            okHttpManager.getMethod(false, url + "id=" + myID, "采购详情", handler, 15);
        } else {
            Toast.makeText(this, "详情信息错误", Toast.LENGTH_SHORT).show();
        }

        if (flag == 1) {//待采购跳过来的，隐藏备注信息，采购单价，采购地点，验收评价，验收意见；显示备注输入框，开始采购按钮
            purchase_status_tv.setText("待采购");
            mBeiZhuTv_Rl.setVisibility(View.GONE);
            mDanJia_Rl.setVisibility(View.GONE);
            mLocation_Rl.setVisibility(View.GONE);
            mComment_Rl.setVisibility(View.GONE);
            mSuggestion_Rl.setVisibility(View.GONE);
            mBeiZhuEdit_Rl.setVisibility(View.VISIBLE);
            mStart_Purchase_Btn.setVisibility(View.VISIBLE);


        } else if (flag == 2) {//采购中跳过来的，隐藏采购单价，采购地点，验收评价，验收意见，备注输入框，开始采购按钮；显示备注信息
            purchase_status_tv.setText("采购中");
            mDanJia_Rl.setVisibility(View.GONE);
            mLocation_Rl.setVisibility(View.GONE);
            mComment_Rl.setVisibility(View.GONE);
            mSuggestion_Rl.setVisibility(View.GONE);
            mBeiZhuEdit_Rl.setVisibility(View.GONE);
            mStart_Purchase_Btn.setVisibility(View.GONE);
            mBeiZhuTv_Rl.setVisibility(View.VISIBLE);

        } else if (flag == 3) {//已验收跳过来的，隐藏 备注输入框，；显示备注信息，采购单价，采购地点，验收评价，验收意见，确认入库按钮
            purchase_status_tv.setText("已验收");
            mBeiZhuEdit_Rl.setVisibility(View.GONE);
            mStart_Purchase_Btn.setVisibility(View.GONE);
            mDanJia_Rl.setVisibility(View.VISIBLE);
            mLocation_Rl.setVisibility(View.VISIBLE);
            mComment_Rl.setVisibility(View.VISIBLE);
            mSuggestion_Rl.setVisibility(View.VISIBLE);
            mBeiZhuTv_Rl.setVisibility(View.VISIBLE);
            ruku_btn.setVisibility(View.VISIBLE);

        } else if (flag == 4) {//已退货跳过来的，隐藏 备注输入框，开始采购按钮；显示备注信息，采购单价，采购地点，验收评价，验收意见，
            purchase_status_tv.setText("已退货");
            mBeiZhuEdit_Rl.setVisibility(View.GONE);
            mStart_Purchase_Btn.setVisibility(View.GONE);
            mDanJia_Rl.setVisibility(View.VISIBLE);
            mLocation_Rl.setVisibility(View.VISIBLE);
            mComment_Rl.setVisibility(View.VISIBLE);
            mSuggestion_Rl.setVisibility(View.VISIBLE);
            mBeiZhuTv_Rl.setVisibility(View.VISIBLE);

        } else if (flag == 5) {//已入库跳过来的，隐藏 备注输入框，开始采购按钮；显示备注信息，采购单价，采购地点，验收评价，验收意见，
            purchase_status_tv.setText("已入库");
            mBeiZhuEdit_Rl.setVisibility(View.GONE);
            mStart_Purchase_Btn.setVisibility(View.GONE);
            mDanJia_Rl.setVisibility(View.VISIBLE);
            mLocation_Rl.setVisibility(View.VISIBLE);
            mComment_Rl.setVisibility(View.VISIBLE);
            mSuggestion_Rl.setVisibility(View.VISIBLE);
            mBeiZhuTv_Rl.setVisibility(View.VISIBLE);

        } else {
            Toast.makeText(this, "详情信息错误", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == mBack_img.getId()) {
            finish();
        } else if (id == mStart_Purchase_Btn.getId()) {//开始采购
            if (!"".equals(mBumen_Tv.getText().toString().trim())) {//如果没有获取详情信息，那么无法提交采购

                mStart_Purchase_Btn.setClickable(false);
                BallProgressUtils.showLoading(PurchaseOrderMsgActivity.this, mAll_rl);
                Map<Object, Object> map = new HashMap<>();
                map.put("comment", mBeiZhu_Edit.getText().toString().trim());
                map.put("id", myID);
                okHttpManager.postMethod(false, submit_url, "开始采购接口", map, handler, 1);

            } else {
                Toast.makeText(this, "详情信息错误", Toast.LENGTH_SHORT).show();
            }
        } else if (id == ruku_btn.getId()) {//确认入库
            if (!"".equals(mBumen_Tv.getText().toString().trim())) {//如果没有获取详情信息，那么无法确认入库
                checkCameraPermission();
            } else {
                Toast.makeText(this, "详情信息错误", Toast.LENGTH_SHORT).show();
            }

        } else if (id == mNoData_rl.getId()) {
            if (myID != -1) {
                okHttpManager.getMethod(false, url + "id=" + myID, "采购详情", handler, 15);
                BallProgressUtils.showLoading(this, mNoData_rl);
            } else {
                Toast.makeText(this, "详情信息错误", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 照相机权限
     */
    public static final int CAMERA_CODE = 1;

    private void checkCameraPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            int checkTag = ContextCompat.checkSelfPermission(PurchaseOrderMsgActivity.this, Manifest.permission.CAMERA);
            //如果相机权限没有授权
            if (checkTag != PackageManager.PERMISSION_GRANTED) {
                //在这个数组中可以添加很多权限
                requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_CODE);
                return;
            } else {
                //如果已经授权，执行业务逻辑
                Intent intent = new Intent(PurchaseOrderMsgActivity.this, CaptureActivity.class);
                startActivityForResult(intent, 0);
            }
        } else {
            //版本小于23时，不需要判断敏感权限，执行业务逻辑
            Intent intent = new Intent(PurchaseOrderMsgActivity.this, CaptureActivity.class);
            startActivityForResult(intent, 0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_CODE) {
            //点击了允许，授权成功
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(PurchaseOrderMsgActivity.this, CaptureActivity.class);
                startActivityForResult(intent, 0);
            } else {
                //点击了拒绝，授权失败
                Toast.makeText(PurchaseOrderMsgActivity.this, "相机授权失败，请授权", Toast.LENGTH_SHORT).show();

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
                    mSaoYiSao_Url = URLTools.urlBase + URLTools.saoyisao + "barcode=" + scanResult;
                    okHttpManager.getMethod(false, mSaoYiSao_Url, "扫一扫接口", handler, 11);
                    Log.e("scanResult=", scanResult);
                } else {
                    Toast.makeText(PurchaseOrderMsgActivity.this, "此二维码为空", Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(PurchaseOrderMsgActivity.this, "扫描信息错误", Toast.LENGTH_SHORT).show();
            }
        } else if (resultCode == RESULT_OK && requestCode == 23) {//确认入库之后销毁这个页面
            setResult(RESULT_OK, intent);
            finish();

        }
    }
}
