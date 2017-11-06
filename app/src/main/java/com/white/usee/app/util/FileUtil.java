package com.white.usee.app.util;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * Created by Administrator on 2015/9/20.
 */
public class FileUtil {
    public static final String CACHE = "cache";
    public static final String ICON = "icon";
    public static final String ROOT = "useeAdmin";

    private FileUtil() {

    }

    public static File getDir(String dirName, Context context) {
        StringBuilder builder = new StringBuilder();
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String basePath = Environment.getExternalStorageDirectory().getAbsolutePath();
            builder.append(basePath);
            builder.append(File.separator);// '/'
            builder.append(ROOT);
            builder.append(File.separator);
            builder.append(dirName);
        } else {
            //无sd卡
            File filesDir = context.getCacheDir();    //  cache  getFileDir file
            builder.append(filesDir.getAbsolutePath());
            builder.append(File.separator);
            builder.append(dirName);
        }

        String path = builder.toString();
        File file = new File(path);

        if ((!file.exists()) || (!file.isDirectory()))

        {
            file.mkdirs();
        }

        return file;
    }


    public static File getIconDir(Context context) {
        return getDir(ICON,context);
    }

    public static File getCacheDir(Context context) {
        return getDir(CACHE,context);
    }
}
