package com.white.usee.app.view;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

/**
 * Created by Administrator on 2017/3/22.
 */

public class VideoPlayView extends SurfaceView implements SurfaceHolder.Callback {

    private MediaPlayer mPlayer;
    private String mPath;
    private SurfaceHolder mHolder;
    private Context mContext;

    public VideoPlayView(Context context) {
        super(context);
        init(context);
    }

    public VideoPlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public VideoPlayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void setPath(String path) {
        this.mPath = path;
    }

    private void init(Context context) {
//        String path = Environment.getExternalStorageDirectory().toString() + "/com.momo.refreshlistviewtest/201732275153.mp4";
        mHolder = getHolder();
        mHolder.addCallback(this);
//        holder.setFixedSize(480,848);
        // setType必须设置，要不出错.
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

    }

    public void playVideo() {
//        String path = Environment.getExternalStorageDirectory().toString() + "/com.momo.refreshlistviewtest/201732275153.mp4";
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mContext,Uri.parse(mPath));
            mPlayer.prepare();
            mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mPlayer.start();
                    mPlayer.setLooping(true);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mHolder = holder;

        mPlayer.setDisplay(mHolder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mHolder = holder;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mHolder = null;
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }
}
