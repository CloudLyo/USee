package com.white.usee.app;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.lzy.ninegrid.NineGridView;
import com.qihoo.updatesdk.lib.UpdateHelper;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.weixin.handler.UmengWXHandler;
import com.white.usee.app.model.UserModel;
import com.white.usee.app.util.BadKeyWordFilter;
import com.white.usee.app.util.FileUtil;
import com.white.usee.app.util.GridImageLoader;
import com.white.usee.app.util.LogUtil;
import com.white.usee.app.util.sensitive.SimpleKWSeekerProcessor;

import java.io.File;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

/**
 * Created by white on 15-10-29.
 */
public class BaseApplication extends Application {
    public static String NOUSER = "0";
    public RequestQueue requestQueue;
    private String userId;
    private UserModel userModel;
    private String creatTagDate ;
    private String phoneNum;
    private double lon,lat;
    private long latestReadTime=0;
    private String headsignature;

    public String getHeadsignature() {
        if (headsignature==null){
            SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
            headsignature = sharedPreferences.getString("headsignature","");
            return headsignature;
        }else {
            return headsignature;
        }
    }

    public void setHeadsignature(String headsignature) {
        SharedPreferences.Editor editor = getSharedPreferences("user", MODE_PRIVATE).edit();
        if (headsignature != null&&!headsignature.isEmpty())
            editor.putString("headsignature", headsignature);
        else editor.remove("headsignature");
        editor.commit();
        this.headsignature = headsignature;
    }

    public long getLatestReadTime() {
        if (0 == latestReadTime){
            SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
            latestReadTime = sharedPreferences.getLong("latestReadTime", 0);
            return latestReadTime;
        }else {
            return latestReadTime;
        }

    }

    public void setLatestReadTime(long latestReadTime) {
        SharedPreferences.Editor editor = getSharedPreferences("user", MODE_PRIVATE).edit();
        if (latestReadTime != 0)
            editor.putLong("latestReadTime", System.currentTimeMillis());
        else editor.remove("latestReadTime");
        editor.commit();
        this.latestReadTime = latestReadTime;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getCreatTagDate() {
        return creatTagDate;
    }

    public void setCreatTagDate(String creatTagDate) {
        this.creatTagDate = creatTagDate;
    }

    public UserModel getUserModel() {
        if (userModel==null){
            SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
            String userJson = sharedPreferences.getString("user", NOUSER);
            if (userJson.equals(NOUSER)) {
                return null;
            } else {
                UserModel userModel = JSONObject.parseObject(userJson, UserModel.class);
                return userModel;
            }
        }else {
            return userModel;
        }

    }

    public void setUserModel(UserModel userModel) {
        SharedPreferences.Editor editor = getSharedPreferences("user", MODE_PRIVATE).edit();
        if (userModel != null)
            editor.putString("user", JSONObject.toJSONString(userModel));
        else editor.remove("user");
        editor.commit();
        this.userModel = userModel;
    }

    public String getUserId() {
        if (userId==null) {
            SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
            String userId = sharedPreferences.getString("userId", NOUSER);
            return userId;
        }else {
            return userId;
        }
    }

    public void setUserId(String userId) {
        SharedPreferences.Editor editor = getSharedPreferences("user", MODE_PRIVATE).edit();
        editor.putString("userId", userId);
        editor.commit();
        this.userId = userId;

    }

    public static BaseApplication mApplication;
    public boolean isFirstOpen = false;
    private BadKeyWordFilter badKeyWordFilter;

    public BadKeyWordFilter getBadKeyWordFilter() {
        if (badKeyWordFilter == null) setBadKeyWordFilter(new BadKeyWordFilter(this));
        return badKeyWordFilter;
    }

    public void setBadKeyWordFilter(BadKeyWordFilter badKeyWordFilter) {
        this.badKeyWordFilter = badKeyWordFilter;
    }

    public BaseApplication() {
        super();
        mApplication = this;
    }

    public static BaseApplication getInstance() {
        return mApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        isFirstOpen = isMainFirstOpen();
        SDKInit();
        File kwFile = new File(FileUtil.getCacheDir(this),"sensitive-word.properties");
        if (kwFile.exists()){
            SimpleKWSeekerProcessor.newInstance(this).checkVersion(this,SimpleKWSeekerProcessor.newInstance(this).getLasteTime(this));
        }else {
            SimpleKWSeekerProcessor.newInstance(this).checkVersion(this,0);
        }
        NineGridView.setImageLoader(new GridImageLoader());
    }

    //初始化各种第三方sdk（友盟分享，极光推送）
    private void SDKInit() {
        PlatformConfig.setWeixin("wxdd0b90b76a2c35d6", "c6e49a36479ab3c37d7dca701604a683");
        PlatformConfig.setQQZone("1105135281", "KWV3azkKYL2TmmwQ");

        String REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";
        PlatformConfig.setSinaWeibo("161708824", "94915a3fa1d4bcdd7b4f0a44158d027f",REDIRECT_URL);
        Config.DEBUG= LogUtil.isLog;
        //讯飞sdk
        SpeechUtility.createUtility(this, SpeechConstant.APPID+"=57d81b75");
        //极光sdk
        JPushInterface.setDebugMode(false); 	// 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);     		// 初始化 JPush
        if (!getUserId().equals(NOUSER)){
            bindJPush(getUserId());
        }
        UpdateHelper.getInstance().init(this,getResources().getColor(R.color.title));
    }

    public void bindJPush(String userId){
        JPushInterface.setAlias(this, userId, new TagAliasCallback() {
            @Override
            public void gotResult(int i, String s, Set<String> set) {
                if (i==0){
                    LogUtil.i("绑定成功");
                }
            }
        });
    }
    /**
     * 开一次app只创建一个requestqueue
     * 获取此requestquene
     */
    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            File cacheDir = new File(getApplicationContext().getCacheDir(), "volley");

            String userAgent = "volley/0";
            try {
                String packageName = getApplicationContext().getPackageName();
                PackageInfo info = getApplicationContext().getPackageManager().getPackageInfo(packageName, 0);
                userAgent = packageName + "/" + info.versionCode;
            } catch (PackageManager.NameNotFoundException e) {
            }
            HttpStack stack = new HurlStack();

            Network network = new BasicNetwork(stack);

            requestQueue = new RequestQueue(new DiskBasedCache(cacheDir), network, 8);
            requestQueue.start();


        }
        return requestQueue;
    }

    /**
     * 获取设备id
     */
    public String getDevId() {
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        return tm.getDeviceId();
    }


    public String getTag_name() {
        SharedPreferences sharedPreferences = getSharedPreferences("LabelName", MODE_PRIVATE);
        String labelName = sharedPreferences.getString("LabelName", "");
        return labelName;
    }

    public void setTag_name(String tag_name) {
        SharedPreferences.Editor editor = getSharedPreferences("LabelName", MODE_PRIVATE).edit();
        editor.putString("LabelName", tag_name);
        editor.commit();
    }

    /**
     * 判断该应用主界面是否是第一次被打开
     */
    public boolean isMainFirstOpen() {
        SharedPreferences sharedPreferences = getSharedPreferences("UseeApp1", MODE_PRIVATE);
        boolean isfirst = sharedPreferences.getBoolean("isfirst", true);
        if (isfirst) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isfirst", false);
            editor.commit();
            return true;
        } else {
            return false;
        }
    }

}
