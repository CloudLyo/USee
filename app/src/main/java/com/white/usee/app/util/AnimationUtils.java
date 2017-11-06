package com.white.usee.app.util;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageButton;

import com.white.usee.app.BaseApplication;
import com.white.usee.app.R;


/**
 * 动画工具累
 * Created by white on 2016/4/22 0022.
 */
public class AnimationUtils {
    //开始fab
    public static void showFab(ImageButton fab) {
        if (fab.getVisibility() == View.GONE) {
            Animation animation = android.view.animation.AnimationUtils.loadAnimation(BaseApplication.getInstance(), R.anim.fab_in);
            animation.setInterpolator(new FastOutSlowInInterpolator());
            fab.setAnimation(animation);
            fab.setVisibility(View.VISIBLE);
        }
    }

    //关闭fab
    public static void closeFab(ImageButton fab) {
        if (fab.getVisibility() == View.VISIBLE) {
            Animation animation = android.view.animation.AnimationUtils.loadAnimation(BaseApplication.getInstance(), R.anim.fab_out);
            animation.setInterpolator(new FastOutSlowInInterpolator());
            fab.setAnimation(animation);
            fab.setVisibility(View.GONE);
        }

    }

    //淡入效果
    public static void alpha_in(View view) {
        if (view.getVisibility() == View.GONE) {
            Animation animation = android.view.animation.AnimationUtils.loadAnimation(BaseApplication.getInstance(), R.anim.alpha_in);
            view.setAnimation(animation);
            view.setVisibility(View.VISIBLE);
        }
    }

    //淡出效果
    public static void alpha_out(View view) {
        if (view.getVisibility() == View.VISIBLE) {
            Animation animation = android.view.animation.AnimationUtils.loadAnimation(BaseApplication.getInstance(), R.anim.alpha_out);
            view.setAnimation(animation);
            view.setVisibility(View.GONE);
        }
    }

    //多少秒后淡出
    public static void alpha_outByTime(final View view, long time) {
        Handler alpha_outHandler = new Handler();
        alpha_outHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                alpha_out(view);
            }
        }, time);
    }

    //旋转 度
    public static void rotation(View view, float value) {
        ViewCompat.animate(view)
                .rotation(value)
                .setDuration(400)
                .setInterpolator(new FastOutSlowInInterpolator())
                .start();
    }

    /**
     * 当Snake出现时,普通的view用此方法来同步运动,延时为1500,动画时间为250
     */
    public static void animateViewIn(final View mView, final long delay, final long duration) {
        animateViewIn(mView, delay, duration, mView.getHeight());
    }

    public static void animateViewIn(final View mView, final long delay, final long duration, int height) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            ViewCompat.animate(mView)
                    .translationY(-height)
                    .setInterpolator(new FastOutSlowInInterpolator())
                    .setDuration(duration)
                    .setStartDelay(0)
                    .setListener(new ViewPropertyAnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(View view) {
                        }

                        @Override
                        public void onAnimationEnd(View view) {
                            view.clearAnimation();
                            animateViewOut(mView, delay, duration);
                        }
                    }).start();
        } else {
            Animation anim = android.view.animation.AnimationUtils.loadAnimation(mView.getContext(),
                    android.support.design.R.anim.design_snackbar_in);
            anim.setInterpolator(new FastOutSlowInInterpolator());
            anim.setDuration(duration);
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationEnd(Animation animation) {
                }

                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            mView.startAnimation(anim);
        }
    }

    public static void animateViewOut(final View mView, long delay, long duration) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            ViewCompat.animate(mView)
                    .translationY(0)
                    .setInterpolator(new FastOutSlowInInterpolator())
                    .setDuration(duration)
                    .setStartDelay(delay)
                    .setListener(new ViewPropertyAnimatorListener() {
                        @Override
                        public void onAnimationStart(View view) {

                        }

                        @Override
                        public void onAnimationEnd(View view) {
                            mView.clearAnimation();
                        }

                        @Override
                        public void onAnimationCancel(View view) {
                            view.clearAnimation();
                        }
                    })
                    .start();
        } else {
            Animation anim = android.view.animation.AnimationUtils.loadAnimation(mView.getContext(),
                    android.support.design.R.anim.design_snackbar_out);
            anim.setInterpolator(new FastOutSlowInInterpolator());
            anim.setDuration(duration);
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationEnd(Animation animation) {
                }

                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            mView.startAnimation(anim);
        }
    }
    //从中间出现的动画
    public static void scaleFromCenter(View view){
//        ViewCompat.animate(view)
//                .setInterpolator(new FastOutSlowInInterpolator())
//                .setDuration(300)
//                .scaleX(1)
//                .scaleY(1)
//                .start();
        Animation scaleAni = android.view.animation.AnimationUtils.loadAnimation(BaseApplication.getInstance(),R.anim.scale_center);
        view.startAnimation(scaleAni);
    }

    public static void translateFromRightToLeft(View view){
        Animation animation = android.view.animation.AnimationUtils.loadAnimation(BaseApplication.getInstance(),R.anim.translate_right2left);
        animation.setInterpolator(new FastOutSlowInInterpolator());
        view.setAnimation(animation);
    }
}
