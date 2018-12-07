package com.storehouse.wanyu.MyUtils;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.storehouse.wanyu.R;

/**
 * Created by liuhaidong on 2017/12/27.
 */

public class BallProgressUtils {
    private static TwoBallRotationProgressBar twoBallRotationProgressBar;

    /**
     * @param context 上下文
     * @param view    包含loading的父布局
     */
    public static void showLoading(Context context, ViewGroup view) {
        twoBallRotationProgressBar = new TwoBallRotationProgressBar(context);
        view.addView(twoBallRotationProgressBar);
    }

    public static void dismisLoading() {
        if (twoBallRotationProgressBar != null) {
            twoBallRotationProgressBar.setVisibility(View.GONE);
        }
    }

}
