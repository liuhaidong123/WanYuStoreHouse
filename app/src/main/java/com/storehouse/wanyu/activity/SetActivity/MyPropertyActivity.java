package com.storehouse.wanyu.activity.SetActivity;

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
import com.storehouse.wanyu.Bean.JieYongDetailsListBean;
import com.storehouse.wanyu.Bean.OneSelfRoot;
import com.storehouse.wanyu.Bean.OneSelfRows;
import com.storehouse.wanyu.IPAddress.URLTools;
import com.storehouse.wanyu.MyUtils.BallProgressUtils;
import com.storehouse.wanyu.OkHttpUtils.OkHttpManager;
import com.storehouse.wanyu.R;
import com.storehouse.wanyu.activity.AllSPZDetailsActivity.SPZJieYongApplyDetailsActivity;
import com.storehouse.wanyu.activity.ApplyActivity.ApplyBaoFeiActivity;
import com.storehouse.wanyu.activity.PropertyManage.PropertyManageActivity;
import com.storehouse.wanyu.activity.PropertyManage.PropertyMessageActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyPropertyActivity extends AppCompatActivity {

    private ImageView mback_img;
    private SmartRefreshLayout smartRefreshLayout;
    private ListView listView;
    private MyAdapter myAdapter;
    private int start = 0, limit = 30;
    private boolean refresh = true;
    private String url;
    private RelativeLayout mNoData_rl;
    private TextView no_mess_tv;
    private Gson gson = new Gson();
    private OkHttpManager okHttpManager;
    private List<OneSelfRows> mList = new ArrayList<>();
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            BallProgressUtils.dismisLoading();
            if (msg.what == 1) {

                try{
                    String mes = (String) msg.obj;
                    Object o = gson.fromJson(mes, OneSelfRoot.class);
                    if (o != null && o instanceof OneSelfRoot) {
                        OneSelfRoot oneSelfRoot = (OneSelfRoot) o;

                        if (oneSelfRoot != null && "0".equals(oneSelfRoot.getCode())) {
                            if (oneSelfRoot.getRows() != null) {

                                if (refresh) {
                                    mList = oneSelfRoot.getRows();
                                    if (oneSelfRoot.getRows().size() == 0) {
                                        mNoData_rl.setVisibility(View.VISIBLE);
                                        no_mess_tv.setText("空空如也");
                                        Toast.makeText(MyPropertyActivity.this, "暂无资产", Toast.LENGTH_SHORT).show();
                                    }else {
                                        mNoData_rl.setVisibility(View.GONE);
                                        no_mess_tv.setText("");
                                    }
                                } else {
                                    for (int i = 0; i < oneSelfRoot.getRows().size(); i++) {
                                        mList.add(oneSelfRoot.getRows().get(i));
                                    }
                                    if (oneSelfRoot.getRows().size() == 0) {
                                        Toast.makeText(MyPropertyActivity.this, "已加载全部资产", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                myAdapter.notifyDataSetChanged();
                            }


                        } else {
                            Toast.makeText(MyPropertyActivity.this, "登录过期，请重新登录", Toast.LENGTH_SHORT).show();
                            mNoData_rl.setVisibility(View.VISIBLE);
                            no_mess_tv.setText("登录过期，请重新登录");
                        }


                    } else {
                        Toast.makeText(MyPropertyActivity.this, "获取个人资产列表数据错误", Toast.LENGTH_SHORT).show();

                    }


                }catch (Exception e){
                    mNoData_rl.setVisibility(View.VISIBLE);
                    no_mess_tv.setText("数据解析错误");
                    Toast.makeText(MyPropertyActivity.this, "数据解析错误,请重新尝试", Toast.LENGTH_SHORT).show();
                }


            } else if (msg.what == 1010) {
                mNoData_rl.setVisibility(View.VISIBLE);
                no_mess_tv.setText("连接服务器失败,请检查网络");
                Toast.makeText(MyPropertyActivity.this, "连接服务器失败,请检查网络", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_property);

        mNoData_rl = (RelativeLayout) findViewById(R.id.no_data_rl);
        mNoData_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BallProgressUtils.showLoading(MyPropertyActivity.this,mNoData_rl);
                refresh = true;
                start = 0;
                Map<Object, Object> map = new HashMap<>();
                map.put("name", "");
                map.put("start", start);
                map.put("limit", limit);
                okHttpManager.postMethod(false, url, "刷新我的资产", map, handler, 1);
            }
        });
        no_mess_tv= (TextView) findViewById(R.id.no_mess_tv);
        okHttpManager = OkHttpManager.getInstance();
        url = URLTools.urlBase + URLTools.query_oneself_property_list;
        Map<Object, Object> map = new HashMap<>();
        map.put("name", "");
        map.put("start", start);
        map.put("limit", limit);
        okHttpManager.postMethod(false, url, "我的资产", map, handler, 1);


        mback_img = (ImageView) findViewById(R.id.back_img);
        mback_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        smartRefreshLayout = (SmartRefreshLayout) findViewById(R.id.my_SmartRefreshLayout);
        smartRefreshLayout.setRefreshHeader(new CircleHeader(this));
        smartRefreshLayout.setRefreshFooter(new BallPulseFooter(this).setSpinnerStyle(SpinnerStyle.Scale).setAnimatingColor(ContextCompat.getColor(this, R.color.color_1c82d4)));
        smartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshlayout.finishRefresh(2000);
                refresh = true;
                start = 0;
                Map<Object, Object> map = new HashMap<>();
                map.put("name", "");
                map.put("start", start);
                map.put("limit", limit);
                okHttpManager.postMethod(false, url, "刷新我的资产", map, handler, 1);

            }
        });
        smartRefreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                refreshlayout.finishLoadmore(2000);
                refresh = false;
                start += 20;
                Map<Object, Object> map = new HashMap<>();
                map.put("name", "");
                map.put("start", start);
                map.put("limit", limit);
                okHttpManager.postMethod(false, url, "加载我的资产", map, handler, 1);

            }
        });

        listView = (ListView) findViewById(R.id.my_listview);
        View header = LayoutInflater.from(this).inflate(R.layout.add_item_table_head, null);
        listView.addHeaderView(header);
        myAdapter = new MyAdapter();
        listView.setAdapter(myAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i!=0){
                    Intent intent = new Intent(MyPropertyActivity.this, PropertyMessageActivity.class);
                    intent.putExtra("assetID", mList.get(i-1).getId());
                    startActivity(intent);
                }

            }
        });
    }


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
            MyHolder jyHolder = null;
            if (view == null) {
                view = LayoutInflater.from(MyPropertyActivity.this).inflate(R.layout.my_property_item, null);
                jyHolder = new MyHolder();
                jyHolder.status = view.findViewById(R.id.select_state);
                jyHolder.num = view.findViewById(R.id.num_tv);
                jyHolder.leibie = view.findViewById(R.id.leibie_tv);
                jyHolder.mingcheng = view.findViewById(R.id.mingcheng_tv);
                view.setTag(jyHolder);
            } else {
                jyHolder = (MyHolder) view.getTag();

            }
            //设置数据
            jyHolder.status.setBackgroundResource(R.drawable.bumen_box_select);
            jyHolder.num.setText(mList.get(i).getNum() + "");
            jyHolder.leibie.setText(mList.get(i).getCategoryName() + "");
            jyHolder.mingcheng.setText(mList.get(i).getAssetName() + "");
            return view;
        }


        class MyHolder {
            TextView status, num, leibie, mingcheng;
        }
    }
}
