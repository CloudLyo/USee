package com.white.usee.app.model;

import java.io.Serializable;

/**
 * Created by 10037 on 2016/8/2 0002.
 */

public class FindPswModel implements Serializable {
    private String returnInfo;
    private UserModel user;

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

    public String getReturnInfo() {
        return returnInfo;
    }

    public void setReturnInfo(String returnInfo) {
        this.returnInfo = returnInfo;
    }
}
