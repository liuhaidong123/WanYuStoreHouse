package com.storehouse.wanyu.fragment;


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
import android.widget.LinearLayout;
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
import com.storehouse.wanyu.Bean.DaiRoot;
import com.storehouse.wanyu.Bean.DaiRows;
import com.storehouse.wanyu.IPAddress.URLTools;
import com.storehouse.wanyu.OkHttpUtils.OkHttpManager;
import com.storehouse.wanyu.R;
import com.storehouse.wanyu.activity.AllSPZDetailsActivity.SPZBaoFeiApplyDetailsActivity;
import com.storehouse.wanyu.activity.AllSPZDetailsActivity.SPZCaiGouApplyDetailsActivity;
import com.storehouse.wanyu.activity.AllSPZDetailsActivity.SPZJieYongApplyDetailsActivity;
import com.storehouse.wanyu.activity.AllSPZDetailsActivity.SPZLingYongApplyDetailsActivity;
import com.storehouse.wanyu.activity.AllSPZDetailsActivity.SPZNewOldApplyDetailsActivity;
import com.storehouse.wanyu.activity.AllSPZDetailsActivity.SPZWeiXiuApplyDetailsActivity;
import com.storehouse.wanyu.activity.AllSPZDetailsActivity.SpzTuiKuApplyDetailsActivity;
import com.storehouse.wanyu.activity.ApplyActivity.ApplyStatusSpz1Activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;

import static android.app.Activity.RESULT_OK;

/**
 * 审批
 */
