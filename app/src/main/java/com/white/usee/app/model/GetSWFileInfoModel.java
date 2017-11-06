package com.white.usee.app.model;

import java.io.Serializable;

/**
 * Created by 10037 on 2016/9/3 0003.
 */

public class GetSWFileInfoModel implements Serializable {
    private String fileURL;
    private long lastModified;

    public String getFileURL() {
        return fileURL;
    }

    public void setFileURL(String fileURL) {
        this.fileURL = fileURL;
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }
}
