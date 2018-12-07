package com.storehouse.wanyu.activity.NotifyActivity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.storehouse.wanyu.Bean.PropertySubmitBean;
import com.storehouse.wanyu.IPAddress.URLTools;
import com.storehouse.wanyu.MyUtils.BallProgressUtils;
import com.storehouse.wanyu.OkHttpUtils.OkHttpManager;
import com.storehouse.wanyu.R;

import java.util.HashMap;
import java.util.Map;

//发布通知
public class PostInformationActivity extends AppCompatActivity implements View.OnClickListener {
    private RelativeLayout mAll_rl;
    private ImageView mBack_Img;
    private TextView mPostNotify_btn;
    private EditText mTitle_Edit, mContent_Edit;
    private String url;
    private OkHttpManager okHttpManager;
    private Gson gson = new Gson();
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mPostNotify_btn.setClickable(true);
            BallProgressUtils.dismisLoading();
            if (msg.what == 1) {
                String s = (String) msg.obj;
                Object o = gson.fromJson(s, PropertySubmitBean.class);
                if (o != null && o instanceof PropertySubmitBean) {
                    PropertySubmitBean propertySubmitBean = (PropertySubmitBean) o;
                    if (propertySubmitBean != null) {
                        if ("0".equals(propertySubmitBean.getCode())) {
                            Toast.makeText(PostInformationActivity.this, "发布成功", Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK,intent);
                            finish();
                        } else {
                            Toast.makeText(PostInformationActivity.this, "发布失败", Toast.LENGTH_SHORT).show();

                        }
                    }


                } else {
                    Toast.makeText(PostInformationActivity.this, "数据错误", Toast.LENGTH_SHORT).show();
                }


            } else {
                Toast.makeText(PostInformationActivity.this, "后台数据错误", Toast.LENGTH_SHORT).show();

            }

        }
    };
private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_information);
        mAll_rl = (RelativeLayout) findViewById(R.id.activity_post_information);
        initUI();
    }

    private void initUI() {
        okHttpManager = OkHttpManager.getInstance();
        url = URLTools.urlBase + URLTools.post_notify;
        intent=getIntent();
        mBack_Img = (ImageView) findViewById(R.id.back_img);
        mBack_Img.setOnClickListener(this);

        mPostNotify_btn = (TextView) findViewById(R.id.post_notify_btn);
        mPostNotify_btn.setOnClickListener(this);

        mTitle_Edit = (EditText) findViewById(R.id.notify_title_edit);
        mContent_Edit = (EditText) findViewById(R.id.notify_content_edit);

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == mBack_Img.getId()) {
            finish();
        } else if (id == mPostNotify_btn.getId()) {
            //发布通知

            if ("".equals(mTitle_Edit.getText().toString().trim())) {
                Toast.makeText(PostInformationActivity.this, "请填写标题", Toast.LENGTH_SHORT).show();
            } else {
                if ("".equals(mContent_Edit.getText().toString().trim())) {
                    Toast.makeText(PostInformationActivity.this, "请填写内容", Toast.LENGTH_SHORT).show();
                } else {
                    mPostNotify_btn.setClickable(false);
                    BallProgressUtils.showLoading(PostInformationActivity.this, mAll_rl);
                    Map<Object, Object> map = new HashMap<>();
                    map.put("msgType", 0);
                    map.put("title", mTitle_Edit.getText().toString().trim());
                    map.put("content", mContent_Edit.getText().toString().trim());
                    okHttpManager.postMethod(false, url, "发布通知接口", map, handler, 1);

                }
            }
        }
    }
}
