package com.uceku.ucekustudy.models;

import java.util.Map;

import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

@RealmClass
public class Semester implements RealmModel {
    @PrimaryKey
    private int id;

    private String name;

    public Semester() {
    }

    public Semester(int id, String name) {
        this.id = id;
        this.name = name;
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

    public static Semester newSemesterObject(Map<String, Object> map) {
        return new Semester(
                Integer.parseInt((String)map.get("id")),
                (String)map.get("name")
        );
    }

}
