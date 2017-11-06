package com.white.usee.app.util;


import android.os.Handler;

import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.white.usee.app.BaseApplication;
import com.white.usee.app.R;
import com.white.usee.app.config.HttpUrlConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

/**
 * 七牛的上传工具
 * Created by 10037 on 2016/9/23 0023.
 */

public class QiniuUtils {
    private static UploadManager uploadManager = new UploadManager();

    /**
     * 上传多个文件(由于七牛不支持多个文件同时上传，使用递归的方式逐个上传)
     *
     * @param filesUrls           上传文件的本地url
     * @param uploadMutliListener 上传的回调
     */
    public static void uploadMutliFiles(final ArrayList<String> filesUrls, final UploadMutliListener uploadMutliListener) {
        if (filesUrls != null && filesUrls.size() > 0) {
            final ArrayList<String> remoteFiles = new ArrayList<>();
            final int[] i = {0};
            uploadFile(filesUrls.get(i[0]), new UploadListener() {
                @Override
                public void onUploadSuccess(String remoteFileUrl) {
                    final UploadListener uploadListener = this;
                    remoteFiles.add(remoteFileUrl);
                    LogUtil.i("第" + i[0] + "张上传成功\t" + remoteFileUrl);
                    i[0]++;
                    if (i[0] < filesUrls.size()) {
                        new Handler().postDelayed(new Runnable() {//七牛后台对上传的文件名是以时间撮来命名，以秒为单位，如果文件上传过快，两张图片就会重名而上传失败，所以间隔1秒，保证上传成功
                            @Override
                            public void run() {
                                uploadFile(filesUrls.get(i[0]), uploadListener);
                            }
                        },1000);

                    } else {
                        uploadMutliListener.onUploadMutliSuccess(remoteFiles);
                    }
                }

                @Override
                public void onUploadFail(Error error) {
                    LogUtil.e("第"+i[0]+"上传失败"+filesUrls.get(i[0]));
                    uploadMutliListener.onUploadMutliFail(error);
                }
            });

        }
    }


    /**
     * 上传一个文件
     *
     * @param filePath 上传文件的本地url
     */
    public static void uploadFile(final String filePath, final UploadListener uploadListener) {
        if (filePath == null) return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                final OkHttpClient httpClient = new OkHttpClient();
                Request req = new Request.Builder().url(HttpUrlConfig.qiniuTokenUrl).method("POST", new RequestBody() {
                    @Override
                    public MediaType contentType() {
                        return null;
                    }

                    @Override
                    public void writeTo(BufferedSink sink) throws IOException {

                    }
                }).build();
                Response resp = null;
                try {
                    resp = httpClient.newCall(req).execute();
                    JSONObject jsonObject = new JSONObject(resp.body().string());
                    String uploadToken = jsonObject.getString("uptoken");
                    String domain = jsonObject.getString("domain");
//                    String domain = "http://odyutrywf.qnssl.com";
//                    LogUtil.i("获取凭证"+jsonObject.toString());
                    upload(uploadToken, domain, filePath, 720, uploadListener);
                } catch (Exception e) {
                    uploadListener.onUploadFail(new Error("七牛上传凭证获取失败" + e.getMessage()));
                } finally {
                    if (resp != null) {
                        resp.body().close();
                    }
                }
            }
        }).start();
    }

    private static void upload(final String uploadToken, final String domain, String photoPath, final int width, final UploadListener uploadListener) {
        if (uploadManager == null) {
            uploadManager = new UploadManager();
        }
        final File uploadFile = new File(photoPath);
        UploadOptions uploadOptions = new UploadOptions(null, null, false,
                null, null);

        uploadManager.put(uploadFile, null, uploadToken,
                new UpCompletionHandler() {
                    @Override
                    public void complete(String key, ResponseInfo respInfo,
                                         JSONObject jsonData) {

                        if (respInfo.isOK()) {
                            try {
                                LogUtil.i("上传成功"+jsonData.toString());
                                String fileKey = jsonData.getString("key");
                                final String imageUrl = domain + "/" + fileKey + "?imageView2/0/w/" + width + "/format/jpg";
                                uploadListener.onUploadSuccess(imageUrl);
                            } catch (JSONException e) {
                                uploadListener.onUploadFail(new Error("七牛上传文件后解析结果失败" + e.getMessage()));
                            }
                        } else {
                            uploadListener.onUploadFail(new Error("七牛上传失败"+respInfo.error));
                        }
                    }

                }, uploadOptions);
    }


    //上传回调
    public interface UploadListener {
        void onUploadSuccess(String remoteFileUrl);

        void onUploadFail(Error error);
    }

    //上传多张文件回调
    public interface UploadMutliListener {
        void onUploadMutliSuccess(ArrayList<String> fileUrls);

        void onUploadMutliFail(Error error);
    }
}
