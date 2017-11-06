package com.white.usee.app.view;

import android.app.Service;
import android.content.Context;
import android.graphics.RectF;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.white.usee.app.bean.USeeDanmu;
import com.white.usee.app.util.LogUtil;

import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.IDanmakuIterator;
import master.flame.danmaku.danmaku.model.IDanmakus;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.ui.widget.DanmakuView;

/**
 * DanmuView的复写，主要实现对Danmu的点击与长按的监听
 * 已经调用了setOnClickListener和setOnLongClickListener,所以不能再次调用
 * Created by 10037 on 2016/9/5 0005.
 */

public class USeeDanmuView extends DanmakuView implements View.OnTouchListener {
    private Context context;
    private OnDanmuListener onDanmuListener;
    private RectF mDanmakuBounds = new RectF();
    private USeeDanmu clickedDanmaku;

    public USeeDanmuView(Context context) {
        super(context);
        this.context = context;
        initListener();
    }


    private void initListener() {
        getView().setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                LogUtil.i("长按");
                performUSeeDanmuViewLongClick();
                return true;
            }
        });
        getView().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                performDanmuViewClick();
                LogUtil.i("点击");
            }
        });

        getView().setOnTouchListener(this);
    }

    public USeeDanmuView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initListener();
    }

    public USeeDanmuView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        initListener();
    }


    public boolean performDanmuViewClick() {
        if (clickedDanmaku != null && onDanmuListener != null) {
            onDanmuListener.onDanmuClick(clickedDanmaku);
        }
        return true;
    }

    public boolean performUSeeDanmuViewLongClick() {
        if (null != clickedDanmaku) {
            performLongClickWitelatest(clickedDanmaku);
        }
        return true;
    }

    //计算是否有弹幕被点击
    private IDanmakus touchHitDanmaku(float x, float y) {
        IDanmakus hitDanmakus = new Danmakus();
        mDanmakuBounds.setEmpty();
        IDanmakus danmakus = this.getCurrentVisibleDanmakus();
        if (null != danmakus && !danmakus.isEmpty()) {
            IDanmakuIterator iterator = danmakus.iterator();
            while (iterator.hasNext()) {
                BaseDanmaku danmaku = iterator.next();
                if (null != danmaku) {
                    mDanmakuBounds.set(danmaku.getLeft(), danmaku.getTop(), danmaku.getRight(), danmaku.getBottom());
                    if (mDanmakuBounds.contains(x, y)) {
                        hitDanmakus.addItem(danmaku);
                    }
                }
            }
        }
        return hitDanmakus;
    }


    private USeeDanmu fetchLatestOne(IDanmakus danmakus) {
        if (danmakus.isEmpty()) {
            return null;
        }
        return (USeeDanmu) danmakus.last();
    }

    public OnDanmuListener gtOnDanmuListener() {
        return this.onDanmuListener;
    }

    public void setOnDanmuListener(OnDanmuListener onDanmuListener) {
        this.onDanmuListener = onDanmuListener;
    }


    private void performLongClickWitelatest(USeeDanmu newest) {
        if (this.onDanmuListener != null) {
            Vibrator vib = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
            vib.vibrate(200);
            this.onDanmuListener.onDanmuLongClick(newest);
        }
    }

    private float x1 = 0, y1 = 0;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float x2, y2;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                y1 = event.getY();

                IDanmakus clickDanmakus = touchHitDanmaku(event.getX(), event.getY());
                if (null != clickDanmakus && !clickDanmakus.isEmpty()) {
                    clickedDanmaku = fetchLatestOne(clickDanmakus);
                    if (onDanmuListener != null) onDanmuListener.onActionDown();
                } else {
                    clickedDanmaku = null;
                    LogUtil.i("ACTION_DOWN+nodanmu");
                    if (onDanmuListener != null) onDanmuListener.onNoDanmusClick();
                }
                break;

            case MotionEvent.ACTION_UP:

                break;
            case MotionEvent.ACTION_MOVE:
                x2 = event.getX();
                y2 = event.getY();
                if (onDanmuListener != null) {
                    if (Math.abs(x1 - x2) > 20 || Math.abs(y1 - y2) > 8) {
                        onDanmuListener.onActionMove();
                        clickedDanmaku = null;
                        LogUtil.i("ACIONTMOVE");
                    }
                    LogUtil.i("x:\t" + (Math.abs(x1 - x2)) + "\ty：\t" + (Math.abs(y1 - y2)));
                }
                break;
            default:
                break;
        }
        return false;
    }

    //监听弹幕点击与长按事件
    public interface OnDanmuListener {
        void onDanmuClick(USeeDanmu baseDanmaku);

        void onDanmuLongClick(USeeDanmu baseDanmaku);

        void onNoDanmusClick();

        void onActionDown();

        void onActionMove();
    }
}
