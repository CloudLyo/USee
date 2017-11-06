package com.white.usee.app.model;

/**
 * Created by 10037 on 2016/8/7 0007.
 */

public class FavDanmuModel {
    private String fav_time;
    private int danmuID;
    private int id;
    private String unfav_time;
    private String userID;
    private String messages;
    private int gender;
    private String nickname;
    private String userIcon;
    private String topicTitle;
    private boolean havecomment;

    public boolean isHavecomment() {
        return havecomment;
    }

    public void setHavecomment(boolean havecomment) {
        this.havecomment = havecomment;
    }

    public String getFav_time() {
        return fav_time;
    }

    public void setFav_time(String fav_time) {
        this.fav_time = fav_time;
    }

    public int getDanmuID() {
        return danmuID;
    }

    public void setDanmuID(int danmuID) {
        this.danmuID = danmuID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUnfav_time() {
        return unfav_time;
    }

    public void setUnfav_time(String unfav_time) {
        this.unfav_time = unfav_time;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getMessages() {
        return messages;
    }

    public void setMessages(String messages) {
        this.messages = messages;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getUserIcon() {
        return userIcon;
    }

    public void setUserIcon(String userIcon) {
        this.userIcon = userIcon;
    }

    public String getTopicTitle() {
        return topicTitle;
    }

    public void setTopicTitle(String topicTitle) {
        this.topicTitle = topicTitle;
    }
}
