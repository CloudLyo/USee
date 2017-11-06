package com.white.usee.app.util;

import android.util.Log;

/**
 *
 * Log工具
 * TODO 上线的时候记得关闭 isLog=false
 * Created by white on 16/4/23.
 */
public class LogUtil {
    public static boolean isLog = false;
    public static String tag ="usee";
    public static void i(String msg){
        if (isLog)Log.i(tag,msg+"");
    }

    public static void e(String msg){
        if (isLog) Log.e(tag,msg+"");
    }
}
