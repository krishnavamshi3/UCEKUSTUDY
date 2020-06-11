package com.uceku.ucekustudy.models;

import java.util.Map;

import io.realm.RealmModel;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

@RealmClass
public class Syllabus implements RealmModel {

    @PrimaryKey
    private int id;
    private String name;
    private String fileCloudURL;
    private String fileLocalURL;
    private String fileAuthor;
    private int departmentID;
    private int semesterID;
    private int courseID;


    public Syllabus() {
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFileCloudURL() {
        return fileCloudURL;
    }

    public void setFileCloudURL(String fileCloudURL) {
        this.fileCloudURL = fileCloudURL;
    }

    public String getFileLocalURL() {
        return fileLocalURL;
    }

    public void setFileLocalURL(String fileLocalURL) {
        this.fileLocalURL = fileLocalURL;
    }

    public String getFileAuthor() {
        return fileAuthor;
    }

    public void setFileAuthor(String fileAuthor) {
        this.fileAuthor = fileAuthor;
    }

    public int getDepartmentID() {
        return departmentID;
    }

    public void setDepartmentID(int departmentID) {
        this.departmentID = departmentID;
    }

    public int getSemesterID() {
        return semesterID;
    }

    public void setSemesterID(int semesterID) {
        this.semesterID = semesterID;
    }

    public int getCourseID() {
        return courseID;
    }

    public void setCourseID(int courseID) {
        this.courseID = courseID;
    }

    public Syllabus(int id, String name, String fileCloudURL, String fileLocalURL, String fileAuthor, int departmentID, int semesterID, int courseID) {
        this.id = id;
        this.name = name;
        this.fileCloudURL = fileCloudURL;
        this.fileLocalURL = fileLocalURL;
        this.fileAuthor = fileAuthor;
        this.departmentID = departmentID;
        this.semesterID = semesterID;
        this.courseID = courseID;
    }


    public static Syllabus newSyllabusObject(Map<String, Object> data) {
        return new Syllabus(
                Integer.parseInt(String.valueOf(data.get("id"))),
                String.valueOf(data.get("name")),
                String.valueOf(data.get("fileURL")),
                "",
                String.valueOf(data.get("author")),
                Integer.parseInt(String.valueOf(data.get("departmentId"))),
                Integer.parseInt(String.valueOf(data.get("semId"))),
                Integer.parseInt(String.valueOf(data.get("courseId")))
        );
    }

}
