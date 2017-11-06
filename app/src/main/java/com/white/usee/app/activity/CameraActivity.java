package com.white.usee.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.white.usee.app.BaseActivity;
import com.white.usee.app.R;
import com.white.usee.app.config.RequstConfig;
import com.white.usee.app.view.CameraButton;
import com.white.usee.app.view.CameraView;

public class CameraActivity extends BaseActivity {


    private CameraView cameraView;
    private CameraButton cameraButton;
    private TextView tv_timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        cameraView = (CameraView) findViewById(R.id.camera_view);
        cameraButton = (CameraButton) findViewById(R.id.camera_button);
        tv_timer = (TextView) findViewById(R.id.tv_timer);

        cameraButton.setOnShootListener(new CameraButton.onShootListener() {
            @Override
            public void onStart() {
                cameraView.startRecord();
            }

            @Override
            public void onFinish() {
                cameraView.stopRecord();
                Intent intent = new Intent(CameraActivity.this, PlayVideoActivity.class);
                intent.putExtra("path", cameraView.getStoredPath());
                Log.d("mytag", "onFinish: " + cameraView.getStoredPath());
                startActivityForResult(intent, RequstConfig.REQUST_PLAY_VEDIO);
            }

            @Override
            public void onProcess(int process) {
                tv_timer.setText(process + "s");
            }

            @Override
            public void onClicked() {
//                Log.d("mytag", "onClicked: ");
                cameraView.takePicture();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequstConfig.REQUST_PLAY_VEDIO && resultCode == RESULT_OK && data != null) {

            String text = data.getExtras().getString("path");
            Intent intent = new Intent();
            Log.d("mytag", "onActivityResult: " + text);
            intent.putExtra("path", text);
            setResult(RESULT_OK, intent);
            finish();
        }else{
            tv_timer.setText("点击拍照，长按录像");
        }
    }
}
