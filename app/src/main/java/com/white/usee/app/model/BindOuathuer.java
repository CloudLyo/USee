package com.white.usee.app.model;

/**
 * Created by 10037 on 2016/7/28 0028.
 */

public class BindOuathuer {
    private String openID_qq;
    private String openID_wx;
    private String openID_wb;
    private String returnInfo;

    public String getReturnInfo() {
        return returnInfo;
    }

    public void setReturnInfo(String returnInfo) {
        this.returnInfo = returnInfo;
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
}
