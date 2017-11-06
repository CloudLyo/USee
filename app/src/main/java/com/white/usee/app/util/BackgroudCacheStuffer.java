package com.white.usee.app.util;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;

import com.white.usee.app.bean.USeeDanmu;

import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.android.SpannedCacheStuffer;

/**
 * 绘制背景(自定义弹幕样式)
 */
public class BackgroudCacheStuffer extends SpannedCacheStuffer {
    // 通过扩展SimpleTextCacheStuffer或SpannedCacheStuffer个性化你的弹幕样式
    final Paint paint = new Paint();
    Paint shadowPaint = new Paint();
    float paddingTop = Dp2Px.dip2px(1);
    float paddingLeft = Dp2Px.dip2px(1);
    float paddingRight = Dp2Px.dip2px(4);
    float paddingBottom = Dp2Px.dip2px(1);
    private RectF rect = new RectF();
    private RectF rectRound = new RectF();
    public boolean hasBg = false;//在有背景的情况下，弹幕背景要有百分四十的透明度
    private RectF shadowRect = new RectF();
    private int[] colors = {Color.parseColor("#00000000"), Color.parseColor("#ededed"), Color.parseColor("#5ed3b6"), Color.parseColor("#f8b238"), Color.parseColor("#f37e53"), Color.parseColor("#a3cda5")};
    private int[] colorsHasBg = {Color.parseColor("#00000000"), Color.parseColor("#66ededed"), Color.parseColor("#665ed3b6"), Color.parseColor("#66f8b238"), Color.parseColor("#66f37e53"), Color.parseColor("#66a3cda5")};
    private OnCollisionListener onCollisionListener;
    private USeeDanmu[] baseDanmakus = new USeeDanmu[6];

    public BackgroudCacheStuffer() {
        paint.setAntiAlias(true);
        shadowPaint.setAntiAlias(true);
        shadowPaint.setColor(Color.parseColor("#4D000000"));
    }

    @Override
    public void measure(BaseDanmaku danmaku, TextPaint paint, boolean fromWorkerThread) {

        super.measure(danmaku, paint, fromWorkerThread);
    }

    public OnCollisionListener getOnCollisionListener() {
        return onCollisionListener;
    }

    public void setOnCollisionListener(OnCollisionListener onCollisionListener) {
        this.onCollisionListener = onCollisionListener;
    }

    @Override
    public void drawBackground(BaseDanmaku danmaku, Canvas canvas, float left, float top) {
        USeeDanmu curDammu = (USeeDanmu) danmaku;
        int danmuStyle = curDammu.danmuStyle;
        //热评弹幕
        int danmuHot = curDammu.danmuHot;

        if (hasBg) paint.setColor(colorsHasBg[danmuStyle + 2]);
        else
            paint.setColor(colors[danmuStyle + 2]);
        rect.left = (left - paddingLeft + danmaku.padding);
        rect.right = (left + danmaku.paintWidth + paddingRight);
        rect.top = (top - paddingTop + danmaku.padding);
        rect.bottom = (top + danmaku.paintHeight + paddingBottom - danmaku.padding);
        shadowRect.set(rect);
        shadowRect.offset(0, Dp2Px.px_1);
        if (danmuStyle != -2)
            canvas.drawRoundRect(shadowRect, rect.height() / 2, rect.height() / 2, shadowPaint);
        canvas.drawRoundRect(rect, rect.height() / 2, rect.height() / 2, paint);

        if(danmuHot>0){
            float radius =(float) (0.15 * rect.height());
            float centerX = rect.right - (float) (0.15* rect.height());
            float centerY = rect.top + (float) (0.15 * rect.height());
            rectRound.top = centerY - radius;
            rectRound.bottom = centerY + radius;
            rectRound.left = centerX - radius;
            rectRound.right = centerX + radius;
//            paint.setColor(Color.parseColor("#ff6f00"));
            paint.setColor(Color.RED);
            canvas.drawOval(rectRound,paint);
        }
        //分别在6个弹道上存储前一个弹幕用于检查弹幕碰撞
        USeeDanmu preDanmu = baseDanmakus[curDammu.line];
        if (preDanmu != null) {
            if (!preDanmu.equals(curDammu)) {
                if (preDanmu.getLeft() < curDammu.getLeft() && preDanmu.getRight() > curDammu.getLeft()) {
//                    preDanmu.time = preDanmu.time - 200;
                    preDanmu.setTime(preDanmu.getTime()-200);
                    updateDanmuScrollSpeed(preDanmu);
//                    if (onCollisionListener!=null) onCollisionListener.OnCollision(preDanmu,curDammu);
                } else if (curDammu.getLeft() < preDanmu.getLeft() && curDammu.getRight() > preDanmu.getLeft()) {
//                    curDammu.time = curDammu.time - 200;
                    curDammu.setTime(curDammu.getTime()-200);
                    updateDanmuScrollSpeed(curDammu);
//                    if (onCollisionListener!=null) onCollisionListener.OnCollision(curDammu,preDanmu);
                }
                baseDanmakus[curDammu.line] = curDammu;
            }
        } else {
            baseDanmakus[curDammu.line] = curDammu;
        }

    }

    //调节弹幕速度
    private void updateDanmuScrollSpeed(USeeDanmu danmaku) {
//        Duration duration = new Duration(danmaku.getDuration());
//        duration.setFactor((float) (0.85+0.005*(danmaku.text.length()/30)));
//        danmaku.setDuration(duration);
    }

    @Override
    public void drawStroke(BaseDanmaku danmaku, String lineText, Canvas canvas, float left, float top, Paint paint) {
        // 禁用描边绘制
    }

    private int getColor(int danmuStyle) {
        int bg_color = Color.parseColor("#4fc0ea");
        switch (danmuStyle) {
            case -2:
                bg_color = Color.parseColor("#00000000");
                break;
            case -1:
                bg_color = Color.parseColor("#ededed");
                break;
            case 0:
                bg_color = Color.parseColor("#4fc0ea");
                break;
            case 1:
                bg_color = Color.parseColor("#5ed3b6");
                break;
            case 2:
                bg_color = Color.parseColor("#f8b238");
                break;
            case 3:
                bg_color = Color.parseColor("#f37e53");
                break;
            case 4:
                bg_color = Color.parseColor("#a3cda5");
                break;

        }
        return bg_color;
    }

    public interface OnCollisionListener {
        void OnCollision(BaseDanmaku preDanmu, BaseDanmaku curDanmu);
    }

    @Override
    public void releaseResource(BaseDanmaku danmaku) {
//        SpannableStringBuilder ssb = (SpannableStringBuilder) danmaku.text;
//        ssb.clearSpans();
        if (danmaku.text instanceof Spanned) {
//            danmaku.text = "";
            SpannableStringBuilder ssb = (SpannableStringBuilder) danmaku.text;
            ssb.clearSpans();
        }
        super.releaseResource(danmaku);
    }
}