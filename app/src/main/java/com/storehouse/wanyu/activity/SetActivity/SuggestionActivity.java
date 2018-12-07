package com.storehouse.wanyu.activity.SetActivity;

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
import com.storehouse.wanyu.Bean.PanDianSubmitBean;
import com.storehouse.wanyu.IPAddress.URLTools;
import com.storehouse.wanyu.MyUtils.BallProgressUtils;
import com.storehouse.wanyu.OkHttpUtils.OkHttpManager;
import com.storehouse.wanyu.R;

import java.util.HashMap;
import java.util.Map;

//意见反馈
public class SuggestionActivity extends AppCompatActivity {

    private RelativeLayout relativeLayout;
    private ImageView mBack;
    private EditText editText;
    private TextView mSubmit_brn;
    private OkHttpManager okHttpManager;
    private Gson gson = new Gson();
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            BallProgressUtils.dismisLoading();
            if (msg.what == 2) {
                String s = (String) msg.obj;
                Object o = gson.fromJson(s, PanDianSubmitBean.class);
                if (o != null && o instanceof PanDianSubmitBean) {
                    PanDianSubmitBean panDianSubmitBean = (PanDianSubmitBean) o;
                    if (panDianSubmitBean != null && "0".equals(panDianSubmitBean.getCode())) {
                        Toast.makeText(SuggestionActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(SuggestionActivity.this, "反馈失败", Toast.LENGTH_SHORT).show();

                    }

                } else {
                    Toast.makeText(SuggestionActivity.this, "反馈失败", Toast.LENGTH_SHORT).show();

                }


            } else {
                Toast.makeText(SuggestionActivity.this, "反馈失败", Toast.LENGTH_SHORT).show();

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestion);
        relativeLayout = (RelativeLayout) findViewById(R.id.activity_suggestion);
        okHttpManager = OkHttpManager.getInstance();

        mBack = (ImageView) findViewById(R.id.back_img);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        editText = (EditText) findViewById(R.id.suggestion_edit);
        mSubmit_brn = (TextView) findViewById(R.id.submit_suggestion_btn);
        mSubmit_brn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!"".equals(editText.getText().toString().trim())) {
                    BallProgressUtils.showLoading(SuggestionActivity.this, relativeLayout);
                    Map<Object, Object> map = new HashMap<Object, Object>();
                    map.put("content", editText.getText().toString().trim());
                    okHttpManager.postMethod(false, URLTools.urlBase + URLTools.suggestion_url, "提交意见反馈接口", map, handler, 2);

                } else {
                    Toast.makeText(SuggestionActivity.this, "请填写反馈内容", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}
