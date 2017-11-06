package com.white.usee.app.model;

import java.io.Serializable;

/**
 * Created by 10037 on 2016/7/26 0026.
 */

public class OauthLoginModel implements Serializable {

    private int firstLogin;

    private UserModel user;

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

    public int getFirstLogin() {
        return firstLogin;
    }

    public void setFirstLogin(int firstLogin) {
        this.firstLogin = firstLogin;
    }
}
