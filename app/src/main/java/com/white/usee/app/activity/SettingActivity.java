package com.white.usee.app.activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.qihoo.updatesdk.lib.UpdateHelper;
import com.white.usee.app.BaseActivity;
import com.white.usee.app.BaseApplication;
import com.white.usee.app.R;
import com.white.usee.app.config.DanmuConfig;
import com.white.usee.app.util.DataCleanManager;
import com.white.usee.app.util.ThemeUtils;
import com.white.usee.app.util.refreah.PullToRefreshBase;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;

public class SettingActivity extends BaseActivity{
    private RelativeLayout rl_about_me,rl_clear_cache,rl_is_push,rl_check_version,rl_report;
    private TextView tv_version ,tv_cacheNumber;
    private ToggleButton tb_is_push;
    private AppCompatSeekBar seekbar_danmu_speed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ThemeUtils.setColor(this, getResources().getColor(R.color.title));
        findById();
        setOnCLick();
    }

    public void initSpeed(){
        SharedPreferences sharedPreferences = getSharedPreferences("speed_data",MODE_PRIVATE);
        int danmu_speed = sharedPreferences.getInt("danmu_speed",50);
        seekbar_danmu_speed.setProgress(danmu_speed);

    }

    private void findById(){
        seekbar_danmu_speed = (AppCompatSeekBar) findViewById(R.id.seekbar_speed_danmu);
        initSpeed();
        rl_about_me = (RelativeLayout)findViewById(R.id.rl_about_me);
        rl_check_version = (RelativeLayout)findViewById(R.id.rl_checkversion);
        rl_clear_cache = (RelativeLayout)findViewById(R.id.rl_clearCache);
        rl_is_push = (RelativeLayout)findViewById(R.id.rl_is_push);
        rl_report = (RelativeLayout)findViewById(R.id.rl_report);
        tv_version=(TextView)findViewById(R.id.tv_version);
        tv_version.setText("当前版本： "+getVersionName());
        tb_is_push = (ToggleButton)findViewById(R.id.toogle_is_push);
        tv_cacheNumber = (TextView)findViewById(R.id.tv_cachennumber);
        try {
            tv_cacheNumber.setText(DataCleanManager.getTotalCacheSize(this));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (JPushInterface.isPushStopped(this)){
            tb_is_push.setChecked(false);
        }else {
            tb_is_push.setChecked(true);
        }
    }

    //绑定监听器
    private void setOnCLick(){
        seekbar_danmu_speed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                DanmuConfig.danmuSpeedSeekbarProgress = progress;
                DanmuConfig.danmuSpeed = (float) (100 - progress) / 100 * 1f + 1f / 2;

                SharedPreferences.Editor editor = getSharedPreferences("speed_data", MODE_PRIVATE).edit();
                editor.putInt("danmu_speed",DanmuConfig.danmuSpeedSeekbarProgress);
                editor.commit();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        (findViewById(R.id.title_back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        rl_about_me.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this,AboutUsActivity.class));
            }
        });
        rl_clear_cache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoadingDialog();
                DataCleanManager.clearAllCache(SettingActivity.this);
                Glide.get(SettingActivity.this).clearMemory();
                try {
                    tv_cacheNumber.setText(DataCleanManager.getTotalCacheSize(SettingActivity.this));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dismissLoadingDialog();
                    }
                },1000*2);
            }
        });
        rl_check_version.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateHelper.getInstance().manualUpdate("com.white.usee.app");
            }
        });
        rl_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this,FeedBackActivity.class));
            }
        });
        tb_is_push.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (BaseApplication.getInstance().getUserId().equals(BaseApplication.NOUSER)){
                    showNoLogInTipDialog();
                    return;
                }
                if (tb_is_push.isChecked()){
                    JPushInterface.resumePush(SettingActivity.this);
                }else {
                    JPushInterface.stopPush(SettingActivity.this);
                }
            }
        });

    }

    //清空缓存
    private void clearCache(){
        Glide.get(this).clearMemory();
        new Thread(){
            @Override
            public void run() {
                super.run();
//                Glide.get(SettingActivity.this).clearDiskCache();
            }
        }.run();
        dismissLoadingDialog();
    }

    //展示关于我们的消息
    private void showAboutUsDialog(){
        final Dialog dialog = new Dialog(this,android.R.style.Theme_DeviceDefault_Dialog_NoActionBar);
        View mView = View.inflate(this,R.layout.dialog_about_us,null);
        TextView tv_version = (TextView)mView.findViewById(R.id.tv_version);
        PackageManager packageManager = getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(),0);
            tv_version.setText("Version: "+packageInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        dialog.setContentView(mView);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    /**
     * 获取当前应用版本
     * */
    private String getVersionName(){
        PackageManager packageManager = getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(),0);
            return packageInfo.versionName;
        }catch (Exception e){
            return "2.1.0";
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
