package com.storehouse.wanyu.activity.PanDian;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
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
import android.widget.Toast;

import com.google.gson.Gson;
import com.storehouse.wanyu.Bean.CaiGouApplySubmit;
import com.storehouse.wanyu.Bean.PanDianRoot;
import com.storehouse.wanyu.Bean.PanDianRows;
import com.storehouse.wanyu.IPAddress.URLTools;
import com.storehouse.wanyu.MyUtils.BallProgressUtils;
import com.storehouse.wanyu.OkHttpUtils.OkHttpManager;
import com.storehouse.wanyu.R;
import com.storehouse.wanyu.activity.ApplyActivity.ApplyJieYongActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewFragment extends Fragment implements View.OnClickListener {
    PDActivity pdActivity;
    private RelativeLayout mAll_RL;
    private AlertDialog.Builder mAddAlertBuilder, mDeleteAlertBuilder;
    private AlertDialog mAddAlertDialog, mDeleteAlertDialog;
    private View mAddAlertView, mDeleteAlertView, mAddHeaderView;
    private ListView mAddAlertLisview, mDeleteAlertLisview;//弹框中的listview
    private TextView mAddAlertSureBtn, mDeleteAlertSureBtn;//弹框中的确认
    private ZiChanAdapter addAdapter;//弹框中的适配器
    // private DeleteAdapter deleteAdapter;//弹框中的适配器
    private List<PanDianRows> mAddList = new ArrayList<>();//弹框中的资产列表
    private List<PanDianRows> idList = new ArrayList<>();//标记已经选择过的资产
    private ImageView mback;
    private TextView mComplete_btn, mAdd_btn, mDelete_btn;
    private EditText mPanDianTitle_Edit, mPanDianExplain_edit;
    private ListView mListView;//新建盘点ListView
    private NewAdapter newAdapter;//新建盘点Adapter
    private OkHttpManager okHttpManager;
    private Gson gson = new Gson();
    private String mProperty_url, mComplete_url;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                String mes = (String) msg.obj;
                Object o = gson.fromJson(mes, PanDianRoot.class);
                if (o != null && o instanceof PanDianRoot) {
                    PanDianRoot panDianRoot = (PanDianRoot) o;
                    if (panDianRoot != null && "0".equals(panDianRoot.getCode())) {

                        if (panDianRoot.getRows() != null) {
                            mAddList = panDianRoot.getRows();
                            addAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getActivity(), "获取资产列表错误", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(getActivity(), "获取资产信息错误", Toast.LENGTH_SHORT).show();
                    }


                } else {
                    Toast.makeText(getActivity(), "获取资产失败", Toast.LENGTH_SHORT).show();
                }


            } else if (msg.what == 1010) {
                Toast.makeText(getActivity(), "数据错误", Toast.LENGTH_SHORT).show();
            } else if (msg.what == 7) {//提交接口
                BallProgressUtils.dismisLoading();
                String mes = (String) msg.obj;
                Log.e("盘点提交=", mes);
                Object o = gson.fromJson(mes, CaiGouApplySubmit.class);
                if (o != null && o instanceof CaiGouApplySubmit) {
                    CaiGouApplySubmit caiGouApplySubmit = (CaiGouApplySubmit) o;
                    if ("0".equals(caiGouApplySubmit.getCode())) {
                        Toast.makeText(getActivity(), "提交成功", Toast.LENGTH_SHORT).show();
                        idList .clear();
                        newAdapter.notifyDataSetChanged();
                        mPanDianTitle_Edit.setText("");
                        mPanDianExplain_edit.setText("");
                        pdActivity.showHistoryFragment();//提交成功后。显示盘点汇总
                    } else if ("-1".equals(caiGouApplySubmit.getCode())) {
                        Toast.makeText(getActivity(), "您的账号已过期请重新登录，请重新登录", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "提交失败", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        }
    };

    public NewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new, container, false);
        initUI(view);
        pdActivity= (PDActivity) getActivity();
        okHttpManager = OkHttpManager.getInstance();
        mProperty_url = URLTools.urlBase + URLTools.pan_dian_property_list_url;//获取资产列表
        okHttpManager.getMethod(false, mProperty_url, "获取盘点资产列表", mHandler, 1);
        mComplete_url = URLTools.urlBase + URLTools.submit_pan_pian;//完成盘点接口
        return view;
    }

    private void initUI(View v) {
        mAll_RL = v.findViewById(R.id.all_rl);
        mback = v.findViewById(R.id.back_img);
        mback.setOnClickListener(this);
        //完成
        mComplete_btn = v.findViewById(R.id.complete_btn);
        mComplete_btn.setOnClickListener(this);
        mAdd_btn = v.findViewById(R.id.add);
        mAdd_btn.setOnClickListener(this);
        mDelete_btn = v.findViewById(R.id.delete);
        mDelete_btn.setOnClickListener(this);
        //盘点主题
        mPanDianTitle_Edit = v.findViewById(R.id.title_Edit);
        //盘点说明
        mPanDianExplain_edit = v.findViewById(R.id.explain_edit);
        mListView = v.findViewById(R.id.new_listview);
        mListView.addHeaderView(LayoutInflater.from(getActivity()).inflate(R.layout.new_pandian_header, null));
        newAdapter = new NewAdapter();
        mListView.setAdapter(newAdapter);

        //增加按钮弹框
        //增加按钮弹框
        mAddAlertBuilder = new AlertDialog.Builder(getActivity(), R.style.dialogNoBg);
        mAddAlertBuilder.setCancelable(false);
        mAddAlertDialog = mAddAlertBuilder.create();
        mAddAlertView = LayoutInflater.from(getActivity()).inflate(R.layout.add_alertbox, null);
        mAddAlertDialog.setView(mAddAlertView);
        mAddHeaderView = LayoutInflater.from(getActivity()).inflate(R.layout.add_item_table_head, null);
        mAddAlertLisview = mAddAlertView.findViewById(R.id.add_listview);//弹框中的listview
        mAddAlertLisview.addHeaderView(mAddHeaderView);//添加头
        addAdapter = new ZiChanAdapter();
        mAddAlertLisview.setAdapter(addAdapter);
        //点击选择资产注意：当ListView添加header或者footer后setOnItemClickListener会响应头和尾，在adapter中使用点击事件不会响应
        mAddAlertLisview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView textView = view.findViewById(R.id.select_state);
                if (i != 0) {
                    //集合中如果有这个物品，点击的时候变为白色状态，并且从集合中删除这个物品
                    if (idList.contains(mAddList.get(i - 1))) {

                        textView.setBackgroundResource(R.drawable.bumen_box);
                        idList.remove(mAddList.get(i - 1));

                        //集合中如果没有这个物品，点击的时候变为黑色状态，并且增加这个物品到集合中
                    } else {
                        textView.setBackgroundResource(R.drawable.bumen_box_select);
                        idList.add(mAddList.get(i - 1));

                    }
                }
            }
        });
        mAddAlertSureBtn = mAddAlertView.findViewById(R.id.add_sure_btn);//弹框中的确认
        //点击确定以后更新适配器
        mAddAlertSureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newAdapter.notifyDataSetChanged();
                mAddAlertDialog.dismiss();
            }
        });

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == mback.getId()) {
            getActivity().finish();
            //完成
        } else if (id == mComplete_btn.getId()) {
            if ("".equals(mPanDianTitle_Edit.getText().toString().trim())) {
                Toast.makeText(getActivity(), "请填写盘点主题", Toast.LENGTH_SHORT).show();

            } else {
                if ("".equals(mPanDianExplain_edit.getText().toString().trim())) {
                    Toast.makeText(getActivity(), "请填写盘点说明", Toast.LENGTH_SHORT).show();

                } else {
                    StringBuilder idL = new StringBuilder();
                    for (int i = 0; i < idList.size(); i++) {
                        idL.append(idList.get(i).getId() + "");
                        if (i != idList.size() - 1) {
                            idL.append(",");
                        }
                    }
                    Log.e("提交借用的id集合", idL.toString());
                    if ("".equals(idL.toString())) {
                        Toast.makeText(getActivity(), "请选择物品", Toast.LENGTH_SHORT).show();
                    } else {
                        BallProgressUtils.showLoading(getActivity(), mAll_RL);
                        Map<Object, Object> map = new HashMap<>();
                        map.put("description", mPanDianExplain_edit.getText().toString().trim());
                        map.put("subject", mPanDianTitle_Edit.getText().toString());
                        map.put("assetsIds", idL.toString());

                        okHttpManager.postMethod(false, mComplete_url, "提交盘点接口", map, mHandler, 7);
                        ((InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                    }
                }
            }

            //增加
        } else if (id == mAdd_btn.getId()) {
            if (mAddList.size() != 0) {
                addAdapter.notifyDataSetChanged();
                mAddAlertDialog.show();
            } else {
                okHttpManager.getMethod(false, mProperty_url, "获取盘点资产列表", mHandler, 1);
                Toast.makeText(getActivity(), "正在获取资产列表,请稍等", Toast.LENGTH_SHORT).show();
            }
//            删除
        } else if (id == mDelete_btn.getId()) {
            if (idList.size() != 0) {
                addAdapter.notifyDataSetChanged();
                mAddAlertDialog.show();
            } else {
                Toast.makeText(getActivity(), "暂无需要删除的物品", Toast.LENGTH_SHORT).show();
            }


        }
    }

    class NewAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return idList.size();
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
            NewHolder newHolder = null;
            if (view == null) {
                newHolder = new NewHolder();
                view = LayoutInflater.from(getActivity()).inflate(R.layout.new_pandian_item, null);
                newHolder.num = view.findViewById(R.id.select_state);
                newHolder.bianhao = view.findViewById(R.id.bianhao_tv);
                newHolder.name = view.findViewById(R.id.name_tv);
                newHolder.manager = view.findViewById(R.id.manager_tv);
                newHolder.location = view.findViewById(R.id.location_tv);
                view.setTag(newHolder);
            } else {
                newHolder = (NewHolder) view.getTag();
            }
            newHolder.num.setText(i + 1 + "");
            newHolder.bianhao.setText(idList.get(i).getId() + "");
            newHolder.name.setText(idList.get(i).getAssetName() + "");
            newHolder.manager.setText(idList.get(i).getSaveUserName() + "");
            newHolder.location.setText(idList.get(i).getAddressName() + "");
            return view;
        }

        class NewHolder {
            TextView num, bianhao, name, manager, location;
        }
    }

    //增加弹框资产列表适配器
    class ZiChanAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mAddList.size() == 0 ? 0 : mAddList.size();
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
        public View getView(final int i, View view, ViewGroup viewGroup) {
            LYHolder lyHolder = null;
            if (view == null) {
                view = LayoutInflater.from(getActivity()).inflate(R.layout.add_item_table, null);
                lyHolder = new LYHolder();
                lyHolder.status = view.findViewById(R.id.select_state);
                lyHolder.bianhao = view.findViewById(R.id.bianhao_tv);
                lyHolder.leibie = view.findViewById(R.id.leibie_tv);
                lyHolder.mingcheng = view.findViewById(R.id.mingcheng_tv);
                view.setTag(lyHolder);
            } else {
                lyHolder = (LYHolder) view.getTag();

            }
            //设置数据
            lyHolder.bianhao.setText(mAddList.get(i).getId() + "");
            lyHolder.leibie.setText(mAddList.get(i).getCategoryName() + "");
            lyHolder.mingcheng.setText(mAddList.get(i).getAssetName() + "");
            Log.e(" i=", i + "");
            if (idList.contains(mAddList.get(i))) {
                lyHolder.status.setBackgroundResource(R.drawable.bumen_box_select);
            } else {
                lyHolder.status.setBackgroundResource(R.drawable.bumen_box);
            }

            return view;
        }

        class LYHolder {
            TextView status, bianhao, leibie, mingcheng;//状态，序号，物品名称，编号，类别
        }
    }

    //删除弹框中的适配器
