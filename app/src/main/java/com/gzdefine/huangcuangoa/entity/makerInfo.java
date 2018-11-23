package com.gzdefine.huangcuangoa.entity;

import com.baidu.mapapi.model.LatLng;

import java.io.Serializable;

/**
 * TODO：
 * 地图坐标实体类
 * Created by Max on 2017/7/18.
 */

public class makerInfo implements Serializable {


    private double lng,lat;
    private String content;
    private LatLng latlng;
    public makerInfo()
    {

    }
    public makerInfo(double lng, double lat, String content) {
        this.lng = lng;
        this.lat = lat;
        this.content = content;
        latlng=new LatLng(lat, lng);
    }
    public double getLng() {
        return lng;
    }
    public void setLng(double lng) {
        this.lng = lng;
    }
    public double getLat() {
        return lat;
    }
    public void setLat(double lat) {
        this.lat = lat;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public LatLng getLatlng() {
        return latlng;
    }
    public void setLatlng(LatLng latlng) {
        this.latlng = latlng;
    }

}