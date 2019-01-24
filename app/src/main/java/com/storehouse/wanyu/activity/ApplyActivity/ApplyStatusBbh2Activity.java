package com.storehouse.wanyu.activity.ApplyActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
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
import com.storehouse.wanyu.Bean.SpStatusRoot;
import com.storehouse.wanyu.Bean.SpStatusRows;
import com.storehouse.wanyu.IPAddress.URLTools;
import com.storehouse.wanyu.MyUtils.BallProgressUtils;
import com.storehouse.wanyu.MyUtils.CircleImg;
import com.storehouse.wanyu.MyUtils.GetAlertApplyListUtils;
import com.storehouse.wanyu.OkHttpUtils.OkHttpManager;
import com.storehouse.wanyu.R;
import com.storehouse.wanyu.activity.AllSPZDetailsActivity.SPZBaoFeiApplyDetailsActivity;
import com.storehouse.wanyu.activity.AllSPZDetailsActivity.SPZCaiGouApplyDetailsActivity;
import com.storehouse.wanyu.activity.AllSPZDetailsActivity.SPZJieYongApplyDetailsActivity;
import com.storehouse.wanyu.activity.AllSPZDetailsActivity.SPZLingYongApplyDetailsActivity;
import com.storehouse.wanyu.activity.AllSPZDetailsActivity.SPZNewOldApplyDetailsActivity;
import com.storehouse.wanyu.activity.AllSPZDetailsActivity.SPZWeiXiuApplyDetailsActivity;
import com.storehouse.wanyu.activity.AllSPZDetailsActivity.SpzTuiKuApplyDetailsActivity;

import java.util.ArrayList;
import java.util.List;