//    class DeleteAdapter extends BaseAdapter {
//
//        @Override
//        public int getCount() {
//            return idList.size();
//        }
//
//        @Override
//        public Object getItem(int i) {
//            return null;
//        }
//
//        @Override
//        public long getItemId(int i) {
//            return i;
//        }
//
//        @Override
//        public View getView(int i, View view, ViewGroup viewGroup) {
//            DHolder lyHolder = null;
//            if (view == null) {
//                view = LayoutInflater.from(getActivity()).inflate(R.layout.add_item_table, null);
//                lyHolder = new DHolder();
//                lyHolder.status = view.findViewById(R.id.select_state);
//                lyHolder.bianhao = view.findViewById(R.id.bianhao_tv);
//                lyHolder.leibie = view.findViewById(R.id.leibie_tv);
//                lyHolder.mingcheng = view.findViewById(R.id.mingcheng_tv);
//                view.setTag(lyHolder);
//            } else {
//                lyHolder = (DHolder) view.getTag();
//
//            }
//            //设置数据
//            lyHolder.bianhao.setText(mAddList.get(i).getId() + "");
//            lyHolder.leibie.setText(mAddList.get(i).getCategoryName() + "");
//            lyHolder.mingcheng.setText(mAddList.get(i).getAssetName() + "");
//            lyHolder.status.setBackgroundResource(R.drawable.bumen_box_select);
//            return view;
//        }
//
//        class DHolder {
//            TextView status, bianhao, leibie, mingcheng;//状态，序号，物品名称，编号，类别
//        }
//    }
}
