package com.storehouse.wanyu.activity.AllSPZDetailsActivity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.storehouse.wanyu.R;
//审批中归还申请详情
public class SPZGuiHuanApplyDetailsActivity extends AppCompatActivity implements View.OnClickListener{
    private ImageView mBack_img;
    private ListView mListview;
    private TextView mAgree_Btn, mDisAgree_btn;
    private GuiHuanAdapter mAdapter;

    private RelativeLayout mAgreeAndDisagree_Rl,mStatus_rl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spzgui_huan_apply_details);
        initUI();
    }

    private void initUI() {
        mBack_img = (ImageView) findViewById(R.id.back_img);
        mBack_img.setOnClickListener(this);

        mAgree_Btn = (TextView) findViewById(R.id.agree_btn);
        mAgree_Btn.setOnClickListener(this);
        mDisAgree_btn = (TextView) findViewById(R.id.disagree_btn);
        mDisAgree_btn.setOnClickListener(this);

        mAgreeAndDisagree_Rl= (RelativeLayout) findViewById(R.id.disagree_rl);
        mStatus_rl= (RelativeLayout) findViewById(R.id.status_rl);

        mAdapter = new GuiHuanAdapter();
        mListview = (ListView) findViewById(R.id.spz_jieyong_listview_id);
        mListview.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == mBack_img.getId()) {
            finish();
        } else if (id == mAgree_Btn.getId()) {

        } else if (id == mDisAgree_btn.getId()) {

        }
    }
    //领用用明细适配器
    class GuiHuanAdapter extends BaseAdapter {

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
            GuiHuanHolder guiHuanHolder = null;
            if (view == null) {
                view = LayoutInflater.from(SPZGuiHuanApplyDetailsActivity.this).inflate(R.layout.spz_lingyong_details_table_listview_item, null);
                guiHuanHolder = new GuiHuanHolder();

                guiHuanHolder.num = view.findViewById(R.id.num_tv);
                guiHuanHolder.bumen = view.findViewById(R.id.bm_tv);
                guiHuanHolder.person = view.findViewById(R.id.person_tv);
                guiHuanHolder.name = view.findViewById(R.id.name_tv);
                guiHuanHolder.beizhu = view.findViewById(R.id.beizhu_tv);
                view.setTag(guiHuanHolder);
            } else {
                guiHuanHolder = (GuiHuanHolder) view.getTag();

            }
            //设置数据

            return view;
        }

        class GuiHuanHolder {
            TextView num, bumen, person, name, beizhu;//编号，申请部门，申请人，物品名称,备注
        }
    }
}
