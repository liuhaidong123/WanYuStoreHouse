package com.storehouse.wanyu.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.storehouse.wanyu.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuhaidong on 2018/5/29.
 */

//public class FirstpageRecycleAdapter extends RecyclerView.Adapter<FirstpageRecycleAdapter.MyHolder> {
//    private List<Integer> mList = new ArrayList<>();
//    private List<String> mNameList = new ArrayList<>();
//    private Context mContext;
//    private LayoutInflater inflater;
//
//    public FirstpageRecycleAdapter(List<Integer> list, List<String> nameList, Context mContext) {
//        this.mList = list;
//        this.mNameList = nameList;
//        this.mContext = mContext;
//        this.inflater = LayoutInflater.from(mContext);
//    }
//
//    //初始化viewholder,并且设置item布局
//    @Override
//    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View view = inflater.inflate(R.layout.recycle_item, null);
//        MyHolder myHolder = new MyHolder(view);
//        return myHolder;
//    }
//
//    //设置数据
//    @Override
//    public void onBindViewHolder(final MyHolder holder, final int position) {
//        holder.textView.setText(mNameList.get(position));
//        Picasso.with(mContext).load(mList.get(position)).into(holder.imageView);
//        Log.e("onBindViewHolder", "=" + position);
//        if (mOnItemClickListener != null) {
//            holder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    //接口赋值
//                    mOnItemClickListener.onItemClick(holder.itemView, position);
//                    Log.e("adapter点击了", "=" + position);
//                }
//            });
//        }
//    }
//
//    @Override
//    public int getItemCount() {
//        return mList.size();
//    }
//
//    class MyHolder extends RecyclerView.ViewHolder {
//        ImageView imageView;
//        TextView textView;
//
//        public MyHolder(View itemView) {
//            super(itemView);
//            imageView = itemView.findViewById(R.id.imageview);
//            textView = itemView.findViewById(R.id.textview);
//        }
//    }
//
//    /**
//     * ItemClick的回调接口
//     */
//
//    public interface OnItemClickListener {
//        void onItemClick(View view, int positon);
//    }
//
//    public OnItemClickListener mOnItemClickListener;
//
//    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
//        this.mOnItemClickListener = onItemClickListener;
//    }
//}
