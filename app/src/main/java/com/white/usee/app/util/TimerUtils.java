package com.white.usee.app.util;

import android.content.Context;
import android.os.Handler;

/**
 * 利用handler post来做定时器
 * Created by 10037 on 2016/8/2 0002.
 */

public class TimerUtils {
    //自定义的计时器，time定时时间，单位为秒
    public static void startTime(Context context, int time, final OnTimerListenr onTimerListenr) {
        final int[] times = {time};
        final Handler handler = new Handler(context.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                times[0]--;
                if (times[0] == 0) {
                    if (onTimerListenr != null) onTimerListenr.TimeOver();
                } else {
                    if (onTimerListenr != null)
                        if (onTimerListenr.TimeIng(handler, this, times[0])){
                            handler.postDelayed(this, 1000);
                        }else {

                        }
                    else handler.postDelayed(this, 1000);
                }

            }
        }, 1000);
    }

    public interface OnTimerListenr {
        void TimeOver();

        boolean TimeIng(Handler handler, Runnable runnable, int time);
    }
}
