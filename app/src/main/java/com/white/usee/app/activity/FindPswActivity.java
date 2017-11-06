package com.white.usee.app.activity;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.white.usee.app.BaseActivity;
import com.white.usee.app.BaseApplication;
import com.white.usee.app.R;
import com.white.usee.app.config.HttpUrlConfig;
import com.white.usee.app.model.FindPswModel;
import com.white.usee.app.model.RegisterSmsModel;
import com.white.usee.app.model.SendCodeModel;
import com.white.usee.app.util.HttpManager;
import com.white.usee.app.util.HttpRequestCallBack;
import com.white.usee.app.util.LogUtil;
import com.white.usee.app.util.MD5Util;

public class FindPswActivity extends BaseActivity {
    private Button bt_next,bt_login;
    private EditText et_phoneNum,et_codes,et_psw,et_psw_again;
    private LinearLayout ly_findPsw_psw,ly_findPSw_phone;
    private TextView tv_sendCode;
    private String  currentCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_psw);
        findById();
        setOnClick();
    }

    private void findById(){
        bt_next = (Button)findViewById(R.id.bt_next);
        et_phoneNum = (EditText)findViewById(R.id.et_phoneNum);
        ly_findPsw_psw = (LinearLayout)findViewById(R.id.ly_findpsw_psw);
        ly_findPSw_phone = (LinearLayout)findViewById(R.id.ly_findpsw_phone);
        bt_login = (Button)findViewById(R.id.bt_login);
        et_codes = (EditText)findViewById(R.id.et_codes);
        et_psw= (EditText)findViewById(R.id.et_psw);
        et_psw_again = (EditText)findViewById(R.id.et_psw_again);
        tv_sendCode = (TextView)findViewById(R.id.tv_sendCode);
    }

    private void setOnClick(){
        (findViewById(R.id.title_back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        bt_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(et_phoneNum.getText())){
                    showToast("请输入手机号");
                    return;
                }else if (et_phoneNum.getText().length()!=11){
                    showToast("请输入正确的手机格式");
                    return;
                }

                String phoneNum = et_phoneNum.getText().toString().trim();
                    com.alibaba.fastjson.JSONObject jsonObject = new com.alibaba.fastjson.JSONObject();
                    jsonObject.put("cellphone", phoneNum);
                    new HttpManager<SendCodeModel>().sendQuest(Request.Method.POST, HttpUrlConfig.sendCode, jsonObject, SendCodeModel.class, new HttpRequestCallBack<SendCodeModel>() {
                        @Override
                        public void onRequestSuccess(SendCodeModel response, boolean cached) {
                            ly_findPSw_phone.setVisibility(View.GONE);
                            ly_findPsw_psw.setVisibility(View.VISIBLE);
                                currentCode = response.getVerificationCode();
                                showToast("发送验证码,请注意查收");
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

                        @Override
                        public void onRequestFailed(VolleyError error) {
                            showToast("请求失败，请检查网络" + error.getMessage());
                        }
                    });


            }
        });
        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(et_codes.getText())) {
                    showToast("验证码号不能为空");
                    return;
                }else if (TextUtils.isEmpty(et_psw.getText())){
                    showToast("请输入新密码");
                    return;
                }else if (TextUtils.isEmpty(et_psw_again.getText())){
                    showToast("请再次输入密码");
                    return;
                }else if (!et_psw.getText().toString().equals(et_psw_again.getText().toString())){
                    showToast("两次输入密码不同，请重新输入");
                    return;
                }

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("cellphone",et_phoneNum.getText().toString().trim());
                jsonObject.put("password",et_psw.getText().toString());
                jsonObject.put("verificationCode", MD5Util.getMD5(et_codes.getText().toString().trim()));
                new HttpManager<FindPswModel>().sendQuest(Request.Method.POST, HttpUrlConfig.forgetpassword, jsonObject, FindPswModel.class, new HttpRequestCallBack<FindPswModel>() {
                    @Override
                    public void onRequestSuccess(FindPswModel response, boolean cached) {
                        if (response.getReturnInfo().equals("cellphoneErr")){
                            showToast("手机号错误");
                        }else if (response.getReturnInfo().equals("verificationCodeErr")){
                            showToast("验证码错误");
                        }else if (response.getReturnInfo().equals("verificationCodeInvalid")){
                            showToast("验证码过期");
                        }else if (response.getReturnInfo().equals("success")){
                            showToast("登录成功");
                            BaseApplication.getInstance().setUserModel(response.getUser());
                            BaseApplication.getInstance().setUserId(response.getUser().getUserID());
                            finish();
                        }
                        LogUtil.i(JSONObject.toJSONString(response));
                    }

                    @Override
                    public void onRequestFailed(VolleyError error) {
                        showToast("验证失败，请重试");
                        ly_findPSw_phone.setVisibility(View.VISIBLE);
                        ly_findPsw_psw.setVisibility(View.GONE);
                    }
                });

            }
        });
        tv_sendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNum = et_phoneNum.getText().toString().trim();
                com.alibaba.fastjson.JSONObject jsonObject = new com.alibaba.fastjson.JSONObject();
                jsonObject.put("cellphone", phoneNum);
                new HttpManager<SendCodeModel>().sendQuest(Request.Method.POST, HttpUrlConfig.sendCode, jsonObject, SendCodeModel.class, new HttpRequestCallBack<SendCodeModel>() {
                    @Override
                    public void onRequestSuccess(SendCodeModel response, boolean cached) {
                        ly_findPSw_phone.setVisibility(View.GONE);
                        ly_findPsw_psw.setVisibility(View.VISIBLE);
                        currentCode = response.getVerificationCode();
                        showToast("发送验证码,请注意查收");
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

                    @Override
                    public void onRequestFailed(VolleyError error) {
                        showToast("请求失败，请检查网络" + error.getMessage());
                    }
                });
            }
        });
    }

}
