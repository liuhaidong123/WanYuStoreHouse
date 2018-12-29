package com.storehouse.wanyu.activity.RepairManage;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.storehouse.wanyu.Bean.RepairFittingsList;
import com.storehouse.wanyu.Bean.RepairMaintenanceLog;
import com.storehouse.wanyu.Bean.RepairMessRoot;
import com.storehouse.wanyu.IPAddress.URLTools;
import com.storehouse.wanyu.OkHttpUtils.OkHttpManager;
import com.storehouse.wanyu.R;

import java.util.ArrayList;
import java.util.List;

public class RepairOlreadyMessActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView mBack_img;
    private TextView mBumen_Tv, mPerson_Tv, mName_Tv, mNum_tv, mBeizhuMsg_Tv, mTime_tv, mBianMa_tv, mLeiXing_tv;
    private TextView mAdd_tv, mDelete_tv;
    private TextView mParts_tv;
    private TextView mRepair_mess_tv, mRepair_date;//故障说明，维修日期
    private RadioButton radioButton;//是否修复
    private ListView mListView;
    private PartsAdapter partsAdapter;
    private List<RepairFittingsList> mList=new ArrayList<>();
    private LinearLayout mll;
    private long myid;//获取详情需要的id
    private Intent intent;
    private View headerView, footerView;
    private String repairUrl;
    private OkHttpManager okHttpManager;
    private Gson mGson = new Gson();
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1010) {
                Toast.makeText(RepairOlreadyMessActivity.this, "连接服务器失败，请重新尝试", Toast.LENGTH_SHORT).show();
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
                            if ("".equals(repairMaintenanceLog.getComment())){
                                mBeizhuMsg_Tv.setText("---");
                            }else {
                                mBeizhuMsg_Tv.setText(repairMaintenanceLog.getComment() + "");
                            }

                            mTime_tv.setText(repairMaintenanceLog.getRepairDateString() + "");

                            if ("".equals(repairMaintenanceLog.getComment())){
                                mRepair_mess_tv.setText("---");
                            }else {
                                mRepair_mess_tv.setText(repairMaintenanceLog.getComment()+"");
                            }

                            if (repairMaintenanceLog.getIsFixed()==0){//未修复
                                radioButton.setText("否");
                            }
                            if (repairMaintenanceLog.getIsFixed()==1){//已修复
                                radioButton.setText("是");
                            }
                            mRepair_date.setText("维修日期   "+repairMaintenanceLog.getInputDateString()+"");
                            if (repairMessRoot.getFittingsList()!=null){
                                if (repairMessRoot.getFittingsList().size()!=0){
                                    mList=repairMessRoot.getFittingsList();
                                    partsAdapter.notifyDataSetChanged();
                                    mll.setVisibility(View.VISIBLE);
                                }else {
                                    mll.setVisibility(View.GONE);
                                    mParts_tv.setText("暂无配件");
                                    mParts_tv.setTextColor(ContextCompat.getColor(RepairOlreadyMessActivity.this,R.color.color_2face4));
                                }
                            }
                        } else {
                            Toast.makeText(RepairOlreadyMessActivity.this, "详情数据错误", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(RepairOlreadyMessActivity.this, "登录过期，请重新登录", Toast.LENGTH_SHORT).show();
                    }


                } else {
                    Toast.makeText(RepairOlreadyMessActivity.this, "详情数据错误", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repair_olready_mess);
        okHttpManager = OkHttpManager.getInstance();
        repairUrl = URLTools.urlBase + URLTools.repair_mess;//维修详情
        intent = getIntent();
        myid = intent.getLongExtra("id", -1);
        okHttpManager.getMethod(false, repairUrl + "id=" + myid, "维修详情", handler, 2);
        initUI();
    }

    private void initUI() {
        mBack_img = (ImageView) findViewById(R.id.back_img);
        mBack_img.setOnClickListener(this);

        headerView = LayoutInflater.from(this).inflate(R.layout.repair_header_view, null);
        footerView = LayoutInflater.from(this).inflate(R.layout.repair_already_footer_view, null);
        mParts_tv = (TextView) headerView.findViewById(R.id.parts_tv);
        mAdd_tv = (TextView) headerView.findViewById(R.id.add);
        mDelete_tv = (TextView) headerView.findViewById(R.id.delete);
        mAdd_tv.setVisibility(View.GONE);
        mDelete_tv.setVisibility(View.GONE);

        mll = headerView.findViewById(R.id.ll_l);
        mBumen_Tv = (TextView) headerView.findViewById(R.id.cg_bumen_msg);
        mPerson_Tv = (TextView) headerView.findViewById(R.id.cg_person_msg);
        mName_Tv = (TextView) headerView.findViewById(R.id.cg_name_msg);
        mNum_tv = (TextView) headerView.findViewById(R.id.cg_num_msg);

        mBeizhuMsg_Tv = (TextView) headerView.findViewById(R.id.cg_beizhu_msg);
        mTime_tv = (TextView) headerView.findViewById(R.id.time_msg);
        mBianMa_tv = (TextView) headerView.findViewById(R.id.bianma_edit);
        mLeiXing_tv = (TextView) headerView.findViewById(R.id.weixiu_edit);

        mRepair_mess_tv = (TextView) footerView.findViewById(R.id.footer_repair_mess);
        mRepair_date = (TextView) footerView.findViewById(R.id.repair_date);
        radioButton = footerView.findViewById(R.id.footer_radio);
        ;//是否修复


        mListView = (ListView) findViewById(R.id.parts_listview);
        partsAdapter = new PartsAdapter();
        mListView.setAdapter(partsAdapter);

        mListView.addHeaderView(headerView);
        mListView.addFooterView(footerView);
    }

    @Override
    public void onClick(View view) {
        int id=view.getId();
        if (id==mBack_img.getId()){
            finish();
        }
    }

    //配件明细适配器
    class PartsAdapter extends BaseAdapter {

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
            PartsHolder partsHolder = null;
            if (view == null) {
                partsHolder = new PartsHolder();
                view = LayoutInflater.from(RepairOlreadyMessActivity.this).inflate(R.layout.repair_alert_header, null);
                partsHolder.status = view.findViewById(R.id.select_state);
                partsHolder.name = view.findViewById(R.id.name_tv);
                partsHolder.xinghao = view.findViewById(R.id.xinghao_tv);
                partsHolder.num = view.findViewById(R.id.num_tv);
                view.setTag(partsHolder);
            } else {
                partsHolder = (PartsHolder) view.getTag();
            }


            partsHolder.status.setVisibility(View.VISIBLE);
            partsHolder.name.setText(mList.get(i).getAssetName() + "");
            partsHolder.xinghao.setText(mList.get(i).getSpecTyp() + "");
            partsHolder.num.setText(mList.get(i).getNum() + "");
            partsHolder.status.setBackgroundResource(R.drawable.bumen_box_select);

            return view;
        }

        class PartsHolder {
            TextView status, name, xinghao;//状态 ,资产名称，型号
            TextView num;//数量
        }
    }
}
