package com.white.usee.app.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by 10037 on 2016/7/16 0016.
 */

public class DanmuListModel implements Serializable {
    private List<DanmuModel>  danmu;

    public List<DanmuModel> getDanmu() {
        return danmu;
    }

    public void setDanmu(List<DanmuModel> danmu) {
        this.danmu = danmu;
    }
}
