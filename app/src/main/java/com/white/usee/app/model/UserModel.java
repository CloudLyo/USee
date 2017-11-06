package com.white.usee.app.model;

import java.io.Serializable;

/**
 * Created by 10037 on 2016/7/25 0025.
 */

public class UserModel implements Serializable {
    private String userID;
    private int gender;//性别 0为男，1为女，2为未填写
    private String nickname;
    private String userIcon;
    private String cellphone;
    private String password;
    private String createTime;
    private String openID_qq;
    private String openID_wx;
    private String openID_wb;
    private String verificationCode;
    private String vcSendTime;

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
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

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getOpenID_qq() {
        return openID_qq;
    }

    public void setOpenID_qq(String openID_qq) {
        this.openID_qq = openID_qq;
    }

    public String getOpenID_wx() {
        return openID_wx;
    }

    public void setOpenID_wx(String openID_wx) {
        this.openID_wx = openID_wx;
    }

    public String getOpenID_wb() {
        return openID_wb;
    }

    public void setOpenID_wb(String openID_wb) {
        this.openID_wb = openID_wb;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public String getVcSendTime() {
        return vcSendTime;
    }

    public void setVcSendTime(String vcSendTime) {
        this.vcSendTime = vcSendTime;
    }
}
