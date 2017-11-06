package com.white.usee.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.tencent.connect.UserInfo;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.white.usee.app.BaseActivity;
import com.white.usee.app.BaseApplication;
import com.white.usee.app.R;
import com.white.usee.app.config.HttpUrlConfig;
import com.white.usee.app.model.OauthLoginModel;
import com.white.usee.app.model.RegisterSmsModel;
import com.white.usee.app.model.UserSigninModel;
import com.white.usee.app.util.HttpManager;
import com.white.usee.app.util.HttpRequestCallBack;
import com.white.usee.app.util.LogUtil;
import com.white.usee.app.util.MD5Util;
import com.white.usee.app.util.ThemeUtils;
import com.white.usee.app.util.UmengUtils;


import java.util.Map;


public class RegisterdActivity extends BaseActivity {
    // 默认使用中国区号
    private Button bt_login;
    private EditText et_phoneNum, et_code, et_pwd;
    private TextView tv_sendCode;
    private ImageButton ib_login_weixin, ib_login_qq, ib_login_weibo;
    private String currentCode;//当前验证码

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registerd);
        ThemeUtils.setColor(this, getResources().getColor(R.color.title));
        findById();
        setOnClick();
        //获取到受支持的国家code
    }

    private void findById() {
        et_phoneNum = (EditText) findViewById(R.id.et_phoneNum);
        bt_login = (Button) findViewById(R.id.bt_login);
        tv_sendCode = (TextView) findViewById(R.id.tv_sendCode);
        et_code = (EditText) findViewById(R.id.et_codes);
        ib_login_qq = (ImageButton) findViewById(R.id.ib_login_qq);
        ib_login_weibo = (ImageButton) findViewById(R.id.ib_login_weibo);
        ib_login_weixin = (ImageButton) findViewById(R.id.ib_login_weixin);
        et_pwd = (EditText) findViewById(R.id.et_psw);
    }

    private void setOnClick() {
        (findViewById(R.id.title_back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        tv_sendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNum = et_phoneNum.getText().toString().trim();
                if (phoneNum.length() != 11) {
                    showToast("请输入正确的手机号");
                    return;
                }
                if (!TextUtils.isEmpty(et_phoneNum.getText())) {
                    com.alibaba.fastjson.JSONObject jsonObject = new com.alibaba.fastjson.JSONObject();
                    jsonObject.put("cellphone", phoneNum);
                    new HttpManager<RegisterSmsModel>().sendQuest(Request.Method.POST, HttpUrlConfig.cellPhoneValidateRegister, jsonObject, RegisterSmsModel.class, new HttpRequestCallBack<RegisterSmsModel>() {
                        @Override
                        public void onRequestSuccess(RegisterSmsModel response, boolean cached) {
                            if (response.getReturnInfo().equals("registered")) {
                                showToast("该手机号已被注册");
                            } else if (response.getReturnInfo().equals("success")) {
                                currentCode = response.getVerificationCode();
                                showToast("验证码已发送，请注意查收");
                                final int[] time = {60};
                                tv_sendCode.setText("重新发送"+ time[0]);
                                tv_sendCode.setTextColor(getResources().getColor(R.color.text_hint));
                                tv_sendCode.setClickable(false);
                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        time[0]--;
                                        tv_sendCode.setText("重新发送"+ time[0]);
                                        if (time[0]==0){
                                            tv_sendCode.setText("重新发送");
                                            tv_sendCode.setTextColor(getResources().getColor(R.color.text_blue));
                                            tv_sendCode.setClickable(true);
                                        }else {
                                        handler.postDelayed(this,1000);}

                                    }
                                },1000);
                            }
                        }

                        @Override
                        public void onRequestFailed(VolleyError error) {
                            showToast("请求失败，请检查网络" + error.getMessage());
                        }
                    });

                } else {
                    showToast("请输入手机号");
                }
            }
        });
        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNum = et_phoneNum.getText().toString().trim();
                if (phoneNum.length() != 11) {
                    showToast("请输入正确的手机号");
                    return;
                }
                if (TextUtils.isEmpty(et_pwd.getText())) {
                    showToast("密码不能为空");
                    return;
                }
                if (!TextUtils.isEmpty(et_code.getText())) {
                    currentCode = MD5Util.getMD5(et_code.getText().toString().trim());
                    com.alibaba.fastjson.JSONObject jsonObject = new com.alibaba.fastjson.JSONObject();
                    jsonObject.put("cellphone", phoneNum);
                    jsonObject.put("password", et_pwd.getText().toString().trim());
                    jsonObject.put("verificationCode", currentCode);
                    new HttpManager<UserSigninModel>().sendQuest(Request.Method.POST, HttpUrlConfig.registerByPhoneNumber, jsonObject, UserSigninModel.class, new HttpRequestCallBack<UserSigninModel>() {
                        @Override
                        public void onRequestSuccess(UserSigninModel response, boolean cached) {
                            if (response.getReturnInfo().equals("success")) {
                                showToast("注册成功" + response.getUser().getCellphone());
                                BaseApplication.getInstance().setUserId(response.getUser().getUserID());
                                BaseApplication.getInstance().setUserModel(response.getUser());
                                BaseApplication.getInstance().bindJPush(response.getUser().getUserID());
                                startActivityForResult(new Intent(RegisterdActivity.this, AccountActivity.class), 1010);
                                finish();
                            }
                        }

                        @Override
                        public void onRequestFailed(VolleyError error) {

                        }
                    });
                } else {
                    showToast("请输入验证码");
                }

            }
        });
        final UMShareAPI umShareAPI = UMShareAPI.get(this);

        ib_login_weixin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (umShareAPI.isInstall(RegisterdActivity.this, SHARE_MEDIA.WEIXIN))
                    umShareAPI.doOauthVerify(RegisterdActivity.this, SHARE_MEDIA.WEIXIN,umAuthListener );
                else showToast("未安装微信客户端");
            }
        });
        ib_login_weibo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                umShareAPI.doOauthVerify(RegisterdActivity.this, SHARE_MEDIA.SINA,umAuthListener );
            }
        });
        ib_login_qq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (umShareAPI.isInstall(RegisterdActivity.this, SHARE_MEDIA.QQ))
                    umShareAPI.doOauthVerify(RegisterdActivity.this, SHARE_MEDIA.QQ,umAuthListener );
                else {
                    final Tencent tencent = Tencent.createInstance("222222",RegisterdActivity.this);
                    tencent.login(RegisterdActivity.this, "get_user_info", new IUiListener() {
                        @Override
                        public void onComplete(Object o) {
                            JSONObject jsonObject = JSON.parseObject(o.toString());
                            final String openid = jsonObject.getString("openid");
                            UserInfo userInfo = new UserInfo(RegisterdActivity.this,tencent.getQQToken());
                            userInfo.getUserInfo(new IUiListener() {
                                @Override
                                public void onComplete(Object o) {
                                    JSONObject jsonObject = JSONObject.parseObject(o.toString());
                                    JSONObject params = new JSONObject();
                                    params.put("openID_qq", openid);
                                    params.put("nickname", jsonObject.getString("nickname"));
                                    params.put("userIcon", jsonObject.getString("figureurl_qq_1"));
                                    ouathuerLogin(params);
                                }

                                @Override
                                public void onError(UiError uiError) {

                                }

                                @Override
                                public void onCancel() {

                                }
                            });

                        }

                        @Override
                        public void onError(UiError uiError) {

                        }

                        @Override
                        public void onCancel() {

                        }
                    });
                }
            }
        });

    }

    private UMAuthListener umAuthListener = new UMAuthListener() {
        @Override
        public void onStart(SHARE_MEDIA share_media) {

        }

        @Override
        public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
            UMShareAPI.get(RegisterdActivity.this).getPlatformInfo(RegisterdActivity.this,share_media,getUserInfoListener);
        }

        @Override
        public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
            showToast("第三方授权失败");
        }

        @Override
        public void onCancel(SHARE_MEDIA share_media, int i) {
            showToast("第三方授权取消");
        }
    };

    private UMAuthListener getUserInfoListener = new UMAuthListener() {
        @Override
        public void onStart(SHARE_MEDIA share_media) {

        }

        @Override
        public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
           if (share_media == SHARE_MEDIA.QQ){
               com.alibaba.fastjson.JSONObject jsonObject = new com.alibaba.fastjson.JSONObject();
               jsonObject.put("openID_qq",map.get("openid"));
               jsonObject.put("nickname",map.get("screen_name"));
               jsonObject.put("userIcon",map.get("profile_image_url"));
               ouathuerLogin(jsonObject);
           }else if (share_media == SHARE_MEDIA.SINA){
               JSONObject jsonObject = new JSONObject();
               jsonObject.put("openID_wb", map.get("idstr"));
               jsonObject.put("nickname", map.get("name"));
               jsonObject.put("userIcon", map.get("avatar_large"));
               ouathuerLogin(jsonObject);
           }else if (share_media==SHARE_MEDIA.WEIXIN){
               JSONObject jsonObject = new JSONObject();
               jsonObject.put("openID_wx",map.get("openid"));
               jsonObject.put("nickname",map.get("name"));
               jsonObject.put("userIcon",map.get("iconurl"));
               ouathuerLogin(jsonObject);
           }
        }

        @Override
        public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
            showToast("第三方授权失败");
        }

        @Override
        public void onCancel(SHARE_MEDIA share_media, int i) {
            showToast("第三方授权取消");
        }
    };

    private void ouathuerLogin(com.alibaba.fastjson.JSONObject params) {
        new HttpManager<OauthLoginModel>().sendQuest(Request.Method.POST, HttpUrlConfig.oauthLogin, params, OauthLoginModel.class, new HttpRequestCallBack<OauthLoginModel>() {
            @Override
            public void onRequestSuccess(OauthLoginModel response, boolean cached) {
                BaseApplication.getInstance().setUserId(response.getUser().getUserID());
                BaseApplication.getInstance().setUserModel(response.getUser());
                showToast("登录成功");
                finish();
            }

            @Override
            public void onRequestFailed(VolleyError error) {

            }
        });
    }

    public void onResume() {
        super.onResume();
    }

    public void onPause() {
        super.onPause();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }
}
