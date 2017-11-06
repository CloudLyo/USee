package com.white.usee.app.view;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Point;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


import com.white.usee.app.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

/**
 * Created by Administrator on 2017/3/21.
 */

public class CameraView extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder mHolder;
    private Camera mCamera;
    private MediaRecorder mRecorder;

    private String mRestorePath;


    private Paint mSquarePaint;

//    private boolean isDrawSquare = false;

    private Point mCurrTouchPoint;


    public CameraView(Context context) {
        super(context);
        init();
    }

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CameraView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {

//        mRestorePath = Environment.getExternalStorageState();

        mHolder = getHolder();
        mHolder.addCallback(this);
//        holder.setFixedSize(480,848);
        // setType必须设置，要不出错.
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mCurrTouchPoint = new Point();
        initPaint();

    }


    private void initPaint() {
        mSquarePaint = new Paint();
        mSquarePaint.setAntiAlias(true);
        mSquarePaint.setStyle(Paint.Style.STROKE);
        mSquarePaint.setStrokeWidth(2);
        mSquarePaint.setColor(getResources().getColor(R.color.blue));
    }

    public void takePicture() {
        mCamera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                savePicture(data);
            }
        });
    }

    private void savePicture(byte[] data) {
        try {
            File file = new File(Environment.getExternalStorageDirectory()+"/com.usee/", System.currentTimeMillis() + ".jpg");
            mRestorePath = file.getPath();
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(data);
            // 在拍照的时候相机是被占用的,拍照之后需要重新预览
            mCamera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setRestorePath(String path) {
        this.mRestorePath = path;
    }


    public void startRecord() {
        if (mRecorder == null) {
            mRecorder = new MediaRecorder();
        }
//                    mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
        if (mCamera != null) {
            mCamera.setDisplayOrientation(90);
            mCamera.unlock();
            mRecorder.setCamera(mCamera);
        }
        try {
            // 这两项需要放在setOutputFormat之前
            mRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
            mRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

            // Set output file format
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);

            // 这两项需要放在setOutputFormat之后
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);

            mRecorder.setVideoSize(960, 540);
            mRecorder.setVideoFrameRate(30);
            mRecorder.setVideoEncodingBitRate(3 * 1024 * 1024);
            mRecorder.setOrientationHint(90);
            //设置记录会话的最大持续时间（毫秒）
            mRecorder.setMaxDuration(30 * 1000);
            mRecorder.setPreviewDisplay(mHolder.getSurface());

            String path = getSDPath();
            if (path != null) {
                File dir = new File(path + "/com.usee");
                if (!dir.exists()) {
                    dir.mkdir();
                }
                path = dir + "/" + getDate() + ".mp4";
                mRestorePath = path;
                mRecorder.setOutputFile(path);
                mRecorder.prepare();
                mRecorder.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopRecord() {
        try {
            mRecorder.stop();
            mRecorder.reset();
            mRecorder.release();
            mRecorder = null;
            if (mCamera != null) {
                mCamera.release();
                mCamera = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            mCamera = Camera.open();
            // 当surfaceview创建就去打开相机
            Camera.Parameters params = mCamera.getParameters();
            Log.i("i", params.flatten());
            params.setPictureSize(960, 540);
            params.setPreviewSize(960, 540);
            params.setPreviewFrameRate(20);  // 预览帧率
            mCamera.setParameters(params); // 将参数设置给相机
            mCamera.setDisplayOrientation(90);
            // 设置预览显示
            mCamera.setPreviewDisplay(getHolder());
            // 开启预览
            mCamera.startPreview();
            mCamera.autoFocus(null);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mHolder = holder;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mHolder = null;

        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mCurrTouchPoint.x = (int) event.getX();
        mCurrTouchPoint.y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
//                drawFocusSquare();
//                isDrawSquare = true;
                mCamera.autoFocus(null);
                break;
            case MotionEvent.ACTION_UP:
//                isDrawSquare = false;
        }
        postInvalidate();
        return true;
    }

//    private void drawFocusSquare() {
//        if(mFocusView != null){
//            mFocusView.setOnTouchListener();
//        }
//    }

//    @Override
//    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//
//        if(isDrawSquare){
//            drawSquare(canvas);
//        }
//    }
//
//    private void drawSquare(Canvas canvas) {
//        canvas.drawRect(new RectF(mCurrTouchPoint.x - 100,
//                mCurrTouchPoint.y - 100,
//                mCurrTouchPoint.x + 100,
//                mCurrTouchPoint.y + 100),mSquarePaint);
//    }

    /**
     * 获取系统时间
     *
     * @return
     */
    public static String getDate() {
        Calendar ca = Calendar.getInstance();
        int year = ca.get(Calendar.YEAR);           // 获取年份
        int month = ca.get(Calendar.MONTH);         // 获取月份
        int day = ca.get(Calendar.DATE);            // 获取日
        int minute = ca.get(Calendar.MINUTE);       // 分
        int hour = ca.get(Calendar.HOUR);           // 小时
        int second = ca.get(Calendar.SECOND);       // 秒

        String date = "" + year + (month + 1) + day + hour + minute + second;

        return date;
    }

    /**
     * 获取SD path
     *
     * @return
     */
    public String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
            return sdDir.toString();
        }

        return null;
    }

    public String getStoredPath(){
        return this.mRestorePath;
    }

}
