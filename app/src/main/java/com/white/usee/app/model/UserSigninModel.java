package com.white.usee.app.model;

import java.io.Serializable;

/**
 * Created by 10037 on 2016/7/25 0025.
 */

public class UserSigninModel implements Serializable{
    private String returnInfo;
    private UserModel user;

    public String getReturnInfo() {
        return returnInfo;
    }

    public void setReturnInfo(String returnInfo) {
        this.returnInfo = returnInfo;
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }
}
