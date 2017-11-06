package com.white.usee.app.model;

import java.io.Serializable;

/**
 * Created by 10037 on 2016/8/4 0004.
 */

public class BindPhoneModel implements Serializable{
    private String returnInfo;
    private String userID;
    private String cellphone;
    private String password;

    public String getReturnInfo() {
        return returnInfo;
    }

    public void setReturnInfo(String returnInfo) {
        this.returnInfo = returnInfo;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getCellphone() {
        return cellphone;
    }

    public void setCellphone(String cellphone) {
        this.cellphone = cellphone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
