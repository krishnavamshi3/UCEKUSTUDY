package com.uceku.ucekustudy.models;

import java.util.Map;

import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

@RealmClass
public class Course implements RealmModel {

    @PrimaryKey
    private int id;

    private String name;

    private String code;

    private int externalMarks;

    private int internalMarks;

    private double credits;

    private int branchId;

    private int semId;

    public Course() {

    }

    public Course(int id, String name, String code, int externalMarks, int internalMarks, double credits, int branchId, int semId) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.externalMarks = externalMarks;
        this.internalMarks = internalMarks;
        this.credits = credits;
        this.branchId = branchId;
        this.semId = semId;
    }

    public static Course newCourseObject(Map<String, Object> data) {
        return new Course(
                Integer.parseInt(String.valueOf(data.get("id"))),
                (String) data.get("name"),
                (String) data.get("code"),
                Integer.parseInt((String) data.get("externalMarks")),
                Integer.parseInt((String) data.get("internalMarks")),
                Double.parseDouble((String) data.get("credits")),
                Integer.parseInt((String) data.get("branchId")),
                Integer.parseInt((String) data.get("semId"))
        );
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getExternalMarks() {
        return externalMarks;
    }

    public void setExternalMarks(int externalMarks) {
        this.externalMarks = externalMarks;
    }

    public int getInternalMarks() {
        return internalMarks;
    }

    public void setInternalMarks(int internalMarks) {
        this.internalMarks = internalMarks;
    }

    public double getCredits() {
        return credits;
    }

    public void setCredits(double credits) {
        this.credits = credits;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBranchId() {
        return branchId;
    }

    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }

    public int getSemId() {
        return semId;
    }

    public void setSemId(int semId) {
        this.semId = semId;
    }
}
