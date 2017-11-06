package com.white.usee.app.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/10/8.
 */

public class CommentDanmuWithCodeModel implements Serializable {
    private int code;

    private CommentDanmuModel comment;

    public void setCode(int code) {
        this.code = code;
    }

    public void setComment(CommentDanmuModel comment) {
        this.comment = comment;
    }

    public int getCode() {

        return code;
    }

    public CommentDanmuModel getComment() {
        return comment;
    }
}
