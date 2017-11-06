package com.white.usee.app.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by 10037 on 2016/8/4 0004.
 */

public class NewMsgsModel implements Serializable{
    private List<NewMsgModel> newMsgs;

    public List<NewMsgModel> getNewMsgs() {
        return newMsgs;
    }

    public void setNewMsgs(List<NewMsgModel> newMsgs) {
        this.newMsgs = newMsgs;
    }
}
