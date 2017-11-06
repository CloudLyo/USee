package com.white.usee.app.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/10/8.
 */

public class DanmuWithCodeModel implements Serializable{
    private int code;
    private DanmuModel danmu;

    public int getCode() {
        return code;
    }

    public DanmuModel getDanmu() {
        return danmu;
    }

    public void setDanmu(DanmuModel danmu) {
        this.danmu = danmu;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