public class Fragment_Property extends Fragment implements View.OnClickListener {
    private SmartRefreshLayout mSmartRefreshLayout;
    private ListView mListView;
    private PropertyAdapter propertyAdapter;
    private LinearLayout mDSP_ll, mYSP_ll;//待审批，已审批
    private View mDSP_line, mYSP_line;
    private TextView mDsp_tv, mYsp_tv;
    private RelativeLayout mNodata_rl;
    private OkHttpManager mOkhttpManager;
    private Gson mGson = new Gson();
    private String url;
    private int flag = 0;//0表示待审批，1表示已审批
    private boolean moreOrRefrush = true;//true表示是刷新，false表示加载更多
    private int start = 0, limit = 20;
    private List<DaiRows> mDaiRowList = new ArrayList<>();//待审批集合
    //private List<DaiRows> mYiRowList = new ArrayList<>();//已审批集合
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 12) {//待审批接口
                String mes = (String) msg.obj;
                Log.e("审批接口==", mes);
                Object o = mGson.fromJson(mes, DaiRoot.class);
                if (o != null && o instanceof DaiRoot) {
                    DaiRoot daiRoot = (DaiRoot) o;
                    if ("0".equals(daiRoot.getCode())) {

                        if (daiRoot.getRows() != null ) {//有全限的可以看到数据
                            mNodata_rl.setVisibility(View.GONE);
                            if (moreOrRefrush) {//刷新
                                mDaiRowList = daiRoot.getRows();
                                if (daiRoot.getRows().size()==0){
                                    mNodata_rl.setVisibility(View.VISIBLE);
                                }
                            } else {//加载更多
                                for (int i = 0; i < daiRoot.getRows().size(); i++) {
                                    mDaiRowList.add(daiRoot.getRows().get(i));
                                }
                            }

                            propertyAdapter.notifyDataSetChanged();

                        } else {//没有权限

                            Toast.makeText(getContext(), "无法获取审批数据", Toast.LENGTH_SHORT).show();
                        }

                    } else if ("-1".equals(daiRoot.getCode())) {
                        Toast.makeText(getContext(), "登录过期，请重新登录", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "无法获取审批数据", Toast.LENGTH_SHORT).show();
                        mNodata_rl.setVisibility(View.VISIBLE);
                    }

                } else {
                    Toast.makeText(getContext(), "获取审批数据解析错误", Toast.LENGTH_SHORT).show();
                }


            } else {
                Toast.makeText(getContext(), "连接服务器失败,请检查网络", Toast.LENGTH_SHORT).show();
            }


        }
    };

    public Fragment_Property() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e("资产", "Fragment_Property");
        View view = inflater.inflate(R.layout.fragment_fragment__property, container, false);
        initUI(view);
        return view;
    }

    private void initUI(View view) {
        mOkhttpManager = OkHttpManager.getInstance();
        url = URLTools.urlBase + URLTools.dai_and_yi + "msgStatus=" + 0 + "&start=" + start + "&limit=" + limit;//待审批接口
        mOkhttpManager.getMethod(false, url, "审批接口", mHandler, 12);
        mNodata_rl = view.findViewById(R.id.nodata_rl);//没有权限时，显示的数据
        //待审批，已审批
        mDSP_ll = view.findViewById(R.id.dsp_ll);
        mYSP_ll = view.findViewById(R.id.ysp_ll);
        mDSP_ll.setOnClickListener(this);
        mYSP_ll.setOnClickListener(this);
        mDSP_line = view.findViewById(R.id.dsp_line);
        mYSP_line = view.findViewById(R.id.ysp_line);
        mDsp_tv = view.findViewById(R.id.dsp_tv_btn);
        mYsp_tv = view.findViewById(R.id.ysp_tv_btn);

        //刷新
        mSmartRefreshLayout = view.findViewById(R.id.property_refresh);
        mListView = view.findViewById(R.id.property_listview);
        propertyAdapter = new PropertyAdapter();
        mListView.setAdapter(propertyAdapter);
        mSmartRefreshLayout.setRefreshHeader(new CircleHeader(getContext()));
        mSmartRefreshLayout.setRefreshFooter(new BallPulseFooter(getContext()).setSpinnerStyle(SpinnerStyle.Scale).setAnimatingColor(ContextCompat.getColor(getContext(), R.color.color_1c82d4)));
        mSmartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshlayout.finishRefresh(2000/*,false*/);//传入false表示刷新失败
                limit = 20;
                start=0;
                moreOrRefrush = true;
                if (flag == 0) {//待审批

                    url = URLTools.urlBase + URLTools.dai_and_yi + "msgStatus=" + 0 + "&start=" + start + "&limit=" + limit;//待审批接口
                    mOkhttpManager.getMethod(false, url, "待审批接口", mHandler, 12);
                } else if (flag == 1) {//已审批

                    url = URLTools.urlBase + URLTools.dai_and_yi + "msgStatus=" + 1 + "&start=" + start + "&limit=" + limit;//待审批接口
                    mOkhttpManager.getMethod(false, url, "已审批接口", mHandler, 12);
                }
                //refreshData();
            }
        });
        mSmartRefreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                refreshlayout.finishLoadmore(2000/*,false*/);//传入false表示加载失败
                start += 20;
                moreOrRefrush = false;
                if (flag == 0) {//待审批

                    url = URLTools.urlBase + URLTools.dai_and_yi + "msgStatus=" + 0 + "&start=" + start + "&limit=" + limit;//待审批接口
                    mOkhttpManager.getMethod(false, url, "待审批接口", mHandler, 12);
                } else if (flag == 1) {//已审批

                    url = URLTools.urlBase + URLTools.dai_and_yi + "msgStatus=" + 1 + "&start=" + start + "&limit=" + limit;//待审批接口
                    mOkhttpManager.getMethod(false, url, "已审批接口", mHandler, 12);
                }
                //loadMoreData();
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Toast.makeText(getContext(), "" + i, Toast.LENGTH_SHORT).show();
                //0待审批,1是已同意，2是已驳回)
                int status = mDaiRowList.get(i).getApprovalState();
                long referId = mDaiRowList.get(i).getReferId();
                int type = mDaiRowList.get(i).getMsgType();
                if (status == 0) {//0待审批

                    checkIntentLB(type, referId);

                } else if (status == 1) {//1是已同意

                    checkIntentLB(type, referId);
                } else {//2是已驳回
                    checkIntentLB(type, referId);
                }

            }
        });
    }

    private void checkIntentLB(int myType, long referid) {
        switch (myType) {//跳转时判断是哪个类型
            case 10://"采购申请"

                Intent intent = new Intent(getActivity(), SPZCaiGouApplyDetailsActivity.class);
                intent.putExtra("referId", referid);//每条申请的id
                intent.putExtra("flag", 3);//3代表所有审批跳转过去的
                if (flag==0){//待审批跳过去的
                    intent.putExtra("status",flag);
                }else {//已审批跳过去的
                    intent.putExtra("status",flag);
                }
                startActivityForResult(intent, 123);
                break;
            case 30://"领用申请"

                Intent intent1 = new Intent(getActivity(), SPZLingYongApplyDetailsActivity.class);
                intent1.putExtra("referId", referid);//每条申请的id
                intent1.putExtra("flag", 3);//3代表所有审批跳转过去的
                if (flag==0){//待审批跳过去的
                    intent1.putExtra("status",flag);
                }else {//已审批跳过去的
                    intent1.putExtra("status",flag);
                }
                startActivityForResult(intent1, 123);
                break;
            case 35://"借用申请"

                Intent intent2 = new Intent(getActivity(), SPZJieYongApplyDetailsActivity.class);
                intent2.putExtra("referId", referid);//每条申请的id
                intent2.putExtra("flag", 3);//3代表所有审批跳转过去的
                if (flag==0){//待审批跳过去的
                    intent2.putExtra("status",flag);
                }else {//已审批跳过去的
                    intent2.putExtra("status",flag);
                }
                startActivityForResult(intent2, 123);
                break;
            case 60://"维修申请"

                Intent intent3 = new Intent(getActivity(), SPZWeiXiuApplyDetailsActivity.class);
                intent3.putExtra("referId", referid);//每条申请的id
                intent3.putExtra("flag", 3);//3代表所有审批跳转过去的
                if (flag==0){//待审批跳过去的
                    intent3.putExtra("status",flag);
                }else {//已审批跳过去的
                    intent3.putExtra("status",flag);
                }
                startActivityForResult(intent3, 123);
                break;
            case 50://"以旧换新"
                Intent intent4 = new Intent(getActivity(), SPZNewOldApplyDetailsActivity.class);
                intent4.putExtra("referId", referid);
                intent4.putExtra("flag", 3);
                if (flag==0){//待审批跳过去的
                    intent4.putExtra("status",flag);
                }else {//已审批跳过去的
                    intent4.putExtra("status",flag);
                }
                startActivityForResult(intent4, 123);
                break;
            case 65://"报废申请"
                Intent intent5 = new Intent(getActivity(), SPZBaoFeiApplyDetailsActivity.class);
                intent5.putExtra("referId", referid);
                intent5.putExtra("flag", 3);
                if (flag==0){//待审批跳过去的
                    intent5.putExtra("status",flag);
                }else {//已审批跳过去的
                    intent5.putExtra("status",flag);
                }
                startActivityForResult(intent5, 123);
                break;

            case 45://"退库申请"
                Intent intent7 = new Intent(getActivity(), SpzTuiKuApplyDetailsActivity.class);
                intent7.putExtra("referId", referid);
                intent7.putExtra("flag", 3);
                if (flag==0){//待审批跳过去的
                    intent7.putExtra("status",flag);
                }else {//已审批跳过去的
                    intent7.putExtra("status",flag);
                }
                startActivityForResult(intent7, 123);
                break;
            default:
                Toast.makeText(getActivity(), "抱歉，没有此类申请", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == mDSP_ll.getId()) {//待审批
            flag = 0;
            start=0;
            moreOrRefrush = true;
            mDSP_line.setBackgroundResource(R.color.color_2face4);
            mYSP_line.setBackgroundResource(R.color.color_ffffff);
            mDsp_tv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_2face4));
            mYsp_tv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_000000));
            url = URLTools.urlBase + URLTools.dai_and_yi + "msgStatus=" + 0 + "&start=" + start + "&limit=" + limit;//待审批接口
            mOkhttpManager.getMethod(false, url, "待审批接口", mHandler, 12);
        } else if (id == mYSP_ll.getId()) {//已审批
            flag = 1;
            start=0;
            moreOrRefrush = true;
            mDSP_line.setBackgroundResource(R.color.color_ffffff);
            mYSP_line.setBackgroundResource(R.color.color_2face4);
            mDsp_tv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_000000));
            mYsp_tv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_2face4));
            url = URLTools.urlBase + URLTools.dai_and_yi + "msgStatus=" + 1 + "&start=" + start + "&limit=" + limit;//已审批接口
            mOkhttpManager.getMethod(false, url, "已审批接口", mHandler, 12);
        }
    }

    //点击同意驳回后，重新刷新数据
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123 && resultCode == RESULT_OK) {
            moreOrRefrush = true;
            if (flag == 0) {//待审批
                start = 0;
                url = URLTools.urlBase + URLTools.dai_and_yi + "msgStatus=" + 0 + "&start=" + start + "&limit=" + limit;//待审批接口
                mOkhttpManager.getMethod(false, url, "待审批接口", mHandler, 12);
            } else {//已审批
                start = 0;
                url = URLTools.urlBase + URLTools.dai_and_yi + "msgStatus=" + 1 + "&start=" + start + "&limit=" + limit;//已审批接口
                mOkhttpManager.getMethod(false, url, "已审批接口", mHandler, 12);
            }
        }
    }

    //适配器
    class PropertyAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        public PropertyAdapter() {

            mInflater = LayoutInflater.from(getContext());
        }

        @Override
        public int getCount() {
            return mDaiRowList.size();
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
            PropertyHolder propertyHolder = null;
            if (view == null) {
                propertyHolder = new PropertyHolder();
                view = mInflater.inflate(R.layout.property_listview_item, null);
                propertyHolder.property_name = view.findViewById(R.id.property_name);//
                propertyHolder.property_state = view.findViewById(R.id.property_state);//
                propertyHolder.  property_date=view.findViewById(R.id.property_date);
                view.setTag(propertyHolder);
            } else {
                propertyHolder = (PropertyHolder) view.getTag();
            }
            //设置数据
            propertyHolder.property_name.setText(mDaiRowList.get(i).getContent()+"");
            propertyHolder.  property_date.setText(mDaiRowList.get(i).getCreateTimeString());
            if (mDaiRowList.get(i).getApprovalState() == 0) {//待审批
                propertyHolder.property_state.setText("待审批");
                propertyHolder.property_state.setTextColor(ContextCompat.getColor(getContext(), R.color.color_five));
            } else if (mDaiRowList.get(i).getApprovalState() == 1) {//已同意
                propertyHolder.property_state.setText("已同意");
                propertyHolder.property_state.setTextColor(ContextCompat.getColor(getContext(), R.color.color_23b880));

            } else if (mDaiRowList.get(i).getApprovalState() == 2) {//已驳回
                propertyHolder.property_state.setText("已驳回");
                propertyHolder.property_state.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
            }

            return view;
        }

        class PropertyHolder {
            TextView property_name, property_state,property_date;//资产名称，状态

        }
    }
}
