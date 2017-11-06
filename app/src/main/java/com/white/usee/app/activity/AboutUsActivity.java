
package com.white.usee.app.activity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.white.usee.app.R;
import com.white.usee.app.util.ThemeUtils;
/**
 * 关于我们界面
 * */
public class AboutUsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        ThemeUtils.setColor(this, getResources().getColor(R.color.title));
        findById();
        setOnClick();

    }

    private void findById(){
        TextView tv_version = (TextView)findViewById(R.id.tv_version);
        PackageManager packageManager = getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            tv_version.setText("V"+packageInfo.versionName);
        }catch (Exception e){

        }
    }

    private void setOnClick(){
        (findViewById(R.id.title_back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
