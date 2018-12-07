package com.storehouse.wanyu.activity.AllBBHDetailsActivity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.storehouse.wanyu.R;

//被驳回归还详情
public class BBHGuiHuanApplyDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView mBack_img;
    private ListView mListview;
    private TextView mStatus_Tv;
    private BBHGuiHuanAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bbhgui_huan_apply_details);
        initUI();
    }

    private void initUI() {
        mBack_img = (ImageView) findViewById(R.id.back_img);
        mBack_img.setOnClickListener(this);
        mStatus_Tv = (TextView) findViewById(R.id.agree_btn);

        mAdapter = new BBHGuiHuanAdapter();
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
    class BBHGuiHuanAdapter extends BaseAdapter {

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
            BBHGuiHuanHolder guiHuanHolder = null;
            if (view == null) {
                view = LayoutInflater.from(BBHGuiHuanApplyDetailsActivity.this).inflate(R.layout.spz_lingyong_details_table_listview_item, null);
                guiHuanHolder = new BBHGuiHuanHolder();

                guiHuanHolder.num = view.findViewById(R.id.num_tv);
                guiHuanHolder.bumen = view.findViewById(R.id.bm_tv);
                guiHuanHolder.person = view.findViewById(R.id.person_tv);
                guiHuanHolder.name = view.findViewById(R.id.name_tv);
                guiHuanHolder.beizhu = view.findViewById(R.id.beizhu_tv);
                view.setTag(guiHuanHolder);
            } else {
                guiHuanHolder = (BBHGuiHuanHolder) view.getTag();

            }
            //设置数据

            return view;
        }

        class BBHGuiHuanHolder {
            TextView num, bumen, person, name, beizhu;//编号，申请部门，申请人，物品名称,备注
        }
    }
}
