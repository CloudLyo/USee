package com.white.usee.app.model;

import java.io.Serializable;

/**
 * Created by 10037 on 2016/8/3 0003.
 */

public class UpdateUserAction implements Serializable {
   private int result;

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }
}
