package com.white.usee.app.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by 10037 on 2016/8/13 0013.
 */

public class HotWordsModel implements Serializable {
    private List<HotWordModel> hotwords;


    public List<HotWordModel> getHotwords() {
        return hotwords;
    }

    public void setHotwords(List<HotWordModel> hotwords) {
        this.hotwords = hotwords;
    }
}
