package com.white.usee.app.util;

import android.app.Activity;
import android.content.Context;
import android.view.Display;
import android.view.View;

import com.white.usee.app.BaseApplication;

/**
 * dp与px之间的转换
 * Created by white on 2015/12/31 0031.
 */
public class Dp2Px {
    public static int px_32 = dip2px(32);
    public static int px_16=dip2px(16);
    public static int px_24=dip2px(24);
    public static int px_2 = dip2px(2);
    public static int px_1=dip2px(1);

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
    public static int dip2px(float dpValue){
        return dip2px(BaseApplication.getInstance(),dpValue);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale+ 0.5f);
    }

    /**
     * 获取屏幕宽度
     * */
    public static int getWindowWidth(Activity activity){
        Display display = activity.getWindowManager().getDefaultDisplay();
        return display.getWidth();
    }
    /**
     * 获取屏幕宽度
     * */
    public static int getWindowHeight(Activity activity){
        Display display = activity.getWindowManager().getDefaultDisplay();
        return display.getHeight();
    }
}
