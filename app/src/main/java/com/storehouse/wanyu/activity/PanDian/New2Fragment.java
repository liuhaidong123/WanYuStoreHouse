package com.storehouse.wanyu.activity.PanDian;


import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.storehouse.wanyu.Bean.DepartmentRoot;
import com.storehouse.wanyu.Bean.DepartmentRows;
import com.storehouse.wanyu.Bean.PropertyClassRoot;
import com.storehouse.wanyu.Bean.PropertyClassRows;
import com.storehouse.wanyu.Bean.PropertyLocationRoot;
import com.storehouse.wanyu.Bean.PropertyLocationRows;
import com.storehouse.wanyu.IPAddress.URLTools;
import com.storehouse.wanyu.MyUtils.BallProgressUtils;
import com.storehouse.wanyu.MyUtils.ToastUtils;
import com.storehouse.wanyu.OkHttpUtils.OkHttpManager;
import com.storehouse.wanyu.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 新建盘点
 * A simple {@link Fragment} subclass.
 */
public class New2Fragment extends Fragment implements View.OnClickListener {
    private RelativeLayout mAll_RL;
    PDActivity pdActivity;
    private EditText mPanDianTitle_Edit, mPanDianExplain_edit;
    private ImageView mback;
    private TextView mWay_tv, mComplete_btn;
    private RelativeLayout mSelect_Way_btn;//选择方式
    private List<String> mWayList = new ArrayList<>();
    private boolean flag = false;//false表示资产方式没有显示，true表示资产方式已经显示出来
    private ListView mWayListView, mOneListView, mTwoListView, mThreeListView;
    private WayAdapter wayAdapter;
    private DepartmentAdapter departmentAdapter;//科室适配器
    private LocationAdapter locationAdapter;//存放地适配器
    private CategoryAdapter categoryAdapter;//类别适配器
    private String code = "";//资产类别编号,存放地编号
    private List<PropertyClassRows> mLeiBieList = new ArrayList<>();
    private List<PropertyLocationRows> mLocationList = new ArrayList<>();
    private List<DepartmentRows> mDepartmentList = new ArrayList<>();
    private String leibie_url, location_url, department_url, submit_url;//类别，存放地
    private int mPosition = -1;//标记每次点击各种盘点方式后的下标
    private int flagPosition = -1;//标记点击的是科室，存放地，类别
    private OkHttpManager okHttpManager;
    private Gson gson = new Gson();
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {//请求资产类别接口
                try {
                    String s = (String) msg.obj;
                    Object o = gson.fromJson(s, PropertyClassRoot.class);
                    if (o != null && o instanceof PropertyClassRoot) {
                        PropertyClassRoot propertyClassRoot = (PropertyClassRoot) o;
                        if (propertyClassRoot != null && "0".equals(propertyClassRoot.getCode())) {

                            if (propertyClassRoot.getRows() != null) {
                                if (propertyClassRoot.getRows().size() != 0) {
                                    mOneListView.setVisibility(View.VISIBLE);
                                    mLeiBieList = propertyClassRoot.getRows();
                                    mOneListView.setAdapter(categoryAdapter);

                                } else {
                                    if (mPosition != -1) {//不为-1，最少最外层有数据
                                        mOneListView.setVisibility(View.GONE);
                                        mWay_tv.setText(mLeiBieList.get(mPosition).getCategoryName() + "");
                                        code = mLeiBieList.get(mPosition).getCategoryCode();
                                    } else {
                                        Toast.makeText(getActivity(), "暂无其他盘点方式", Toast.LENGTH_SHORT).show();
                                    }


                                }
                            } else {
                                Toast.makeText(getActivity(), "资产类别数据集合为null", Toast.LENGTH_SHORT).show();

                            }
                        } else {
                            Toast.makeText(getActivity(), "登录过期,请重新登录", Toast.LENGTH_SHORT).show();
                        }


                    } else {
                        Toast.makeText(getActivity(), "资产类别数据错误", Toast.LENGTH_SHORT).show();

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ToastUtils.show(getContext(), "数据解析错误，联系后台");
                }


            } else if (msg.what == 4) {//请求资产存放地接口
                String s = (String) msg.obj;
                try {
                    Object o = gson.fromJson(s, PropertyLocationRoot.class);
                    if (o != null && o instanceof PropertyLocationRoot) {
                        PropertyLocationRoot propertyLocationRoot = (PropertyLocationRoot) o;
                        if (propertyLocationRoot != null && "0".equals(propertyLocationRoot.getCode())) {

                            if (propertyLocationRoot.getRows() != null) {
                                if (propertyLocationRoot.getRows().size() != 0) {
                                    mOneListView.setVisibility(View.VISIBLE);
                                    mLocationList = propertyLocationRoot.getRows();
                                    mOneListView.setAdapter(locationAdapter);

                                } else {
                                    if (mPosition != -1) {
                                        mOneListView.setVisibility(View.GONE);
                                        mWay_tv.setText(mLocationList.get(mPosition).getAddressName() + "");
                                        code = mLocationList.get(mPosition).getAddressCode();
                                    } else {
                                        Toast.makeText(getActivity(), "暂无其他盘点方式", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            } else {
                                Toast.makeText(getActivity(), "资产存放地数据集合为null", Toast.LENGTH_SHORT).show();

                            }
                        } else if (propertyLocationRoot != null && "-1".equals(propertyLocationRoot.getCode())) {
                            Toast.makeText(getActivity(), "登录过期,请重新登录", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "存放地数据错误", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(getActivity(), "资产存放地数据错误", Toast.LENGTH_SHORT).show();

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ToastUtils.show(getContext(), "数据解析错误，联系后台");
                }


            } else if (msg.what == 3) {//科室接口
                String s = (String) msg.obj;
                try {
                    Object o = gson.fromJson(s, DepartmentRoot.class);
                    if (o != null && o instanceof DepartmentRoot) {
                        DepartmentRoot departmentRoot = (DepartmentRoot) o;
                        if (departmentRoot != null && "0".equals(departmentRoot.getCode())) {

                            if (departmentRoot.getRows() != null) {
                                if (departmentRoot.getRows().size() != 0) {
                                    mOneListView.setVisibility(View.VISIBLE);
                                    mDepartmentList = departmentRoot.getRows();
                                    mOneListView.setAdapter(departmentAdapter);

                                } else {
                                    if (mPosition != -1) {
                                        mOneListView.setVisibility(View.GONE);
                                        mWay_tv.setText(mDepartmentList.get(mPosition).getDepartmentName() + "");
                                        code = mDepartmentList.get(mPosition).getDepartmentCode();
                                    } else {
                                        Toast.makeText(getActivity(), "暂无其他盘点方式", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            } else {
                                Toast.makeText(getActivity(), "科室数据集合为null", Toast.LENGTH_SHORT).show();

                            }
                        } else if (departmentRoot != null && "-1".equals(departmentRoot.getCode())) {
                            Toast.makeText(getActivity(), "登录过期,请重新登录", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "科室数据错误", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(getActivity(), "科室数据错误", Toast.LENGTH_SHORT).show();

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ToastUtils.show(getContext(), "数据解析错误，联系后台");
                }


            } else if (msg.what == 1010) {
                mComplete_btn.setClickable(true);
                Toast.makeText(getActivity(), "网络异常,请检查网络", Toast.LENGTH_SHORT).show();
            } else if (msg.what == 2) {//提交接口
                mComplete_btn.setClickable(true);
                BallProgressUtils.dismisLoading();
                String mes = (String) msg.obj;
                Log.e("盘点提交=", mes);
                try {
                    Object o = gson.fromJson(mes, CaiGouApplySubmit.class);
                    if (o != null && o instanceof CaiGouApplySubmit) {
                        CaiGouApplySubmit caiGouApplySubmit = (CaiGouApplySubmit) o;
                        if ("0".equals(caiGouApplySubmit.getCode())) {
                            Toast.makeText(getActivity(), "提交成功", Toast.LENGTH_SHORT).show();
                            mWay_tv.setText("");
                            mPanDianTitle_Edit.setText("");
                            mPanDianExplain_edit.setText("");
                            pdActivity.showHistoryFragment();//提交成功后。显示盘点汇总
                        } else if ("-1".equals(caiGouApplySubmit.getCode())) {
                            Toast.makeText(getActivity(), "您的账号已过期请重新登录，请重新登录", Toast.LENGTH_SHORT).show();
                        } else if ("3".equals(caiGouApplySubmit.getCode())) {
                            Toast.makeText(getActivity(), "抱歉,没有可盘点的资产", Toast.LENGTH_SHORT).show();
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ToastUtils.show(getContext(), "数据解析错误，联系后台");
                }

            }
        }
    };

    public New2Fragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new2, container, false);
        initUI(view);
        pdActivity = (PDActivity) getActivity();
        return view;
    }

    private void initUI(View v) {
        mAll_RL = v.findViewById(R.id.all_rl);
        okHttpManager = OkHttpManager.getInstance();
        submit_url = URLTools.urlBase + URLTools.submit_Inventory;//提交接口
        leibie_url = URLTools.urlBase + URLTools.property_leibie + "parentId=0";//类别接口
        location_url = URLTools.urlBase + URLTools.property_location_list + "parentId=0";//存放地接口
        department_url = URLTools.urlBase + URLTools.department_list + "parentId=0";//科室类别
        //盘点主题
        mPanDianTitle_Edit = v.findViewById(R.id.title_Edit);
        //盘点说明
        mPanDianExplain_edit = v.findViewById(R.id.explain_edit);

        mback = v.findViewById(R.id.back_img);
        mback.setOnClickListener(this);

        //  完成
        mComplete_btn = v.findViewById(R.id.complete_btn);
        mComplete_btn.setOnClickListener(this);
        //盘点方式
        mWay_tv = v.findViewById(R.id.way_mes);

        mSelect_Way_btn = v.findViewById(R.id.way_select_btn);
        mSelect_Way_btn.setOnClickListener(this);
        mWayList.add("科室类别");
        mWayList.add("存放地");
        mWayList.add("资产类别");

        mWayListView = v.findViewById(R.id.way_listview);
        wayAdapter = new WayAdapter();
        mWayListView.setAdapter(wayAdapter);
        mWayListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // mWay_tv.setText(mWayList.get(i));
                hide(mWayListView);
                mWayListView.setVisibility(View.GONE);
                flag = false;
                if (i == 0) {//科室适配器
                    flagPosition = 0;
                    okHttpManager.getMethod(false, department_url, "科室类别接口", mHandler, 3);
                } else if (i == 1) {//存放地适配器
                    flagPosition = 1;
                    okHttpManager.getMethod(false, location_url, "资产存放地接口", mHandler, 4);
                } else if (i == 2) {//类别适配器
                    flagPosition = 2;
                    okHttpManager.getMethod(false, leibie_url, "资产类别接口", mHandler, 1);
                }
            }
        });

        mOneListView = v.findViewById(R.id.one_listview);
        mOneListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mPosition = i;
                if (flagPosition == 0) {
                    department_url = URLTools.urlBase + URLTools.department_list + "parentId=" + mDepartmentList.get(i).getId();
                    okHttpManager.getMethod(false, department_url, "科室类别接口", mHandler, 3);
                } else if (flagPosition == 1) {
                    location_url = URLTools.urlBase + URLTools.property_location_list + "parentId=" + mLocationList.get(i).getId();//存放地接口
                    okHttpManager.getMethod(false, location_url, "资产存放地接口", mHandler, 4);
                } else {
                    leibie_url = URLTools.urlBase + URLTools.property_leibie + "parentId=" + mLeiBieList.get(i).getId();
                    okHttpManager.getMethod(false, leibie_url, "资产类别接口", mHandler, 1);
                }
            }
        });
        departmentAdapter = new DepartmentAdapter();//科室适配器
        locationAdapter = new LocationAdapter();//存放地适配器
        categoryAdapter = new CategoryAdapter();//类别适配器

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == mback.getId()) {
            getActivity().finish();
        } else if (id == mComplete_btn.getId()) {//完成，提交盘点
            if (!"".equals(mPanDianTitle_Edit.getText().toString().trim())) {

                if (!"".equals(mWay_tv.getText().toString().trim())) {

                    if (!"".equals(code)) {
                        mComplete_btn.setClickable(false);
                        BallProgressUtils.showLoading(getActivity(), mAll_RL);
                        Map<Object, Object> map = new HashMap<>();
                        map.put("mode", flagPosition);
                        map.put("code", code);
                        map.put("subject", mPanDianTitle_Edit.getText().toString());
                        map.put("description", mPanDianExplain_edit.getText().toString());
                        okHttpManager.postMethod(false, submit_url, "提交新建盘点", map, mHandler, 2);
                    } else {
                        Toast.makeText(getActivity(), "请选择盘点方式", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "请选择盘点方式", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), "请填写盘点主题", Toast.LENGTH_SHORT).show();
            }


        } else if (id == mSelect_Way_btn.getId()) {//选择盘点方式
            mOneListView.setVisibility(View.GONE);
            mPosition = -1;
            leibie_url = URLTools.urlBase + URLTools.property_leibie + "parentId=0";//类别接口
            location_url = URLTools.urlBase + URLTools.property_location_list + "parentId=0";//存放地接口
            department_url = URLTools.urlBase + URLTools.department_list + "parentId=0";//科室类别
            if (flag) {//如果显示就隐藏
                hide(mWayListView);
                mWayListView.setVisibility(View.GONE);
                flag = false;
            } else {//如果隐藏就显示出来
                mWayListView.setVisibility(View.VISIBLE);
                show(mWayListView);
                flag = true;
            }

        }
    }


    /**
     * 盘点方式配器
     */
    class WayAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mWayList.size();
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
            WayHolder wayHolder = null;
            if (view == null) {
                wayHolder = new WayHolder();
                view = LayoutInflater.from(getActivity()).inflate(R.layout.anyone_item, null);
                wayHolder.tv = view.findViewById(R.id.anyone_tv);
                view.setTag(wayHolder);
            } else {
                wayHolder = (WayHolder) view.getTag();
            }
            wayHolder.tv.setText(mWayList.get(i));
            return view;
        }

        class WayHolder {
            TextView tv;
        }
    }

    /**
     * 科室适配器
     */
    class DepartmentAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mDepartmentList.size();
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
            DepartmentHolder departmentHolder = null;
            if (view == null) {
                departmentHolder = new DepartmentHolder();
                view = LayoutInflater.from(getActivity()).inflate(R.layout.anyone_item, null);
                departmentHolder.tv = view.findViewById(R.id.anyone_tv);
                view.setTag(departmentHolder);
            } else {
                departmentHolder = (DepartmentHolder) view.getTag();
            }
            departmentHolder.tv.setText(mDepartmentList.get(i).getDepartmentName() + "");
            return view;
        }

        class DepartmentHolder {
            TextView tv;
        }
    }

    /**
     * 存放地适配器
     */
    class LocationAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mLocationList.size();
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

            LocationHolder locationHolder = null;
            if (view == null) {
                locationHolder = new LocationHolder();
                view = LayoutInflater.from(getActivity()).inflate(R.layout.anyone_item, null);
                locationHolder.tv = view.findViewById(R.id.anyone_tv);
                view.setTag(locationHolder);
            } else {
                locationHolder = (LocationHolder) view.getTag();
            }
            locationHolder.tv.setText(mLocationList.get(i).getAddressName() + "");
            return view;
        }

