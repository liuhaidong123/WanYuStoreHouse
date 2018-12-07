package com.storehouse.wanyu.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.storehouse.wanyu.Bean.NotifyRows;
import com.storehouse.wanyu.R;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by liuhaidong on 2018/5/30.
 */

public class FirstPageListViewAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater inflater;
    private List<NotifyRows> list=new ArrayList<>();
    public FirstPageListViewAdapter(Context mContext,List<NotifyRows> list) {
        this.mContext = mContext;
        inflater=LayoutInflater.from(mContext);
        this.list=list;
    }

    public void setList(List<NotifyRows> list){
        this.list=list;
    }
    @Override
    public int getCount() {
        return list.size();
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
            MyFirstHolder myFirstHolder=null;
        if (view==null){
            myFirstHolder=new MyFirstHolder();
            view=inflater.inflate(R.layout.firstpage_listview_item,null);
            myFirstHolder.message=view.findViewById(R.id.message_tv);//信息
            myFirstHolder.date=view.findViewById(R.id.date_tv);//日期
            myFirstHolder.imageView=view.findViewById(R.id.notification_img);//
            view.setTag(myFirstHolder);
        }else {
            myFirstHolder= (MyFirstHolder) view.getTag();
        }
        //填充数据
        myFirstHolder.message.setText(list.get(i).getTitle()+"");
        myFirstHolder.date.setText(list.get(i).getCreateTimeString()+"");
        return view;
    }

    class MyFirstHolder{
        TextView message,date;
       ImageView imageView;
    }
}
