package com.uceku.ucekustudy.models;

import java.util.Map;

import io.realm.RealmModel;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

@RealmClass
public class Department implements RealmModel {

    @PrimaryKey
    private int branchId;
    private String shortName;
    private String fullName;
    private String imgBase64String;
    private String imgUrl;
    private String imgPath;

    public Department() {
    }

    public Department(int branchId, String shortName, String fullName) {
        this.branchId = branchId;
        this.shortName = shortName;
        this.fullName = fullName;
    }


    public Department(String shortName) {
        this.shortName = shortName;
    }

    public Department(int id, String short_name, String full_name, String img_url, String img_base_64_string) {
        this.branchId = id;
        this.shortName = short_name;
        this.fullName = full_name;
        this.imgUrl = img_url;
        this.imgBase64String = img_base_64_string;
        this.imgPath = "";
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getImgBase64String() {
        return imgBase64String;
    }

    public void setImgBase64String(String imgBase64String) {
        this.imgBase64String = imgBase64String;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public int getBranchId() {
        return branchId;
    }

    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }

    public static Department newDepartmentObject(Map<String, Object> branchMap) {
        if (branchMap == null) {
            return null;
        }
        return new Department(
                Integer.parseInt((String) branchMap.get("id")),
                (String)branchMap.get("short_name"),
                (String)branchMap.get("full_name"),
                (String)branchMap.get("img_url"),
                (String)branchMap.get("img_base_64_string")
        );
    }

}
