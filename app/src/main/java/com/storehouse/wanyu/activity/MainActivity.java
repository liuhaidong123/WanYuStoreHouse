package com.storehouse.wanyu.activity;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.storehouse.wanyu.MyUtils.SharedPrefrenceTools;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
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
}
