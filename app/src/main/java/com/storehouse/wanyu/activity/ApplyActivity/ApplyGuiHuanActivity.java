package com.storehouse.wanyu.activity.ApplyActivity;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.storehouse.wanyu.R;

import java.util.ArrayList;
import java.util.List;

public class ApplyGuiHuanActivity extends AppCompatActivity implements View.OnClickListener{
    private ImageView mBack;
    private TextView mSubmit, mAdd_Btn, mDelete_Btn, mBumen_Tv;
    private RelativeLayout mBuMen_Rl;
    private EditText mPerson_Edit, mName_Edit, mBeiZhu_Edit;
    private AlertDialog.Builder mBuilderBuMen;
    private AlertDialog mAlertDialogBuMen;
    private ListView mBumen_ListView;
    private List<String> mBuMenList = new ArrayList<>();
    private ListView mJieYongListView;//领用明细ListView
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_gui_huan);
        initUI();
    }
    private void initUI() {
        mBack = (ImageView) findViewById(R.id.back_img);
        mBack.setOnClickListener(this);
        mSubmit = (TextView) findViewById(R.id.submit_btn);
        mSubmit.setOnClickListener(this);
        mAdd_Btn = (TextView) findViewById(R.id.add_tv);
        mDelete_Btn = (TextView) findViewById(R.id.delete_tv);
        mAdd_Btn.setOnClickListener(this);//添加
        mDelete_Btn.setOnClickListener(this); //删除
        mBumen_Tv = (TextView) findViewById(R.id.bumen_name);
        mBuMen_Rl = (RelativeLayout) findViewById(R.id.bumen_btn);
        mBuMen_Rl.setOnClickListener(this);

        mJieYongListView = (ListView) findViewById(R.id.mingxi_listview_id);
        mJieYongListView.addHeaderView(LayoutInflater.from(ApplyGuiHuanActivity.this).inflate(R.layout.table_listview_item, null));
        mJieYongListView.setAdapter(new JieYongAdapter());
        //部门弹框
        mBuilderBuMen = new AlertDialog.Builder(this);
        mAlertDialogBuMen = mBuilderBuMen.create();
        View view = LayoutInflater.from(this).inflate(R.layout.bumen_alert, null);
        mBumen_ListView = view.findViewById(R.id.bumen_listview);
        mBuMenList.add("呼吸科");
        mBuMenList.add("妇产科");
        mBuMenList.add("神经内科");
        mBuMenList.add("肿瘤科");
        mBuMenList.add("内分泌科");
        mBuMenList.add("皮肤科");
        mAlertDialogBuMen.setView(view);
        mBumen_ListView.setAdapter(new BumenAdapter());
        mBumen_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mBumen_Tv.setText(mBuMenList.get(i));
                mAlertDialogBuMen.dismiss();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //返回
            case R.id.back_img:
                finish();
                //如果键盘显示，就隐藏
                ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                break;
            //提交
            case R.id.submit_btn:
                break;
            //选择部门
            case R.id.bumen_btn:
                mAlertDialogBuMen.show();
                break;
            //增加
            case R.id.add_tv:

                break;
            //删除
            case R.id.delete_tv:

                break;
        }
    }

    //申请部门适配器
    class BumenAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mBuMenList.size();
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

            BuMenHolder buMenHolder = null;
            if (view == null) {
                view = LayoutInflater.from(ApplyGuiHuanActivity.this).inflate(R.layout.bumen_alert_item, null);
                buMenHolder = new BuMenHolder();
                buMenHolder.textView = view.findViewById(R.id.bumen_item_tv);
                view.setTag(buMenHolder);
            } else {
                buMenHolder = (BuMenHolder) view.getTag();
            }
            buMenHolder.textView.setText(mBuMenList.get(i));
            return view;
        }

        class BuMenHolder {
            TextView textView;
        }
    }


    //借用明细适配器
    class JieYongAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 2;
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
            LYHolder jyHolder = null;
            if (view == null) {
                view = LayoutInflater.from(ApplyGuiHuanActivity.this).inflate(R.layout.table_listview_item, null);
                jyHolder = new LYHolder();
                jyHolder.selectTv = view.findViewById(R.id.select_state);
                jyHolder.num = view.findViewById(R.id.num_tv);
                jyHolder.bumen = view.findViewById(R.id.bm_tv);
                jyHolder.person = view.findViewById(R.id.person_tv);
                jyHolder.name = view.findViewById(R.id.name_tv);
                view.setTag(jyHolder);
            } else {
                jyHolder = (LYHolder) view.getTag();

            }
            //设置数据

            return view;
        }

        class LYHolder {
            TextView selectTv, num, bumen, person, name;//选中状态，编号，申请部门，申请人，物品名称
        }
    }
}
