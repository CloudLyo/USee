package com.white.usee.app.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.white.usee.app.model.UserModel;
import com.white.usee.app.model.UserSigninModel;
import com.white.usee.app.util.AssetImageUtils;
import com.white.usee.app.util.HttpManager;
import com.white.usee.app.util.HttpRequestCallBack;
import com.white.usee.app.util.LogUtil;
import com.white.usee.app.util.ThemeUtils;

import java.util.Map;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

public class UserManagerActivity extends BaseActivity {
    public static int requestCode = 1010;
    private Button bt_register, bt_login;
    private View view_mesh;
    private boolean hasLogin = false;
    private LinearLayout ly_collect, ly_setting;
    private LinearLayout ly_no_login;
    private RelativeLayout ly_has_login;
    private EditText et_phoneNum, et_psw;
    private ImageButton ib_login_wx, ib_login_wb, ib_login_qq;
    private ImageView iv_gender;
    private ImageView iv_head;
    private TextView tv_name,tv_findPsw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_manager);
        ThemeUtils.setColor(this, getResources().getColor(R.color.title));
        if (BaseApplication.getInstance().getUserId().equals(BaseApplication.NOUSER)) {
            hasLogin = false;
        } else {
            hasLogin = true;
        }
        findById();
        setOnClick();

    }

    private void findById() {
        tv_findPsw = (TextView)findViewById(R.id.tv_findPsw);
        iv_gender = (ImageView) findViewById(R.id.iv_gender);
        iv_head = (ImageView) findViewById(R.id.iv_head);
        tv_name = (TextView) findViewById(R.id.tv_name);
        bt_register = (Button) findViewById(R.id.bt_register);
        bt_login = (Button) findViewById(R.id.bt_login);
        ly_collect = (LinearLayout) findViewById(R.id.ly_collect);
        ly_setting = (LinearLayout) findViewById(R.id.ly_setting);
        et_phoneNum = (EditText) findViewById(R.id.et_phoneNum);
        et_psw = (EditText) findViewById(R.id.et_psw);
        view_mesh = (View) findViewById(R.id.view_mesh);
        ly_no_login = (LinearLayout) findViewById(R.id.ly_no_login_detail);
        ly_has_login = (RelativeLayout) findViewById(R.id.ly_has_login_detail);
        ib_login_qq = (ImageButton) findViewById(R.id.ib_login_qq);
        ib_login_wb = (ImageButton) findViewById(R.id.ib_login_weibo);
        ib_login_wx = (ImageButton) findViewById(R.id.ib_login_weixin);
        if (hasLogin) {
            ly_no_login.setVisibility(View.GONE);
            ly_has_login.setVisibility(View.VISIBLE);
        } else {
            ly_has_login.setVisibility(View.GONE);
            ly_no_login.setVisibility(View.VISIBLE);
            if (BaseApplication.getInstance().getPhoneNum()!=null&&!BaseApplication.getInstance().getPhoneNum().isEmpty()){
                et_phoneNum.setText(BaseApplication.getInstance().getPhoneNum());
            }
        }
        initUserData();
    }

    private void setOnClick() {
        ((ImageButton) findViewById(R.id.title_back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(et_phoneNum.getText())) {
                    showToast("请输入手机号");
                    return;
                }
                if (TextUtils.isEmpty(et_psw.getText())) {
                    showToast("请输入密码");
                    return;
                }
                login(et_phoneNum.getText().toString().trim(), et_psw.getText().toString().trim());
            }
        });
        bt_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserManagerActivity.this, RegisterdActivity.class);
                startActivityForResult(intent, requestCode);
            }
        });
        ly_collect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hasLogin)
                    startActivity(new Intent(UserManagerActivity.this, DanmuCollectActivity.class));
                else showUserNoLogInTipDialog();
            }
        });
        ly_has_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(UserManagerActivity.this, AccountActivity.class), requestCode);
            }
        });
        ly_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserManagerActivity.this,SettingActivity.class));
            }
        });
        tv_findPsw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(UserManagerActivity.this, FindPswActivity.class), requestCode);
            }
        });
        final UMShareAPI umShareAPI = UMShareAPI.get(this);
        ib_login_wx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLoadingDialog(true);

                    umShareAPI.doOauthVerify(UserManagerActivity.this, SHARE_MEDIA.WEIXIN, umAuthListener);
            }
        });
        ib_login_wb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLoadingDialog();
                    umShareAPI.doOauthVerify(UserManagerActivity.this, SHARE_MEDIA.SINA, umAuthListener);
            }
        });
        ib_login_qq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLoadingDialog();
                if (umShareAPI.isInstall(UserManagerActivity.this, SHARE_MEDIA.QQ))
                    umShareAPI.doOauthVerify(UserManagerActivity.this, SHARE_MEDIA.QQ, umAuthListener);
                else {
                    final Tencent tencent = Tencent.createInstance("222222",UserManagerActivity.this);
                    tencent.login(UserManagerActivity.this, "get_user_info", new IUiListener() {
                        @Override
                        public void onComplete(Object o) {
                           JSONObject jsonObject = JSON.parseObject(o.toString());
                            final String openid = jsonObject.getString("openid");
                            UserInfo userInfo = new UserInfo(UserManagerActivity.this,tencent.getQQToken());
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
                                        dismissLoadingDialog();
                                 }

                                 @Override
                                 public void onCancel() {
                                     dismissLoadingDialog();
                                 }
                             });

                        }

                        @Override
                        public void onError(UiError uiError) {
                            dismissLoadingDialog();
                        }

                        @Override
                        public void onCancel() {
                            dismissLoadingDialog();
                        }
                    });
                }

            }
        });
    }

    private UMAuthListener getUserInfo = new UMAuthListener() {
        @Override
        public void onStart(SHARE_MEDIA share_media) {

        }

        @Override
        public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
            LogUtil.i("getUserInfo"+map.toString());
            if (share_media == SHARE_MEDIA.QQ) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("openID_qq", map.get("openid"));
                jsonObject.put("nickname", map.get("screen_name"));
                jsonObject.put("userIcon", map.get("profile_image_url"));
                ouathuerLogin(jsonObject);
            }else if (share_media == SHARE_MEDIA.SINA){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("openID_wb", map.get("idstr"));
                jsonObject.put("nickname", map.get("name"));
                jsonObject.put("userIcon", map.get("avatar_large"));
                ouathuerLogin(jsonObject);
            }else if (share_media==SHARE_MEDIA.WEIXIN){
                final JSONObject jsonObject = new JSONObject();
                jsonObject.put("openID_wx",map.get("openid"));
                jsonObject.put("nickname",map.get("name"));
                jsonObject.put("userIcon",map.get("iconurl"));

                ouathuerLogin(jsonObject);
            }
        }

        @Override
        public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
            dismissLoadingDialog();
        }

        @Override
        public void onCancel(SHARE_MEDIA share_media, int i) {
        dismissLoadingDialog();
        }
    };

    //第三方登录回调
    private UMAuthListener umAuthListener = new UMAuthListener() {
        @Override
        public void onStart(SHARE_MEDIA share_media) {

        }

        @Override
        public void onComplete(SHARE_MEDIA share_media, int i, final Map<String, String> map) {
            LogUtil.i("umAutnListener"+map.toString());
            UMShareAPI.get(UserManagerActivity.this).getPlatformInfo(UserManagerActivity.this, share_media, getUserInfo);
        }

        @Override
        public void onError(SHARE_MEDIA share_media, final int i, Throwable throwable) {
            showToast("第三方授权失败");
            dismissLoadingDialog();
        }

        @Override
        public void onCancel(SHARE_MEDIA share_media, int i) {
            showToast("第三方授权取消");
            dismissLoadingDialog();
        }
    };

    private void ouathuerLogin(JSONObject params) {
        new HttpManager<OauthLoginModel>().sendQuest(Request.Method.POST, HttpUrlConfig.oauthLogin, params, OauthLoginModel.class, new HttpRequestCallBack<OauthLoginModel>() {
            @Override
            public void onRequestSuccess(OauthLoginModel response, boolean cached) {
                BaseApplication.getInstance().setUserId(response.getUser().getUserID());
                BaseApplication.getInstance().setUserModel(response.getUser());
                JPushInterface.setAlias(UserManagerActivity.this, response.getUser().getUserID(), new TagAliasCallback() {
                    @Override
                    public void gotResult(int i, String s, Set<String> set) {

                    }
                });
                hasLogin = true;
                showToast("登录成功");
                UserManagerActivity.this.setResult(ChooseTagNewActivity.RESULT_NEED);
                ly_has_login.setVisibility(View.VISIBLE);
                ly_no_login.setVisibility(View.GONE);
                initUserData();
                dismissLoadingDialog();

            }

            @Override
            public void onRequestFailed(final VolleyError error) {
                dismissLoadingDialog();
            }
        });
    }

    //如果本地有User数据，初始化用户数据
    private void initUserData() {
        UserModel userModel = BaseApplication.getInstance().getUserModel();
        if (userModel != null) {
            tv_name.setText(userModel.getNickname());
            if (userModel.getGender() == 0) {
                iv_gender.setImageDrawable(getResources().getDrawable(R.drawable.sex_man));
            } else if (userModel.getGender() == 1) {
                iv_gender.setImageDrawable(getResources().getDrawable(R.drawable.sex_woman));
            } else if (userModel.getGender() == 2) {
                iv_gender.setVisibility(View.GONE);
            }
            AssetImageUtils.loadUserHead(UserManagerActivity.this,userModel.getUserIcon(),iv_head);
        }
    }

    private void login(String phoneNum, String psw) {
        JSONObject json = new JSONObject();
        json.put("cellphone", phoneNum);
        json.put("password", psw);
        new HttpManager<UserSigninModel>().sendQuest(Request.Method.POST, HttpUrlConfig.userLogin, json, UserSigninModel.class, new HttpRequestCallBack<UserSigninModel>() {
            @Override
            public void onRequestSuccess(UserSigninModel response, boolean cached) {
                LogUtil.i("登入接口内容"+JSONObject.toJSONString(response));
                String returnInfo = response.getReturnInfo();
                if (returnInfo.equals("inexistence")) {
                    showToast("该手机号尚未注册");
                }else if (returnInfo.equals("cellphoneErr")){
                    showToast("手机号错误，请重新输入");
                    et_phoneNum.setText("");
                    et_psw.setText("");
                }else if (returnInfo.equals("passwordErr")) {
                    showToast("密码错误，请重新输入");
                    et_psw.setText("");
                } else if (returnInfo.equals("success")) {
                    showToast("登录成功");
                    hasLogin = true;
                    UserManagerActivity.this.setResult(ChooseTagNewActivity.RESULT_NEED);
                    hideInputWindow(et_psw.getWindowToken());
                    BaseApplication.getInstance().setUserId(response.getUser().getUserID());
                    BaseApplication.getInstance().setUserModel(response.getUser());
                    BaseApplication.getInstance().bindJPush(response.getUser().getUserID());
                    ly_has_login.setVisibility(View.VISIBLE);
                    ly_no_login.setVisibility(View.GONE);
                    initUserData();
                }
            }

            @Override
            public void onRequestFailed(VolleyError error) {
                showToast("用户名或密码不正确");
            }
        });
    }

    private void showUserNoLogInTipDialog() {
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
                tipDialog.dismiss();
            }
        });
        tipDialog.setContentView(mainView);
        view_mesh.setVisibility(View.VISIBLE);
        tipDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                view_mesh.setVisibility(View.GONE);
            }
        });
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.height=WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.width = getResources().getDimensionPixelOffset(R.dimen.px_four_hundred_sixty);
        tipDialog.getWindow().setAttributes(layoutParams);
        tipDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1010) {
            this.recreate();
            if (BaseApplication.getInstance().getPhoneNum()!=null&&!BaseApplication.getInstance().getPhoneNum().isEmpty()){
                et_phoneNum.setText(BaseApplication.getInstance().getPhoneNum());
            }
            return;
        }
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }
}
