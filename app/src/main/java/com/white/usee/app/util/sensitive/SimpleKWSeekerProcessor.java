package com.white.usee.app.util.sensitive;

import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.white.usee.app.config.HttpUrlConfig;
import com.white.usee.app.model.GetSWFileInfoModel;
import com.white.usee.app.util.FileUtil;
import com.white.usee.app.util.HttpManager;
import com.white.usee.app.util.HttpRequestCallBack;
import com.white.usee.app.util.LogUtil;
import com.white.usee.app.util.sensitive.conf.Config;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


/**
 * 简单的敏感词处理器，从配置文件读取敏感词初始化，<br>
 * 使用者只需要在classpath放置sensitive-word.properties敏感词文件即可
 *
 * @author hailin0@yeah.net
 * @createDate 2016年5月22日
 */
public class SimpleKWSeekerProcessor extends KWSeekerManage {

    private static volatile SimpleKWSeekerProcessor instance;

    //检测敏感词文件是否有更新
    public void checkVersion(final Context context, final long lasteTime) {
        new HttpManager<GetSWFileInfoModel>().sendQuest(Request.Method.POST, HttpUrlConfig.getSWFileInfo, new JSONObject(), GetSWFileInfoModel.class, new HttpRequestCallBack<GetSWFileInfoModel>() {
            @Override
            public void onRequestSuccess(GetSWFileInfoModel response, boolean cached) {
                LogUtil.i("敏感词接口" + JSONObject.toJSONString(response));
                if (lasteTime < response.getLastModified()) {
                    downKw(response.getFileURL(), context);
                    setLasteTime(response.getLastModified(), context);
                }
            }

            @Override
            public void onRequestFailed(VolleyError error) {

            }
        });
    }

    private long downLoadId;

    //更新敏感词文件
    private void downKw(String urlString, Context context) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(urlString));
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
        File kwFile = new File(FileUtil.getCacheDir(context), "sensitive-word.properties");
        if (kwFile.exists()) kwFile.delete();
        request.setDestinationUri(Uri.fromFile(kwFile));
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        downLoadId = downloadManager.enqueue(request);
    }


    private long lasteTime = 0;

    public long getLasteTime(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("SimpleKWSeekerProcessor", Context.MODE_PRIVATE);
        lasteTime = sharedPreferences.getLong("lastModified", 0);
        return lasteTime;
    }

    public void setLasteTime(long lasteTime, Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences("SimpleKWSeekerProcessor", Context.MODE_PRIVATE).edit();
        editor.putLong("lastModified", lasteTime);
        editor.commit();
    }

    /**
     * 获取实例
     *
     * @return
     */
    public static SimpleKWSeekerProcessor newInstance(Context context) {
        if (null == instance) {
            synchronized (SimpleKWSeekerProcessor.class) {
                if (null == instance) {
                    instance = new SimpleKWSeekerProcessor(context);
                }
            }
        }

        return instance;
    }

    /**
     * 私有构造器
     */
    private SimpleKWSeekerProcessor(Context context) {
        initialize(context);
    }

    /**
     * 初始化敏感词
     */
    private void initialize(Context context) {
        Map<String, String> map = Config.newInstance(context).getAll();
        Set<Entry<String, String>> entrySet = map.entrySet();

        Map<String, KWSeeker> seekers = new HashMap<String, KWSeeker>();
        Set<KeyWord> kws;

        for (Entry<String, String> entry : entrySet) {
            String[] words = entry.getValue().split(",");
            kws = new HashSet<KeyWord>();
            for (String word : words) {
                kws.add(new KeyWord(word));
            }
            seekers.put(entry.getKey(), new KWSeeker(kws));
        }
        this.seekers.putAll(seekers);
    }
}
