package com.uceku.ucekustudy.realm_db;

import android.content.Context;

import com.uceku.ucekustudy.models.Course;
import com.uceku.ucekustudy.models.Department;
import com.uceku.ucekustudy.models.NoteOverview;
import com.uceku.ucekustudy.models.Semester;
import com.uceku.ucekustudy.models.Syllabus;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmCollection;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class Reads {

    public static boolean isDepartmentEmpty(Realm realm) {
        int size = realm.where(Department.class).findAll().size();
        return size == 0;
    }

    public static List<Department> fetchAllDepartmentsUnManaged(Realm realm) {
        RealmCollection<Department> managedDepartments =  realm.where(Department.class).findAll();
        if (managedDepartments == null || managedDepartments.isEmpty()) return new ArrayList<>();
        return realm.copyFromRealm(managedDepartments);
    }

    public static String getDepartmentNameFromId(int branchId, Realm realm) {
        Department department = realm.where(Department.class).equalTo("branchId", branchId).findFirst();
        if (department == null) return "";
        return department.getShortName();
    }

    public static boolean isCoursesEmptyForDepartmentAndSemester(Context baseContext, int departmentId, int semesterId, Realm realm) {
        int size = realm.where(Course.class).equalTo("branchId", departmentId).equalTo("semId", semesterId).findAll().size();
        return size == 0;
    }

    public static String getSemesterNameFromId(int semesterId, Realm realm) {
        Semester semester = realm.where(Semester.class).equalTo("id", semesterId).findFirst();
        if (semester == null) return "";
        return semester.getName();
    }

    public static boolean isSemesterDBEmpty(Realm realm, int semesterId) {
        int size = realm.where(Semester.class).equalTo("id", semesterId).findAll().size();
        return size == 0;
    }

    public static RealmQuery getQueryForAllCourses(int departmentId, int semesterId, Realm realm) {
        return realm.where(Course.class).equalTo("branchId", departmentId).equalTo("semId", semesterId);
    }

    public static boolean isNotesDBEmpty(Realm realm, int courseId) {
        int size = realm.where(NoteOverview.class).equalTo("courseId", courseId).findAll().size();
        return size == 0;
    }

    public static RealmResults<NoteOverview> getAllStarredNotes(Realm realm) {
        return realm.where(NoteOverview.class).equalTo("starred", true).findAll();
    }

    public static Syllabus getCourseSyllabus(int mCourseId) {
        Syllabus syllabus = null;
        try(Realm realm = Realm.getDefaultInstance()) {
            Syllabus manageSyllabus = realm.where(Syllabus.class).equalTo("courseID", mCourseId).findFirst();
            if (manageSyllabus != null)
            syllabus = realm.copyFromRealm(manageSyllabus);
        }
        return syllabus;
    }

    public static Syllabus getSyllabusByID(int syllabusID, Realm realm) {
        return realm.where(Syllabus.class).equalTo("id", syllabusID).findFirst();
    }
}
