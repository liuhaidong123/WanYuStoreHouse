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
import com.squareup.picasso.Picasso;
import com.storehouse.wanyu.Bean.RepairRoot;
import com.storehouse.wanyu.Bean.RepairRows;
import com.storehouse.wanyu.IPAddress.URLTools;
import com.storehouse.wanyu.MyUtils.BallProgressUtils;
import com.storehouse.wanyu.MyUtils.CircleImg;
import com.storehouse.wanyu.OkHttpUtils.OkHttpManager;
import com.storehouse.wanyu.R;
import com.storehouse.wanyu.activity.PurchaseOrder.PurchaseOrderActivity;

import java.util.ArrayList;
import java.util.List;

public class RepairActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView mBack;
    private TextView repair_waitfor, repair_already,no_mess_tv;
    private SmartRefreshLayout smartRefreshLayout;
    private ListView myListView;
    private RepairAdapter repairAdapter;
    private List<RepairRows> mList = new ArrayList<>();
    private Gson gson = new Gson();
    private OkHttpManager okHttpManager;
    private int selectFlag = 1;
    private boolean refreshFlaf = true;
    private RelativeLayout mAll, mNoData_rl;
    private String url;
    private int start = 0, limit = 20;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            BallProgressUtils.dismisLoading();
            if (msg.what == 1) {
                try {
                    BallProgressUtils.dismisLoading();
                    String mes = (String) msg.obj;
                    Object o = gson.fromJson(mes, RepairRoot.class);
                    if (o != null && o instanceof RepairRoot) {
                        RepairRoot repairRoot = (RepairRoot) o;
                        if (repairRoot != null && "0".equals(repairRoot.getCode())) {
                            if (repairRoot.getRows() != null) {
                                if (refreshFlaf) {
                                    mList = repairRoot.getRows();
                                    if (repairRoot.getRows().size() == 0) {
                                        mNoData_rl.setVisibility(View.VISIBLE);
                                        no_mess_tv.setText("空空如也");
                                    } else {
                                        mNoData_rl.setVisibility(View.GONE);
                                        no_mess_tv.setText("");
                                    }
                                } else {
                                    for (int i = 0; i < repairRoot.getRows().size(); i++) {
                                        mList.add(repairRoot.getRows().get(i));
                                    }
                                    if (repairRoot.getRows().size() == 0) {
                                        Toast.makeText(RepairActivity.this, "全部加载完毕", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                repairAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(RepairActivity.this, "请求维修数据错误", Toast.LENGTH_SHORT).show();
                            }

                        } else if (repairRoot != null && "-1".equals(repairRoot.getCode())) {
                            Toast.makeText(RepairActivity.this, "登录过期,请重新登录", Toast.LENGTH_SHORT).show();
                            no_mess_tv.setText("登录过期,请重新登录");
                            mNoData_rl.setVisibility(View.VISIBLE);
                        } else {
                            Toast.makeText(RepairActivity.this, "请求数据数据错误", Toast.LENGTH_SHORT).show();

                        }

                    } else {
                        BallProgressUtils.dismisLoading();
                        Toast.makeText(RepairActivity.this, "连接服务器失败,请重新尝试", Toast.LENGTH_SHORT).show();
                        no_mess_tv.setText("连接服务器失败，请重新尝试");
                        mNoData_rl.setVisibility(View.VISIBLE);
                    }
                }catch (Exception e){
                    BallProgressUtils.dismisLoading();
                    Toast.makeText(RepairActivity.this, "数据解析错误,请重新尝试", Toast.LENGTH_SHORT).show();
                    no_mess_tv.setText("数据解析错误,请重新尝试");
                    mNoData_rl.setVisibility(View.VISIBLE);
                }


            } else {
                BallProgressUtils.dismisLoading();
                Toast.makeText(RepairActivity.this, "连接服务器失败,请重新尝试", Toast.LENGTH_SHORT).show();
                no_mess_tv.setText("连接服务器失败，请检查网络");
                mNoData_rl.setVisibility(View.VISIBLE);
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repair);
        okHttpManager = OkHttpManager.getInstance();
        mAll = (RelativeLayout) findViewById(R.id.activity_repair);
        mNoData_rl = (RelativeLayout) findViewById(R.id.no_data_rl);
        mNoData_rl.setOnClickListener(this);
        no_mess_tv= (TextView) findViewById(R.id.no_mess_tv);
        url = URLTools.urlBase + URLTools.repair_list;
        okHttpManager.getMethod(false, url + "start=" + start + "&limit=" + limit + "&status=" + 1, "待维修列表", handler, 1);//请求待维修
        iniUI();
    }

    private void iniUI() {
        mBack = (ImageView) findViewById(R.id.back_img);
        mBack.setOnClickListener(this);

        repair_waitfor = (TextView) findViewById(R.id.repair_waitfor);
        repair_already = (TextView) findViewById(R.id.repair_already);
        repair_waitfor.setOnClickListener(this);
        repair_already.setOnClickListener(this);

        myListView = (ListView) findViewById(R.id.repair_listview);
        repairAdapter = new RepairAdapter();
        myListView.setAdapter(repairAdapter);
        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (selectFlag==1){//待维修详情
                    Intent intent = new Intent(RepairActivity.this, RepairMessActivity.class);
                    intent.putExtra("id",mList.get(i).getId());
                    startActivityForResult(intent, 1);
                }else {//已经维修
                    Intent intent = new Intent(RepairActivity.this, RepairOlreadyMessActivity.class);
                    intent.putExtra("id",mList.get(i).getId());
                    startActivity(intent);
                }

            }
        });
        smartRefreshLayout = (SmartRefreshLayout) findViewById(R.id.repair_refresh);
        smartRefreshLayout.setRefreshHeader(new CircleHeader(this));
        smartRefreshLayout.setRefreshFooter(new BallPulseFooter(this).setSpinnerStyle(SpinnerStyle.Scale).setAnimatingColor(ContextCompat.getColor(this, R.color.color_1c82d4)));
        smartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshlayout.finishRefresh(2000);
                refreshFlaf = true;
                start = 0;
                okHttpManager.getMethod(false, url + "start=" + start + "&limit=" + limit + "&status=" + selectFlag, "维修列表", handler, 1);//请求维修

            }
        });
        smartRefreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                refreshlayout.finishLoadmore(2000);
                refreshFlaf = false;
                start += 20;
                okHttpManager.getMethod(false, url + "start=" + start + "&limit=" + limit + "&status=" + selectFlag, "维修列表", handler, 1);//请求维修

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1&&resultCode==RESULT_OK){//提交维修后，刷新列表
            refreshFlaf = true;
            start = 0;
            okHttpManager.getMethod(false, url + "start=" + start + "&limit=" + limit + "&status=" + selectFlag, "维修列表", handler, 1);//请求维修

        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == mBack.getId()) {
            finish();
        } else if (id == repair_already.getId()) {//维修记录
            mList.clear();
            repairAdapter.notifyDataSetChanged();

            BallProgressUtils.showLoading(RepairActivity.this, mAll);
            repair_already.setBackgroundResource(R.drawable.repair_bg);
            repair_already.setTextColor(ContextCompat.getColor(RepairActivity.this, R.color.color_22b2e7));

            repair_waitfor.setTextColor(ContextCompat.getColor(RepairActivity.this, R.color.color_373a41));
            repair_waitfor.setBackgroundResource(R.color.color_ffffff);

            selectFlag = 2;
            refreshFlaf = true;
            start = 0;

            okHttpManager.getMethod(false, url + "start=" + start + "&limit=" + limit + "&status=" + selectFlag, "已维修列表", handler, 1);//请求待维修

        } else if (id == repair_waitfor.getId()) {//待维修
            mList.clear();
            repairAdapter.notifyDataSetChanged();

            BallProgressUtils.showLoading(RepairActivity.this, mAll);

            repair_already.setBackgroundResource(R.color.color_ffffff);
            repair_already.setTextColor(ContextCompat.getColor(RepairActivity.this, R.color.color_373a41));

            repair_waitfor.setTextColor(ContextCompat.getColor(RepairActivity.this, R.color.color_22b2e7));
            repair_waitfor.setBackgroundResource(R.drawable.repair_bg);

            selectFlag = 1;
            refreshFlaf = true;
            start = 0;

            okHttpManager.getMethod(false, url + "start=" + start + "&limit=" + limit + "&status=" + selectFlag, "待维修列表", handler, 1);//请求待维修

        }else if (id==mNoData_rl.getId()){
            BallProgressUtils.showLoading(RepairActivity.this, mAll);
            refreshFlaf = true;
            start = 0;
            okHttpManager.getMethod(false, url + "start=" + start + "&limit=" + limit + "&status=" + selectFlag, "维修列表", handler, 1);//请求维修
        }
    }

    class RepairAdapter extends BaseAdapter {

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
            RepairHolder repairHolder = null;
            if (view == null) {
                repairHolder = new RepairHolder();
                view = LayoutInflater.from(RepairActivity.this).inflate(R.layout.order_listview_item, null);

                repairHolder.headImg = view.findViewById(R.id.order_head_img);
                repairHolder.person = view.findViewById(R.id.order_person);
                repairHolder.name = view.findViewById(R.id.order_name);
                repairHolder.date = view.findViewById(R.id.order_date);
                repairHolder.status = view.findViewById(R.id.order_status);
                view.setTag(repairHolder);
            } else {
                repairHolder = (RepairHolder) view.getTag();
            }

            // Picasso.with(PurchaseOrderActivity.this).load(URLTools.urlBase + mList.get(i).).error(R.mipmap).into(orderHolder.headImg);
            Picasso.with(RepairActivity.this).load(R.mipmap.head).into(repairHolder.headImg);
            repairHolder.person.setText(mList.get(i).getDepartmentName() + "  " + mList.get(i).getUserName());
            repairHolder.name.setText(mList.get(i).getAssetName() + "");
            repairHolder.date.setText(mList.get(i).getRepairDateString() + "");
            if (selectFlag == 1) {
                repairHolder.status.setText("待维修");
                repairHolder.status.setTextColor(ContextCompat.getColor(RepairActivity.this, R.color.color_dc8268));
            }
            if (selectFlag == 2) {
                repairHolder.status.setText("已维修");
                repairHolder.status.setTextColor(ContextCompat.getColor(RepairActivity.this, R.color.color_two));
            }


            return view;
        }

        class RepairHolder {
            CircleImg headImg;
            TextView person, name, date, status;
        }
    }
}
