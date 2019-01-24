package com.storehouse.wanyu.activity.Ku;

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
import com.storehouse.wanyu.Bean.KuJieYongRoot;
import com.storehouse.wanyu.Bean.KuJieYongRows;
import com.storehouse.wanyu.Bean.KuLingYongRoot;
import com.storehouse.wanyu.Bean.KuLingYongRows;
import com.storehouse.wanyu.Bean.KuNewOldRoot;
import com.storehouse.wanyu.Bean.KuNewOldRows;
import com.storehouse.wanyu.Bean.KuTuiKuRoot;
import com.storehouse.wanyu.Bean.KuTuiKuRows;
import com.storehouse.wanyu.Bean.KuYanShouRoot;
import com.storehouse.wanyu.Bean.KuYanShouRows;
import com.storehouse.wanyu.IPAddress.URLTools;
import com.storehouse.wanyu.MyUtils.BallProgressUtils;
import com.storehouse.wanyu.MyUtils.CircleImg;
import com.storehouse.wanyu.OkHttpUtils.OkHttpManager;
import com.storehouse.wanyu.R;
import com.storehouse.wanyu.activity.PurchaseOrder.PurchaseOrderActivity;
import com.storehouse.wanyu.activity.PurchaseOrder.PurchaseOrderMsgActivity;

import java.util.ArrayList;
import java.util.List;

