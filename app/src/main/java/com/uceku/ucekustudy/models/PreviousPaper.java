package com.uceku.ucekustudy.models;

import io.realm.RealmModel;
import io.realm.annotations.RealmClass;

@RealmClass
public class PreviousPaper implements RealmModel {
    private String year;
    private String subject;
    private boolean revaluation;
    private String paperCloudFullFileUrl;
    private String paperLocalFileUrl;
    private String paperCloudThumbnailUrl;
    private String paperLocalThumbnailUrl;

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getPaperCloudFullFileUrl() {
        return paperCloudFullFileUrl;
    }

    public void setPaperCloudFullFileUrl(String paperCloudFullFileUrl) {
        this.paperCloudFullFileUrl = paperCloudFullFileUrl;
    }

    public String getPaperLocalFileUrl() {
        return paperLocalFileUrl;
    }

    public void setPaperLocalFileUrl(String paperLocalFileUrl) {
        this.paperLocalFileUrl = paperLocalFileUrl;
    }

    public String getPaperCloudThumbnailUrl() {
        return paperCloudThumbnailUrl;
    }

    public void setPaperCloudThumbnailUrl(String paperCloudThumbnailUrl) {
        this.paperCloudThumbnailUrl = paperCloudThumbnailUrl;
    }

    public String getPaperLocalThumbnailUrl() {
        return paperLocalThumbnailUrl;
    }

    public void setPaperLocalThumbnailUrl(String paperLocalThumbnailUrl) {
        this.paperLocalThumbnailUrl = paperLocalThumbnailUrl;
    }

    public boolean isRevaluation() {
        return revaluation;
    }

    public void setRevaluation(boolean revaluation) {
        this.revaluation = revaluation;
    }
}
