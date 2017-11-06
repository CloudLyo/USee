package com.white.usee.app.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by 10037 on 2016/7/26 0026.
 */

public class TopicsModel implements Serializable {
    private List<TopicModel> topic;

    public List<TopicModel> getTopic() {
        return topic;
    }

    public void setTopic(List<TopicModel> topic) {
        this.topic = topic;
    }
}