//被驳回
public class ApplyStatusBbh2Activity extends AppCompatActivity implements View.OnClickListener {
    private ImageView mBack_Img;
    private RelativeLayout mSelect_LeiBie_rl, no_data_rl;//选择全部分类弹框
    private TextView mLeiBie_Tv, no_mess_tv;
    private AlertDialog.Builder mBuilder;
    private AlertDialog mAlertDialog;
    private SmartRefreshLayout smartRefreshLayout;
    private ListView mSp_ListView;
    private ListView mAlertDialogListView;
    private List<String> mAlertList = new ArrayList<>();//弹框list
    // private List<String> mLeiBeiList = new ArrayList<>();//选择弹框某个申请后的list，
    private List<SpStatusRows> mSpStatusRowsList = new ArrayList<>();
    private BBHAdapter bbhAdapter;
    private RelativeLayout mAll_RL;
    private OkHttpManager mOkHttpManager;
    private String url, everyoneUrl;
    private int start = 0, limit = 20;
    private int flag = 0;//0表示刷新1表示加载更多
    private int mSqFlag = -1;//全部分类为-1，点击之后对应某个申请的标志
    private Gson mGson = new Gson();
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            BallProgressUtils.dismisLoading();
            if (msg.what == 12) {
                try {
                    String mes = (String) msg.obj;
                    // Log.e("审批中接口=", mes);
                    Object o = mGson.fromJson(mes, SpStatusRoot.class);
                    if (o != null && o instanceof SpStatusRoot) {
                        SpStatusRoot spStatusRoot = (SpStatusRoot) o;

                        if (spStatusRoot != null) {
                            if ("0".equals(spStatusRoot.getCode())) {
                                if (spStatusRoot.getRows() != null) {

                                    if (flag == 0) {//刷新
                                        if (spStatusRoot.getRows().size() == 0) {
                                            Toast.makeText(ApplyStatusBbh2Activity.this, "暂无驳回项目", Toast.LENGTH_SHORT).show();
                                            no_data_rl.setVisibility(View.VISIBLE);
                                            no_mess_tv.setText("空空如也");
                                        } else {
                                            no_data_rl.setVisibility(View.GONE);
                                            no_mess_tv.setText("");

                                        }

                                        mSpStatusRowsList = spStatusRoot.getRows();
                                    }
                                    if (flag == 1) {
                                        for (int i = 0; i < spStatusRoot.getRows().size(); i++) {
                                            mSpStatusRowsList.add(spStatusRoot.getRows().get(i));
                                        }

                                        if (spStatusRoot.getRows().size() == 0) {
                                            Toast.makeText(ApplyStatusBbh2Activity.this, "加载完毕", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    bbhAdapter.notifyDataSetChanged();


                                }
                            } else if ("-1".equals(spStatusRoot.getCode())) {
                                Toast.makeText(ApplyStatusBbh2Activity.this, "登录过期，请重新登录", Toast.LENGTH_SHORT).show();
                                no_data_rl.setVisibility(View.VISIBLE);
                                no_mess_tv.setText("登录过期，请重新登录");

                            }
                        }


                    } else {
                        Toast.makeText(ApplyStatusBbh2Activity.this, "驳回列表数据解析错误", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(ApplyStatusBbh2Activity.this, "数据解析错误，请重新尝试", Toast.LENGTH_SHORT).show();
                    no_data_rl.setVisibility(View.VISIBLE);
                    no_mess_tv.setText("数据解析错误");
                }

            } else if (msg.what == 1010) {
                Toast.makeText(ApplyStatusBbh2Activity.this, "连接服务器失败，请检查网络", Toast.LENGTH_SHORT).show();
                no_data_rl.setVisibility(View.VISIBLE);
                no_mess_tv.setText("连接服务器失败，请检查网络");
            }


        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_status_bbh2);
        no_data_rl = (RelativeLayout) findViewById(R.id.no_data_rl);
        no_data_rl.setOnClickListener(this);
        no_mess_tv = (TextView) findViewById(R.id.no_mess_tv);
        mAll_RL = (RelativeLayout) findViewById(R.id.activity_apply_status_bbh);
        initUI();
    }

    private void initUI() {
        mOkHttpManager = OkHttpManager.getInstance();
        url = URLTools.urlBase + URLTools.sp_status_list + "msgStatus=" + 1 + "&start=" + start + "&limit=" + limit;//驳回所有类型的申请
        mOkHttpManager.getMethod(false, url, "驳回接口", mHandler, 12);


        mAlertList = GetAlertApplyListUtils.getAlertList();//获取弹框申请分类
        mBack_Img = (ImageView) findViewById(R.id.back_img);
        mBack_Img.setOnClickListener(this);
        //全部分类弹框
        mSelect_LeiBie_rl = (RelativeLayout) findViewById(R.id.xiala_rl);
        mSelect_LeiBie_rl.setOnClickListener(this);
        mLeiBie_Tv = (TextView) findViewById(R.id.all_class);
        mBuilder = new AlertDialog.Builder(this);
        mAlertDialog = mBuilder.create();
        View v = LayoutInflater.from(this).inflate(R.layout.bumen_alert, null);
        mAlertDialog.setView(v);
        mAlertDialogListView = v.findViewById(R.id.bumen_listview);
        mAlertDialogListView.setAdapter(new AlertAdapter());
        mAlertDialogListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                start = 0;
                flag = 0;
                switch (i) {
                    case 0://采购
                        mSqFlag = 10;
                        everyoneUrl = URLTools.urlBase + URLTools.sp_status_list + "msgType=" + 10 + "&msgStatus=" + 1 + "&start=" + start + "&limit=" + limit;//某个采购类型的申请列表
                        mOkHttpManager.getMethod(false, everyoneUrl, "驳回采购接口", mHandler, 12);
                        break;
                    case 1://领用
                        mSqFlag = 30;
                        everyoneUrl = URLTools.urlBase + URLTools.sp_status_list + "msgType=" + 30 + "&msgStatus=" + 1 + "&start=" + start + "&limit=" + limit;//某个采购类型的申请列表
                        mOkHttpManager.getMethod(false, everyoneUrl, "驳回领用接口", mHandler, 12);
                        break;
                    case 2://借用
                        mSqFlag = 35;
                        everyoneUrl = URLTools.urlBase + URLTools.sp_status_list + "msgType=" + 35 + "&msgStatus=" + 1 + "&start=" + start + "&limit=" + limit;//某个采购类型的申请列表
                        mOkHttpManager.getMethod(false, everyoneUrl, "驳回借用接口", mHandler, 12);
                        break;
                    case 3://维修
                        mSqFlag = 60;
                        everyoneUrl = URLTools.urlBase + URLTools.sp_status_list + "msgType=" + 60 + "&msgStatus=" + 1 + "&start=" + start + "&limit=" + limit;//某个采购类型的申请列表
                        mOkHttpManager.getMethod(false, everyoneUrl, "驳回维修接口", mHandler, 12);
                        break;
                    case 4://以旧换新
                        mSqFlag = 50;
                        everyoneUrl = URLTools.urlBase + URLTools.sp_status_list + "msgType=" + 50 + "&msgStatus=" + 1 + "&start=" + start + "&limit=" + limit;//某个采购类型的申请列表
                        mOkHttpManager.getMethod(false, everyoneUrl, "驳回以旧换新接口", mHandler, 12);
                        break;
                    case 5://报废
                        mSqFlag = 65;
                        everyoneUrl = URLTools.urlBase + URLTools.sp_status_list + "msgType=" + 65 + "&msgStatus=" + 1 + "&start=" + start + "&limit=" + limit;//某个采购类型的申请列表
                        mOkHttpManager.getMethod(false, everyoneUrl, "驳回报废接口", mHandler, 12);
                        break;
                    case 6://退库
                        mSqFlag = 45;
                        everyoneUrl = URLTools.urlBase + URLTools.sp_status_list + "msgType=" + 45 + "&msgStatus=" + 1 + "&start=" + start + "&limit=" + limit;//某个采购类型的申请列表
                        mOkHttpManager.getMethod(false, everyoneUrl, "驳回退库接口", mHandler, 12);
                        break;

                }

                mLeiBie_Tv.setText(mAlertList.get(i));
                mAlertDialog.dismiss();
                BallProgressUtils.showLoading(ApplyStatusBbh2Activity.this, mAll_RL);
                //延迟5，加载动画消失
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        BallProgressUtils.dismisLoading();
                    }
                }, 1000);
            }
        });
        //当前被驳回状态下对应的某个申请的列表
        smartRefreshLayout = (SmartRefreshLayout) findViewById(R.id.spz_refresh);
        mSp_ListView = (ListView) findViewById(R.id.sqz_listview);
        bbhAdapter = new BBHAdapter();
        mSp_ListView.setAdapter(bbhAdapter);//设置数据
        mSp_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //跳转时，这里需要判断是什么类型的申请
                int type = (int) mSpStatusRowsList.get(i).getMsgType();
                long referId = mSpStatusRowsList.get(i).getReferId();//传过去的id
                switch (type) {//跳转时判断是哪个类型
                    case 10://"采购申请"
                        Intent intent = new Intent(ApplyStatusBbh2Activity.this, SPZCaiGouApplyDetailsActivity.class);
                        intent.putExtra("referId", referId);//每条申请的id
                        intent.putExtra("flag", 2);//这里2代表所有申请跳转过去的，3代表所有审批跳转过去的
                        startActivity(intent);
                        break;
                    case 30://"领用申请"
                        Intent intent1 = new Intent(ApplyStatusBbh2Activity.this, SPZLingYongApplyDetailsActivity.class);
                        intent1.putExtra("referId", referId);
                        intent1.putExtra("flag", 2);
                        startActivity(intent1);
                        break;
                    case 35://"借用申请"
                        Intent intent2 = new Intent(ApplyStatusBbh2Activity.this, SPZJieYongApplyDetailsActivity.class);
                        intent2.putExtra("referId", referId);
                        intent2.putExtra("flag", 2);
                        startActivity(intent2);
                        break;
                    case 60://"维修申请"
                        Intent intent3 = new Intent(ApplyStatusBbh2Activity.this, SPZWeiXiuApplyDetailsActivity.class);
                        intent3.putExtra("referId", referId);
                        intent3.putExtra("flag", 2);
                        startActivity(intent3);
                        break;
                    case 50://"以旧换新"
                        Intent intent4 = new Intent(ApplyStatusBbh2Activity.this, SPZNewOldApplyDetailsActivity.class);
                        intent4.putExtra("referId", referId);
                        intent4.putExtra("flag", 2);
                        startActivity(intent4);
                        break;
                    case 65://"报废申请"
                        Intent intent5 = new Intent(ApplyStatusBbh2Activity.this, SPZBaoFeiApplyDetailsActivity.class);
                        intent5.putExtra("referId", referId);
                        intent5.putExtra("flag", 2);
                        startActivity(intent5);
                        break;
//                    case "归还申请":
//                        Intent intent6 = new Intent(ApplyStatusSpz1Activity.this, SPZGuiHuanApplyDetailsActivity.class);
//                        //intent.putExtra("tag", mLeiBeiList.get(i));
//                        startActivity(intent6);
//                        break;
                    case 45://"退库申请"
                        Intent intent7 = new Intent(ApplyStatusBbh2Activity.this, SpzTuiKuApplyDetailsActivity.class);
                        intent7.putExtra("referId", referId);
                        intent7.putExtra("flag", 2);
                        startActivity(intent7);
                        break;
                    default:
                        Toast.makeText(ApplyStatusBbh2Activity.this, "抱歉，没有此类申请", Toast.LENGTH_SHORT).show();
                        break;
                }

            }
        });
        smartRefreshLayout.setRefreshHeader(new CircleHeader(this));
        smartRefreshLayout.setRefreshFooter(new BallPulseFooter(this).setSpinnerStyle(SpinnerStyle.Scale).setAnimatingColor(ContextCompat.getColor(this, R.color.color_1c82d4)));


        smartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshlayout.finishRefresh(2000/*,false*/);//传入false表示刷新失败
                flag = 0;
                start = 0;
                if (mSqFlag == -1) {
                    url = URLTools.urlBase + URLTools.sp_status_list + "msgStatus=" + 1 + "&start=" + start + "&limit=" + limit;//驳回所有类型的申请

                } else {
                    url = URLTools.urlBase + URLTools.sp_status_list + "msgType=" + mSqFlag + "&msgStatus=" + 1 + "&start=" + start + "&limit=" + limit;//驳回某个类型的申请

                }
                mOkHttpManager.getMethod(false, url, "驳回接口", mHandler, 12);
            }
        });
        smartRefreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                refreshlayout.finishLoadmore(2000/*,false*/);//传入false表示加载失败
                //loadMoreData();
                flag = 1;
                start += 20;
                if (mSqFlag == -1) {
                    url = URLTools.urlBase + URLTools.sp_status_list + "msgStatus=" + 1 + "&start=" + start + "&limit=" + limit;//驳回所有类型的申请

                } else {
                    url = URLTools.urlBase + URLTools.sp_status_list + "msgType=" + mSqFlag + "&msgStatus=" + 1 + "&start=" + start + "&limit=" + limit;//驳回某个类型的申请

                }
                mOkHttpManager.getMethod(false, url, "驳回接口", mHandler, 12);
            }
        });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == mBack_Img.getId()) {
            finish();
        } else if (id == mSelect_LeiBie_rl.getId()) {//全部分类弹框
            mAlertDialog.show();
        } else if (id == no_data_rl.getId()) {
            flag = 0;
            start = 0;
            if (mSqFlag == -1) {
                url = URLTools.urlBase + URLTools.sp_status_list + "msgStatus=" + 1 + "&start=" + start + "&limit=" + limit;//驳回所有类型的申请

            } else {
                url = URLTools.urlBase + URLTools.sp_status_list + "msgType=" + mSqFlag + "&msgStatus=" + 1 + "&start=" + start + "&limit=" + limit;//驳回某个类型的申请

            }
            BallProgressUtils.showLoading(ApplyStatusBbh2Activity.this, no_data_rl);
            mOkHttpManager.getMethod(false, url, "驳回接口", mHandler, 12);
        }
    }


    /**
     * 当前被驳回状态下对应的某个申请的列表adapter
     */
    class BBHAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mSpStatusRowsList.size() == 0 ? 0 : mSpStatusRowsList.size();
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
            BBHHolder bbhHolder = null;
            if (view == null) {
                bbhHolder = new BBHHolder();
                view = LayoutInflater.from(ApplyStatusBbh2Activity.this).inflate(R.layout.sq_status_item, null);
                bbhHolder.headImg = view.findViewById(R.id.sq_head_img);
                bbhHolder.bumenAndName = view.findViewById(R.id.sq_bumen_and_name);
                bbhHolder.leibie = view.findViewById(R.id.sq_leibie);
                bbhHolder.status = view.findViewById(R.id.sq_status);
                bbhHolder.date = view.findViewById(R.id.sq_date);
                view.setTag(bbhHolder);
            } else {
                bbhHolder = (BBHHolder) view.getTag();
            }
            //填充数据
            bbhHolder.bumenAndName.setText("审批人  " + mSpStatusRowsList.get(i).getApprovalTrueName());
            bbhHolder.status.setTextColor(ContextCompat.getColor(ApplyStatusBbh2Activity.this, R.color.color_dc8268));
            bbhHolder.status.setText("被驳回");
            bbhHolder.date.setText(mSpStatusRowsList.get(i).getCreateTimeString());
            switch ((int) mSpStatusRowsList.get(i).getMsgType()) {
                case 10://采购申请标志
                    bbhHolder.headImg.setImageResource(R.mipmap.sq_cg_icon);
                    bbhHolder.leibie.setText("采购申请");
                    break;
                case 30://领用申请标志
                    bbhHolder.headImg.setImageResource(R.mipmap.sq_ly_icon);
                    bbhHolder.leibie.setText("领用申请");
                    break;
                case 35://借用申请标志
                    bbhHolder.headImg.setImageResource(R.mipmap.sq_jy_icon);
                    bbhHolder.leibie.setText("借用申请");
                    break;
                case 60://维修申请标志
                    bbhHolder.headImg.setImageResource(R.mipmap.sq_wx_icon);
                    bbhHolder.leibie.setText("维修申请");
                    break;
                case 50://以旧换新标志
                    bbhHolder.headImg.setImageResource(R.mipmap.sq_newold_icon);
                    bbhHolder.leibie.setText("以旧换新");
                    break;
                case 65://报废申请标志
                    bbhHolder.headImg.setImageResource(R.mipmap.sq_bf_icon);
                    bbhHolder.leibie.setText("报废申请");
                    break;
                case 45://退库申请标志
                    bbhHolder.headImg.setImageResource(R.mipmap.sq_tk_icon);
                    bbhHolder.leibie.setText("退库申请");
                    break;
            }
            return view;
        }

        class BBHHolder {
            CircleImg headImg;
            TextView bumenAndName, leibie, status, date;//部门和姓名，申请的类别，状态，日期
        }
    }

    /**
     * 弹框列表adapter
     */

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
            AlertBhhHolder alertBhhHolder = null;
            if (view == null) {
                view = LayoutInflater.from(ApplyStatusBbh2Activity.this).inflate(R.layout.sq_alert_item, null);
                alertBhhHolder = new AlertBhhHolder();
                alertBhhHolder.sqName = view.findViewById(R.id.alert_item_tv);
                view.setTag(alertBhhHolder);
            } else {
                alertBhhHolder = (AlertBhhHolder) view.getTag();
            }
            //设置数据
            alertBhhHolder.sqName.setText(mAlertList.get(i));
            return view;
        }

        class AlertBhhHolder {
            TextView sqName;//申请名称
        }
    }
}
