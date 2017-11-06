package com.white.usee.app.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2017/3/21.
 */

public class FocusView extends View {

    private int mWidth;
    private int mHeight;

    private Paint p;
    private Rect rect;
    private Timer mTimer;
    private TimerTask mTimerTask;
    private boolean isClear = false;

    private Point mCurrTouchPoint;

    public FocusView(Context context) {
        super(context);
        init();
    }

    public FocusView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FocusView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {

        p = new Paint();
        p.setAntiAlias(true);
        p.setColor(Color.WHITE);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(3);

        rect = new Rect();
        mCurrTouchPoint = new Point();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(mTimer != null) {
                    mTimer.cancel();
                    isClear = false;
                }
                rect.set(x - 100, y - 100, x + 100, y + 100);

                mTimer = new Timer();

                mTimerTask = new TimerTask() {
                    @Override
                    public void run() {
                        isClear = true;
                        postInvalidate();
                    }
                };
                mTimer.schedule(mTimerTask,1000);
                break;
            case MotionEvent.ACTION_UP:

                break;
        }
        postInvalidate();
        return false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(!isClear){
            canvas.drawRect(rect,p);
        }


    }

}
