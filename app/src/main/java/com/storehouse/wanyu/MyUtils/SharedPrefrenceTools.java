package com.storehouse.wanyu.MyUtils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;

import com.storehouse.wanyu.Bean.Permission;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by liuhaidong on 2018/8/2.
 */

public class SharedPrefrenceTools {
    public static SharedPreferences sharedPreferences;
    public static SharedPreferences.Editor editor;
    public static SharedPrefrenceTools sharedPrefrenceTools;
    private List<Permission> mPermission=new ArrayList<>();
    public static SharedPrefrenceTools getSharedPrefrenceToolsInstance(Context context) {
        if (sharedPrefrenceTools == null) {
            sharedPrefrenceTools = new SharedPrefrenceTools(context);
        }
        return sharedPrefrenceTools;
    }

    private SharedPrefrenceTools(Context context) {
        if (sharedPreferences == null) {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            editor = sharedPreferences.edit();
        }
    }

    /**
     * 存储
     */
    public static void put(String key, Object object) {
        if (object instanceof String) {
            editor.putString(key, (String) object);
        } else if (object instanceof Integer) {
            editor.putInt(key, (Integer) object);
        } else if (object instanceof Boolean) {
            editor.putBoolean(key, (Boolean) object);
        } else if (object instanceof Float) {
            editor.putFloat(key, (Float) object);
        } else if (object instanceof Long) {
            editor.putLong(key, (Long) object);
        } else {
            editor.putString(key, object.toString());
        }
        editor.commit();
    }

    /**
     * 获取保存的数据
     */
    public static Object getValueByKey(String key, Object defaultObject) {
        if (defaultObject instanceof String) {
            return sharedPreferences.getString(key, (String) defaultObject);
        } else if (defaultObject instanceof Integer) {
            return sharedPreferences.getInt(key, (Integer) defaultObject);
        } else if (defaultObject instanceof Boolean) {
            return sharedPreferences.getBoolean(key, (Boolean) defaultObject);
        } else if (defaultObject instanceof Float) {
            return sharedPreferences.getFloat(key, (Float) defaultObject);
        } else if (defaultObject instanceof Long) {
            return sharedPreferences.getLong(key, (Long) defaultObject);
        } else {
            return sharedPreferences.getString(key, null);
        }
    }

    /**
     * 添加对象
     *添加对象,注意，添加的实体类需要序列化
     * @param key
     * @param value
     */
    public static void saveObject(String key, Object value) {
        ObjectOutputStream objectOutputStream = null;
        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            objectOutputStream = new ObjectOutputStream(byteOut);
            objectOutputStream.writeObject(value);
            objectOutputStream.flush();
            String str=  Base64.encodeToString(byteOut.toByteArray(),Base64.DEFAULT);
            editor.putString(key, str);
            editor.commit();
        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            if (objectOutputStream != null) {
                try {
                    objectOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }


    //使用sharedPre获取对象
    public static Object getObject(String key) {
        String strObj = sharedPreferences.getString(key,"");
        if (strObj==null){
            return null;
        }
        byte[] b = Base64.decode(strObj,Base64.DEFAULT);
        ObjectInputStream objectInputStream = null;
        Object object = null;
        try {
            ByteArrayInputStream byteArray = new ByteArrayInputStream(b);
            objectInputStream = new ObjectInputStream(byteArray);
            object = objectInputStream.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (objectInputStream != null) {
                try {
                    objectInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (object != null && !object.equals("")) {
            Log.e("Tag-get", b.length + "---------------------");
            return object;
        }
        return null;
    }
    /**
     * 移除某个key值已经对应的值
     */
    public static void remove(String key) {
        editor.remove(key);
        editor.commit();
    }

    /**
     * 清除所有数据
     */
    public static void clear() {
        editor.clear();
        editor.commit();
    }

    /**
     * 查询某个key是否存在
     */
    public static Boolean contain(String key) {
        return sharedPreferences.contains(key);
    }

    /**
     * 返回所有的键值对
     */
    public Map<String, ?> getAll() {
        return sharedPreferences.getAll();
    }
}
