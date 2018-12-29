package com.storehouse.wanyu.fragment;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.storehouse.wanyu.Bean.SetPasswordRoot;
import com.storehouse.wanyu.IPAddress.URLTools;
import com.storehouse.wanyu.MyUtils.CircleImg;
import com.storehouse.wanyu.MyUtils.SharedPrefrenceTools;
import com.storehouse.wanyu.OkHttpUtils.OkHttpManager;
import com.storehouse.wanyu.R;
import com.storehouse.wanyu.activity.LoginActivity.LoginActivity;
import com.storehouse.wanyu.activity.SetActivity.MyMessageActivity;
import com.storehouse.wanyu.activity.SetActivity.MyPropertyActivity;
import com.storehouse.wanyu.activity.SetActivity.MySetActivity;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_My extends Fragment {
    private CircleImg mHead_img;
    private RelativeLayout mMySet_Rl, mMyProperty_rl, mMyMessage_rl;
    private SharedPrefrenceTools sharedPrefrenceTools;
    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;
    private View alertView;
    private TextView mCamera_Btn, mPicture_Btn, mPhone_tv, mName_tv;
    private static final int PERMISSIONS_REQUESTCODE = 300;
    private int flag;//1代表跳转到相册，2代表是调起相机拍照
    public static final int INTENT_ALBUM = 100;//跳转相册
    public static final int INTENT_CAMERA = 200;//拍照
    public static final int MEDIA_TYPE_IMAGE = 1;
    private int ScreenWith, ScreenHeight;
    private File file;//相册file
    private File mediaFile;
    private Uri fileUri;
    private OkHttpManager okHttpManager;
    private Gson gson = new Gson();
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {//上传头像
                String s = (String) msg.obj;
                Object o = gson.fromJson(s, SetPasswordRoot.class);
                if (o != null && o instanceof SetPasswordRoot) {
                    SetPasswordRoot root = (SetPasswordRoot) o;
                    if ("0".equals(root.getCode())) {
                        Log.e("头像返回路径", root.getMessage() + "");
                        SharedPrefrenceTools.put("avatar", root.getMessage() + "");//头像
                        Toast.makeText(getContext(), "头像上传成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "头像上传失败", Toast.LENGTH_SHORT).show();
                    }
                }


            } else {
                Toast.makeText(getContext(), "数据错误", Toast.LENGTH_SHORT).show();
            }
        }
    };

    public Fragment_My() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.e("我的", "Fragment_My");
        sharedPrefrenceTools = SharedPrefrenceTools.getSharedPrefrenceToolsInstance(getActivity());
        View view = inflater.inflate(R.layout.fragment_fragment__my, container, false);
        init(view);
        return view;
    }


    private void init(View view) {
        okHttpManager = OkHttpManager.getInstance();

        builder = new AlertDialog.Builder(getContext());
        alertDialog = builder.create();
        alertView = LayoutInflater.from(getContext()).inflate(R.layout.camrea_picture, null);
        alertDialog.setView(alertView);
        mCamera_Btn = (TextView) alertView.findViewById(R.id.camrea_btn);
        mPicture_Btn = (TextView) alertView.findViewById(R.id.picture_btn);
        ScreenWith = getResources().getDisplayMetrics().widthPixels;
        ScreenHeight = getResources().getDisplayMetrics().heightPixels;
        //拍照
        mCamera_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag = 2;
                alertDialog.dismiss();
                checkMyPermission();
            }
        });
        //选择图片
        mPicture_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag = 1;
                alertDialog.dismiss();
                checkMyPermission();
            }
        });


        mMySet_Rl = view.findViewById(R.id.my_set_rl);
        //设置
        mMySet_Rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MySetActivity.class);
                startActivity(intent);

            }
        });

        //我的资产
        mMyProperty_rl = view.findViewById(R.id.my_property_rl);
        mMyProperty_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MyPropertyActivity.class);
                startActivity(intent);

            }
        });

        //我的消息
        mMyMessage_rl = view.findViewById(R.id.my_msg_rl);
        mMyMessage_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MyMessageActivity.class);
                startActivity(intent);

            }
        });
        mHead_img = view.findViewById(R.id.my_head_img);
        mHead_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.show();
            }
        });
        Picasso.with(getActivity()).load(URLTools.urlBase + SharedPrefrenceTools.getValueByKey("avatar", "")).error(R.mipmap.head).into(mHead_img);
        mPhone_tv = view.findViewById(R.id.my_tel_tv);
        mName_tv = view.findViewById(R.id.my_name_tv);
        mPhone_tv.setText("电话:" + SharedPrefrenceTools.getValueByKey("phone", "--"));
        mName_tv.setText("姓名:" + SharedPrefrenceTools.getValueByKey("truename", "--"));

    }

    //判断权限并跳转相册
    public void checkMyPermission() {
        //sdk版本>=23时，
        if (Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
            //如果没有授权
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, PERMISSIONS_REQUESTCODE);
                return;
                //如果已经授权，执行业务逻辑
            } else {
                if (flag == 1) {//相册
                    intentAlbum();
                } else {//相机
                    intentCamera();
                }


            }
            //版本小于23时，不需要判断敏感权限，执行业务逻辑
        } else {
            if (flag == 1) {//相册
                intentAlbum();
            } else {//相机
                intentCamera();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUESTCODE:
                //点击了允许
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (flag == 1) {//相册
                        intentAlbum();
                    } else {//相机
                        intentCamera();
                    }
                    //点击了拒绝
                } else {
                    Toast.makeText(getContext(), "您已禁止访问相册", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == INTENT_ALBUM) {//获取相册图片并显示
            showAlbumPicture(data);
        } else {//拍照以后显示图片
            if (data != null) {
                if (data.hasExtra("data")) {
                    Bitmap thumbnail = data.getParcelableExtra("data");
                    mHead_img.setImageBitmap(thumbnail);
                }

            } else {
                Bitmap resizeBitmap = null;

                //拍照完毕后，获取返回数据显示照片，api24以后，需要转换输入流来获取Bitmap，
                // 而输入流的获取需要通过getContentResolvrer.openInputStream()来获取，
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(getContext().getContentResolver().openInputStream(fileUri));
                        int bitmapWidth = bitmap.getWidth();
                        int bitmapHeight = bitmap.getHeight();
                        // 缩放图片的尺寸
                        float scaleWidth = (float) ScreenWith / bitmapWidth - 0.1f;
                        float scaleHeight = (float) ScreenHeight / bitmapHeight - 0.1f;
                        Matrix matrix = new Matrix();
                        matrix.postScale(scaleWidth, scaleHeight);
                        // 产生缩放后的Bitmap对象
                        resizeBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmapWidth, bitmapHeight, matrix, false);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();

                    }

                } else {

                    BitmapFactory.Options factoryOptions = new BitmapFactory.Options();
                    factoryOptions.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(fileUri.getPath(), factoryOptions);
                    int imageWidth = factoryOptions.outWidth;
                    int imageHeight = factoryOptions.outHeight;
                    int scaleFactor = Math.min(imageWidth / ScreenWith, imageHeight / ScreenWith);
                    factoryOptions.inJustDecodeBounds = false;
                    factoryOptions.inSampleSize = scaleFactor;
                    factoryOptions.inPurgeable = true;
                    resizeBitmap = BitmapFactory.decodeFile(fileUri.getPath(), factoryOptions);


                }

                if (resizeBitmap != null) {
                    mHead_img.setImageBitmap(resizeBitmap);
                    //拍照上传头像
                    File file = new File(mediaFile.getAbsolutePath());//将要保存图片的路径
                    try {
                        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
                        resizeBitmap.compress(Bitmap.CompressFormat.JPEG, 50, bos);
                        bos.flush();
                        bos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Map<Object, Object> map = new HashMap<>();
                    File[] files = {file};
                    String url = URLTools.urlBase + URLTools.post_head_url;
                    okHttpManager.postFileMethod(url, "拍照上传头像", map, files, handler, 1);
                } else {
                    //如果没有拍照

                }


            }
        }
    }

