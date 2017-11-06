package com.white.usee.app.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.white.usee.app.BaseActivity;
import com.white.usee.app.R;
import com.white.usee.app.config.IntentKeyConfig;
import com.white.usee.app.view.particleview.ParticleView;


public class SplashTestActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_test);
        //ThemeUtils.setColor(this,getResources().getColor(R.color.title));
        checkPermiss();
    }

    //加载粒子动画
    private void startAni(){
        ParticleView mParticleView = (ParticleView)findViewById(R.id.particleview);
        mParticleView.startAnim();
        mParticleView.setOnParticleAnimListener(new ParticleView.ParticleAnimListener() {
            @Override
            public void onAnimationEnd() {
                Intent intent = new Intent(SplashTestActivity.this,ChooseTagNewActivity.class);
                intent.putExtra(IntentKeyConfig.LOCATION, new double[]{getLon(), getLat()});
                startActivity(intent);
                finish();
            }
        });
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
    //检查授权
    private void  checkPermiss(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED
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
