package com.white.usee.app.model;


import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016/10/4.
 */

public class UserInfoModel implements Serializable{

    private String userID;

    private int gender;

    private String nickName;

    private String userIcon;

    private ArrayList<TopicModel> topic;

    public void setTopic(ArrayList<TopicModel> topic) {
        this.topic = topic;
    }

    public ArrayList<TopicModel> getTopic() {
        return topic;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public void setUserIcon(String userIcon) {
        this.userIcon = userIcon;
    }


    public String getUserID() {

        return userID;
    }

    public int getGender() {
        return gender;
    }

    public String getNickName() {
        return nickName;
    }

    public String getUserIcon() {
        return userIcon;
    }

    public UserInfoModel(){
        topic = new ArrayList<>();
    }


}
