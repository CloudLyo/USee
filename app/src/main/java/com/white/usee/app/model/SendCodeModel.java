package com.white.usee.app.model;

import java.io.Serializable;

/**
 * Created by 10037 on 2016/8/2 0002.
 */

public class SendCodeModel implements Serializable {
    private String cellphone;
    private String verificationCode;
    private String vcSendTime;

    public String getCellphone() {
        return cellphone;
    }

    public void setCellphone(String cellphone) {
        this.cellphone = cellphone;
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
