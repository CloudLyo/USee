package com.white.usee.app.model;

import java.io.Serializable;

/**
 * 活动model
 * Created by 10037 on 2017/6/12 0012.
 */
public class ActivityModel implements Serializable {
    private String imageurl;
    private String title;
    private int type;

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