//===================调用相册==============================

    /**
     * 调用相册： 跳转相册
     */
    private void intentAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, INTENT_ALBUM);
    }

    /**
     * 调用相册： 获取相册图片路径
     *
     * @param uri
     * @return
     */
    private String getRealPathFromURI(Uri uri) {
        String strPath = null;
        Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            strPath = cursor.getString(column_index);
        }
        cursor.close();
        return strPath;
    }

    /**
     * 调用相册： 显示相册选取的图片
     */
    private void showAlbumPicture(Intent data) {
        //获取相册图片
        if (data != null && data.getData() != null) {
            String path = getRealPathFromURI(data.getData());
            file = new File(path);
            if (file == null) {
                return;
            }
            if (file.length() == 0) {
                file.delete();
                return;
            }

            try {
                BitmapFactory.Options o = new BitmapFactory.Options();
                o.inJustDecodeBounds = true;//如果该 值设为true那么将不返回实际的bitmap，也不给其分配内存空间这样就避免内存溢出了。但是允许我们查询图片的信息这其中就包括图片大小信息
                FileInputStream fileInputStream = new FileInputStream(file);//获取文件输入流
                BitmapFactory.decodeStream(fileInputStream, null, o);//

                int bitmap_width = o.outWidth;
                int bitmap_height = o.outHeight;
                int scale = 2;
                while (true) {
                    //如果返回bitmap的宽度除以缩放的比例后，仍然比屏幕的的宽度大，那么继续缩放
                    if (bitmap_width / scale < ScreenWith) {
                        break;
                    }
                    scale *= 2;
                }

                scale /= 2;//再次缩小2
                BitmapFactory.Options o2 = new BitmapFactory.Options();
                o2.inSampleSize = scale;
                FileInputStream fileInpustream2 = new FileInputStream(file);
                o2.inPreferredConfig = Bitmap.Config.RGB_565;//默认是ARGB_8888 一个像素占用4个字节，RGB_565一个像素2个字节
                Bitmap bitmap = BitmapFactory.decodeStream(fileInpustream2, null, o2);
                mHead_img.setImageBitmap(bitmap);//显示图片
                Log.e("缩放前bitmap宽", bitmap_width + "");
                Log.e("缩放前bitmap高", bitmap_height + "");
                Log.e("缩放后bitmap宽", bitmap.getWidth() + "");
                Log.e("缩放后bitmap高", bitmap.getHeight() + "");
                Log.e("scale缩放比例：", scale + "");
                Log.e("file路径：", path);
                Log.e("file名称：", file.getName());
                Log.e("file长度：", file.length() + "");
                Log.e("file大小：", file.length() / 1024 + "k");

                //相册上传头像

                Map<Object, Object> map = new HashMap<>();
                File[] files = {file};
                String url = URLTools.urlBase + URLTools.post_head_url;
                okHttpManager.postFileMethod(url, "相册上传头像", map, files, handler, 1);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.e("bitmap error Msg", e.toString());
            }
        } else {
            // Toast.makeText(getContext(), "图片错误", Toast.LENGTH_SHORT).show();
        }


    }

    //===================拍照：调出相机==============================

    //拍照：调出相机
    private void intentCamera() {
        // 利用系统自带的相机应用:拍照
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        startActivityForResult(intent, INTENT_CAMERA);
    }

    //拍照： 由file文件获取的uri
    private Uri getOutputMediaFileUri(int type) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //SDK大于等于24的时候，必须使用下面这种方法获取uri
            return FileProvider.getUriForFile(getContext(), getContext().getPackageName() + ".fileProvider", getOutputMediaFile(type));
        }
        return Uri.fromFile(getOutputMediaFile(type));
    }

    //拍照： 创建一个文件
    private File getOutputMediaFile(int type) {
        File mediaStorageDir = null;
        try {
            mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MyCameraApp");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        //mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }
        Log.e("mediaFile", mediaFile.getAbsolutePath());
        return mediaFile;
    }


}
