package com.white.usee.app.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by 10037 on 2016/7/26 0026.
 */

public class TopicModel implements Serializable {

    private long create_time;

    private int danmuNum;

    private List<String> imgurls;

    private long delete_time;

    private String description;

    private  int expired;

    private String id;

    private long lastDanmu_time;

    private double lat;

    private double lon;

    private String poi;
    private int radius;
    private String title;
    private String type;
    private String userID;

    private boolean isread;

    public boolean isread() {
        return isread;
    }

    public void setIsread(boolean isread) {
        this.isread = isread;
    }

    public long getCreate_time() {
        return create_time;
    }

    public void setCreate_time(long create_time) {
        this.create_time = create_time;
    }

    public int getDanmuNum() {
        return danmuNum;
    }

    public void setDanmuNum(int danmuNum) {
        this.danmuNum = danmuNum;
    }

    public long getDelete_time() {
        return delete_time;
    }

    public void setDelete_time(long delete_time) {
        this.delete_time = delete_time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getExpired() {
        return expired;
    }

    public void setExpired(int expired) {
        this.expired = expired;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getLastDanmu_time() {
        return lastDanmu_time;
    }

    public void setLastDanmu_time(long lastDanmu_time) {
        this.lastDanmu_time = lastDanmu_time;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getPoi() {
        return poi;
    }

    public void setPoi(String poi) {
        this.poi = poi;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public List<String> getImgurls() {
        return imgurls;
    }

    public void setImgurls(List<String> imgurls) {
        this.imgurls = imgurls;
    }

    //用于去重，当topicid一样时判断位同一个类
    @Override
    public boolean equals(Object obj) {
        if (obj==null){
            return false;
        }

        if (obj instanceof TopicModel){
            TopicModel other = (TopicModel) obj;
            if (this.getId().equals(other.getId())){
                return true;
            }
        }

        return false;
    }

    @Override
    public String toString() {
        return  "title:" + getTitle() + "-id:" + getId()+ "---lastTime:" + getLastDanmu_time() + "---danmuNumber:" + getDanmuNum();
    }
}
