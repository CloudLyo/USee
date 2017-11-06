package com.white.usee.app.model;

import java.io.Serializable;

/**
 * Created by 10037 on 2016/9/3 0003.
 */

public class ShareTopicModel implements Serializable {
    private String topicUrl;
    private String shareContent;
    private String title;
    private String topicImg;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTopicImg(String topicImg) {
        this.topicImg = topicImg;
    }

    public String getTitle() {

        return title;
    }

    public String getTopicImg() {
        return topicImg;
    }

    public String getTopicUrl() {
        return topicUrl;
    }

    public void setTopicUrl(String topicUrl) {
        this.topicUrl = topicUrl;
    }

    public String getShareContent() {
        return shareContent;
    }

    public void setShareContent(String shareContent) {
        this.shareContent = shareContent;
    }
}