public class KuActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView mback;
    private TextView yanshou_tv, lingyong_tv, jieyong_tv, newold_tv, tuiku_tv,no_mess_tv;
    private SmartRefreshLayout smartRefreshLayout;
    private RelativeLayout mAll, mNoData_rl;
    private ListView mListView;
    private KuAdapter kuAdapter;
    private List<KuYanShouRows> mYanShouList = new ArrayList<>();
    private List<KuLingYongRows> mLingYongList = new ArrayList<>();
    private List<KuJieYongRows> mJieYongList = new ArrayList<>();
    private List<KuNewOldRows> mNewOldList = new ArrayList<>();
    private List<KuTuiKuRows> mTuiKuList = new ArrayList<>();

    private Gson gson = new Gson();
    private int limit = 20, start = 0;
    private String yanshou_url, lingyong_url, jieyong_url, newold_url, tuiku_url;
    private boolean refresh = true;//true表示刷新 false表示加载更多
    private int clickFlag = 1;//1表示点击验收，2领用，3借用，4依旧换新，5退库
    private OkHttpManager okHttpManager;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {//验收
                try {
                    BallProgressUtils.dismisLoading();
                    String mess = (String) msg.obj;
                    Object o = gson.fromJson(mess, KuYanShouRoot.class);
                    if (o != null && o instanceof KuYanShouRoot) {
                        KuYanShouRoot kuYanShouRoot = (KuYanShouRoot) o;
                        if (kuYanShouRoot != null && "0".equals(kuYanShouRoot.getCode())) {
                            if (kuYanShouRoot.getRows() != null) {
                                if (refresh) {
                                    mYanShouList = kuYanShouRoot.getRows();
                                    if (kuYanShouRoot.getRows().size() == 0) {
                                        mNoData_rl.setVisibility(View.VISIBLE);
                                        no_mess_tv.setText("空空如也");
                                    } else {
                                        mNoData_rl.setVisibility(View.GONE);
                                        no_mess_tv.setText("");
                                    }
                                } else {
                                    for (int i = 0; i < kuYanShouRoot.getRows().size(); i++) {
                                        mYanShouList.add(kuYanShouRoot.getRows().get(i));
                                    }
                                    if (kuYanShouRoot.getRows().size() == 0) {
                                        Toast.makeText(KuActivity.this, "全部加载完毕", Toast.LENGTH_SHORT).show();
                                    }

                                }
                                kuAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(KuActivity.this, "请求验收数据错误", Toast.LENGTH_SHORT).show();
                            }


                        } else if (kuYanShouRoot != null && "-1".equals(kuYanShouRoot.getCode())) {
                            Toast.makeText(KuActivity.this, "登录过期,请重新登录", Toast.LENGTH_SHORT).show();
                            mNoData_rl.setVisibility(View.VISIBLE);
                            no_mess_tv.setText("登录过期,请重新登录");
                        } else {
                            Toast.makeText(KuActivity.this, "请求数据数据错误", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        BallProgressUtils.dismisLoading();
                        Toast.makeText(KuActivity.this, "连接服务器失败,请重新尝试", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    mNoData_rl.setVisibility(View.VISIBLE);
                    no_mess_tv.setText("数据解析错误，请重新尝试");
                    Toast.makeText(KuActivity.this, "数据解析错误，请重新尝试", Toast.LENGTH_SHORT).show();
                }

            } else if (msg.what == 2) {//领用
                try {
                    BallProgressUtils.dismisLoading();
                    String mess = (String) msg.obj;
                    Object o = gson.fromJson(mess, KuLingYongRoot.class);
                    if (o != null && o instanceof KuLingYongRoot) {
                        KuLingYongRoot kuLingYongRoot = (KuLingYongRoot) o;
                        if (kuLingYongRoot != null && "0".equals(kuLingYongRoot.getCode())) {
                            if (kuLingYongRoot.getRows() != null) {

                                if (refresh) {
                                    mLingYongList = kuLingYongRoot.getRows();
                                    if (kuLingYongRoot.getRows().size() == 0) {
                                        mNoData_rl.setVisibility(View.VISIBLE);
                                        no_mess_tv.setText("空空如也");
                                    } else {
                                        mNoData_rl.setVisibility(View.GONE);
                                        no_mess_tv.setText("");
                                    }
                                } else {
                                    for (int i = 0; i < kuLingYongRoot.getRows().size(); i++) {
                                        mLingYongList.add(kuLingYongRoot.getRows().get(i));
                                    }
                                    if (kuLingYongRoot.getRows().size() == 0) {
                                        Toast.makeText(KuActivity.this, "全部加载完毕", Toast.LENGTH_SHORT).show();
                                    }

                                }
                                kuAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(KuActivity.this, "请求领用数据错误", Toast.LENGTH_SHORT).show();
                            }


                        } else if (kuLingYongRoot != null && "-1".equals(kuLingYongRoot.getCode())) {
                            Toast.makeText(KuActivity.this, "登录过期,请重新登录", Toast.LENGTH_SHORT).show();
                            mNoData_rl.setVisibility(View.VISIBLE);
                            no_mess_tv.setText("登录过期,请重新登录");
                        } else {
                            Toast.makeText(KuActivity.this, "请求数据数据错误", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        BallProgressUtils.dismisLoading();
                        Toast.makeText(KuActivity.this, "连接服务器失败,请重新尝试", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    mNoData_rl.setVisibility(View.VISIBLE);
                    no_mess_tv.setText("数据解析错误，请重新尝试");
                    Toast.makeText(KuActivity.this, "数据解析错误，请重新尝试", Toast.LENGTH_SHORT).show();
                }


            } else if (msg.what == 3) {//借用
                try {
                    BallProgressUtils.dismisLoading();
                    String mess = (String) msg.obj;
                    Object o = gson.fromJson(mess, KuJieYongRoot.class);
                    if (o != null && o instanceof KuJieYongRoot) {
                        KuJieYongRoot kuJieYongRoot = (KuJieYongRoot) o;
                        if (kuJieYongRoot != null && "0".equals(kuJieYongRoot.getCode())) {
                            if (kuJieYongRoot.getRows() != null) {
                                if (refresh) {
                                    mJieYongList = kuJieYongRoot.getRows();
                                    if (kuJieYongRoot.getRows().size() == 0) {
                                        mNoData_rl.setVisibility(View.VISIBLE);
                                        no_mess_tv.setText("空空如也");
                                    } else {
                                        mNoData_rl.setVisibility(View.GONE);
                                        no_mess_tv.setText("");
                                    }
                                } else {
                                    for (int i = 0; i < kuJieYongRoot.getRows().size(); i++) {
                                        mJieYongList.add(kuJieYongRoot.getRows().get(i));
                                    }
                                    if (kuJieYongRoot.getRows().size() == 0) {
                                        Toast.makeText(KuActivity.this, "全部加载完毕", Toast.LENGTH_SHORT).show();
                                    }

                                }
                                kuAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(KuActivity.this, "请求领用数据错误", Toast.LENGTH_SHORT).show();
                            }


                        } else if (kuJieYongRoot != null && "-1".equals(kuJieYongRoot.getCode())) {
                            Toast.makeText(KuActivity.this, "登录过期,请重新登录", Toast.LENGTH_SHORT).show();
                            mNoData_rl.setVisibility(View.VISIBLE);
                            no_mess_tv.setText("登录过期,请重新登录");
                        } else {
                            Toast.makeText(KuActivity.this, "请求数据数据错误", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        BallProgressUtils.dismisLoading();
                        Toast.makeText(KuActivity.this, "连接服务器失败,请重新尝试", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    mNoData_rl.setVisibility(View.VISIBLE);
                    no_mess_tv.setText("数据解析错误，请重新尝试");
                    Toast.makeText(KuActivity.this, "数据解析错误，请重新尝试", Toast.LENGTH_SHORT).show();
                }

            } else if (msg.what == 4) {//以旧换新
                try{
                    BallProgressUtils.dismisLoading();
                    String mess = (String) msg.obj;
                    Object o = gson.fromJson(mess, KuNewOldRoot.class);
                    if (o != null && o instanceof KuNewOldRoot) {
                        KuNewOldRoot kuNewOldRoot = (KuNewOldRoot) o;
                        if (kuNewOldRoot != null && "0".equals(kuNewOldRoot.getCode())) {
                            if (kuNewOldRoot.getRows() != null) {
                                if (refresh) {
                                    mNewOldList = kuNewOldRoot.getRows();
                                    if (kuNewOldRoot.getRows().size() == 0) {
                                        mNoData_rl.setVisibility(View.VISIBLE);
                                        no_mess_tv.setText("空空如也");
                                    } else {
                                        mNoData_rl.setVisibility(View.GONE);
                                        no_mess_tv.setText("");
                                    }
                                } else {
                                    for (int i = 0; i < kuNewOldRoot.getRows().size(); i++) {
                                        mNewOldList.add(kuNewOldRoot.getRows().get(i));
                                    }
                                    if (kuNewOldRoot.getRows().size() == 0) {
                                        Toast.makeText(KuActivity.this, "全部加载完毕", Toast.LENGTH_SHORT).show();
                                    }

                                }
                                kuAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(KuActivity.this, "请求以旧换新数据错误", Toast.LENGTH_SHORT).show();
                            }


                        } else if (kuNewOldRoot != null && "-1".equals(kuNewOldRoot.getCode())) {
                            Toast.makeText(KuActivity.this, "登录过期,请重新登录", Toast.LENGTH_SHORT).show();
                            mNoData_rl.setVisibility(View.VISIBLE);
                            no_mess_tv.setText("登录过期,请重新登录");
                        } else {
                            Toast.makeText(KuActivity.this, "请求数据数据错误", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        BallProgressUtils.dismisLoading();
                        Toast.makeText(KuActivity.this, "连接服务器失败,请重新尝试", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    mNoData_rl.setVisibility(View.VISIBLE);
                    no_mess_tv.setText("数据解析错误，请重新尝试");
                    Toast.makeText(KuActivity.this, "数据解析错误，请重新尝试", Toast.LENGTH_SHORT).show();
                }

            } else if (msg.what == 5) {//退库
                try{
                    BallProgressUtils.dismisLoading();
                    String mess = (String) msg.obj;
                    Object o = gson.fromJson(mess, KuTuiKuRoot.class);
                    if (o != null && o instanceof KuTuiKuRoot) {
                        KuTuiKuRoot kuTuiKuRoot = (KuTuiKuRoot) o;
                        if (kuTuiKuRoot != null && "0".equals(kuTuiKuRoot.getCode())) {
                            if (kuTuiKuRoot.getRows() != null) {
                                if (refresh) {
                                    mTuiKuList = kuTuiKuRoot.getRows();
                                    if (kuTuiKuRoot.getRows().size() == 0) {
                                        mNoData_rl.setVisibility(View.VISIBLE);
                                        no_mess_tv.setText("空空如也");
                                    } else {
                                        mNoData_rl.setVisibility(View.GONE);
                                        no_mess_tv.setText("");
                                    }
                                } else {
                                    for (int i = 0; i < kuTuiKuRoot.getRows().size(); i++) {
                                        mTuiKuList.add(kuTuiKuRoot.getRows().get(i));
                                    }
                                    if (kuTuiKuRoot.getRows().size() == 0) {
                                        Toast.makeText(KuActivity.this, "全部加载完毕", Toast.LENGTH_SHORT).show();
                                    }

                                }
                                kuAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(KuActivity.this, "请求退库数据错误", Toast.LENGTH_SHORT).show();
                            }


                        } else if (kuTuiKuRoot != null && "-1".equals(kuTuiKuRoot.getCode())) {
                            Toast.makeText(KuActivity.this, "登录过期,请重新登录", Toast.LENGTH_SHORT).show();
                            mNoData_rl.setVisibility(View.VISIBLE);
                            no_mess_tv.setText("登录过期,请重新登录");
                        } else {
                            Toast.makeText(KuActivity.this, "请求数据数据错误", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        BallProgressUtils.dismisLoading();
                        Toast.makeText(KuActivity.this, "连接服务器失败,请重新尝试", Toast.LENGTH_SHORT).show();
                    }

                }catch (Exception e){
                    e.printStackTrace();
                    mNoData_rl.setVisibility(View.VISIBLE);
                    no_mess_tv.setText("数据解析错误，请重新尝试");
                    Toast.makeText(KuActivity.this, "数据解析错误，请重新尝试", Toast.LENGTH_SHORT).show();
                }

            }

            else if (msg.what == 1010) {
                mNoData_rl.setVisibility(View.VISIBLE);
                no_mess_tv.setText("连接服务器失败,请检查网络");
                BallProgressUtils.dismisLoading();
                Toast.makeText(KuActivity.this, "连接服务器失败,请检查网络", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ku);
        mAll = (RelativeLayout) findViewById(R.id.activity_ku);
        mNoData_rl = (RelativeLayout) findViewById(R.id.no_data_rl);
        mNoData_rl.setOnClickListener(this);
        no_mess_tv= (TextView) findViewById(R.id.no_mess_tv);
        okHttpManager = OkHttpManager.getInstance();
        yanshou_url = URLTools.urlBase + URLTools.ku_yanshou;//验收接口
        lingyong_url = URLTools.urlBase + URLTools.ku_lingyong;//领用接口
        jieyong_url = URLTools.urlBase + URLTools.ku_jieying;//借用接口
        newold_url = URLTools.urlBase + URLTools.ku_oldnew;//以旧换新接口
        tuiku_url = URLTools.urlBase + URLTools.ku_tuiku;//退库接口
        okHttpManager.getMethod(false, yanshou_url + "start=" + start + "&limit=" + limit, "验收接口", handler, 1);
        initUI();
    }

    private void initUI() {
        mback = (ImageView) findViewById(R.id.back_img);
        mback.setOnClickListener(this);
        yanshou_tv = (TextView) findViewById(R.id.yanshou_btn);
        lingyong_tv = (TextView) findViewById(R.id.lingyong_btn);
        jieyong_tv = (TextView) findViewById(R.id.jieyong_btn);
        newold_tv = (TextView) findViewById(R.id.newold_btn);
        tuiku_tv = (TextView) findViewById(R.id.tuiku_btn);
        yanshou_tv.setOnClickListener(this);
        lingyong_tv.setOnClickListener(this);
        jieyong_tv.setOnClickListener(this);
        newold_tv.setOnClickListener(this);
        tuiku_tv.setOnClickListener(this);

        smartRefreshLayout = (SmartRefreshLayout) findViewById(R.id.ku_refresh);
        smartRefreshLayout.setRefreshHeader(new CircleHeader(this));
        smartRefreshLayout.setRefreshFooter(new BallPulseFooter(this).setSpinnerStyle(SpinnerStyle.Scale).setAnimatingColor(ContextCompat.getColor(this, R.color.color_1c82d4)));
        smartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshlayout.finishRefresh(2000);
                refresh = true;
                start = 0;
                if (clickFlag == 1) {//验收
                    okHttpManager.getMethod(false, yanshou_url + "start=" + start + "&limit=" + limit, "验收接口", handler, 1);
                } else if (clickFlag == 2) {//领用
                    okHttpManager.getMethod(false, lingyong_url + "start=" + start + "&limit=" + limit, "领用接口", handler, 2);
                } else if (clickFlag == 3) {//借用
                    okHttpManager.getMethod(false, jieyong_url + "start=" + start + "&limit=" + limit, "借用接口", handler, 3);
                } else if (clickFlag == 4) {//以旧换新
                    okHttpManager.getMethod(false, newold_url + "start=" + start + "&limit=" + limit, "以旧换新接口", handler, 4);
                } else if (clickFlag == 5) {//退库
                    okHttpManager.getMethod(false, tuiku_url + "start=" + start + "&limit=" + limit, "退库接口", handler, 5);
                }


            }
        });
        smartRefreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                refreshlayout.finishLoadmore(2000);
                refresh = false;
                start += 20;
                if (clickFlag == 1) {//验收
                    okHttpManager.getMethod(false, yanshou_url + "start=" + start + "&limit=" + limit, "验收接口", handler, 1);
                } else if (clickFlag == 2) {//领用
                    okHttpManager.getMethod(false, lingyong_url + "start=" + start + "&limit=" + limit, "领用接口", handler, 2);
                } else if (clickFlag == 3) {//借用
                    okHttpManager.getMethod(false, jieyong_url + "start=" + start + "&limit=" + limit, "借用接口", handler, 3);
                } else if (clickFlag == 4) {//以旧换新
                    okHttpManager.getMethod(false, newold_url + "start=" + start + "&limit=" + limit, "以旧换新接口", handler, 4);
                } else if (clickFlag == 5) {//退库
                    okHttpManager.getMethod(false, tuiku_url + "start=" + start + "&limit=" + limit, "退库接口", handler, 5);
                }

            }
        });


        mListView = (ListView) findViewById(R.id.ku_listview);
        kuAdapter = new KuAdapter();
        mListView.setAdapter(kuAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (clickFlag == 1) {//验收
                    if (0 == mYanShouList.get(i).getAcceptanceOpinion()) {//跳转待验收页面
                        Intent intent = new Intent(KuActivity.this, KuDaiYanShouMessActivity.class);
                        intent.putExtra("id", mYanShouList.get(i).getId());
                        startActivityForResult(intent, 1);
                    } else if (1 == mYanShouList.get(i).getAcceptanceOpinion()) {//跳转已验收页面或者已入库页面

                        if (mYanShouList.get(i).getBarcode()!=null&&!"".equals(mYanShouList.get(i).getBarcode())){//已入库
                            Intent intent = new Intent(KuActivity.this, PurchaseOrderMsgActivity.class);
                            intent.putExtra("id", mYanShouList.get(i).getId());
                            intent.putExtra("flag", 5);
                            startActivity(intent);
                        }else{//已验收(如果点击确定入库，返回这个页面需要刷新数据)
                            Intent intent = new Intent(KuActivity.this, PurchaseOrderMsgActivity.class);
                            intent.putExtra("id", mYanShouList.get(i).getId());
                            intent.putExtra("flag", 3);
                            startActivityForResult(intent,1);
                        }

                    } else {//跳转已退货页面
                        Intent intent = new Intent(KuActivity.this, PurchaseOrderMsgActivity.class);
                        intent.putExtra("id", mYanShouList.get(i).getId());
                        intent.putExtra("flag", 4);
                        startActivity(intent);
                    }

                } else if (clickFlag == 2) {//领用
                    Intent intent = new Intent(KuActivity.this, KuLingYongMessActivity.class);
                    intent.putExtra("referId", mLingYongList.get(i).getId());
                    startActivityForResult(intent, 2);
                } else if (clickFlag == 3) {//借用
                    Intent intent = new Intent(KuActivity.this, KuJieYongMessActivity.class);
                    intent.putExtra("referId", mJieYongList.get(i).getId());
                    startActivityForResult(intent, 3);
                } else if (clickFlag == 4) {//以旧换新
                    Intent intent = new Intent(KuActivity.this, KuNewOldMessActivity.class);
                    intent.putExtra("referId", mNewOldList.get(i).getId());
                    startActivityForResult(intent, 4);
                } else if (clickFlag == 5) {//退库
                    Intent intent = new Intent(KuActivity.this, KuTuiKuMessActivity.class);
                    intent.putExtra("referId", mTuiKuList.get(i).getId());
                    startActivityForResult(intent, 5);
                }

            }
        });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == mback.getId()) {
            finish();
        } else if (id == yanshou_tv.getId()) {
            mLingYongList.clear();
            mJieYongList.clear();
            mNewOldList.clear();
            mTuiKuList.clear();
            kuAdapter.notifyDataSetChanged();
            BallProgressUtils.showLoading(KuActivity.this, mAll);
            chaneColor(1);
            refresh = true;
            start = 0;
            clickFlag = 1;
            okHttpManager.getMethod(false, yanshou_url + "start=" + start + "&limit=" + limit, "验收接口", handler, 1);
        } else if (id == lingyong_tv.getId()) {
            mYanShouList.clear();
            mJieYongList.clear();
            mNewOldList.clear();
            mTuiKuList.clear();
            kuAdapter.notifyDataSetChanged();
            BallProgressUtils.showLoading(KuActivity.this, mAll);
            chaneColor(2);
            refresh = true;
            start = 0;
            clickFlag = 2;
            okHttpManager.getMethod(false, lingyong_url + "start=" + start + "&limit=" + limit, "领用接口", handler, 2);
        } else if (id == jieyong_tv.getId()) {
            mYanShouList.clear();
            mLingYongList.clear();
            mNewOldList.clear();
            mTuiKuList.clear();
            BallProgressUtils.showLoading(KuActivity.this, mAll);
            kuAdapter.notifyDataSetChanged();
            chaneColor(3);
            refresh = true;
            start = 0;
            clickFlag = 3;
            okHttpManager.getMethod(false, jieyong_url + "start=" + start + "&limit=" + limit, "借用接口", handler, 3);

        } else if (id == newold_tv.getId()) {
            mYanShouList.clear();
            mJieYongList.clear();
            mLingYongList.clear();
            mTuiKuList.clear();
            kuAdapter.notifyDataSetChanged();
            BallProgressUtils.showLoading(KuActivity.this, mAll);
            chaneColor(4);
            refresh = true;
            start = 0;
            clickFlag = 4;
            okHttpManager.getMethod(false, newold_url + "start=" + start + "&limit=" + limit, "以旧换新接口", handler, 4);
        } else if (id == tuiku_tv.getId()) {
            mYanShouList.clear();
            mJieYongList.clear();
            mNewOldList.clear();
            mLingYongList.clear();
            kuAdapter.notifyDataSetChanged();
            BallProgressUtils.showLoading(KuActivity.this, mAll);
            chaneColor(5);
            refresh = true;
            start = 0;
            clickFlag = 5;
            okHttpManager.getMethod(false, tuiku_url + "start=" + start + "&limit=" + limit, "退库接口", handler, 5);

        }else if (id==mNoData_rl.getId()){
            BallProgressUtils.showLoading(KuActivity.this, mAll);
            refresh = true;
            start = 0;
            if (clickFlag == 1) {//验收
                okHttpManager.getMethod(false, yanshou_url + "start=" + start + "&limit=" + limit, "验收接口", handler, 1);
            } else if (clickFlag == 2) {//领用
                okHttpManager.getMethod(false, lingyong_url + "start=" + start + "&limit=" + limit, "领用接口", handler, 2);
            } else if (clickFlag == 3) {//借用
                okHttpManager.getMethod(false, jieyong_url + "start=" + start + "&limit=" + limit, "借用接口", handler, 3);
            } else if (clickFlag == 4) {//以旧换新
                okHttpManager.getMethod(false, newold_url + "start=" + start + "&limit=" + limit, "以旧换新接口", handler, 4);
            } else if (clickFlag == 5) {//退库
                okHttpManager.getMethod(false, tuiku_url + "start=" + start + "&limit=" + limit, "退库接口", handler, 5);
            }
        }
    }

    private void chaneColor(int flag) {
        if (flag == 1) {//变换验收颜色，背景
            yanshou_tv.setBackgroundResource(R.color.color_30a2d4);
            yanshou_tv.setTextColor(ContextCompat.getColor(KuActivity.this, R.color.color_ffffff));

            lingyong_tv.setBackgroundResource(R.color.color_ffffff);
            lingyong_tv.setTextColor(ContextCompat.getColor(KuActivity.this, R.color.color_373a41));

            jieyong_tv.setBackgroundResource(R.color.color_ffffff);
            jieyong_tv.setTextColor(ContextCompat.getColor(KuActivity.this, R.color.color_373a41));

            newold_tv.setBackgroundResource(R.color.color_ffffff);
            newold_tv.setTextColor(ContextCompat.getColor(KuActivity.this, R.color.color_373a41));

            tuiku_tv.setBackgroundResource(R.color.color_ffffff);
            tuiku_tv.setTextColor(ContextCompat.getColor(KuActivity.this, R.color.color_373a41));

        } else if (flag == 2) {//变领用收颜色，背景
            lingyong_tv.setBackgroundResource(R.color.color_30a2d4);
            lingyong_tv.setTextColor(ContextCompat.getColor(KuActivity.this, R.color.color_ffffff));

            yanshou_tv.setBackgroundResource(R.color.color_ffffff);
            yanshou_tv.setTextColor(ContextCompat.getColor(KuActivity.this, R.color.color_373a41));

            jieyong_tv.setBackgroundResource(R.color.color_ffffff);
            jieyong_tv.setTextColor(ContextCompat.getColor(KuActivity.this, R.color.color_373a41));

            newold_tv.setBackgroundResource(R.color.color_ffffff);
            newold_tv.setTextColor(ContextCompat.getColor(KuActivity.this, R.color.color_373a41));

            tuiku_tv.setBackgroundResource(R.color.color_ffffff);
            tuiku_tv.setTextColor(ContextCompat.getColor(KuActivity.this, R.color.color_373a41));
        } else if (flag == 3) {//变借用收颜色，背景
            jieyong_tv.setBackgroundResource(R.color.color_30a2d4);
            jieyong_tv.setTextColor(ContextCompat.getColor(KuActivity.this, R.color.color_ffffff));

            yanshou_tv.setBackgroundResource(R.color.color_ffffff);
            yanshou_tv.setTextColor(ContextCompat.getColor(KuActivity.this, R.color.color_373a41));

            lingyong_tv.setBackgroundResource(R.color.color_ffffff);
            lingyong_tv.setTextColor(ContextCompat.getColor(KuActivity.this, R.color.color_373a41));

            newold_tv.setBackgroundResource(R.color.color_ffffff);
            newold_tv.setTextColor(ContextCompat.getColor(KuActivity.this, R.color.color_373a41));

            tuiku_tv.setBackgroundResource(R.color.color_ffffff);
            tuiku_tv.setTextColor(ContextCompat.getColor(KuActivity.this, R.color.color_373a41));
        } else if (flag == 4) {//变以旧换新收颜色，背景
            newold_tv.setBackgroundResource(R.color.color_30a2d4);
            newold_tv.setTextColor(ContextCompat.getColor(KuActivity.this, R.color.color_ffffff));

            yanshou_tv.setBackgroundResource(R.color.color_ffffff);
            yanshou_tv.setTextColor(ContextCompat.getColor(KuActivity.this, R.color.color_373a41));

            lingyong_tv.setBackgroundResource(R.color.color_ffffff);
            lingyong_tv.setTextColor(ContextCompat.getColor(KuActivity.this, R.color.color_373a41));

            jieyong_tv.setBackgroundResource(R.color.color_ffffff);
            jieyong_tv.setTextColor(ContextCompat.getColor(KuActivity.this, R.color.color_373a41));

            tuiku_tv.setBackgroundResource(R.color.color_ffffff);
            tuiku_tv.setTextColor(ContextCompat.getColor(KuActivity.this, R.color.color_373a41));
        } else if (flag == 5) {//变退库收颜色，背景
            tuiku_tv.setBackgroundResource(R.color.color_30a2d4);
            tuiku_tv.setTextColor(ContextCompat.getColor(KuActivity.this, R.color.color_ffffff));

            yanshou_tv.setBackgroundResource(R.color.color_ffffff);
            yanshou_tv.setTextColor(ContextCompat.getColor(KuActivity.this, R.color.color_373a41));

            lingyong_tv.setBackgroundResource(R.color.color_ffffff);
            lingyong_tv.setTextColor(ContextCompat.getColor(KuActivity.this, R.color.color_373a41));

            jieyong_tv.setBackgroundResource(R.color.color_ffffff);
            jieyong_tv.setTextColor(ContextCompat.getColor(KuActivity.this, R.color.color_373a41));

            newold_tv.setBackgroundResource(R.color.color_ffffff);
            newold_tv.setTextColor(ContextCompat.getColor(KuActivity.this, R.color.color_373a41));
        }
    }

    class KuAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (clickFlag == 1) {//验收
                return mYanShouList.size();
            } else if (clickFlag == 2) {//领用
                return mLingYongList.size();
            } else if (clickFlag == 3) {//借用
                return mJieYongList.size();
            } else if (clickFlag == 4) {//以旧换新
                return mNewOldList.size();
            } else if (clickFlag == 5) {//退库
                return mTuiKuList.size();
            }
            return 0;


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
            KuHolder kuholder = null;
            if (view == null) {
                kuholder = new KuHolder();
                view = LayoutInflater.from(KuActivity.this).inflate(R.layout.order_listview_item, null);
                kuholder.headImg = view.findViewById(R.id.order_head_img);
                kuholder.person = view.findViewById(R.id.order_person);
                kuholder.name = view.findViewById(R.id.order_name);
                kuholder.date = view.findViewById(R.id.order_date);
                kuholder.status = view.findViewById(R.id.order_status);
                view.setTag(kuholder);
            } else {
                kuholder = (KuHolder) view.getTag();
            }


            Picasso.with(KuActivity.this).load(R.mipmap.head).into(kuholder.headImg);

            if (clickFlag == 1) {//验收
                kuholder.person.setText(mYanShouList.get(i).getDepartmentName() + "  " + mYanShouList.get(i).getApplyUserName());
                kuholder.name.setVisibility(View.VISIBLE);
                kuholder.name.setText(mYanShouList.get(i).getAssetName() + "");
                kuholder.date.setText(mYanShouList.get(i).getApplyTimeString() + "");
                if (0 == mYanShouList.get(i).getAcceptanceOpinion()) {
                    kuholder.status.setText("待验收");
                    kuholder.status.setTextColor(ContextCompat.getColor(KuActivity.this, R.color.color_dc8268));
                } else if (1 == mYanShouList.get(i).getAcceptanceOpinion()) {

                    if (mYanShouList.get(i).getBarcode()!=null&&!"".equals(mYanShouList.get(i).getBarcode())){
                        kuholder.status.setText("已入库");
                        kuholder.status.setTextColor(ContextCompat.getColor(KuActivity.this, R.color.color_23b880));
                    }else {
                        kuholder.status.setText("已验收");
                        kuholder.status.setTextColor(ContextCompat.getColor(KuActivity.this, R.color.color_30a2d4));
                    }

                } else {
                    kuholder.status.setText("已退货");
                    kuholder.status.setTextColor(ContextCompat.getColor(KuActivity.this, R.color.red));
                }
            } else if (clickFlag == 2) {//领用
                kuholder.person.setText(mLingYongList.get(i).getDepartmentName() + "  " + mLingYongList.get(i).getUserName());
                kuholder.name.setVisibility(View.GONE);
                kuholder.date.setText(mLingYongList.get(i).getRecipientsDateString() + "");
                if ("".equals(mLingYongList.get(i).getOutboundDateString())) {
                    kuholder.status.setText("未领用");
                    kuholder.status.setTextColor(ContextCompat.getColor(KuActivity.this, R.color.color_dc8268));
                } else {
                    kuholder.status.setText("已领用");
                    kuholder.status.setTextColor(ContextCompat.getColor(KuActivity.this, R.color.color_23b880));
                }

            } else if (clickFlag == 3) {//借用
                kuholder.person.setText(mJieYongList.get(i).getDepartmentName() + "  " + mJieYongList.get(i).getUserName());
                kuholder.name.setVisibility(View.GONE);
                kuholder.date.setText(mJieYongList.get(i).getBorrowDateString() + "");
                if ("".equals(mJieYongList.get(i).getOutboundDateString())) {
                    kuholder.status.setText("未借用");
                    kuholder.status.setTextColor(ContextCompat.getColor(KuActivity.this, R.color.color_dc8268));
                } else {
                    if ("".equals(mJieYongList.get(i).getInboundDateString())) {
                        kuholder.status.setText("未归还");
                        kuholder.status.setTextColor(ContextCompat.getColor(KuActivity.this, R.color.red));
                    } else {
                        kuholder.status.setText("已归还");
                        kuholder.status.setTextColor(ContextCompat.getColor(KuActivity.this, R.color.color_23b880));
                    }

                }
            } else if (clickFlag == 4) {//以旧换新
                kuholder.person.setText(mNewOldList.get(i).getDepartmentName() + "  " + mNewOldList.get(i).getUserName());
                kuholder.name.setVisibility(View.VISIBLE);
                kuholder.name.setText(mNewOldList.get(i).getAssetName() + "");
                kuholder.date.setText(mNewOldList.get(i).getChangeDateString() + "");
                if ("".equals(mNewOldList.get(i).getOutboundDateString())) {
                    kuholder.status.setText("未换新");
                    kuholder.status.setTextColor(ContextCompat.getColor(KuActivity.this, R.color.color_dc8268));
                } else {
                    kuholder.status.setText("已换新");
                    kuholder.status.setTextColor(ContextCompat.getColor(KuActivity.this, R.color.color_23b880));

                }


            } else if (clickFlag == 5) {//退库
                kuholder.person.setText(mTuiKuList.get(i).getDepartmentName() + "  " + mTuiKuList.get(i).getUserName());
                kuholder.name.setVisibility(View.GONE);
                kuholder.date.setText(mTuiKuList.get(i).getReturnDateString() + "");
                if ("".equals(mTuiKuList.get(i).getInboundDateString())) {
                    kuholder.status.setText("未入库");
                    kuholder.status.setTextColor(ContextCompat.getColor(KuActivity.this, R.color.color_dc8268));
                } else {
                    kuholder.status.setText("已入库");
                    kuholder.status.setTextColor(ContextCompat.getColor(KuActivity.this, R.color.color_23b880));
                }
            }

            return view;
        }

        class KuHolder {
            CircleImg headImg;
            TextView person, name, date, status;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {//刷新验收
            refresh = true;
            start = 0;
            okHttpManager.getMethod(false, yanshou_url + "start=" + start + "&limit=" + limit, "验收接口", handler, 1);
        } else if (requestCode == 2 && resultCode == RESULT_OK) {//领用刷新
            //领用刷新
            refresh = true;
            start = 0;
            okHttpManager.getMethod(false, lingyong_url + "start=" + start + "&limit=" + limit, "领用接口", handler, 2);

        } else if (requestCode == 3 && resultCode == RESULT_OK) {//刷新借用
            //刷新验收
            refresh = true;
            start = 0;
            okHttpManager.getMethod(false, jieyong_url + "start=" + start + "&limit=" + limit, "借用接口", handler, 3);

        } else if (requestCode == 4 && resultCode == RESULT_OK) {//刷新以旧换新
            //以旧换新
            refresh = true;
            start = 0;
            okHttpManager.getMethod(false, newold_url + "start=" + start + "&limit=" + limit, "以旧换新", handler, 4);

        } else if (requestCode == 5 && resultCode == RESULT_OK) {//刷新退库
            //刷新退库
            refresh = true;
            start = 0;
            okHttpManager.getMethod(false, tuiku_url + "start=" + start + "&limit=" + limit, "刷新退库", handler, 5);

        }
    }
}
