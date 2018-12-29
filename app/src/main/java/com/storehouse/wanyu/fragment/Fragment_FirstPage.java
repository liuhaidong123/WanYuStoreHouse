package com.storehouse.wanyu.fragment;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
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
import com.storehouse.wanyu.Bean.AssetQrCode;
import com.storehouse.wanyu.Bean.ErwermaRoot;
import com.storehouse.wanyu.Bean.NotifyRoot;
import com.storehouse.wanyu.Bean.NotifyRows;
import com.storehouse.wanyu.Bean.Permission;
import com.storehouse.wanyu.IPAddress.URLTools;
import com.storehouse.wanyu.MyUtils.SharedPrefrenceTools;
import com.storehouse.wanyu.OkHttpUtils.OkHttpManager;
import com.storehouse.wanyu.R;
import com.storehouse.wanyu.activity.Ku.KuActivity;
import com.storehouse.wanyu.activity.NotifyActivity.NotifyMessageActivity;
import com.storehouse.wanyu.activity.NotifyActivity.PostInformationActivity;
import com.storehouse.wanyu.activity.PanDian.PDActivity;
import com.storehouse.wanyu.activity.PropertyManage.AddPropertyActivity;
import com.storehouse.wanyu.activity.PropertyManage.PropertyManageActivity;
import com.storehouse.wanyu.activity.PropertyManage.PropertyMessageActivity;
import com.storehouse.wanyu.activity.PurchaseOrder.PurchaseOrderActivity;
import com.storehouse.wanyu.activity.RepairManage.RepairActivity;
import com.storehouse.wanyu.activity.SaoYiSaoActivity.SaoYiSaoActivity;
import com.storehouse.wanyu.activity.startActivity.StartActivity;
import com.storehouse.wanyu.adapter.FirstPageListViewAdapter;
import com.xys.libzxing.activity.CaptureActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.jpush.android.api.JPushInterface;
import okhttp3.Response;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_FirstPage extends Fragment {
    private RelativeLayout mNodata_rl;
    private List<Integer> mList = new ArrayList<>();
    private List<String> mNameList = new ArrayList<>();
    private GridView mGridView;

    private SmartRefreshLayout mSmartRefreshLayout;
    private ListView mListView;
    private List<NotifyRows> mNotifyList = new ArrayList<>();
    private FirstPageListViewAdapter firstPageListViewAdapter;
    private int start = 0, limit = 20;
    private String notifyURL;
    private boolean notifyFlag = true;//true表示刷新，false表示加载更多
    private OkHttpManager mOkHttpManager;
    private String mSaoYiSao_Url;
    private Gson mGson = new Gson();
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.what == 11) {
                String mes = (String) msg.obj;

                Object o = mGson.fromJson(mes, ErwermaRoot.class);
                Log.e("扫一扫接口信息=", mes);
                if (o != null && o instanceof ErwermaRoot) {
                    ErwermaRoot erwermaRoot = (ErwermaRoot) o;
                    if ("0".equals(erwermaRoot.getCode())) {
                        AssetQrCode assetQrCode = erwermaRoot.getAssetQrCode();
                        if (assetQrCode != null) {
                            if (assetQrCode.getIsUse() == 0) {//这是一个新的二维码，可以保存资产信息，跳转到资产入库页面
                                // Toast.makeText(getActivity(), "这是一个新的二维码，可以保存资产信息，跳转到资产入库页面", Toast.LENGTH_SHORT).show();
                                //将资产编码传过去
                                Intent intent = new Intent(getContext(), AddPropertyActivity.class);
                                intent.putExtra("barcode", assetQrCode.getBarcode());
                                startActivity(intent);
                            } else if (assetQrCode.getIsUse() != 0 && assetQrCode.getAssetId() != 0) {//这是一个已经使用过的二维码，已经保存过资产信息，跳转到资产详情页面
                                // 接这个接口  http://192.168.1.168:8085/mobileapi/asset/get.do?id=assetQrCode.getAssetId()
                                // Toast.makeText(getActivity(), "这是一个已经使用过的二维码，已经保存过资产信息，跳转到资产详情页面", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(getContext(), PropertyMessageActivity.class);
                                i.putExtra("assetID", assetQrCode.getAssetId());
                                startActivity(i);
                            } else {
                                Toast.makeText(getActivity(), "此二维码无效", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getActivity(), "此二维码不存在", Toast.LENGTH_SHORT).show();

                        }


                    } else if ("-1".equals(erwermaRoot.getCode())) {
                        Toast.makeText(getActivity(), "账号过期，请重新登录", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "二维码扫数据错误", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(getActivity(), "此二维码无效", Toast.LENGTH_SHORT).show();
                }


            } else if (msg.what == 1010) {
                Toast.makeText(getActivity(), "连接服务器失败，请重新尝试", Toast.LENGTH_SHORT).show();
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
                                }else {
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
                            Toast.makeText(getActivity(), "获取通知列表失败", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(getActivity(), "获取通知数据失败", Toast.LENGTH_SHORT).show();
                    }


                } else {
                    Toast.makeText(getActivity(), "通知数据错误", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    public Fragment_FirstPage() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e("首页", "Fragment_FirstPage");

        View view = inflater.inflate(R.layout.fragment_fragment__first_page, container, false);
        initUI(view);
        return view;
    }

    //初始化数据,因为是ViewPager,在左右切换布局的时候，会重新走onCreateView方法，所以在这里需要判断，防止数据重复加载
    private void initUI(View view) {
        mNodata_rl = view.findViewById(R.id.nodata_rl);

        mOkHttpManager = OkHttpManager.getInstance();

        mList.add(R.mipmap.main_sao);
        mList.add(R.mipmap.main_pandian);
        mList.add(R.mipmap.main_caigou);
        mList.add(R.mipmap.main_weixiu);
        mList.add(R.mipmap.main_fabu);
        mList.add(R.mipmap.main_zichan);
        mList.add(R.mipmap.ku);
        mList.add(R.mipmap.main_tongji);
        mNameList.add("扫一扫");
        mNameList.add("盘点");
        mNameList.add("采购订单");
        mNameList.add("维修管理");
        mNameList.add("发布通知");
        mNameList.add("资产管理");
        mNameList.add("出库入库");
        mNameList.add("统计报表");


        mGridView = view.findViewById(R.id.firstpage_gridview);
        mGridView.setAdapter(new MyGridViewAdapter());
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    //扫一扫
                    case 0:
                        checkCameraPermission();
                        break;
                    //盘点,需要判断有没有盘点权限
                    case 1:

                        boolean permissionPan = false;
                        for (int k = 0; k < (int) SharedPrefrenceTools.getValueByKey("PermissionNum", 0); k++) {
                            Permission permission = (Permission) SharedPrefrenceTools.getObject("Permission" + k);
                            if (permission != null) {
                                //如果有这个权限
                                if ("inventory".equals(permission.getTarget())) {
                                    //如果权限等于*，就有盘点权限
                                    if ("*".equals(permission.getOperaton())) {
                                        permissionPan = true;
                                        break;
                                    }
                                }
                            } else {
                                Toast.makeText(getActivity(), "存储权限对象为空", Toast.LENGTH_SHORT).show();
                            }

                        }
                        if (permissionPan) {
                            Intent intent1 = new Intent(getActivity(), PDActivity.class);
                            startActivity(intent1);
                        } else {
                            Toast.makeText(getActivity(), "抱歉，您没有盘点权限", Toast.LENGTH_SHORT).show();

                        }

                        break;
                    //采购订单
                    case 2:
                        boolean permissionC = false;
                        for (int k = 0; k < (int) SharedPrefrenceTools.getValueByKey("PermissionNum", 0); k++) {
                            Permission permission = (Permission) SharedPrefrenceTools.getObject("Permission" + k);
                            if (permission != null) {
                                //如果有这个权限
                                if ("buyManage".equals(permission.getTarget())) {
                                    //如果权限等于*，就有盘点权限
                                    if ("*".equals(permission.getOperaton())) {
                                        permissionC = true;
                                        break;
                                    }
                                }
                            } else {
                                Toast.makeText(getActivity(), "存储权限对象为空", Toast.LENGTH_SHORT).show();
                            }

                        }
                        if (permissionC) {
                            Intent intent1 = new Intent(getActivity(), PurchaseOrderActivity.class);
                            startActivity(intent1);
                        } else {
                            Toast.makeText(getActivity(), "抱歉，您没有查看采购权限", Toast.LENGTH_SHORT).show();

                        }

                        break;
                    //维修管理 需要判断权限
                    case 3:
                        boolean permissionW = false;
                        for (int k = 0; k < (int) SharedPrefrenceTools.getValueByKey("PermissionNum", 0); k++) {
                            Permission permission = (Permission) SharedPrefrenceTools.getObject("Permission" + k);
                            if (permission != null) {
                                //如果有这个权限
                                if ("mainManage".equals(permission.getTarget())) {
                                    //如果权限等于*或者create的时候，就有发布通知的权限
                                    if ("*".equals(permission.getOperaton())) {
                                        permissionW = true;
                                        break;
                                    }
                                }
                            } else {
                                Toast.makeText(getActivity(), "存储权限对象为空", Toast.LENGTH_SHORT).show();
                            }

                        }

                        if (permissionW) {
                            Intent intent3 = new Intent(getActivity(), RepairActivity.class);
                            startActivity(intent3);
                        } else {
                            Toast.makeText(getActivity(), "抱歉，您没有维修管理的权限", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    //发布通知,需要判断有没有发布通知的权限
                    case 4:
                        boolean permissionFlag = false;
                        for (int k = 0; k < (int) SharedPrefrenceTools.getValueByKey("PermissionNum", 0); k++) {
                            Permission permission = (Permission) SharedPrefrenceTools.getObject("Permission" + k);
                            if (permission != null) {
                                //如果有这个权限
                                if ("notice".equals(permission.getTarget())) {
                                    //如果权限等于*或者create的时候，就有发布通知的权限
                                    if ("*".equals(permission.getOperaton()) || "create".equals(permission.getOperaton())) {
                                        permissionFlag = true;
                                        break;
                                    }
                                }
                            } else {
                                Toast.makeText(getActivity(), "存储权限对象为空", Toast.LENGTH_SHORT).show();
                            }

                        }

                        if (permissionFlag) {
                            Intent intent4 = new Intent(getActivity(), PostInformationActivity.class);
                            startActivityForResult(intent4, 1);
                        } else {
                            Toast.makeText(getActivity(), "抱歉，您没有发布通知的权限", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    //资产管理,需要判断有没资产管理权限
                    case 5:

                        boolean permissionZi = false;
                        for (int k = 0; k < (int) SharedPrefrenceTools.getValueByKey("PermissionNum", 0); k++) {
                            Permission permission = (Permission) SharedPrefrenceTools.getObject("Permission" + k);
                            if (permission != null) {
                                //如果有这个权限
                                if ("assetManage".equals(permission.getTarget())) {
                                    //如果权限等于*就有资产管理的权限
                                    if ("*".equals(permission.getOperaton())) {
                                        permissionZi = true;
                                        break;
                                    }
                                }
                            } else {
                                Toast.makeText(getActivity(), "存储权限对象为空", Toast.LENGTH_SHORT).show();
                            }

                        }

                        if (permissionZi) {
                            Intent intent5 = new Intent(getActivity(), PropertyManageActivity.class);
                            startActivity(intent5);
                        } else {
                            Toast.makeText(getActivity(), "抱歉,您没有资产管理权限", Toast.LENGTH_SHORT).show();

                        }

                        break;
                    //出库入库
                    case 6:
                        boolean permissionKu= false;
                        for (int k = 0; k < (int) SharedPrefrenceTools.getValueByKey("PermissionNum", 0); k++) {
                            Permission permission = (Permission) SharedPrefrenceTools.getObject("Permission" + k);
                            if (permission != null) {
                                //如果有这个权限
                                if ("stockInOut".equals(permission.getTarget())) {
                                    //如果权限等于*就有出库入库的权限
                                    if ("*".equals(permission.getOperaton())) {
                                        permissionKu = true;
                                        break;
                                    }
                                }
                            } else {
                                Toast.makeText(getActivity(), "存储权限对象为空", Toast.LENGTH_SHORT).show();
                            }

                        }

                        if (permissionKu) {
                            Intent intent6 = new Intent(getActivity(), KuActivity.class);
                            startActivity(intent6);
                        } else {
                            Toast.makeText(getActivity(), "抱歉,您没有出入库权限", Toast.LENGTH_SHORT).show();

                        }
                        break;
                    //统计报表
                    case 7:
                        Toast.makeText(getActivity(), "此功能暂未开通", Toast.LENGTH_SHORT).show();

                        break;

                }

            }
        });
        //通知列表数据

        notifyURL = URLTools.urlBase + URLTools.query_notify_list + "start=" + start + "&limit=" + limit;
        mNodata_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOkHttpManager.getMethod(false, notifyURL, "通知列表接口", mHandler, 12);
            }
        });
        mOkHttpManager.getMethod(false, notifyURL, "通知列表接口", mHandler, 12);
        mSmartRefreshLayout = view.findViewById(R.id.smart_refresh);
        mListView = view.findViewById(R.id.firstpage_listview);
        firstPageListViewAdapter = new FirstPageListViewAdapter(getContext(), mNotifyList);
        mListView.setAdapter(firstPageListViewAdapter);
        mSmartRefreshLayout.setRefreshHeader(new CircleHeader(getContext()));
        mSmartRefreshLayout.setRefreshFooter(new BallPulseFooter(getContext()).setSpinnerStyle(SpinnerStyle.Scale).setAnimatingColor(ContextCompat.getColor(getContext(), R.color.color_1c82d4)));


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
                Intent intent = new Intent(getActivity(), NotifyMessageActivity.class);
                intent.putExtra("title", mNotifyList.get(i).getTitle());
                intent.putExtra("content", mNotifyList.get(i).getContent());
                intent.putExtra("date", mNotifyList.get(i).getCreateTimeString());
                intent.putExtra("id", mNotifyList.get(i).getId());
                intent.putExtra("read",mNotifyList.get(i).isRead());
                startActivityForResult(intent,1);
            }
        });

    }

    /**
     * 扫描二维码后的回调
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 0) {
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                String scanResult = bundle.getString("result");
                mSaoYiSao_Url = URLTools.urlBase + URLTools.saoyisao + "barcode=" + scanResult;
                mOkHttpManager.getMethod(false, mSaoYiSao_Url, "扫一扫接口", mHandler, 11);
                Log.e("scanResult=", scanResult);
            } else {
                Toast.makeText(getActivity(), "扫描信息错误", Toast.LENGTH_SHORT).show();
            }
        } else if (resultCode == RESULT_OK && requestCode == 1) {//发布通知以后刷新通知列表
            notifyFlag = true;
            start = 0;
            notifyURL = URLTools.urlBase + URLTools.query_notify_list + "start=" + start + "&limit=" + limit;
            mOkHttpManager.getMethod(false, notifyURL, "通知列表接口", mHandler, 12);
        }
    }

    /**
     * 判断权限点击允许或拒绝
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == CAMERA_CODE) {
            //点击了允许，授权成功
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(getContext(), CaptureActivity.class);
                startActivityForResult(intent, 0);
            } else {
                //点击了拒绝，授权失败
                Toast.makeText(getActivity(), "相机授权失败，请授权", Toast.LENGTH_SHORT).show();

            }


        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }

    /**
     * 照相机权限
     */
    public static final int CAMERA_CODE = 1;

    private void checkCameraPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            int checkTag = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA);
            //如果相机权限没有授权
            if (checkTag != PackageManager.PERMISSION_GRANTED) {
                //在这个数组中可以添加很多权限
                requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_CODE);
                return;
            } else {
                //如果已经授权，执行业务逻辑
                Intent intent = new Intent(getContext(), CaptureActivity.class);
                startActivityForResult(intent, 0);
            }
        } else {
            //版本小于23时，不需要判断敏感权限，执行业务逻辑
            Intent intent = new Intent(getContext(), CaptureActivity.class);
            startActivityForResult(intent, 0);
        }
    }

    class MyGridViewAdapter extends BaseAdapter {
        private LayoutInflater inflater;

        public MyGridViewAdapter() {
            inflater = LayoutInflater.from(getContext());
        }

        @Override
        public int getCount() {
            return mNameList.size();
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

            MyGridViewHolder myGridViewHolder = null;
            if (view == null) {
                myGridViewHolder = new MyGridViewHolder();
                view = inflater.inflate(R.layout.firstpage_gridview_item, null);
                myGridViewHolder.textView = view.findViewById(R.id.gridview_tvicon_id);
                myGridViewHolder.imageView = view.findViewById(R.id.gridview_imgicon_id);
                view.setTag(myGridViewHolder);

            } else {
                myGridViewHolder = (MyGridViewHolder) view.getTag();

            }

            Picasso.with(getContext()).load(mList.get(i)).into(myGridViewHolder.imageView);
            myGridViewHolder.textView.setText(mNameList.get(i));
            return view;
        }

        class MyGridViewHolder {
            TextView textView;
            ImageView imageView;
        }
    }

}
