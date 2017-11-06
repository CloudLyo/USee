package com.white.usee.app.model;

import java.io.Serializable;

/**
 * Created by 10037 on 2016/7/27 0027.
 */

public class CommentDanmuModel implements Serializable {
    private String id;
    private String danmuId;
    private String sender;
    private String receiver;
    private String content;
    private String replay_commentId;
    private int type;
    private String create_time;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDanmuId() {
        return danmuId;
    }

    public void setDanmuId(String danmuId) {
        this.danmuId = danmuId;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getReplay_commentId() {
        return replay_commentId;
    }

    public void setReplay_commentId(String replay_commentId) {
        this.replay_commentId = replay_commentId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }
}
