package com.white.usee.app.bean;

import com.white.usee.app.util.Dp2Px;
import master.flame.danmaku.danmaku.model.Duration;
import master.flame.danmaku.danmaku.model.IDisplayer;
import master.flame.danmaku.danmaku.model.R2LDanmaku;

/**
 * 重写R2LDanmuku,为了实现控制Danmu在哪一行出现
 * Created by 10037 on 2016/7/11 0011.
 */
public class USeeDanmu extends R2LDanmaku {
    private int lineHight = Dp2Px.dip2px(48);
    private int topPad = Dp2Px.dip2px(16);
    protected float x = 0;
    protected float y = -1;

    public String userid;
    public int danmuStyle;//弹幕风格

    public int danmuHot;

    protected int mDistance;

    protected float[] RECT = null;

    protected float mStepX;

    protected long mLastTime;
    public String id;
    public int line;//出现在第几行

    public USeeDanmu(Duration duration,int line) {
        super(duration);
        this.line = line;
    }

    @Override
    public void layout(IDisplayer displayer, float x, float y) {
        if (mTimer != null) {
            long currMS = mTimer.currMillisecond;
            long deltaDuration = currMS - getTime();
            if (deltaDuration > 0 && deltaDuration < duration.value) {
                this.x = getAccurateLeft(displayer, currMS);
                if (!this.isShown()) {
                    this.y =  lineHight*line+topPad;
                    this.setVisibility(true);
                }
                mLastTime = currMS;
                return;
            }
            mLastTime = currMS;
        }
        this.setVisibility(false);
    }

    protected float getAccurateLeft(IDisplayer displayer, long currTime) {
        long elapsedTime = currTime - getTime();
        if (elapsedTime >= duration.value) {
            return -paintWidth;
        }

        return displayer.getWidth() - elapsedTime * mStepX;
    }

    @Override
    public float[] getRectAtTime(IDisplayer displayer, long time) {
        if (!isMeasured())
            return null;
        float left = getAccurateLeft(displayer, time);
        if (RECT == null) {
            RECT = new float[4];
        }
        RECT[0] = left;
        RECT[1] = y;
        RECT[2] = left + paintWidth;
        RECT[3] = y + paintHeight;
        return RECT;
    }

    @Override
    public float getLeft() {
        return x;
    }

    @Override
    public float getTop() {
        return y;
    }

    @Override
    public float getRight() {
        return x + paintWidth;
    }

    @Override
    public float getBottom() {
        return y + paintHeight;
    }

    @Override
    public int getType() {
        return TYPE_SCROLL_RL;
    }

    @Override
    public void measure(IDisplayer displayer, boolean fromWorkerThread) {
        super.measure(displayer, fromWorkerThread);
        mDistance = (int) (displayer.getWidth() + paintWidth);
        mStepX = mDistance / (float) duration.value;
    }


}
