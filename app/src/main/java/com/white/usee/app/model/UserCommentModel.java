package com.white.usee.app.model;

import java.io.Serializable;

/**
 * Created by 10037 on 2016/7/25 0025.
 */

public class UserCommentModel implements Serializable {
    private UserModel user;

    private CommentModel comment;

    private String replycomment_name;
    private int replycomment_gender;

    public int getReplycomment_gender() {
        return replycomment_gender;
    }

    public void setReplycomment_gender(int replycomment_gender) {
        this.replycomment_gender = replycomment_gender;
    }

    public String getReplycomment_name() {
        return replycomment_name;
    }

    public void setReplycomment_name(String replycomment_name) {
        this.replycomment_name = replycomment_name;
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

    public CommentModel getComment() {
        return comment;
    }

    public void setComment(CommentModel comment) {
        this.comment = comment;
    }
}
