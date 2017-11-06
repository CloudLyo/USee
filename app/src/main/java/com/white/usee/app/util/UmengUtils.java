package com.white.usee.app.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.white.usee.app.BaseActivity;
import com.white.usee.app.R;
import com.white.usee.app.config.HttpUrlConfig;
import com.white.usee.app.model.ShareTopicModel;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;


/**
 * 第三方登入与分享的接口
 * Created by 10037 on 2016/7/18 0018.
 */

public class UmengUtils {

    public static void share(final String topocID, final String topicTtile, final BaseActivity activity, final SHARE_MEDIA share_media, final UMShareListener umShareListener){
        activity.showLoadingDialog();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("topicId",topocID);
        new HttpManager<ShareTopicModel>().sendQuest(Request.Method.POST, HttpUrlConfig.sharetopic, jsonObject, ShareTopicModel.class, new HttpRequestCallBack<ShareTopicModel>() {
            @Override
            public void onRequestSuccess(final ShareTopicModel response, boolean cached) {
                LogUtil.i("分享内容"+JSONObject.toJSONString(response));

                String imageUrl = response.getTopicImg();

                if(imageUrl.equals("")||imageUrl.isEmpty()){
                    UMImage image = new UMImage(activity,
                            BitmapFactory.decodeResource(activity.getResources(), R.mipmap.ic_launcher));
                    UMWeb web = new UMWeb(response.getTopicUrl());
                    web.setTitle(topicTtile);
                    web.setThumb(image);
                    web.setDescription(response.getShareContent());
                    new ShareAction(activity)
                            .setPlatform(share_media)
                            .setCallback(umShareListener)
                            .withText(response.getTitle() + "\n" + response.getShareContent())
                            .withMedia(web)
                            .share();
                    activity.dismissLoadingDialog();
                }else{
                    new AsyncTask<String,Void,Bitmap>(){

                        @Override
                        protected Bitmap doInBackground(String... params) {
                            return getBitmap(params[0]);
                        }

                        @Override
                        protected void onPostExecute(Bitmap bitmap) {
                            super.onPostExecute(bitmap);
                            Bitmap bm = bitmap;
                            UMImage image = new UMImage(activity,bitmap);
                            UMWeb web = new UMWeb(response.getTopicUrl());
                            web.setThumb(image);
                            web.setTitle(topicTtile);
                            new ShareAction(activity)
                                    .setPlatform(share_media)
                                    .setCallback(umShareListener)
                                    .withText(response.getShareContent())
                                    .withMedia(image)
                                    .withMedia(web)
                                    .share();
                            activity.dismissLoadingDialog();
                        }
                    }.execute(imageUrl);


                }

            }

            @Override
            public void onRequestFailed(VolleyError error) {
                activity.dismissLoadingDialog();
            }
        });

    }

    public static Bitmap getBitmap(String url) {
        Bitmap bm = null;
        try {
            URL iconUrl = new URL(url);
            URLConnection conn = iconUrl.openConnection();
            HttpURLConnection http = (HttpURLConnection) conn;

            int length = http.getContentLength();

            conn.connect();
            // 获得图像的字符流
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is, length);
            bm = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();// 关闭流
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return bm;
    }
}
