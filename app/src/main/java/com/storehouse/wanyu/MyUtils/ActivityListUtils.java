package com.storehouse.wanyu.MyUtils;

import android.app.Activity;

import com.storehouse.wanyu.activity.MainActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuhaidong on 2018/12/27.
 */

public class ActivityListUtils {
    private static ActivityListUtils activityListUtils;
    private List<Activity> list = new ArrayList<>();

    public static ActivityListUtils getActivityListUtilsInstance() {
        if (activityListUtils == null) {
            activityListUtils = new ActivityListUtils();
        }
        return activityListUtils;
    }

    private ActivityListUtils() {

    }

    public void saveMainActivity(Activity activity) {
        list.add(activity);
    }

    public List<Activity> getList(){
        return list;
    }
}
