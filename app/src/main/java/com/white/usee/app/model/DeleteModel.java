package com.white.usee.app.model;

import java.io.Serializable;

/**
 * Created by white on 16-9-15.
 */

public class DeleteModel implements Serializable {
    private boolean status;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
