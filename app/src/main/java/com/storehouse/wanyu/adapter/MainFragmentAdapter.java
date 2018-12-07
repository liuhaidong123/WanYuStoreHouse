package com.storehouse.wanyu.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.storehouse.wanyu.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuhaidong on 2018/5/28.
 */

public class MainFragmentAdapter extends FragmentPagerAdapter {
    private Context mContext;
    private List<Fragment> mList = new ArrayList<>();
    private String[] str = new String[]{"首页", "申请", "审批", "我的"};
    private int[] iconImg = {R.mipmap.main_firstpage_icon, R.mipmap.main_no_shenqing_icon, R.mipmap.main_no_shenpi_icon, R.mipmap.main_no_my_icon};

    public MainFragmentAdapter(FragmentManager fm, List<Fragment> list, Context context) {
        super(fm);
        this.mList = list;
        this.mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        return mList.get(position);
    }

    @Override
    public int getCount() {
        return mList != null ? mList.size() : 0;
    }

    //自定义tablayout布局

    public View getTabView(int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.tablayout_item, null);
        TextView tv = view.findViewById(R.id.icon_tv);
        ImageView img = view.findViewById(R.id.icon_img);
        tv.setText(str[position]);
        img.setImageResource(iconImg[position]);
        if (position == 0) {
            tv.setTextColor(ContextCompat.getColor(mContext, R.color.color_22b2e7));
        } else {
            tv.setTextColor(ContextCompat.getColor(mContext, R.color.color_a5a8af));
        }
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
       // super.destroyItem(container, position, object);
    }
}
