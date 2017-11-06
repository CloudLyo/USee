package com.white.usee.app.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
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
import com.white.usee.app.model.BindOuathuer;
import com.white.usee.app.model.UpdateUserModel;
import com.white.usee.app.model.UploadIconModel;
import com.white.usee.app.model.UserModel;
import com.white.usee.app.util.AssetImageUtils;
import com.white.usee.app.util.DataCleanManager;
import com.white.usee.app.util.FileUtil;
import com.white.usee.app.util.HttpManager;
import com.white.usee.app.util.HttpRequestCallBack;
import com.white.usee.app.util.ImageUtils;
import com.white.usee.app.util.LogUtil;
import com.white.usee.app.util.ThemeUtils;
import com.white.usee.app.util.volley.MultipartRequestParams;

import java.io.File;
import java.util.Map;

public class AccountActivity extends BaseActivity {
    private static final int RESULT_LOAD_IMAGE = 1111;
    private static final int RESULT_BIND_PHONE = 1010;
    private static final int RESULT_CROP = 2020;
    private RelativeLayout rl_user_name, rl_user_sex, rl_head,rl_phoneNum;
    private ImageView iv_head;
    private View view_mesh;
    private TextView tv_userName, tv_user_sex, tv_phone_num;
    private ToggleButton toggle_weixin, toggle_qq, toggle_weibo;
    private Button bt_logout;
    private UserModel curUser = BaseApplication.getInstance().getUserModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        ThemeUtils.setColor(this, getResources().getColor(R.color.title));
        findById();
        setOnCLick();
        initUserData();
        LogUtil.i("用户消息"+JSONObject.toJSONString(curUser));
    }

    private void initUserData() {
        if (curUser != null) {
            tv_userName.setText(curUser.getNickname());
            switch (curUser.getGender()) {
                case 0:
                    tv_user_sex.setText(R.string.male);
                    break;
                case 1:
                    tv_user_sex.setText(R.string.female);
                    break;
                case 2:
                    tv_user_sex.setText(R.string.not_tell);
                    break;
            }
            if (curUser.getCellphone() != null && !curUser.getCellphone().isEmpty()) {
                String showPhoneNum = curUser.getCellphone();
                String middle = showPhoneNum.substring(3, 9);
                tv_phone_num.setText(showPhoneNum.replace(middle, "***"));
                rl_phoneNum.setOnClickListener(null);
            } else {
                tv_phone_num.setText(R.string.not_bind);
                rl_phoneNum.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivityForResult(new Intent(AccountActivity.this,BindPhoneActivity.class),RESULT_BIND_PHONE);
                    }
                });
            }
        }
        if (curUser != null) {
            if (curUser.getOpenID_qq() != null && !curUser.getOpenID_qq().isEmpty()) {
                toggle_qq.setChecked(true);
            } else toggle_qq.setChecked(false);

            if (curUser.getOpenID_wb() != null && !curUser.getOpenID_wb().isEmpty()) {
                toggle_weibo.setChecked(true);
            } else toggle_weibo.setChecked(false);

            if (curUser.getOpenID_wx() != null && !curUser.getOpenID_wx().isEmpty()) {
                toggle_weixin.setChecked(true);
            } else toggle_weixin.setChecked(false);
        }
        AssetImageUtils.loadUserHead(this,curUser.getUserIcon(),iv_head);
    }
    private void findById() {
        iv_head = (ImageView) findViewById(R.id.iv_head);

        rl_head = (RelativeLayout) findViewById(R.id.rl_head);
        rl_phoneNum = (RelativeLayout)findViewById(R.id.rl_about_me);
        rl_user_name = (RelativeLayout) findViewById(R.id.rl_clearCache);
        rl_user_sex = (RelativeLayout) findViewById(R.id.rl_checkversion);
        tv_userName = (TextView) findViewById(R.id.tv_user_name);
        tv_user_sex = (TextView) findViewById(R.id.tv_version);
        tv_phone_num = (TextView) findViewById(R.id.tv_phonenum);
        view_mesh = (View) findViewById(R.id.view_mesh);
        bt_logout = (Button) findViewById(R.id.bt_logout);
        toggle_qq = (ToggleButton) findViewById(R.id.toogle_qq);
        toggle_weibo = (ToggleButton) findViewById(R.id.toogle_weibo);
        toggle_weixin = (ToggleButton) findViewById(R.id.toogle_is_push);

    }

    private void setOnCLick() {
        ((ImageButton) findViewById(R.id.title_back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        rl_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });
        rl_user_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view_mesh.setVisibility(View.VISIBLE);
                showPopEditUserName();
            }
        });
        rl_user_sex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view_mesh.setVisibility(View.VISIBLE);
                showPopChooseSex();
            }
        });
        bt_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BaseApplication.getInstance().setUserId(BaseApplication.NOUSER);
              if (curUser.getCellphone()!=null&&!curUser.getCellphone().isEmpty()) BaseApplication.getInstance().setPhoneNum(curUser.getCellphone());
                BaseApplication.getInstance().setUserModel(null);
                showToast("退出登录成功");
                finish();
            }
        });
        final UMShareAPI umShareAPI = UMShareAPI.get(this);
        toggle_qq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (toggle_qq.isChecked()) {
                    if (umShareAPI.isInstall(AccountActivity.this, SHARE_MEDIA.QQ)) {
                        umShareAPI.doOauthVerify(AccountActivity.this, SHARE_MEDIA.QQ, umAuthListener);
                    } else {
                        final Tencent tencent = Tencent.createInstance("222222",AccountActivity.this);
                        tencent.login(AccountActivity.this, "get_user_info", new IUiListener() {
                            @Override
                            public void onComplete(Object o) {
                                JSONObject jsonObject = JSON.parseObject(o.toString());
                                LogUtil.i("网页登录："+o.toString());
                                final String openid = jsonObject.getString("openid");
                                JSONObject params = new JSONObject();
                                params.put("userID",curUser.getUserID());
                                params.put("openID_qq", openid);
                                params.put("openID_wx", null);
                                params.put("openID_wb", null);
                                bindouathuer(params);
                            }

                            @Override
                            public void onError(UiError uiError) {
                                toggle_qq.setChecked(false);
                            }

                            @Override
                            public void onCancel() {
                                toggle_qq.setChecked(false);
                            }
                        });
                        toggle_qq.setChecked(false);
                    }
                }
            }
        });
        toggle_weibo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (toggle_weibo.isChecked()) {
                        umShareAPI.doOauthVerify(AccountActivity.this, SHARE_MEDIA.SINA, umAuthListener);
                }
            }
        });
        toggle_weixin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (toggle_weixin.isChecked()) {
                    if (umShareAPI.isInstall(AccountActivity.this, SHARE_MEDIA.WEIXIN)) {
                        umShareAPI.doOauthVerify(AccountActivity.this, SHARE_MEDIA.WEIXIN, umAuthListener);
                    } else {
                        toggle_weixin.setChecked(false);
                    }

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
            UMShareAPI.get(AccountActivity.this).getPlatformInfo(AccountActivity.this, share_media, getUserInfoListener);
        }

        @Override
        public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
            if (toggle_qq.isChecked()) toggle_qq.setChecked(false);
            if (toggle_weixin.isChecked()) toggle_weixin.setChecked(false);
            if (toggle_weibo.isChecked()) toggle_weibo.setChecked(false);
        }

        @Override
        public void onCancel(SHARE_MEDIA share_media, int i) {
            if (toggle_qq.isChecked()) toggle_qq.setChecked(false);
            if (toggle_weixin.isChecked()) toggle_weixin.setChecked(false);
            if (toggle_weibo.isChecked()) toggle_weibo.setChecked(false);
        }
    };

    private UMAuthListener getUserInfoListener = new UMAuthListener() {
        @Override
        public void onStart(SHARE_MEDIA share_media) {

        }

        @Override
        public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
            if (share_media == SHARE_MEDIA.QQ) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("userID", curUser.getUserID());
                jsonObject.put("openID_qq", map.get("openid"));
                jsonObject.put("openID_wx", null);
                jsonObject.put("openID_wb", null);
                bindouathuer(jsonObject);
            }else if (share_media == SHARE_MEDIA.SINA){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("userID", curUser.getUserID());
                jsonObject.put("openID_qq", null);
                jsonObject.put("openID_wx", null);
                jsonObject.put("openID_wb", map.get("idstr"));
                LogUtil.i("绑定微博"+jsonObject.toJSONString());
                bindouathuer(jsonObject);
            }else if (share_media == SHARE_MEDIA.WEIXIN){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("userID", curUser.getUserID());
                jsonObject.put("openID_qq", null);
                jsonObject.put("openID_wx", map.get("openid"));
                jsonObject.put("openID_wb", null);
                bindouathuer(jsonObject);
            }
        }

        @Override
        public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
            if (toggle_qq.isChecked()) toggle_qq.setChecked(false);
            if (toggle_weixin.isChecked()) toggle_weixin.setChecked(false);
            if (toggle_weibo.isChecked()) toggle_weibo.setChecked(false);
        }

        @Override
        public void onCancel(SHARE_MEDIA share_media, int i) {
            if (toggle_qq.isChecked()) toggle_qq.setChecked(false);
            if (toggle_weixin.isChecked()) toggle_weixin.setChecked(false);
            if (toggle_weibo.isChecked()) toggle_weibo.setChecked(false);
        }
    };

    private void showPopEditUserName() {
        View mainView = View.inflate(this, R.layout.pop_edit_user_name, null);
        final PopupWindow popupWindow = new PopupWindow(mainView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable());

        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);


        final EditText ed_user_name = (EditText) mainView.findViewById(R.id.et_user_name);
        ed_user_name.setText(tv_userName.getText());
        final TextView tv_rest = (TextView) mainView.findViewById(R.id.tv_rest);
        ed_user_name.setFocusable(true);
        ed_user_name.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN){
                    if (TextUtils.isEmpty(ed_user_name.getText())) {
                        showToast(getString(R.string.name_no_null));
                        return false;
                    }
                    String et_name = ed_user_name.getText().toString().trim();
                    if (!et_name.equals(curUser.getNickname())) {
                        JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(curUser));
                        jsonObject.put("nickname", et_name);
                        changeUserInfo(jsonObject);
                    }
                    popupWindow.dismiss();
                    return true;
                }
                return false;
            }
        });
        ed_user_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int rest = 10 - charSequence.length();
                tv_rest.setText(rest + "");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                view_mesh.setVisibility(View.GONE);


            }
        });
        popupWindow.showAtLocation(this.getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
    }

    private void showPopChooseSex() {
        View mainView = View.inflate(this, R.layout.pop_choose_sex, null);
        final PopupWindow popupWindow = new PopupWindow(mainView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                view_mesh.setVisibility(View.GONE);
            }
        });

        TextView tv_man = (TextView) mainView.findViewById(R.id.tv_ad);
        TextView tv_woman = (TextView) mainView.findViewById(R.id.tv_bilk);
        TextView tv_no_sex = (TextView) mainView.findViewById(R.id.tv_pornography);
        TextView tv_cancel = (TextView) mainView.findViewById(R.id.tv_cancel);
        tv_man.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
                if (curUser.getGender() != 0) {
                    JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(curUser));
                    jsonObject.put("gender", 0);
                    changeUserInfo(jsonObject);
                }


            }
        });
        tv_woman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
                if (curUser.getGender() != 1) {
                    JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(curUser));
                    jsonObject.put("gender", 1);
                    changeUserInfo(jsonObject);
                }
            }
        });
        tv_no_sex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
                if (curUser.getGender() != 2) {
                    JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(curUser));
                    jsonObject.put("gender", 2);
                    changeUserInfo(jsonObject);
                }
            }
        });
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });

        popupWindow.showAtLocation(this.getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
    }

    //修改用户参数
    private void changeUserInfo(JSONObject params) {
        new HttpManager<UpdateUserModel>().sendQuest(Request.Method.POST, HttpUrlConfig.updateUser, params, UpdateUserModel.class, new HttpRequestCallBack<UpdateUserModel>() {
            @Override
            public void onRequestSuccess(UpdateUserModel response, boolean cached) {
                curUser.setNickname(response.getNickname());
                curUser.setUserIcon(response.getUserIcon());
                curUser.setGender(response.getGender());
                BaseApplication.getInstance().setUserModel(curUser);
                showToast("修改成功");
                initUserData();
            }

            @Override
            public void onRequestFailed(VolleyError error) {

            }
        });
    }

    private void bindouathuer(JSONObject params) {
        new HttpManager<BindOuathuer>().sendQuest(Request.Method.POST, HttpUrlConfig.bindOauth, params, BindOuathuer.class, new HttpRequestCallBack<BindOuathuer>() {
            @Override
            public void onRequestSuccess(BindOuathuer response, boolean cached) {
                if (response.getReturnInfo().equals("exit")) {
                    showToast(getString(R.string.account_has_used));
                    return;
                }
                curUser.setOpenID_qq(response.getOpenID_qq());
                curUser.setOpenID_wb(response.getOpenID_wb());
                curUser.setOpenID_wx(response.getOpenID_wx());
                BaseApplication.getInstance().setUserModel(curUser);
                showToast(getString(R.string.bind_success));
                initUserData();
            }

            @Override
            public void onRequestFailed(VolleyError error) {
                showToast(getString(R.string.account_has_used));
                if (toggle_qq.isChecked()) toggle_qq.setChecked(false);
                if (toggle_weixin.isChecked()) toggle_weixin.setChecked(false);
                if (toggle_weibo.isChecked()) toggle_weibo.setChecked(false);
            }
        });
    }
    private Uri outImageUri = Uri.fromFile(new File(FileUtil.getCacheDir(this),System.currentTimeMillis()+".png"));

    //跳转到系统裁剪界面
    private void startCrop(Uri imageUri){
        outImageUri = Uri.fromFile(new File(FileUtil.getCacheDir(this),System.currentTimeMillis()+".png"));
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outImageUri);
        intent.setDataAndType(imageUri, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", false);
        intent.putExtra("noFaceDetection", true);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
        startActivityForResult(intent,RESULT_CROP);
    }
    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==RESULT_CROP){
            if (resultCode==RESULT_OK&&null!=data){
                final File icon = new File(ImageUtils.getRealFilePath(AccountActivity.this,outImageUri));
                Glide.clear(iv_head);
                MultipartRequestParams params = new MultipartRequestParams();
                params.put("userID",curUser.getUserID());
                params.put("headPhotoFile",icon);
                new HttpManager<UploadIconModel>().uploadImage(HttpUrlConfig.uploadicon, params, UploadIconModel.class, new HttpRequestCallBack<UploadIconModel>() {
                    @Override
                    public void onRequestSuccess(UploadIconModel response, boolean cached) {
                        showToast(getString(R.string.change_usericon_success));
                        curUser.setUserIcon(response.getUserIcon());
                        BaseApplication.getInstance().setUserModel(curUser);
                        //清空缓存 然后重新加载图片
                        DataCleanManager.clearImageDiskCache();
                        BaseApplication.getInstance().setHeadsignature(String.valueOf(System.currentTimeMillis()));
                        AssetImageUtils.loadUserHead(AccountActivity.this,response.getUserIcon(),iv_head);
                        if (response.getReturnInfo().equals("success")){

                        }else {
                            showToast("头像为空");
                        }
                        dismissLoadingDialog();
                    }

                    @Override
                    public void onRequestFailed(VolleyError error) {
                        LogUtil.i(error.getMessage()+"上传头像失败");
                        dismissLoadingDialog();
                        showToast(getString(R.string.change_usericon_fail));
                    }
                });
            }
        }else if (requestCode == RESULT_LOAD_IMAGE){
            if (resultCode == RESULT_OK && null != data) {
                showLoadingDialog();
                startCrop(data.getData());
            }
            return;
        }else if (requestCode==RESULT_BIND_PHONE){
            initUserData();
        }else {
            dismissLoadingDialog();
        }

        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }
}
