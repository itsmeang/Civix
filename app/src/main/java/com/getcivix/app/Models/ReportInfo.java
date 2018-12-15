package com.getcivix.app.Models;

import com.google.android.gms.maps.model.LatLng;

public class ReportInfo {

    private String name;
    private String type;
    private String address;
    private LatLng latLng;
    private String id;

    public ReportInfo(String name, String type, String address, LatLng latLng, String id) {
        this.name = name;
        this.type = type;
        this.address = address;
        this.latLng = latLng;
        this.id = id;
    }

    public ReportInfo(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "ReportInfo{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", address='" + address + '\'' +
                ", latLng=" + latLng +
                ", id='" + id + '\'' +
                '}';
    }
}
