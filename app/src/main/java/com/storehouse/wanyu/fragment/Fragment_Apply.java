package com.storehouse.wanyu.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.storehouse.wanyu.R;
import com.storehouse.wanyu.activity.ApplyActivity.ApplyBaoFeiActivity;
import com.storehouse.wanyu.activity.ApplyActivity.ApplyCaiGouActivity;
import com.storehouse.wanyu.activity.ApplyActivity.ApplyGuiHuanActivity;
import com.storehouse.wanyu.activity.ApplyActivity.ApplyJieYongActivity;
import com.storehouse.wanyu.activity.ApplyActivity.ApplyLingYongActivity;
import com.storehouse.wanyu.activity.ApplyActivity.ApplyNewOldActivity;
import com.storehouse.wanyu.activity.ApplyActivity.ApplyStatusBbh2Activity;
import com.storehouse.wanyu.activity.ApplyActivity.ApplyStatusSpz1Activity;
import com.storehouse.wanyu.activity.ApplyActivity.ApplyStatusYsx4Activity;
import com.storehouse.wanyu.activity.ApplyActivity.ApplyStatusYwc3Activity;
import com.storehouse.wanyu.activity.ApplyActivity.ApplyTuiKuActivity;
import com.storehouse.wanyu.activity.ApplyActivity.ApplyWeiXiuActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * 申请
 */
public class Fragment_Apply extends Fragment implements AdapterView.OnItemClickListener,View.OnClickListener {

    private GridView mApplyGridView;
    private List<String> mNameList = new ArrayList();
    private List<Integer> mColorList = new ArrayList();
    private int mFlag = 0;
    private LinearLayout mSpz_ll,mBbh_ll,mYwc_ll,mYsx_ll;//审批中，被驳回，已完成，已失效
    public Fragment_Apply() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.e("申请", "onCreateView");
        View view = inflater.inflate(R.layout.fragment_fragment__apply, container, false);

        initUI(view);

        mFlag += 1;
        return view;
    }

    private void initUI(View view) {

            mNameList.add("采购申请");
            mNameList.add("领用申请");
            mNameList.add("借用申请");
            mNameList.add("维修申请");
            mNameList.add("以旧换新");
            mNameList.add("报废申请");
          //  mNameList.add("归还申请");
            mNameList.add("退库申请");
            mColorList.add(R.color.color_one);
            mColorList.add(R.color.color_two);
            mColorList.add(R.color.color_three);
            mColorList.add(R.color.color_four);
            mColorList.add(R.color.color_five);
            mColorList.add(R.color.color_one);
           // mColorList.add(R.color.color_two);
            mColorList.add(R.color.color_three);

        mApplyGridView = view.findViewById(R.id.apply_category_gridview);
        mApplyGridView.setAdapter(new ApplyAdapter(mNameList, mColorList));
        mApplyGridView.setOnItemClickListener(this);
        //审批中，被驳回，已完成，已失效
        mSpz_ll=view.findViewById(R.id.spz_ll);
        mBbh_ll=view.findViewById(R.id.bbh_ll);
        mYwc_ll=view.findViewById(R.id.ywc_ll);
        mYsx_ll=view.findViewById(R.id.ysx_ll);
        mSpz_ll.setOnClickListener(this);
        mBbh_ll.setOnClickListener(this);
        mYwc_ll.setOnClickListener(this);
        mYsx_ll.setOnClickListener(this);

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        switch (i) {
            //采购申请
            case 0:
                Intent intent=new Intent(getActivity(),ApplyCaiGouActivity.class);
                startActivity(intent);
                break;
            //领用申请
            case 1:
                Intent intent1=new Intent(getActivity(),ApplyLingYongActivity.class);
                startActivity(intent1);
                break;
            //借用申请
            case 2:
                Intent intent2=new Intent(getActivity(),ApplyJieYongActivity.class);
                startActivity(intent2);
                break;
            //维修申请
            case 3:
                Intent intent3=new Intent(getActivity(),ApplyWeiXiuActivity.class);
                startActivity(intent3);
                break;
            //以旧换新
            case 4:
                Intent intent4=new Intent(getActivity(),ApplyNewOldActivity.class);
                startActivity(intent4);
                break;
            //报废申请
            case 5:
                Intent intent5=new Intent(getActivity(),ApplyBaoFeiActivity.class);
                startActivity(intent5);
                break;
            //退库申请
            case 6:
                Intent intent6=new Intent(getActivity(),ApplyTuiKuActivity.class);
                startActivity(intent6);
                break;
//            //归还申请
//            case 7:
//                Intent intent7=new Intent(getActivity(),ApplyGuiHuanActivity.class);
//                startActivity(intent7);
//                break;
        }

    }

    @Override
    public void onClick(View view) {
        int id=view.getId();
        switch (id){
            //审批中，，，
            case R.id.spz_ll:
                Intent intent=new Intent(getContext(),ApplyStatusSpz1Activity.class);
                startActivity(intent);
                break;
            //被驳回
            case R.id.bbh_ll:
                Intent intent1=new Intent(getContext(),ApplyStatusBbh2Activity.class);
                startActivity(intent1);
                break;
            //已完成
            case R.id.ywc_ll:
                Intent intent2=new Intent(getContext(),ApplyStatusYwc3Activity.class);
                startActivity(intent2);
                break;
            //已失效
            case R.id.ysx_ll:
                Intent intent3=new Intent(getContext(),ApplyStatusYsx4Activity.class);
                startActivity(intent3);
                break;
        }
    }

    //适配器
    class ApplyAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private List<String> nameList = new ArrayList();
        private List<Integer> colorList = new ArrayList();

        public ApplyAdapter(List nameList, List colorList) {
            this.nameList = nameList;
            this.colorList = colorList;
            mInflater = LayoutInflater.from(getContext());
        }

        @Override
        public int getCount() {
            return nameList.size();
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
            ApplyHolder applyHolder = null;
            if (view == null) {
                applyHolder = new ApplyHolder();
                view = mInflater.inflate(R.layout.apply_gridview_item, null);
                applyHolder.name = view.findViewById(R.id.apply_tv);//申请类型
                view.setTag(applyHolder);
            } else {
                applyHolder = (ApplyHolder) view.getTag();
            }
            //设置数据
            applyHolder.name.setText(nameList.get(i));
            applyHolder.name.setBackgroundResource(colorList.get(i));
            return view;
        }

        class ApplyHolder {
            TextView name;
        }
    }
}
