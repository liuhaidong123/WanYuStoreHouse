package com.storehouse.wanyu.activity.AllBBHDetailsActivity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.storehouse.wanyu.R;
//被驳回借用申请详情
public class BBHJieYongApplyDetailsActivity extends AppCompatActivity implements View.OnClickListener{
    private ImageView mBack_img;
    private ListView mListview;
    private TextView mStatus_Tv;
    private BBHJieYongAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bbhjie_yong_apply_details);
        initUI();
    }
    private void initUI() {
        mBack_img = (ImageView) findViewById(R.id.back_img);
        mBack_img.setOnClickListener(this);

        mAdapter = new BBHJieYongAdapter();
        mListview = (ListView) findViewById(R.id.spz_jieyong_listview_id);
        mListview.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == mBack_img.getId()) {
            finish();
        }
    }
    //领用用明细适配器
    class BBHJieYongAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 5;
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
            BBHJYHolder bbhjyHolder = null;
            if (view == null) {
                view = LayoutInflater.from(BBHJieYongApplyDetailsActivity.this).inflate(R.layout.spz_lingyong_details_table_listview_item, null);
                bbhjyHolder = new BBHJYHolder();

                bbhjyHolder.num = view.findViewById(R.id.num_tv);
                bbhjyHolder.bumen = view.findViewById(R.id.bm_tv);
                bbhjyHolder.person = view.findViewById(R.id.person_tv);
                bbhjyHolder.name = view.findViewById(R.id.name_tv);
                bbhjyHolder.beizhu = view.findViewById(R.id.beizhu_tv);
                view.setTag(bbhjyHolder);
            } else {
                bbhjyHolder = (BBHJYHolder) view.getTag();

            }
            //设置数据

            return view;
        }

        class BBHJYHolder {
            TextView num, bumen, person, name, beizhu;//编号，申请部门，申请人，物品名称,备注
        }
    }
}
