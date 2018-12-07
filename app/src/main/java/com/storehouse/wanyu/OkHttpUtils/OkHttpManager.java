package com.storehouse.wanyu.OkHttpUtils;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.gson.Gson;
import com.storehouse.wanyu.MyUtils.SharedPrefrenceTools;
//import com.home.wanyu.User.UserInfo;
//import com.home.wanyu.bean.haveAddress.Root;

import java.io.File;
import java.io.IOException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;


/**
 * Created by liuhaidong on 2017/6/6.
 */

public class OkHttpManager extends AppCompatActivity {

    private static OkHttpManager mOkHttpManager;
    private OkHttpClient mOkhttpClient;
    private OkHttpClient.Builder builder;
    private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();

    private OkHttpManager() {
        mOkhttpClient = new OkHttpClient();
        builder = mOkhttpClient.newBuilder();
        builder.connectTimeout(10, TimeUnit.SECONDS).readTimeout(10, TimeUnit.SECONDS).writeTimeout(10, TimeUnit.SECONDS);


    }

    public static OkHttpManager getInstance() {

        if (mOkHttpManager == null) {
            mOkHttpManager = new OkHttpManager();
        }
        return mOkHttpManager;
    }

    /**
     * 普通get方式请求
     *
     * @param flag    这个参数只是为了判断是不是为登录接口，true为登录接口,false为其他接口
     * @param url     请求地址
     * @param msg     为了查看方便
     * @param handler
     * @param mwhat
     */
    public void getMethod(boolean flag, String url, final String msg, final Handler handler, final int mwhat) {
        Request request = buildRequest(flag, url, msg, null, HttpMethodType.GET);
        Log.e("接口：", msg + "的地址" + url);
        doRequest(flag, request, msg, handler, mwhat);
    }

    /**
     * 普通post方式请求
     *
     * @param flag    这个参数只是为了判断是不是为登录接口，true为登录接口,false为其他接口
     * @param url     请求地址
     * @param msg     为了查看方便
     * @param handler
     * @param mwhat
     */
    public void postMethod(boolean flag, String url, final String msg, Map<Object, Object> map, final Handler handler, final int mwhat) {
        Request request = buildRequest(flag, url, msg, map, HttpMethodType.POST);
        Log.e("", msg + "的地址" + url);
        doRequest(flag, request, msg, handler, mwhat);

    }

    /**
     * cookie=response.headers().get("Set-Cookie");//这个是从网络上获取的cookie
     * 上传文件（需要添加cookie）request.header("cookie","token="+token)
     *
     * @param url     请求地址
     * @param msg     为了查看方便
     * @param map     参数集合
     * @param files   文件集合
     * @param handler
     * @param mwhat
     */
    public void postFileMethod(String url, final String msg, Map<Object, Object> map, File[] files, final Handler handler, final int mwhat) {
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        //添加文件
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                Log.e("files[i].getName()", files[i].getName());
                Log.e("files[i].getName()", files[i].getName());
                RequestBody fileBody = RequestBody.create(MediaType.parse(getMediaType(files[i].getName())), files[i]);

                builder.addFormDataPart("文件" + i, files[i].getName(), fileBody);
            }
        }
        //添加参数
        if (map != null) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(url);
            for (Map.Entry<Object, Object> entry : map.entrySet()) {
                builder.addFormDataPart((String) entry.getKey(), (String) entry.getValue());
                stringBuilder.append(entry.getKey());
                stringBuilder.append("=" + entry.getValue() + "&");
            }
            Log.e("url地址=", msg + stringBuilder.toString().substring(0, stringBuilder.toString().length() - 1));
        }

        Request request = new Request.Builder().url(url).header("cookie", (String) SharedPrefrenceTools.getValueByKey("cookie", "cookie=null")).post(builder.build()).build();
        mOkhttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("失败原因", msg + e.toString());
                Message message = new Message();
                message.what = 1010;//1010代表数据错误
                handler.sendMessage(message);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String string = response.body().string();
                    Log.e("成功结果", msg + string);
                    Message message = new Message();
                    message.what = mwhat;
                    message.obj = string;
                    handler.sendMessage(message);
                } else {
                    Message message = new Message();
                    message.what = 1010;//1010代表数据错误
                    handler.sendMessage(message);
                }
            }
        });

    }


    /**
     * 根据文件的名称判断文件的Mine值
     */
    private String getMediaType(String fileName) {
        FileNameMap map = URLConnection.getFileNameMap();
        String contentTypeFor = map.getContentTypeFor(fileName);
        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }

    /**
     * 网络请求回调
     *
     * @param request
     * @param msg
     * @param handler
     * @param mwhat
     */
    public void doRequest(final boolean flag, Request request, final String msg, final Handler handler, final int mwhat) {

        mOkhttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("失败原因", msg + e.toString());
                Message message = new Message();
                message.what = 1010;//0代表数据错误
                handler.sendMessage(message);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String string = response.body().string();
                    if (flag) {
                        String cookie = response.headers().get("Set-Cookie");
                        if (cookie!=null&&!"".equals(cookie)){
                            SharedPrefrenceTools.put("cookie", cookie);
                        }

                    }
                    Log.e("成功", msg + string);
                    Message message = new Message();
                    message.what = mwhat;
                    message.obj = string;
                    handler.sendMessage(message);
                } else {
                    Message message = new Message();
                    message.what = 1010;//1010代表数据错误
                    handler.sendMessage(message);
                }
            }
        });
    }


    /**
     * 构建请求对象
     *
     * @param flag 如果是true，则为登录接口，不需要添加cookie
     * @param url
     * @param map
     * @param type
     * @return
     */
    private Request buildRequest(boolean flag, String url, String msg, Map<Object, Object> map, HttpMethodType type) {
        Request.Builder builder = new Request.Builder();
        //这里需要判断，如果是调用时登录接口，则不需要添加cookie，
        if (flag) {
            builder.url(url);
        } else {
            builder.url(url).header("cookie", (String) SharedPrefrenceTools.getValueByKey("cookie", "cookie=null"));
        }

        if (type == HttpMethodType.GET) {//get方法请求
            builder.get();
        } else if (type == HttpMethodType.POST) {//post方法请求
            builder.post(buildFormData(map, url, msg));
        }
        return builder.build();
    }

    //构建请求所需的参数表单
    private RequestBody buildFormData(Map<Object, Object> map, String url, String msg) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(url);
        FormBody.Builder builder = new FormBody.Builder();
        if (map != null) {
            for (Map.Entry<Object, Object> entry : map.entrySet()) {
                builder.add(entry.getKey() + "", entry.getValue() + "");
                stringBuilder.append(entry.getKey());
                stringBuilder.append("=" + entry.getValue() + "&");
            }
            Log.e("url地址=", msg + stringBuilder.toString().substring(0, stringBuilder.toString().length() - 1));
        }
        return builder.build();
    }

}
