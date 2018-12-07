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
import com.storehouse.wanyu.Bean.SetPasswordRoot;
import com.storehouse.wanyu.IPAddress.URLTools;
import com.storehouse.wanyu.MyUtils.BallProgressUtils;
import com.storehouse.wanyu.OkHttpUtils.OkHttpManager;
import com.storehouse.wanyu.R;

import java.util.HashMap;
import java.util.Map;

//修改密码
public class SetPasswordActivity extends AppCompatActivity {
    private ImageView mBack;
    private EditText mOldPassword, mNewpassword, mSureNewPassword;
    private TextView mSubmit_Btn;
    private OkHttpManager okHttpManager;
    private Gson gson = new Gson();
    private String url;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            BallProgressUtils.dismisLoading();
            if (msg.what == 1) {
                String s = (String) msg.obj;
                Object o = gson.fromJson(s, SetPasswordRoot.class);
                if (o != null && o instanceof SetPasswordRoot) {

                    SetPasswordRoot setPasswordRoot = (SetPasswordRoot) o;
                    if (setPasswordRoot!=null){
                        if ("0".equals(setPasswordRoot.getCode())){
                            Toast.makeText(SetPasswordActivity.this,setPasswordRoot.getMessage()+"",Toast.LENGTH_SHORT).show();
                            finish();
                        }else if ("1".equals(setPasswordRoot.getCode())){
                            Toast.makeText(SetPasswordActivity.this,setPasswordRoot.getMessage()+"",Toast.LENGTH_SHORT).show();
                        }else if ("2".equals(setPasswordRoot.getCode())){
                            Toast.makeText(SetPasswordActivity.this,setPasswordRoot.getMessage()+"",Toast.LENGTH_SHORT).show();
                        }else if ("3".equals(setPasswordRoot.getCode())){
                            Toast.makeText(SetPasswordActivity.this,setPasswordRoot.getMessage()+"",Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(SetPasswordActivity.this,setPasswordRoot.getMessage()+"",Toast.LENGTH_SHORT).show();
                        }


                    }else {
                        Toast.makeText(SetPasswordActivity.this, "修改密码数据错误", Toast.LENGTH_SHORT).show();

                    }
                } else {
                    Toast.makeText(SetPasswordActivity.this, "修改密码数据错误", Toast.LENGTH_SHORT).show();

                }


            } else {
                Toast.makeText(SetPasswordActivity.this, "修改失败", Toast.LENGTH_SHORT).show();
            }
        }
    };
    private RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_password);
        relativeLayout = (RelativeLayout) findViewById(R.id.activity_set_password);
        okHttpManager = OkHttpManager.getInstance();
        url = URLTools.urlBase + URLTools.modify_password_url;
        mBack = (ImageView) findViewById(R.id.back_img);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mOldPassword = (EditText) findViewById(R.id.original_password_edit);
        mNewpassword = (EditText) findViewById(R.id.new_password_edit);
        mSureNewPassword = (EditText) findViewById(R.id.sure_new_password_edit);


        mSubmit_Btn = (TextView) findViewById(R.id.submit_password_btn);
        mSubmit_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ("".equals(mOldPassword.getText().toString().trim())) {
                    Toast.makeText(SetPasswordActivity.this, "请输入旧密码", Toast.LENGTH_SHORT).show();
                } else {
                    if (checkPasswordEqual()) {
                        BallProgressUtils.showLoading(SetPasswordActivity.this, relativeLayout);
                        Map<Object, Object> map = new HashMap<Object, Object>();
                        map.put("password", mOldPassword.getText().toString());
                        map.put("password2", mNewpassword.getText().toString());
                        okHttpManager.postMethod(false, url, "修改密码接口", map, mHandler, 1);
                    }
                }
            }
        });

    }

    //判断俩次新密码是否一样
    private boolean checkPasswordEqual() {
        //新密码是否为""
        if (!"".equals(mNewpassword.getText().toString())) {
            //新密码长度是否大于6
            if (mNewpassword.getText().toString().length() >= 6) {
                //再次确认新密码是否为""
                if (!"".equals(mSureNewPassword.getText().toString())) {
                    //再次确认新密码长度是否大于6
                    if (mSureNewPassword.getText().toString().length() >= 6) {
                        //判断两次输入的新密码是否相同
                        if (mNewpassword.getText().toString().equals(mSureNewPassword.getText().toString())) {

                            return true;
                        } else {
                            Toast.makeText(this, "两次新密码不相同，请确认", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(this, "新密码的长度必须大于等于6", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(this, "请确认新密码", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "新密码的长度必须大于等于6", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, "请输入新密码", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

}
