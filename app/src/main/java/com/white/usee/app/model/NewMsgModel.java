package com.white.usee.app.model;

import java.io.Serializable;

/**
 * nickname": "Spark",
 "gender": 1,
 "userIcon": "14_E673C9",
 "content": "yy",
 "type": 1,
 "create_time": "1472961345",
 "danmuId": 3735,
 "commentId": 572,
 "sender": "B9EAC945604E443CBEC15838CE95DA08",
 "danmuUserID": "B9EAC945604E443CBEC15838CE95DA08",
 "topicTitle": "测
 * Created by 10037 on 2016/8/4 0004.
 */

public class NewMsgModel implements Serializable{
    private String nickname;

    private int gender;

    private String userIcon;

    private String content;

    private int type;

    private String create_time;

    private int danmuId;
    private int commentId;
    private String sender;
    private String danmuUserID;
    private String topicTitle;

    public String getDanmuUserID() {
        return danmuUserID;
    }

    public void setDanmuUserID(String danmuUserID) {
        this.danmuUserID = danmuUserID;
    }

    public String getTopicTitle() {
        return topicTitle;
    }

    public void setTopicTitle(String topicTitle) {
        this.topicTitle = topicTitle;
    }

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getUserIcon() {
        return userIcon;
    }

    public void setUserIcon(String userIcon) {
        this.userIcon = userIcon;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public int getDanmuId() {
        return danmuId;
    }

    public void setDanmuId(int danmuId) {
        this.danmuId = danmuId;
    }
}
