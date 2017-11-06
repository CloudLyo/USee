package com.white.usee.app.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.white.usee.app.BaseActivity;
import com.white.usee.app.BaseApplication;
import com.white.usee.app.R;
import com.white.usee.app.config.IntentKeyConfig;
import com.white.usee.app.util.ThemeUtils;
/**
 * 旧版的SplashActivity，现在还没决定用哪个
 * 目前开启的是SplashTestActivity界面
 * */
public class SplashActivity extends BaseActivity {
    private final int DURATION_TIME = 1200;
    private ImageView mImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ThemeUtils.setColor(this, getResources().getColor(R.color.title));
        mImageView = (ImageView)findViewById(R.id.iv_splash);
        mImageView.setAlpha(0.8f);
        if (BaseApplication.getInstance().isMainFirstOpen()){
            BaseApplication.getInstance().isFirstOpen = true;
        }
        checkPermiss();
    }

    //定位成功后的回调
    private AMapLocationListener aMapLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (aMapLocation != null) {
                if (aMapLocation.getErrorCode() == 0) {
                    //定位成功回调信息，设置相关消息
                    setLat(aMapLocation.getLatitude());//获取纬度
                    setLon(aMapLocation.getLongitude());//获取经度
                } else {
                    setLon(0);
                    setLat(0);
                }
                startAni();
            }
        }
    };

    private void startAni() {
//        AnimationSet aSet = new AnimationSet(true);
        AlphaAnimation alphaAni;
        alphaAni = new AlphaAnimation(0.8f, 1f);
        alphaAni.setDuration(DURATION_TIME);
//        ScaleAnimation scaleAni = new ScaleAnimation(1f, 1.2f, 1f, 1.2f, Animation.RELATIVE_TO_SELF, 0.5f,
//                Animation.RELATIVE_TO_SELF, 0.5f);
//        scaleAni.setDuration(DURATION_TIME);

//        aSet.addAnimation(alphaAni);
//        aSet.addAnimation(scaleAni);
//        aSet.setFillAfter(true);
        mImageView.startAnimation(alphaAni);
        alphaAni.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(SplashActivity.this,ChooseTagNewActivity.class);
                intent.putExtra(IntentKeyConfig.LOCATION, new double[]{getLon(), getLat()});
                startActivity(intent);
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub

            }

        });
    }

    private void  checkPermiss(){
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED
                ||ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED
                ||ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.CALL_PHONE,Manifest.permission.READ_PHONE_STATE,Manifest.permission.ACCOUNT_MANAGER,Manifest.permission.RECORD_AUDIO,Manifest.permission.READ_CONTACTS},1);
        }else {
            startGpsByLow(100,true,aMapLocationListener);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==1&&grantResults.length>0){
            if (grantResults[0]==PackageManager.PERMISSION_GRANTED){

            }else if (grantResults[0]==PackageManager.PERMISSION_DENIED){
                showToast("应用没有获取到权限,请从设置->应用->权限中打开权限后，重新启动");
            }
           startGpsByLow(100,true,aMapLocationListener);
        }
    }
}
