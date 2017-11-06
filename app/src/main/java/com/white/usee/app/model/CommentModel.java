package com.white.usee.app.model;

import java.io.Serializable;

/**
 * Created by 10037 on 2016/7/28 0028.
 */

public class CommentModel implements Serializable {

    private String content;

    private String create_time;

    private String danmuId;
    private int id;
    private String receiver;

    private String reply_commentId;

    private String sender;

    private int type;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getDanmuId() {
        return danmuId;
    }

    public void setDanmuId(String danmuId) {
        this.danmuId = danmuId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getReply_commentId() {
        return reply_commentId;
    }

    public void setReply_commentId(String reply_commentId) {
        this.reply_commentId = reply_commentId;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
