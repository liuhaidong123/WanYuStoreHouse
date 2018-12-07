package com.storehouse.wanyu.activity.LoginActivity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.storehouse.wanyu.Bean.LoginBean;
import com.storehouse.wanyu.IPAddress.URLTools;
import com.storehouse.wanyu.MyUtils.BallProgressUtils;
import com.storehouse.wanyu.MyUtils.CheckPhone;
import com.storehouse.wanyu.MyUtils.SharedPrefrenceTools;
import com.storehouse.wanyu.OkHttpUtils.OkHttpManager;
import com.storehouse.wanyu.R;
import com.storehouse.wanyu.activity.MainActivity;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Response;

//登录页面
public class LoginActivity extends AppCompatActivity {
    private RelativeLayout mAll_rl;
    private SharedPrefrenceTools sharedPrefrenceTools;
    private OkHttpManager mOkHttpManager;
    private EditText mName_Edit, mPassword_edit;
    private TextView mLogin_Btn;
    private String url;
    // private String cookie;
    private Gson gson = new Gson();
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            BallProgressUtils.dismisLoading();
            if (msg.what == 1) {//数据正确返回
                String mes = (String) msg.obj;
                Log.e("登录成功=", mes);
                Object o = gson.fromJson(mes, LoginBean.class);
                if (o != null && o instanceof LoginBean) {
                    LoginBean loginBean = (LoginBean) o;
                    if ("0".equals(loginBean.getCode())) {
                        SharedPrefrenceTools.put("phone", getName());//手机号
                        SharedPrefrenceTools.put("password", getPassword());
                        SharedPrefrenceTools.put("truename", loginBean.getUser().getTrueName() + "");//姓名
                        SharedPrefrenceTools.put("avatar", loginBean.getUser().getAvatar() + "");//头像
                        SharedPrefrenceTools.put("departmentName", loginBean.getUser().getDepartmentName() + "");//部门
                        SharedPrefrenceTools.put("userID", loginBean.getUser().getId());//部门

                        for (int i = 0; i < loginBean.getUser().getPermission().size(); i++) {//存放权限实体类
                            SharedPrefrenceTools.saveObject("Permission" + i, loginBean.getUser().getPermission().get(i));
                        }
                        SharedPrefrenceTools.put("PermissionNum", loginBean.getUser().getPermission().size());//存放功能数量
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "登录失败，请重新登录", Toast.LENGTH_SHORT).show();
                    }
                }

            } else {
                //数据错误
                Toast.makeText(LoginActivity.this, "登录数据错误", Toast.LENGTH_SHORT).show();
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAll_rl = (RelativeLayout) findViewById(R.id.activity_login);
        sharedPrefrenceTools = SharedPrefrenceTools.getSharedPrefrenceToolsInstance(this);
        mOkHttpManager = OkHttpManager.getInstance();
        url = URLTools.urlBase + URLTools.loginUrl;//登录地址
        mName_Edit = (EditText) findViewById(R.id.name_edit);
        mPassword_edit = (EditText) findViewById(R.id.password_edit);
        mLogin_Btn = (TextView) findViewById(R.id.login_btn);
        mLogin_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!"".equals(getName()) && !"".equals(getPassword())) {
                    BallProgressUtils.showLoading(LoginActivity.this, mAll_rl);
                    Map<Object, Object> map = new HashMap<>();
                    map.put("name", getName());
                    map.put("password", getPassword());
                    mOkHttpManager.postMethod(true, url, "登录接口", map, handler, 1);
                } else {
                    Toast.makeText(LoginActivity.this, "请填写正确的手机号或密码", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public String getName() {
        if (mName_Edit.getText().toString() != null && !mName_Edit.getText().toString().trim().equals("") && CheckPhone.checkPhone(mName_Edit.getText().toString().trim())) {
            return mName_Edit.getText().toString().trim();
        }
        return "";
    }

    public String getPassword() {
        if (mPassword_edit.getText().toString() != null && !mPassword_edit.getText().toString().trim().equals("")) {
            return mPassword_edit.getText().toString().trim();
        }
        return "";
    }
}
