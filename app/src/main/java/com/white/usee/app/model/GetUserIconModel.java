package com.white.usee.app.model;

import java.io.Serializable;

/**
 * Created by 10037 on 2016/8/3 0003.
 */

public class GetUserIconModel implements Serializable {
    private String usericon;
    private String username;
    private int isanonymous;
    private int randomIconId;

    public int getIsanonymous() {
        return isanonymous;
    }

    public void setIsanonymous(int isanonymous) {
        this.isanonymous = isanonymous;
    }

    public int getRandomIconId() {
        return randomIconId;
    }

    public void setRandomIconId(int randomIconId) {
        this.randomIconId = randomIconId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsericon() {
        return usericon;
    }

    public void setUsericon(String usericon) {
        this.usericon = usericon;
    }
}
