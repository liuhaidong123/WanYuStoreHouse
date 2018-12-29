package com.storehouse.wanyu.activity.PurchaseOrder;

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
import com.storehouse.wanyu.Bean.PurchaseOrderRoot;
import com.storehouse.wanyu.Bean.PurchaseOrderRows;
import com.storehouse.wanyu.IPAddress.URLTools;
import com.storehouse.wanyu.MyUtils.BallProgressUtils;
import com.storehouse.wanyu.MyUtils.CircleImg;
import com.storehouse.wanyu.OkHttpUtils.OkHttpManager;
import com.storehouse.wanyu.R;

import java.util.ArrayList;
import java.util.List;

//采购订单页面
public class PurchaseOrderActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView mBack;
    private TextView order_one_tv, order_two_tv, order_three_tv, order_four_tv;
    private SmartRefreshLayout smartRefreshLayout;
    private ListView orderListview;
    private OrderAdapter orderAdapter;
    private Gson gson = new Gson();
    private OkHttpManager okHttpManager;
    private int selectFlag = 1;
    private boolean refreshFlaf = true;
    private RelativeLayout mAll, mNoData_rl;
    private String url;
    private List<PurchaseOrderRows> mList = new ArrayList<>();
    private int start = 0, limit = 20;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                BallProgressUtils.dismisLoading();
                String mes = (String) msg.obj;
                Object o = gson.fromJson(mes, PurchaseOrderRoot.class);
                if (o != null && o instanceof PurchaseOrderRoot) {
                    PurchaseOrderRoot purchaseOrderRoot = (PurchaseOrderRoot) o;
                    if (purchaseOrderRoot != null && "0".equals(purchaseOrderRoot.getCode())) {
                        if (purchaseOrderRoot.getRows() != null) {
                            if (refreshFlaf) {
                                mList = purchaseOrderRoot.getRows();
                                if (purchaseOrderRoot.getRows().size() == 0) {
                                    mNoData_rl.setVisibility(View.VISIBLE);
                                } else {
                                    mNoData_rl.setVisibility(View.GONE);
                                }
                            } else {
                                for (int i = 0; i < purchaseOrderRoot.getRows().size(); i++) {
                                    mList.add(purchaseOrderRoot.getRows().get(i));
                                }
                                if (purchaseOrderRoot.getRows().size() == 0) {
                                    Toast.makeText(PurchaseOrderActivity.this, "全部加载完毕", Toast.LENGTH_SHORT).show();
                                }
                            }

                            orderAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(PurchaseOrderActivity.this, "请求订单数据错误", Toast.LENGTH_SHORT).show();
                        }

                    } else if (purchaseOrderRoot != null && "-1".equals(purchaseOrderRoot.getCode())) {
                        Toast.makeText(PurchaseOrderActivity.this, "登录过期,请重新登录", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(PurchaseOrderActivity.this, "请求数据数据错误", Toast.LENGTH_SHORT).show();

                    }

                } else {
                    BallProgressUtils.dismisLoading();
                    Toast.makeText(PurchaseOrderActivity.this, "连接服务器失败,请重新尝试", Toast.LENGTH_SHORT).show();
                }


            } else {
                BallProgressUtils.dismisLoading();
                Toast.makeText(PurchaseOrderActivity.this, "连接服务器失败,请重新尝试", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_order);
        mAll = (RelativeLayout) findViewById(R.id.activity_purchase_order);
        mNoData_rl = (RelativeLayout) findViewById(R.id.no_data_rl);
        initUI();
    }

    //初始化数据
    private void initUI() {
        mBack = (ImageView) findViewById(R.id.back_img);
        mBack.setOnClickListener(this);
        url = URLTools.urlBase + URLTools.purchase_order_list;//采购订单接口
        okHttpManager = OkHttpManager.getInstance();
        okHttpManager.getMethod(false, url + "status=" + 1 + "&start=" + start + "&limit=" + limit, "待采购接口", handler, 1);//待采购接口

        order_one_tv = (TextView) findViewById(R.id.order_one);
        order_two_tv = (TextView) findViewById(R.id.order_two);
        order_three_tv = (TextView) findViewById(R.id.order_three);
        order_four_tv = (TextView) findViewById(R.id.order_four);
        order_one_tv.setOnClickListener(this);
        order_two_tv.setOnClickListener(this);
        order_three_tv.setOnClickListener(this);
        order_four_tv.setOnClickListener(this);


        smartRefreshLayout = (SmartRefreshLayout) findViewById(R.id.order_refresh);
        smartRefreshLayout.setRefreshHeader(new CircleHeader(this));
        smartRefreshLayout.setRefreshFooter(new BallPulseFooter(this).setSpinnerStyle(SpinnerStyle.Scale).setAnimatingColor(ContextCompat.getColor(this, R.color.color_1c82d4)));
        smartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshlayout.finishRefresh(2000);
                refreshFlaf = true;
                start = 0;
                okHttpManager.getMethod(false, url + "status=" + selectFlag + "&start=" + start + "&limit=" + limit, "刷新订单接口", handler, 1);//待采购接口

            }
        });
        smartRefreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                refreshlayout.finishLoadmore(2000);
                refreshFlaf = false;
                start += 20;
                okHttpManager.getMethod(false, url + "status=" + selectFlag + "&start=" + start + "&limit=" + limit, "加载订单接口", handler, 1);//待采购接口

            }
        });

        orderListview = (ListView) findViewById(R.id.order_listview);
        orderAdapter = new OrderAdapter();
        orderListview.setAdapter(orderAdapter);
        //跳转各种详情
        orderListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(PurchaseOrderActivity.this, PurchaseOrderMsgActivity.class);
                intent.putExtra("flag", selectFlag);
                intent.putExtra("id", mList.get(i).getId());
                if (selectFlag == 1) {//待采购跳过去以后，如果点击采购了以后，跳回来后要刷新页面
                    startActivityForResult(intent, 2);
                } else {
                    startActivity(intent);
                }

            }
        });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == mBack.getId()) {
            finish();
        } else if (id == order_one_tv.getId()) {//待采购
            mList.clear();
            orderAdapter.notifyDataSetChanged();
            selectFlag = 1;
            refreshFlaf = true;
            start = 0;
            BallProgressUtils.showLoading(PurchaseOrderActivity.this, mAll);
            changeBgColor(1);
            okHttpManager.getMethod(false, url + "status=" + selectFlag + "&start=" + start + "&limit=" + limit, "待采购接口", handler, 1);//待采购接口
        } else if (id == order_two_tv.getId()) {//采购中
            mList.clear();
            orderAdapter.notifyDataSetChanged();
            selectFlag = 2;
            refreshFlaf = true;
            start = 0;
            BallProgressUtils.showLoading(PurchaseOrderActivity.this, mAll);
            changeBgColor(2);
            okHttpManager.getMethod(false, url + "status=" + selectFlag + "&start=" + start + "&limit=" + limit, "采购中接口", handler, 1);//待采购接口
        } else if (id == order_three_tv.getId()) {//已入库
            mList.clear();
            orderAdapter.notifyDataSetChanged();
            selectFlag = 3;
            refreshFlaf = true;
            start = 0;
            BallProgressUtils.showLoading(PurchaseOrderActivity.this, mAll);
            changeBgColor(3);
            okHttpManager.getMethod(false, url + "status=" + selectFlag + "&start=" + start + "&limit=" + limit, "已入库接口", handler, 1);//待采购接口
        } else if (id == order_four_tv.getId()) {//已退货
            mList.clear();
            orderAdapter.notifyDataSetChanged();
            selectFlag = 4;
            refreshFlaf = true;
            start = 0;
            BallProgressUtils.showLoading(PurchaseOrderActivity.this, mAll);
            changeBgColor(4);
            okHttpManager.getMethod(false, url + "status=" + selectFlag + "&start=" + start + "&limit=" + limit, "已退货接口", handler, 1);//待采购接口
        }
    }

    private void changeBgColor(int flag) {
        if (flag == 1) {
            order_one_tv.setBackgroundResource(R.color.color_30a2d4);
            order_one_tv.setTextColor(ContextCompat.getColor(PurchaseOrderActivity.this, R.color.color_ffffff));
            order_two_tv.setBackgroundResource(R.color.color_ffffff);
            order_two_tv.setTextColor(ContextCompat.getColor(PurchaseOrderActivity.this, R.color.color_373a41));
            order_three_tv.setBackgroundResource(R.color.color_ffffff);
            order_three_tv.setTextColor(ContextCompat.getColor(PurchaseOrderActivity.this, R.color.color_373a41));
            order_four_tv.setBackgroundResource(R.color.color_ffffff);
            order_four_tv.setTextColor(ContextCompat.getColor(PurchaseOrderActivity.this, R.color.color_373a41));

        } else if (flag == 2) {
            order_two_tv.setBackgroundResource(R.color.color_30a2d4);
            order_two_tv.setTextColor(ContextCompat.getColor(PurchaseOrderActivity.this, R.color.color_ffffff));
            order_one_tv.setBackgroundResource(R.color.color_ffffff);
            order_one_tv.setTextColor(ContextCompat.getColor(PurchaseOrderActivity.this, R.color.color_373a41));
            order_three_tv.setBackgroundResource(R.color.color_ffffff);
            order_three_tv.setTextColor(ContextCompat.getColor(PurchaseOrderActivity.this, R.color.color_373a41));
            order_four_tv.setBackgroundResource(R.color.color_ffffff);
            order_four_tv.setTextColor(ContextCompat.getColor(PurchaseOrderActivity.this, R.color.color_373a41));
        } else if (flag == 3) {
            order_three_tv.setBackgroundResource(R.color.color_30a2d4);
            order_three_tv.setTextColor(ContextCompat.getColor(PurchaseOrderActivity.this, R.color.color_ffffff));
            order_one_tv.setBackgroundResource(R.color.color_ffffff);
            order_one_tv.setTextColor(ContextCompat.getColor(PurchaseOrderActivity.this, R.color.color_373a41));
            order_two_tv.setBackgroundResource(R.color.color_ffffff);
            order_two_tv.setTextColor(ContextCompat.getColor(PurchaseOrderActivity.this, R.color.color_373a41));
            order_four_tv.setBackgroundResource(R.color.color_ffffff);
            order_four_tv.setTextColor(ContextCompat.getColor(PurchaseOrderActivity.this, R.color.color_373a41));
        } else {
            order_four_tv.setBackgroundResource(R.color.color_30a2d4);
            order_four_tv.setTextColor(ContextCompat.getColor(PurchaseOrderActivity.this, R.color.color_ffffff));
            order_one_tv.setBackgroundResource(R.color.color_ffffff);
            order_one_tv.setTextColor(ContextCompat.getColor(PurchaseOrderActivity.this, R.color.color_373a41));
            order_two_tv.setBackgroundResource(R.color.color_ffffff);
            order_two_tv.setTextColor(ContextCompat.getColor(PurchaseOrderActivity.this, R.color.color_373a41));
            order_three_tv.setBackgroundResource(R.color.color_ffffff);
            order_three_tv.setTextColor(ContextCompat.getColor(PurchaseOrderActivity.this, R.color.color_373a41));
        }
    }

    class OrderAdapter extends BaseAdapter {

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
            OrderHolder orderHolder = null;
            if (view == null) {
                orderHolder = new OrderHolder();
                view = LayoutInflater.from(PurchaseOrderActivity.this).inflate(R.layout.order_listview_item, null);

                orderHolder.headImg = view.findViewById(R.id.order_head_img);
                orderHolder.person = view.findViewById(R.id.order_person);
                orderHolder.name = view.findViewById(R.id.order_name);
                orderHolder.date = view.findViewById(R.id.order_date);
                orderHolder.status = view.findViewById(R.id.order_status);
                view.setTag(orderHolder);
            } else {
                orderHolder = (OrderHolder) view.getTag();
            }

            // Picasso.with(PurchaseOrderActivity.this).load(URLTools.urlBase + mList.get(i).).error(R.mipmap).into(orderHolder.headImg);
            Picasso.with(PurchaseOrderActivity.this).load(R.mipmap.head).into(orderHolder.headImg);
            orderHolder.person.setText(mList.get(i).getDepartmentName() + "  " + mList.get(i).getApplyUserName());
            orderHolder.name.setText(mList.get(i).getAssetName() + "");
            orderHolder.date.setText(mList.get(i).getApplyTimeString() + "");
            if (selectFlag == 1) {
                orderHolder.status.setText("待采购");
                orderHolder.status.setTextColor(ContextCompat.getColor(PurchaseOrderActivity.this, R.color.color_dc8268));
            } else if (selectFlag == 2) {
                orderHolder.status.setText("采购中");
                orderHolder.status.setTextColor(ContextCompat.getColor(PurchaseOrderActivity.this, R.color.color_dc8268));
            } else if (selectFlag == 3) {
                orderHolder.status.setText("已入库");
                orderHolder.status.setTextColor(ContextCompat.getColor(PurchaseOrderActivity.this, R.color.color_two));
            } else {
                orderHolder.status.setText("已退货");
                orderHolder.status.setTextColor(ContextCompat.getColor(PurchaseOrderActivity.this, R.color.red));
            }

            return view;
        }

        class OrderHolder {
            CircleImg headImg;
            TextView person, name, date, status;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //点击详情中开始采购按钮后，这个页面中的代采购列表需要刷新
        if (requestCode==2&&resultCode==RESULT_OK){
            refreshFlaf = true;
            start = 0;
            okHttpManager.getMethod(false, url + "status=" + selectFlag + "&start=" + start + "&limit=" + limit, "刷新订单接口", handler, 1);//待采购接口
        }
    }
}
