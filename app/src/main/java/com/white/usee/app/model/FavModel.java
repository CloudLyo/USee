package com.white.usee.app.model;

import java.io.Serializable;

/**
 * Created by 10037 on 2016/8/3 0003.
 */

public class FavModel  implements Serializable{
    private boolean status;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
