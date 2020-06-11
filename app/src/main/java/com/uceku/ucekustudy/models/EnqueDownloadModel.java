package com.uceku.ucekustudy.models;




public class EnqueDownloadModel {
    private long downloadID;
    private String destURL;
    private DownloadModelType type;

    public EnqueDownloadModel(long downloadID, String absolutePath, DownloadModelType type) {
        this.downloadID = downloadID;
        this.destURL = absolutePath;
        this.type = type;
    }

    public enum DownloadModelType {
        NOTE,
        PREVIOUS_PAPER,
        BOOK
    }

    public long getDownloadID() {
        return downloadID;
    }

    public void setDownloadID(long downloadID) {
        this.downloadID = downloadID;
    }

    public String getDestURL() {
        return destURL;
    }

    public void setDestURL(String destURL) {
        this.destURL = destURL;
    }

    public DownloadModelType getType() {
        return type;
    }

    public void setType(DownloadModelType type) {
        this.type = type;
    }
}