        class LocationHolder {
            TextView tv;
        }
    }

    /**
     * 类别适配器
     */
    class CategoryAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mLeiBieList.size();
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
            CategoryHolder categoryHolder = null;
            if (view == null) {
                categoryHolder = new CategoryHolder();
                view = LayoutInflater.from(getActivity()).inflate(R.layout.anyone_item, null);
                categoryHolder.tv = view.findViewById(R.id.anyone_tv);
                view.setTag(categoryHolder);
            } else {
                categoryHolder = (CategoryHolder) view.getTag();
            }

            categoryHolder.tv.setText(mLeiBieList.get(i).getCategoryName() + "");
            return view;
        }

        class CategoryHolder {
            TextView tv;
        }
    }


    public void show(ListView listView) {
        PropertyValuesHolder holder1 = PropertyValuesHolder.ofFloat("scaleX", 0.0f, 1.0f);
        PropertyValuesHolder holder2 = PropertyValuesHolder.ofFloat("scaleY", 0.0f, 1.0f);
        PropertyValuesHolder holder3 = PropertyValuesHolder.ofFloat("alpha", 0.0f, 1.0f);
        ObjectAnimator.ofPropertyValuesHolder(listView, holder1, holder2, holder3).setDuration(500).start();
    }

    public void hide(ListView listView) {
        PropertyValuesHolder holder1 = PropertyValuesHolder.ofFloat("scaleX", 1.0f, 0.0f);
        PropertyValuesHolder holder2 = PropertyValuesHolder.ofFloat("scaleY", 1.0f, 0.0f);
        PropertyValuesHolder holder3 = PropertyValuesHolder.ofFloat("alpha", 1.0f, 0.0f);
        ObjectAnimator.ofPropertyValuesHolder(listView, holder1, holder2, holder3).setDuration(500).start();
    }
}
