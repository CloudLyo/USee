package com.white.usee.app.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.white.usee.app.R;
import com.white.usee.app.view.VideoPlayView;

import java.io.File;

public class PlayVideoActivity extends AppCompatActivity implements View.OnClickListener {
    private String mVideoPath;
    private VideoPlayView mPlay;

    private Button btnConfirm;
    private Button btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);

        mPlay = (VideoPlayView) findViewById(R.id.video_play_view);
        btnConfirm = (Button) findViewById(R.id.btn_confirm);
        btnCancel = (Button) findViewById(R.id.btn_cancel);

        mVideoPath = getVideoPath();

//        mVideoPath = Environment.getExternalStorageDirectory().toString() + "/com.momo.refreshlistviewtest/201732275153.mp4";

        mPlay.setPath(mVideoPath);
        mPlay.playVideo();

        btnConfirm.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
    }

    private String getVideoPath() {
        Intent intent = getIntent();
        return intent.getStringExtra("path");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_confirm:
                Log.d("mytag", "onClick: " + mVideoPath);

                Intent intent = new Intent();
                intent.putExtra("path",mVideoPath);
                intent.putExtra("flag","flag2");
                setResult(RESULT_OK,intent);
                finish();

                break;


            case R.id.btn_cancel:
                back();
                break;
        }
    }

    private void back() {
        File deleteFile = new File(mVideoPath);
        deleteFile.delete();
//        Toast.makeText(this, "删除成功！", Toast.LENGTH_SHORT).show();
//        Intent intent = new Intent();
//        intent.putExtra("flag","flag1");
//        setResult(1,intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        back();
    }
}
