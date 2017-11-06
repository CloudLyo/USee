package com.white.usee.app.model;

import java.io.Serializable;

/**
 * Created by white on 16/4/23.
 */
public class AboutMeModel implements Serializable {
    private String remindid;
    private String postid;
    private String replyid;
    private String messageid;
    private String kind;
    private String context;
    private boolean station;//是否已读,false为未读,true为已读
    private String message;

    public String getRemindid() {
        return remindid;
    }

    public void setRemindid(String remindid) {
        this.remindid = remindid;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getReplyid() {
        return replyid;
    }

    public void setReplyid(String replyid) {
        this.replyid = replyid;
    }

    public String getMessageid() {
        return messageid;
    }

    public void setMessageid(String messageid) {
        this.messageid = messageid;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public boolean isStation() {
        return station;
    }

    public void setStation(boolean station) {
        this.station = station;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
