package com.white.usee.app.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.white.usee.app.BaseActivity;
import com.white.usee.app.BaseApplication;
import com.white.usee.app.R;
import com.white.usee.app.config.HttpUrlConfig;
import com.white.usee.app.config.IntentKeyConfig;
import com.white.usee.app.model.DanmuModel;
import com.white.usee.app.model.DanmuWithCodeModel;
import com.white.usee.app.util.HttpManager;
import com.white.usee.app.util.HttpRequestCallBack;
import com.white.usee.app.util.ImageUtils;
import com.white.usee.app.util.LogUtil;
import com.white.usee.app.util.PhotoUtils;
import com.white.usee.app.util.QiniuUtils;
import com.white.usee.app.util.ThemeUtils;
import com.white.usee.app.util.sensitive.KWSeekerManage;
import com.white.usee.app.util.sensitive.SimpleKWSeekerProcessor;
import com.yancy.imageselector.ImageSelector;
import com.yancy.imageselector.ImageSelectorActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.white.usee.app.BaseApplication.getInstance;

public class ImageDanmuActivity extends BaseActivity {
    private ArrayList<String> photosPath;
    private ArrayList<String> compressedPhotoPath;
    private LinearLayout ly_photos ;
    private EditText et_danmu;
    private ImageButton ib_add_photo;
    private Button bt_send_danmu;
    private String topicid,userIcon,randomUserName;
    private int randomIconId;
    private boolean isannoymous;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_danmu);
        ThemeUtils.setColor(this, getResources().getColor(R.color.title));
        photosPath = getIntent().getStringArrayListExtra("photos");

        findById();
        setOnClick();
        initPhotos();
    }

    //压缩
    private ArrayList<String> compress(ArrayList<String> photosPath) {
        ArrayList<String> compressedPhotopath = new ArrayList<>();

        for(int i=0;i<photosPath.size();i++){
            LogUtil.i("path:" + photosPath.get(i));
        }

        for(int i=0;i<photosPath.size();i++) {
            Bitmap srcImage = BitmapFactory.decodeFile(photosPath.get(i));
////            if(photosPath.get(i).endsWith(".jpg")){
////                srcImage = ImageUtils.compressImage(srcImage,ImageUtils.TYPE_JPEG);
////            }else if(photosPath.get(i).endsWith(".png")){
////                srcImage = ImageUtils.compressImage(srcImage,ImageUtils.TYPE_PNG);
//            }
            int index = photosPath.get(i).lastIndexOf("/");
            String imageName = photosPath.get(i).substring(index);
////
            File imageFile = new File(Environment.getExternalStorageDirectory() + "/useeAdmin/cache/picture/");
            if(!imageFile.exists()){
                imageFile.mkdir();
            }
            String outputPath = imageFile.getAbsolutePath() +  imageName;

            Bitmap bitmap = ImageUtils.compressBySize(photosPath.get(i),480,800);

            try {
                ImageUtils.saveFile(bitmap,outputPath);
            } catch (Exception e) {
                e.printStackTrace();
            }

//            File img = ImageUtils.compressImage2(ImageDanmuActivity.this,photosPath.get(i),outputPath);
//            File img = new File(imageFile.getAbsolutePath() +  imageName);

//            try{
//                FileOutputStream fos = new FileOutputStream(img);
//                if(photosPath.get(i).endsWith(".jpg")) {
//                    srcImage.compress(Bitmap.CompressFormat.JPEG, 50, fos);
//                }else{
//                    srcImage.compress(Bitmap.CompressFormat.PNG, 50, fos);
//                }
//                fos.flush();
//                fos.close();
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            compressedPhotopath.add(outputPath);
            LogUtil.i("path:" + compressedPhotopath.get(i));
        }

        return compressedPhotopath;
    }

    private void findById(){
        ly_photos = (LinearLayout)findViewById(R.id.ly_photos);
        et_danmu = (EditText)findViewById(R.id.et_danmu);
        ib_add_photo = (ImageButton)findViewById(R.id.ib_add_phote);
        bt_send_danmu = (Button)findViewById(R.id.bt_send_danmu);

        topicid = getIntent().getStringExtra(IntentKeyConfig.TOPICID);
        userIcon = getIntent().getStringExtra(IntentKeyConfig.USERICON);
        randomIconId = getIntent().getIntExtra(IntentKeyConfig.RANDOMICONID,0);
        randomUserName = getIntent().getStringExtra(IntentKeyConfig.RANDOMUSERNAME);
        isannoymous = getIntent().getBooleanExtra(IntentKeyConfig.IsAnnoymous,true);
    }

    private void setOnClick(){
        findViewById(R.id.title_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ib_add_photo.setOnClickListener(toPhotoPickListener);
        bt_send_danmu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(et_danmu.getText().toString().trim())){
                    showToast("文字内容不能为空");
                    return;
                }

                if (photosPath!=null&&photosPath.size()>0){
                    showLoadingDialog(false);
                    photosPath = compress(photosPath);
                    QiniuUtils.uploadMutliFiles(photosPath, new QiniuUtils.UploadMutliListener() {
                        @Override
                        public void onUploadMutliSuccess(ArrayList<String> fileUrls) {

                            sendDanmu(et_danmu.getText().toString().trim(),isannoymous,fileUrls);
                        }

                        @Override
                        public void onUploadMutliFail(Error error) {
                            showToast("上传图片失败");
                            dismissLoadingDialog();
                        }
                    });

                }else {
                    showToast("图片内容不能为空");
                }
            }
        });

    }
    private View.OnClickListener toPhotoPickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            PhotoUtils.toPickPhoto(ImageDanmuActivity.this,photosPath,3);
        }
    };

    //初始化图片
    private void initPhotos(){
        if (photosPath.size()>=3) ib_add_photo.setVisibility(View.GONE);
        else ib_add_photo.setVisibility(View.VISIBLE);
        ly_photos.removeAllViews();

        if (photosPath.size()>0){
            for (String path:photosPath){
                PhotoUtils.addPhotoToLinearLayout(this,ly_photos,path,toPhotoPickListener);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ImageSelector.IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {//图片选择后的结果

            // Get Image Path List
            photosPath = data.getStringArrayListExtra(ImageSelectorActivity.EXTRA_RESULT);
           initPhotos();
        }
    }
    private void sendDanmu(String msg, boolean isannoymous,List<String> photoPath) {
        if (BaseApplication.getInstance().getUserId().equals(BaseApplication.NOUSER)) {
            showNoLogInTipDialog();
            dismissLoadingDialog();
            return;
        }

        if (getLon() == 0 && getLat() == 0) {
            showToast("定位未成功");
            dismissLoadingDialog();
            return;
        }
        KWSeekerManage kwSeekerManage = SimpleKWSeekerProcessor.newInstance(this);
        msg = kwSeekerManage.getKWSeeker("topic-sensitive-word").replaceWords(msg);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("isannoymous", isannoymous);
        jsonObject.put("lon", getLon());
        jsonObject.put("lat", getLat());
        jsonObject.put("delete_time", "1577844610");
        jsonObject.put("messages", msg);
        jsonObject.put("devid", getInstance().getDevId());
        jsonObject.put("userid", getInstance().getUserId());
        jsonObject.put("topicid", topicid);
        jsonObject.put("randomUserIcon", userIcon);
        jsonObject.put("randomIconId", randomIconId);
        jsonObject.put("randomUserName", randomUserName);
        if (photoPath==null) photoPath = new ArrayList<String>();
        jsonObject.put("imgurls",photoPath);
        new HttpManager<DanmuWithCodeModel>().sendQuest(Request.Method.POST, HttpUrlConfig.sendDanmu, jsonObject, DanmuWithCodeModel.class, new HttpRequestCallBack<DanmuWithCodeModel>() {
            @Override
            public void onRequestSuccess(DanmuWithCodeModel response, boolean cached) {
                LogUtil.i("danmumodel" + JSONObject.toJSONString(response));
                int code = response.getCode();

                if(code == 200) {
                    showToast("发送成功");
                }else if(code == 400){
                    showToast("请求失败");
                }else if(code == 401){
                    showToast("未登录");
                }else if(code == 402){
                    showToast("需要充值");
                }else if(code == 403){
                    showToast("你被封号");
                }else if(code == 404){
                    showToast("弹幕不存在");
                }else if(code == 405){
                    showToast("请勿发送相同弹幕");
                }else if(code == 406){
                    showToast("不接受请求");
                }else if(code == 407){
                    showToast("需要代理认证");
                }else if(code == 408){
                    showToast("服务器超时");
                }else if(code == 409) {
                    showToast("请求格式错误");
                }else if(code == 410){
                    showToast("客户端版本过低");
                }else if(code == 500){
                    showToast("服务器内部错误");
                }

                DanmuModel model = response.getDanmu();
                Intent data = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable("danmuModel",model);
                data.putExtra("data",bundle);
                setResult(RESULT_OK,data);
                dismissLoadingDialog();
                finish();
            }

            @Override
            public void onRequestFailed(VolleyError error) {
                showToast("发送失败");
                dismissLoadingDialog();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideInputWindow(et_danmu.getWindowToken());//该activity退出后，输入框也要隐藏
    }
}
