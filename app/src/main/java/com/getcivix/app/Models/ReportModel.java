package com.getcivix.app.Models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class ReportModel {

    public String comment;
    public Category category;
    public Long reportTime;
    public String reportLocation;
    public String reporterUserID;
    public String uploadedReportImageKey;
    public Integer credibility;

    public ReportModel(String comment, Category category, Long reportTime, String location, String reporterId, String uploadedReportImageKey,Integer credibility) {
        this.comment = comment;
        this.reportTime = reportTime;
        this.reportLocation = location;
        this.reporterUserID = reporterId;
        this.category = category;
        this.credibility = credibility;
        this.uploadedReportImageKey = uploadedReportImageKey;
    }

    public ReportModel() {

    }


    @Override
    public String toString() {
        return "ReportModel{" +
                "comment='" + comment + '\'' +
                ", category=" + category +
                ", reportTime=" + reportTime +
                ", reportLocation='" + reportLocation + '\'' +
                ", reporterUserID='" + reporterUserID + '\'' +
                ", uplaodedReportImageKey='" + uploadedReportImageKey + '\'' +
                ", credibility=" + credibility + '\'' +
                '}';
    }
}
