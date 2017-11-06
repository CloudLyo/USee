package com.white.usee.app.model;

import java.io.Serializable;

/**
 * Created by 10037 on 2016/8/13 0013.
 */

public class HotWordModel implements Serializable{
    private String word;
    private double weight;

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}
