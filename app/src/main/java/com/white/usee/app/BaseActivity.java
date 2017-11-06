package com.white.usee.app;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.white.usee.app.activity.UserManagerActivity;
import com.white.usee.app.util.DialogUtils;


/**
 * 所有Activity的基类，包含了showToast方法，和开启定位的方法，显示加载对话框方法
 *
 * Created by white on 15-10-25.
 */
public class BaseActivity extends AppCompatActivity {
    public AMapLocationClient mapLocationClient;
    private String addressDetail;//详细地址 省/市/县/街道
    private Dialog loadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Toast.makeText(this,getClass().getSimpleName(),Toast.LENGTH_LONG).show();
        showToast(getClass().getSimpleName());
    }


    /**
     * 显示加载对话框
     * */

    public void showLoadingDialog(){
        showLoadingDialog(true);
    }

    public void showLoadingDialog(boolean isCancelOutSide){
        if (loadingDialog==null){
            loadingDialog = DialogUtils.createLoadingDialog(this,isCancelOutSide);
        }
        loadingDialog.show();
    }

    /**
     * 消除加载对话框
     * */
    public void dismissLoadingDialog(){
        if (loadingDialog!=null){
            loadingDialog.dismiss();
        }
    }


    /**
     * 显示自定义Toast
     * */
    public void showToast(String msg) {
        Toast toast = new Toast(this);
        View view = getLayoutInflater().inflate(R.layout.toast_bg, null);
        toast.setView(view);
        // 创建自定义 view
        TextView textView= (TextView) toast.getView().findViewById(R.id.txtMessage);
        textView.setTextColor(getResources().getColor(R.color.ali_feedback_color_white));
        textView.setText(msg);
        // 设置自定义 view
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * 显示未登录对话框
     * */
    public void showNoLogInTipDialog() {
        final Dialog tipDialog = new Dialog(this, R.style.MyAlertDialog);
        View mainView = View.inflate(this, R.layout.dialog_tip_no_login, null);
        Button bt_cancel = (Button) mainView.findViewById(R.id.bt_cancel);
        Button bt_login = (Button) mainView.findViewById(R.id.bt_login);

        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tipDialog.dismiss();
            }
        });
        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BaseActivity.this, UserManagerActivity.class));
                tipDialog.dismiss();
            }
        });
        tipDialog.setContentView(mainView);
        tipDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
            }
        });
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.height=WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.width = getResources().getDimensionPixelOffset(R.dimen.px_four_hundred_sixty);
        tipDialog.getWindow().setAttributes(layoutParams);
        tipDialog.show();
    }

    /**
     * 开始定位
     * 记得要使用stopGps来结束定位
     *
     * @param time 每个多少毫秒监听一次，-1为只监听一次
     */

    public void startGps(int time, boolean onlyOnce, boolean isNeedAddress) {
        if (mapLocationClient == null) mapLocationClient = new AMapLocationClient(this);
        mapLocationClient.setLocationListener(aMapLocationListener);
        AMapLocationClientOption mapLocationClientOption = new AMapLocationClientOption();
        mapLocationClientOption.setOnceLocation(onlyOnce);
        mapLocationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mapLocationClientOption.setNeedAddress(isNeedAddress);
        mapLocationClientOption.setInterval(time);
        mapLocationClientOption.setGpsFirst(true);
        mapLocationClientOption.setWifiActiveScan(true);
        mapLocationClient.setLocationOption(mapLocationClientOption);
        mapLocationClient.startLocation();
    }

    /**
     * @param time                 定位间隔时间
     * @param onlyOnce             是否只定位一次
     * @param isNeedAddress        是否需要详细地址
     * @param aMapLocationListener 当定位地址改变时的回调
     * @param aMapLocationMode     定位模式，分为高精度，网络基站，手机gps
     */
    public void startGps(int time, boolean onlyOnce, boolean isNeedAddress, AMapLocationListener aMapLocationListener, AMapLocationClientOption.AMapLocationMode aMapLocationMode) {
        if (mapLocationClient == null) mapLocationClient = new AMapLocationClient(this);
        mapLocationClient.setLocationListener(aMapLocationListener);
        AMapLocationClientOption mapLocationClientOption = new AMapLocationClientOption();
        mapLocationClientOption.setOnceLocation(onlyOnce);
        mapLocationClientOption.setLocationMode(aMapLocationMode);
        mapLocationClientOption.setNeedAddress(isNeedAddress);
        mapLocationClientOption.setInterval(time);
        mapLocationClientOption.setGpsFirst(true);
        mapLocationClientOption.setWifiActiveScan(true);
        mapLocationClient.setLocationOption(mapLocationClientOption);
        mapLocationClient.startLocation();
    }


    /**
     * 开启低精度
     **/
    public void startGpsByLow(int time, boolean onlyOnce, AMapLocationListener locationListener) {
        startGps(time, onlyOnce, false, locationListener, AMapLocationClientOption.AMapLocationMode.Battery_Saving);
    }

    /**
     * 开启高精度定位
     */
    public void startGpsByHight(int time, boolean onlyOnce, AMapLocationListener locationListener) {
        startGps(time, onlyOnce, true, locationListener, AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
    }

    public void startGps(int time) {
        startGps(time, false, true);
    }

    public double getLon() {
        return BaseApplication.getInstance().getLon();
    }

    public void setLon(double lon) {
        BaseApplication.getInstance().setLon(lon);
    }

    public void setLat(double lat) {
        BaseApplication.getInstance().setLat(lat);
    }

    public double getLat() {
        return BaseApplication.getInstance().getLat();

    }


    /**
     * 结束定位，最好在Activity中的OnPause中执行
     */
    public void stopGps() {
        if (mapLocationClient != null) {
            mapLocationClient.stopLocation();
            mapLocationClient.onDestroy();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopGps();
    }

    private AMapLocationListener aMapLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            getLonAndLat(aMapLocation);
        }
    };

    public void getLonAndLat(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
                setLat(aMapLocation.getLatitude()); //获取纬度
                setLon(aMapLocation.getLongitude());//获取经度
                addressDetail = aMapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("usee", "location Error, ErrCode:"
                        + aMapLocation.getErrorCode() + ", errInfo:"
                        + aMapLocation.getErrorInfo());
            }
        }
    }





    //隐藏输入法
    public void hideInputWindow(IBinder iBinder){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
       if (imm.isActive())
        imm.hideSoftInputFromWindow(iBinder,0);
    }

    //弹出输入法
    public void showInputWindow(View editText){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText,InputMethodManager.SHOW_FORCED);
    }

}
