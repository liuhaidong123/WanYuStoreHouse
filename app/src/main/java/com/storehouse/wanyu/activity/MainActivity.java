package com.storehouse.wanyu.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.storehouse.wanyu.Bean.VersonCodeRoot;
import com.storehouse.wanyu.Bean.VersonCodeRows;
import com.storehouse.wanyu.IPAddress.URLTools;
import com.storehouse.wanyu.MyUtils.ActivityListUtils;
import com.storehouse.wanyu.MyUtils.SharedPrefrenceTools;
import com.storehouse.wanyu.OkHttpUtils.OkHttpManager;
import com.storehouse.wanyu.R;
import com.storehouse.wanyu.adapter.MainFragmentAdapter;
import com.storehouse.wanyu.fragment.Fragment_Apply;
import com.storehouse.wanyu.fragment.Fragment_FirstPage;
import com.storehouse.wanyu.fragment.Fragment_My;
import com.storehouse.wanyu.fragment.Fragment_Property;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ViewPager mViewpager;
    private MainFragmentAdapter mFragmentAda;
    private Fragment_FirstPage mFragment_firstPage;
    private Fragment_Apply mFragment_Apply;
    private Fragment_Property mFragment_Property;
    private Fragment_My mFragment_My;
    private FragmentManager mFragmentManager;
    private List<Fragment> mListFragment = new ArrayList<>();
    private TabLayout mTablayout;

    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;
    private int mVersonCode;//本地版本号
    private Gson gson = new Gson();
    private String url;
    private OkHttpManager okHttpManager;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                String s = (String) msg.obj;
                Object o = gson.fromJson(s, VersonCodeRoot.class);
                if (o != null && o instanceof VersonCodeRoot) {
                    VersonCodeRoot versonCodeRoot = (VersonCodeRoot) o;
                    if (versonCodeRoot != null && "0".equals(versonCodeRoot.getCode())) {
                        //判断服务器版本号与本地版本号
                        VersonCodeRows versonCodeRows = versonCodeRoot.getDict();
                        if (versonCodeRows != null) {

                            String versonçode = versonCodeRows.getValue();
                            Integer code = Integer.valueOf(versonçode);
                            if (code>mVersonCode){
                                //弹框更新(等待上线后再测试)
                               alertDialog.show();
                            }
                        }


                    } else {
                        Toast.makeText(MainActivity.this, "获取版本号失败", Toast.LENGTH_SHORT).show();
                    }


                } else {
                    Toast.makeText(MainActivity.this, "获取版本号错误", Toast.LENGTH_SHORT).show();
                }


            } else {
                Toast.makeText(MainActivity.this, "连接服务器失败,请重新尝试", Toast.LENGTH_SHORT).show();
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityListUtils.getActivityListUtilsInstance().saveMainActivity(this);
        setContentView(R.layout.activity_main);
        initUI();
        //获取本地版本号
        mVersonCode=getVersionCode();
        //检测版本号
        okHttpManager = OkHttpManager.getInstance();
        url = URLTools.urlBase + URLTools.check_verson_code;
        okHttpManager.getMethod(false, url, "检测版本号", handler, 1);
        builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage("检测到最新版本,请更新");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.setData(Uri.parse("http://sj.qq.com/myapp/search.htm?kw=%E5%AE%87%E5%8C%BB"));
//                startActivity(intent);
//                alertDialog.dismiss();
//                finish();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://59.110.169.148:8085/static/app-release.apk"));
                startActivity(intent);
                alertDialog.dismiss();
               // launchAppDetail(MainActivity.this,"com.storehouse.wanyu","com.tencent.android.qqdownloader");
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialog.dismiss();
            }
        });
        alertDialog = builder.create();
    }

    private void initUI() {

        Log.e("name=", (String) SharedPrefrenceTools.getValueByKey("name", "name=null"));
        Log.e("password", (String) SharedPrefrenceTools.getValueByKey("password", "password=null"));
        Log.e("cookie", (String) SharedPrefrenceTools.getValueByKey("cookie", "cookie=null"));
        Log.e("departmentName", (String) SharedPrefrenceTools.getValueByKey("departmentName", "departmentName=null"));

        mViewpager = (ViewPager) findViewById(R.id.main_viewpager);
        mFragmentManager = getSupportFragmentManager();
        mFragment_firstPage = new Fragment_FirstPage();
        mFragment_Apply = new Fragment_Apply();
        mFragment_Property = new Fragment_Property();
        mFragment_My = new Fragment_My();
        mListFragment.add(mFragment_firstPage);
        mListFragment.add(mFragment_Apply);
        mListFragment.add(mFragment_Property);
        mListFragment.add(mFragment_My);
        mFragmentAda = new MainFragmentAdapter(mFragmentManager, mListFragment, this);
        mViewpager.setAdapter(mFragmentAda);

        mTablayout = (TabLayout) findViewById(R.id.main_tablayout);
        mTablayout.setupWithViewPager(mViewpager);//setupWithViewPager必须在ViewPager.setAdapter()之后调用
        mTablayout.setTabMode(TabLayout.MODE_FIXED);
        for (int i = 0; i < mTablayout.getTabCount(); i++) {
            TabLayout.Tab tab = mTablayout.getTabAt(i);
            tab.setCustomView(mFragmentAda.getTabView(i));
        }

        mTablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                View view = tab.getCustomView();
                TextView tv = view.findViewById(R.id.icon_tv);
                ImageView img2 = view.findViewById(R.id.icon_img);
                tv.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.color_22b2e7));
                if (tab.getPosition() == 0) {
                    img2.setImageResource(R.mipmap.main_firstpage_icon);
                } else if (tab.getPosition() == 1) {
                    img2.setImageResource(R.mipmap.main_shenqing_icon);
                } else if (tab.getPosition() == 2) {
                    img2.setImageResource(R.mipmap.main_shenpi_icon);
                } else if (tab.getPosition() == 3) {
                    img2.setImageResource(R.mipmap.main_my_icon);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                View view = tab.getCustomView();
                TextView tv = view.findViewById(R.id.icon_tv);
                tv.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.color_a5a8af));
                ImageView img2 = view.findViewById(R.id.icon_img);
                if (tab.getPosition() == 0) {
                    img2.setImageResource(R.mipmap.main_no_firstpage_icon);
                } else if (tab.getPosition() == 1) {
                    img2.setImageResource(R.mipmap.main_no_shenqing_icon);
                } else if (tab.getPosition() == 2) {
                    img2.setImageResource(R.mipmap.main_no_shenpi_icon);
                } else if (tab.getPosition() == 3) {
                    img2.setImageResource(R.mipmap.main_no_my_icon);
                }
            }

            //再次被选中
            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    long time = 0;

    private int getVersionCode() {
        // 获取packagemanager的实例
        PackageManager packageManager = getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        int versionCode = packInfo.versionCode;
        Log.e("本地软件版本号=", versionCode + "");
        return versionCode;
    }

    /**
     * 启动到app详情界面
     *
     * @param appPkg
     *            App的包名
     * @param marketPkg
     *            应用商店包名 ,如果为""则由系统弹出应用商店列表供用户选择,否则调转到目标市场的应用详情界面，某些应用商店可能会失败
     */
    public static void launchAppDetail(Context context, String appPkg, String marketPkg) {
        try {
            if (TextUtils.isEmpty(appPkg)){
                return;
            }else {
                Uri uri = Uri.parse("market://details?id=" + appPkg);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                if (!TextUtils.isEmpty(marketPkg)){
                    intent.setPackage(marketPkg);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onBackPressed() {
        if (time > 0) {
            if (System.currentTimeMillis() - time < 2000) {
                super.onBackPressed();
            } else {
                time = System.currentTimeMillis();
                Toast.makeText(MainActivity.this, "再按一次退出", Toast.LENGTH_SHORT).show();
            }

        } else {
            time = System.currentTimeMillis();
            Toast.makeText(MainActivity.this, "再按一次退出", Toast.LENGTH_SHORT).show();

        }
    }
}
