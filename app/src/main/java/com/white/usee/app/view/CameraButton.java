package com.white.usee.app.view;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.white.usee.app.R;


/**
 * Created by Administrator on 2017/3/16.
 */

public class CameraButton extends View {
    private static final String TAG = "mytag";
    private static final int POST_DELAY_TIME = 25;

    private Paint mWhileCirclePaint;
    private Paint mInnerCirclePaint;
    private Paint mProcessPaint;

    private Point mCenter = new Point();

    private boolean mIsLongPressed;
    private boolean mIsReset;
    private boolean mIsDrawProcess;

    private int mDefaultSize = 250;
    private int mMaxInnerRadius = 90;
    private int mMaxOutRadius = 120;
    private int mOutRaidus = 100;
    private float mSweepAngles = 0;
    private int mTimes = 10;
    private int mSecond = 6;
    int count = 0;

    private Runnable mLongPressedRunnable;


    public CameraButton(Context context) {
        super(context);
        initView();
    }


    public CameraButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public CameraButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public void setSecond(int second) {
        this.mSecond = second;
    }

    private void initView() {
        mDefaultSize = dp2px(mDefaultSize);

        mLongPressedRunnable = new Runnable() {
            @Override
            public void run() {
                mIsLongPressed = true;
                performLongClick();
                if(mOnShootListener != null) {
                    mOnShootListener.onStart();
                }
                postInvalidate();
            }
        };

        initPaints();
    }

    private void initPaints() {
        mWhileCirclePaint = new Paint();
        mWhileCirclePaint.setColor(getResources().getColor(R.color.white));
        mWhileCirclePaint.setAntiAlias(true);
        mWhileCirclePaint.setStyle(Paint.Style.STROKE);
        mWhileCirclePaint.setStrokeWidth(15);

        mInnerCirclePaint = new Paint();
        mInnerCirclePaint.setColor(getResources().getColor(R.color.blue));
        mInnerCirclePaint.setAntiAlias(true);
        mInnerCirclePaint.setStyle(Paint.Style.FILL);

        mProcessPaint = new Paint();
        mProcessPaint.setColor(getResources().getColor(R.color.blue));
        mProcessPaint.setAntiAlias(true);
        mProcessPaint.setStyle(Paint.Style.STROKE);
        mProcessPaint.setStrokeWidth(15);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        if (mIsLongPressed) {
            drawOutWhiteIncreseCircle(canvas);
            drawInnerCirCle(canvas);
            if (mIsDrawProcess) {

                if (mSweepAngles == 360 && mOnShootListener != null) {
                    mOnShootListener.onFinish();
                }

                if(mSweepAngles <= 360) {

                    drawProcess(canvas);
                    if (count % 1000 == 0 && mOnShootListener != null) {
                        mOnShootListener.onProcess(count / 1000);
                    }
                    count += POST_DELAY_TIME;
                }
            }

            postInvalidateDelayed(POST_DELAY_TIME);
        } else {
            drawOutWhiteCircle(canvas);
        }

    }

    private void drawProcess(Canvas canvas) {
        float step = (float) (360 * 1.0 / (mSecond * (1000 / POST_DELAY_TIME)));
        mSweepAngles += step;
        canvas.drawArc(new RectF(mCenter.x - mMaxOutRadius,
                mCenter.y - mMaxOutRadius,
                mCenter.x + mMaxOutRadius,
                mCenter.y + mMaxOutRadius), -90, mSweepAngles, false, mProcessPaint);
    }

    int outRadius = mOutRaidus;

    private void drawOutWhiteIncreseCircle(Canvas canvas) {
        int step = (mMaxOutRadius - mOutRaidus) / mTimes;
        outRadius += step;
        if (outRadius <= mMaxOutRadius) {
            canvas.drawArc(new RectF(mCenter.x - outRadius,
                    mCenter.y - outRadius,
                    mCenter.x + outRadius,
                    mCenter.y + outRadius), 0, 360, true, mWhileCirclePaint);
        } else {
            canvas.drawArc(new RectF(mCenter.x - mMaxOutRadius,
                    mCenter.y - mMaxOutRadius,
                    mCenter.x + mMaxOutRadius,
                    mCenter.y + mMaxOutRadius), 0, 360, true, mWhileCirclePaint);

        }

    }

    int radius = 0;

    private void drawInnerCirCle(Canvas canvas) {
        int step = mMaxInnerRadius / mTimes;
        radius += step;
        if (radius <= mMaxInnerRadius) {
            canvas.drawOval(new RectF(mCenter.x - radius,
                    mCenter.y - radius,
                    mCenter.x + radius,
                    mCenter.y + radius), mInnerCirclePaint);
        } else {
            canvas.drawOval(new RectF(mCenter.x - mMaxInnerRadius,
                    mCenter.y - mMaxInnerRadius,
                    mCenter.x + mMaxInnerRadius,
                    mCenter.y + mMaxInnerRadius), mInnerCirclePaint);
            mIsDrawProcess = true;
        }
    }

    private void drawOutWhiteCircle(Canvas canvas) {
        canvas.drawArc(new RectF(mCenter.x - mOutRaidus,
                mCenter.y - mOutRaidus,
                mCenter.x + mOutRaidus,
                mCenter.y + mOutRaidus), 0, 360, true, mWhileCirclePaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                postDelayed(mLongPressedRunnable, ViewConfiguration.getLongPressTimeout());
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:

                reset();
                break;
        }
        return true;
    }

    private void reset() {
        if (!mIsLongPressed && mOnShootListener != null) {
            mOnShootListener.onClicked();
            performClick();
        } else {
            if (mOnShootListener != null && mSweepAngles < 360)
                mOnShootListener.onFinish();
        }
        mIsLongPressed = false;
        radius = 0;
        outRadius = mOutRaidus;
        mSweepAngles = 0;
        count = 0;
        mIsDrawProcess = false;
        removeCallbacks(mLongPressedRunnable);

    }

    private int dp2px(int values) {
        float density = getResources().getDisplayMetrics().density;
        return (int) (values * density + 0.5f);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mCenter.x = getWidth() / 2;
        mCenter.y = getHeight() / 2;
    }

    private onShootListener mOnShootListener;

    public void setOnShootListener(onShootListener onShootListener) {
        this.mOnShootListener = onShootListener;
    }

    public interface onShootListener {
        void onStart();
        void onFinish();
        void onProcess(int process);
        void onClicked();
    }
}
