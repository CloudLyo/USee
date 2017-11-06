package com.white.usee.app.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/10/9.
 */

public class SingleTopicModel implements Serializable {
    private TopicModel topic;

    public void setTopic(TopicModel topic) {
        this.topic = topic;
    }

    public TopicModel getTopic() {

        return topic;
    }
}
