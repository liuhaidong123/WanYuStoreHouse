package com.storehouse.wanyu.activity.PropertyManage;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.storehouse.wanyu.Bean.PropertyManageRoot;
import com.storehouse.wanyu.Bean.PropertyManageRows;
import com.storehouse.wanyu.IPAddress.URLTools;
import com.storehouse.wanyu.OkHttpUtils.OkHttpManager;
import com.storehouse.wanyu.R;

import java.util.ArrayList;
import java.util.List;

//资产管理
public class PropertyManageActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView mBack, mSearch_btn;
    private EditText mEdit;
    private TextView mAdd_btn;
    private SmartRefreshLayout mSmartRefreshLayout;
    private ListView mListView;
    private MyAdapter myAdapter;
    private View mHead;
    private String property_list_url, search_url;
    private int start = 0, limit = 20;
    private boolean flag = true;//true表示刷新，false表示加载更多
    private OkHttpManager okHttpManager;
    private List<PropertyManageRows> propertyList = new ArrayList();
    private Gson gson = new Gson();
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                String s = (String) msg.obj;
                Object o = gson.fromJson(s, PropertyManageRoot.class);
                if (o != null && o instanceof PropertyManageRoot) {
                    PropertyManageRoot propertyManageRoot = (PropertyManageRoot) o;
                    if (propertyManageRoot != null && propertyManageRoot.getRows() != null) {
                        if (propertyManageRoot.getRows().size()==0){
                            Toast.makeText(PropertyManageActivity.this, "抱歉，没有查到数据哦", Toast.LENGTH_SHORT).show();

                        }
                        if (flag) {
                            propertyList = propertyManageRoot.getRows();
                            myAdapter.notifyDataSetChanged();
                        } else {
                            for (int i = 0; i < propertyManageRoot.getRows().size(); i++) {
                                propertyList.add(propertyManageRoot.getRows().get(i));
                            }
                            myAdapter.notifyDataSetChanged();
                        }


                    } else {
                        Toast.makeText(PropertyManageActivity.this, "请求资产列表错误,请检查服务器", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(PropertyManageActivity.this, "请求资产列表错误", Toast.LENGTH_SHORT).show();
                }
            } else if (msg.what == 1010) {
                Toast.makeText(PropertyManageActivity.this, "数据错误，请检查后台服务器", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_manage);
        initUI();
    }


    private void initUI() {
        //请求资产列表
        property_list_url = URLTools.urlBase + URLTools.property_list + "start=" + start + "&limit=" + limit;
        okHttpManager = OkHttpManager.getInstance();
        okHttpManager.getMethod(false, property_list_url, "请求资产列表", mHandler, 1);
        mBack = (ImageView) findViewById(R.id.back_img);
        mBack.setOnClickListener(this);
        //搜索
        mSearch_btn = (ImageView) findViewById(R.id.property_search_btn);
        mSearch_btn.setOnClickListener(this);
        //输入框
        mEdit = (EditText) findViewById(R.id.input_edit);
        mEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                                            @Override
                                            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                                                if (!"".equals(mEdit.getText().toString().trim())) {
                                                    if (i == EditorInfo.IME_ACTION_SEND || (keyEvent != null && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                                                        //按下的时候会会执行：手指按下和手指松开俩个过程
                                                        if (keyEvent.getAction() == KeyEvent.ACTION_UP) {
                                                            search_url = URLTools.urlBase + URLTools.property_list + "name=" + mEdit.getText().toString().trim();
                                                            flag = true;
                                                            okHttpManager.getMethod(false, search_url, "请求搜索资产列表", mHandler, 1);
                                                        }

                                                        return true;
                                                    }
                                                } else {
                                                    Toast.makeText(PropertyManageActivity.this, "请输入搜索内容", Toast.LENGTH_SHORT).show();
                                                }

                                                return false;
                                            }


                                        }

        );
        //增加
        mAdd_btn = (TextView) findViewById(R.id.property_add_btn);
        mAdd_btn.setOnClickListener(this);
        //刷新
        mSmartRefreshLayout = (SmartRefreshLayout) findViewById(R.id.property_refresh);
        mListView = (ListView) findViewById(R.id.property_listview);
        myAdapter = new MyAdapter();
        mHead = LayoutInflater.from(this).inflate(R.layout.property_head, null);
        mListView.addHeaderView(mHead);
        mListView.setAdapter(myAdapter);
        mSmartRefreshLayout.setRefreshHeader(new CircleHeader(this));
        mSmartRefreshLayout.setRefreshFooter(new BallPulseFooter(this).setSpinnerStyle(SpinnerStyle.Scale).setAnimatingColor(ContextCompat.getColor(this, R.color.color_1c82d4)));
        mSmartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
        @Override
        public void onRefresh(RefreshLayout refreshlayout) {
        refreshlayout.finishRefresh(2000/*,false*/);//传入false表示刷新失败
        flag = true;
        start = 0;
        mEdit.setText("");
        property_list_url = URLTools.urlBase + URLTools.property_list + "start=" + start + "&limit=" + limit;
            okHttpManager.getMethod(false, property_list_url, "请求资产列表", mHandler, 1);
          }
          }
        );
        mSmartRefreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
                                                              @Override
                                                              public void onLoadmore(RefreshLayout refreshlayout) {
          refreshlayout.finishLoadmore(2000/*,false*/);//传入false表示加载失败
           flag = false;
              start += 20;
               mEdit.setText("");
                property_list_url = URLTools.urlBase + URLTools.property_list + "start=" + start + "&limit=" + limit;
                 okHttpManager.getMethod(false, property_list_url, "请求资产列表", mHandler, 1);
                 }
                              }

        );

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1&&resultCode==RESULT_OK){
            flag = true;
            start = 0;
            property_list_url = URLTools.urlBase + URLTools.property_list + "start=" + start + "&limit=" + limit;
            okHttpManager.getMethod(false, property_list_url, "请求资产列表", mHandler, 1);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == mBack.getId()) {
            finish();
        } else if (id == mAdd_btn.getId()) {
            Intent intent5 = new Intent(this, AddPropertyActivity.class);
            startActivityForResult(intent5,1);
            //startActivity(intent5);
        } else if (id == mSearch_btn.getId()) {
            if (!"".equals(mEdit.getText().toString().trim())) {
                search_url = URLTools.urlBase + URLTools.property_list + "name=" + mEdit.getText().toString().trim();
                flag = true;
                okHttpManager.getMethod(false, search_url, "请求搜索资产列表", mHandler, 1);
            } else {
                Toast.makeText(PropertyManageActivity.this, "请输入搜索内容", Toast.LENGTH_SHORT).show();

            }

        }
    }


    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return propertyList.size();
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
            if (view == null) {
                myHolder = new MyHolder();
                view = LayoutInflater.from(PropertyManageActivity.this).inflate(R.layout.property_item, null);
                myHolder.number = view.findViewById(R.id.select_state);
                myHolder.mbianhao = view.findViewById(R.id.bianhao_tv);
                myHolder.name = view.findViewById(R.id.name_tv);
                myHolder.manager = view.findViewById(R.id.manager_tv);
                myHolder.location = view.findViewById(R.id.location_tv);
                view.setTag(myHolder);
            } else {
                myHolder = (MyHolder) view.getTag();
            }

            myHolder.number.setText(i + 1 + "");
            myHolder.mbianhao.setText(propertyList.get(i).getId() + "");
            myHolder.name.setText(propertyList.get(i).getAssetName() + "");
            myHolder.manager.setText(propertyList.get(i).getSaveUserName() + "");
            myHolder.location.setText(propertyList.get(i).getAddressName() + "");

            return view;
        }

        class MyHolder {
            TextView number, mbianhao, name, manager, location;
        }
    }
}
