package com.storehouse.wanyu.activity.SetActivity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.scwang.smartrefresh.header.CircleHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.storehouse.wanyu.Bean.NotifyRoot;
import com.storehouse.wanyu.Bean.NotifyRows;
import com.storehouse.wanyu.IPAddress.URLTools;
import com.storehouse.wanyu.OkHttpUtils.OkHttpManager;
import com.storehouse.wanyu.R;
import com.storehouse.wanyu.activity.NotifyActivity.NotifyMessageActivity;
import com.storehouse.wanyu.adapter.FirstPageListViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class MyMessageActivity extends AppCompatActivity {
    private ImageView mBack_img;
    private RelativeLayout mNodata_rl;
    private SmartRefreshLayout mSmartRefreshLayout;
    private ListView mListView;
    private List<NotifyRows> mNotifyList = new ArrayList<>();
    private FirstPageListViewAdapter firstPageListViewAdapter;
    private int start = 0, limit = 20;
    private String notifyURL;
    private Gson mGson = new Gson();
    private boolean notifyFlag = true;//true表示刷新，false表示加载更多
    private OkHttpManager mOkHttpManager;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1010) {
                Toast.makeText(MyMessageActivity.this, "连接服务器失败，请重新尝试", Toast.LENGTH_SHORT).show();
            } else if (msg.what == 12) {//请求通知列表接口
                String s = (String) msg.obj;
                Object o = mGson.fromJson(s, NotifyRoot.class);
                if (o != null && o instanceof NotifyRoot) {
                    NotifyRoot notifyRoot = (NotifyRoot) o;
                    if ("0".equals(notifyRoot.getCode())) {
                        if (notifyRoot.getRows() != null) {
                            mNodata_rl.setVisibility(View.GONE);
                            if (notifyFlag) {//刷新
                                mNotifyList = notifyRoot.getRows();
                                if (mNotifyList.size() == 0) {
                                    mNodata_rl.setVisibility(View.VISIBLE);
                                } else {
                                    mNodata_rl.setVisibility(View.GONE);
                                }

                            } else {//加载更多

                                for (int i = 0; i < notifyRoot.getRows().size(); i++) {
                                    mNotifyList.add(notifyRoot.getRows().get(i));
                                }
                            }
                            firstPageListViewAdapter.setList(mNotifyList);
                            firstPageListViewAdapter.notifyDataSetChanged();

                        } else {
                            Toast.makeText(MyMessageActivity.this, "获取通知列表失败", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(MyMessageActivity.this, "登录过期，请重新登录", Toast.LENGTH_SHORT).show();
                    }


                } else {
                    Toast.makeText(MyMessageActivity.this, "通知数据错误", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_message);
        //通知列表数据
        mBack_img = (ImageView) findViewById(R.id.back_img);
        mBack_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mOkHttpManager=OkHttpManager.getInstance();
        notifyURL = URLTools.urlBase + URLTools.query_notify_list + "start=" + start + "&limit=" + limit;
        mNodata_rl= (RelativeLayout) findViewById(R.id.no_data_rl);
        mNodata_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOkHttpManager.getMethod(false, notifyURL, "通知列表接口", mHandler, 12);
            }
        });
        mOkHttpManager.getMethod(false, notifyURL, "通知列表接口", mHandler, 12);
        mSmartRefreshLayout = (SmartRefreshLayout) findViewById(R.id.mess_refresh);
        mListView = (ListView) findViewById(R.id.mess_listview);
        firstPageListViewAdapter = new FirstPageListViewAdapter(MyMessageActivity.this, mNotifyList);
        mListView.setAdapter(firstPageListViewAdapter);
        mSmartRefreshLayout.setRefreshHeader(new CircleHeader(MyMessageActivity.this));
        mSmartRefreshLayout.setRefreshFooter(new BallPulseFooter(MyMessageActivity.this).setSpinnerStyle(SpinnerStyle.Scale).setAnimatingColor(ContextCompat.getColor(MyMessageActivity.this, R.color.color_1c82d4)));


        mSmartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshlayout.finishRefresh(2000/*,false*/);//传入false表示刷新失败
                notifyFlag = true;
                start = 0;
                notifyURL = URLTools.urlBase + URLTools.query_notify_list + "start=" + start + "&limit=" + limit;
                mOkHttpManager.getMethod(false, notifyURL, "通知列表接口", mHandler, 12);
            }
        });
        mSmartRefreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                refreshlayout.finishLoadmore(2000/*,false*/);//传入false表示加载失败
                notifyFlag = false;
                start += 20;
                notifyURL = URLTools.urlBase + URLTools.query_notify_list + "start=" + start + "&limit=" + limit;
                mOkHttpManager.getMethod(false, notifyURL, "通知列表接口", mHandler, 12);
            }
        });
        //跳转通知详情
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MyMessageActivity.this, NotifyMessageActivity.class);
                intent.putExtra("title", mNotifyList.get(i).getTitle());
                intent.putExtra("content", mNotifyList.get(i).getContent());
                intent.putExtra("date", mNotifyList.get(i).getCreateTimeString());
                intent.putExtra("id", mNotifyList.get(i).getId());
                intent.putExtra("read", mNotifyList.get(i).isRead());
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1) {//发布通知以后刷新通知列表
            notifyFlag = true;
            start = 0;
            notifyURL = URLTools.urlBase + URLTools.query_notify_list + "start=" + start + "&limit=" + limit;
            mOkHttpManager.getMethod(false, notifyURL, "通知列表接口", mHandler, 12);
        }
    }
}
