package com.storehouse.wanyu.activity.PanDian;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
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
import com.storehouse.wanyu.Bean.PanDianHuiZongRoot;
import com.storehouse.wanyu.Bean.PanDianHuiZongRows;
import com.storehouse.wanyu.IPAddress.URLTools;
import com.storehouse.wanyu.MyUtils.CircleImg;
import com.storehouse.wanyu.OkHttpUtils.OkHttpManager;
import com.storehouse.wanyu.R;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
//盘点汇总

/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends Fragment {
    private RelativeLayout mNodata_rl;
    private ImageView mback;
    private SmartRefreshLayout mSmartRefreshLayout;
    private boolean moreOrRefrush = true;//true表示是刷新，false表示加载更多
    private int start = 0, limit = 20;
    private ListView mListView;
    private PDHZAdapter pdhzAdapter;
    private List<PanDianHuiZongRows> mList = new ArrayList<>();
    private OkHttpManager okHttpManager;
    private Gson gson = new Gson();
    private String url;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 6) {

                String mes = (String) msg.obj;
                Object o = gson.fromJson(mes, PanDianHuiZongRoot.class);
                if (o != null && o instanceof PanDianHuiZongRoot) {
                    PanDianHuiZongRoot panDianHuiZongRoot = (PanDianHuiZongRoot) o;
                    if (panDianHuiZongRoot != null && "0".equals(panDianHuiZongRoot.getCode())) {
                        if (panDianHuiZongRoot.getRows() != null) {
                            mNodata_rl.setVisibility(View.GONE);
                            if (moreOrRefrush) {//刷新
                                mList = panDianHuiZongRoot.getRows();
                                if (mList.size() == 0) {
                                    mNodata_rl.setVisibility(View.VISIBLE);
                                }

                            } else {//加载更多
                                for (int i = 0; i < panDianHuiZongRoot.getRows().size(); i++) {
                                    mList.add(panDianHuiZongRoot.getRows().get(i));
                                }
                            }

                            pdhzAdapter.notifyDataSetChanged();
                        }
                    } else {
                        Toast.makeText(getActivity(), "登录过期，请重新登录", Toast.LENGTH_SHORT).show();

                    }

                } else {
                    Toast.makeText(getActivity(), "获取汇总列表错误", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), "获取汇总列表失败", Toast.LENGTH_SHORT).show();
            }
        }
    };

    public HistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        initUI(view);
        return view;
    }

    private void initUI(View view1) {
        mNodata_rl = view1.findViewById(R.id.nodata_rl);
        mback = view1.findViewById(R.id.back_img);
        mback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
        mSmartRefreshLayout = view1.findViewById(R.id.smart_refresh);
        mSmartRefreshLayout.setRefreshHeader(new CircleHeader(getContext()));
        mSmartRefreshLayout.setRefreshFooter(new BallPulseFooter(getContext()).setSpinnerStyle(SpinnerStyle.Scale).setAnimatingColor(ContextCompat.getColor(getContext(), R.color.color_1c82d4)));


        mSmartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshlayout.finishRefresh(2000/*,false*/);//传入false表示刷新失败
                start = 0;
                moreOrRefrush = true;
                url = URLTools.urlBase + URLTools.pandian_huizong_list_url + "start=" + start + "&limit=" + limit;
                okHttpManager.getMethod(false, url, "盘点汇总接口", handler, 6);
            }
        });
        mSmartRefreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                refreshlayout.finishLoadmore(2000/*,false*/);//传入false表示加载失败
                start += 20;
                moreOrRefrush = false;
                url = URLTools.urlBase + URLTools.pandian_huizong_list_url + "start=" + start + "&limit=" + limit;
                okHttpManager.getMethod(false, url, "盘点汇总接口", handler, 6);
            }
        });
        mListView = view1.findViewById(R.id.pandian_huizong_listview);
        pdhzAdapter = new PDHZAdapter();
        mListView.setAdapter(pdhzAdapter);
        okHttpManager = OkHttpManager.getInstance();
        url = URLTools.urlBase + URLTools.pandian_huizong_list_url + "&start=" + start + "&limit=" + limit;
        okHttpManager.getMethod(false, url, "盘点汇总接口", handler, 6);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), PDMessageActivity.class);
                intent.putExtra("id", mList.get(i).getId());
                startActivityForResult(intent, 100);
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 100) {
            start = 0;
            url = URLTools.urlBase + URLTools.pandian_huizong_list_url + "&start=" + start + "&limit=" + limit;
            okHttpManager.getMethod(false, url, "盘点汇总接口", handler, 6);
        }
    }

    class PDHZAdapter extends BaseAdapter {

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
            PHolder pHolder = null;
            if (view == null) {
                pHolder = new PHolder();
                view = LayoutInflater.from(getActivity()).inflate(R.layout.pandian_huizong_item, null);
                pHolder.img = view.findViewById(R.id.pd_head_img);
                pHolder.title = view.findViewById(R.id.pd_title);
                pHolder.message = view.findViewById(R.id.pd_message);
                pHolder.status = view.findViewById(R.id.pd_status);
                pHolder.date = view.findViewById(R.id.pd_date);
                view.setTag(pHolder);
            } else {
                pHolder = (PHolder) view.getTag();
            }
            Picasso.with(getActivity()).load(R.mipmap.login_bg).into(pHolder.img);
            pHolder.title.setText(mList.get(i).getSubject() + "");
            pHolder.message.setText(mList.get(i).getDescription() + "");
            pHolder.date.setText(mList.get(i).getBeginDateString() + "");
            if ("".equals(mList.get(i).getClosedDateString())) {
                pHolder.status.setText("进行中");
                pHolder.status.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_five));
            } else {
                pHolder.status.setText("已完成");
                pHolder.status.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_23b880));
            }

            return view;
        }

        class PHolder {
            CircleImg img;
            TextView title, message, date, status;
        }
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden){//如果盘点汇总显示，那就刷新盘点汇总数据
            start = 0;
            moreOrRefrush = true;
            url = URLTools.urlBase + URLTools.pandian_huizong_list_url + "start=" + start + "&limit=" + limit;
            okHttpManager.getMethod(false, url, "盘点汇总接口", handler, 6);
        }
    }
}
