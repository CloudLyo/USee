package com.white.usee.app.activity;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.white.usee.app.BaseActivity;
import com.white.usee.app.BaseApplication;
import com.white.usee.app.R;
import com.white.usee.app.config.HttpUrlConfig;
import com.white.usee.app.model.BindPhoneModel;
import com.white.usee.app.model.SendCodeModel;
import com.white.usee.app.model.UserModel;
import com.white.usee.app.util.HttpManager;
import com.white.usee.app.util.HttpRequestCallBack;
import com.white.usee.app.util.MD5Util;
import com.white.usee.app.util.ThemeUtils;

public class BindPhoneActivity extends BaseActivity {
    private TextView tv_sendCode;
    private EditText et_phoneNum,et_code,et_psw;
    private Button bt_bind_phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_phone);
        ThemeUtils.setColor(this, getResources().getColor(R.color.title));
        findById();
        setOnClick();
    }

    private void findById(){
        tv_sendCode = (TextView)findViewById(R.id.tv_sendCode);
        et_phoneNum = (EditText)findViewById(R.id.et_phoneNum);
        et_code=(EditText)findViewById(R.id.et_codes);
        et_psw = (EditText)findViewById(R.id.et_pwd);
        bt_bind_phone = (Button)findViewById(R.id.bt_bind_phone);
    }

    private void setOnClick(){
        (findViewById(R.id.title_back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        tv_sendCode.setOnClickListener(new View.OnClickListener() {
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
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("cellphone", phoneNum);
                new HttpManager<SendCodeModel>().sendQuest(Request.Method.POST, HttpUrlConfig.sendCode, jsonObject, SendCodeModel.class, new HttpRequestCallBack<SendCodeModel>() {
                    @Override
                    public void onRequestSuccess(SendCodeModel response, boolean cached) {
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

                    }
                });
            }
        });

        bt_bind_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(et_code.getText())) {
                    showToast("验证码号不能为空");
                    return;
                }else if (TextUtils.isEmpty(et_psw.getText())){
                    showToast("请输入新密码");
                    return;
                }else if (TextUtils.isEmpty(et_phoneNum.getText())){
                    showToast("手机号码不能为空");
                    return;
                }else if (et_phoneNum.getText().toString().length()<11){
                    showToast("请输入正确的手机号");
                    return;
                }
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("userID",BaseApplication.getInstance().getUserId());
                jsonObject.put("cellphone",et_phoneNum.getText().toString().trim());
                jsonObject.put("password",et_psw.getText().toString());
                jsonObject.put("verificationCode", MD5Util.getMD5(et_code.getText().toString().trim()));
                new HttpManager<BindPhoneModel>().sendQuest(Request.Method.POST, HttpUrlConfig.bindCellPhone, jsonObject, BindPhoneModel.class, new HttpRequestCallBack<BindPhoneModel>() {
                    @Override
                    public void onRequestSuccess(BindPhoneModel response, boolean cached) {
                       if (response.getReturnInfo().equals("success")){
                           showToast("绑定成功");
                           UserModel userModel = BaseApplication.getInstance().getUserModel();
                           userModel.setCellphone(response.getCellphone());
                           BaseApplication.getInstance().setUserModel(userModel);
                           finish();
                       }else if (response.getReturnInfo().equals("cellphoneErr")){
                           showToast("手机号错误");
                       }else if (response.getReturnInfo().equals("cellphoneBinding")){
                           showToast("此号码已绑定别的账号");
                       }else if (response.getReturnInfo().equals("verificationCodeErr")){
                           showToast("验证码错误");
                       }else if (response.getReturnInfo().equals("verificationCodeInvalid")){
                            showToast("验证码过期");
                       }

                    }

                    @Override
                    public void onRequestFailed(VolleyError error) {
                        showToast("验证失败，请检查网络");
                    }
                });

            }
        });
    }

}
