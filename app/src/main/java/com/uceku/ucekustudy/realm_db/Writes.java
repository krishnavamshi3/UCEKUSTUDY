package com.uceku.ucekustudy.realm_db;

import androidx.annotation.NonNull;

import com.uceku.ucekustudy.models.Course;
import com.uceku.ucekustudy.models.NoteOverview;
import com.uceku.ucekustudy.models.Semester;
import com.uceku.ucekustudy.models.Syllabus;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class Writes {

    public static void insertOrUpdateCoursesInDB(final List<Course> courseList, final Realm.Callback callback) {
        if (courseList == null) {
            callback.onError(new RuntimeException("courses list is empty. couldn't insert in to realm."));
            return;
        }
        try (Realm r = Realm.getDefaultInstance()) {
            r.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(@NonNull Realm realm) {
                    realm.insertOrUpdate(courseList);
                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    callback.onSuccess(r);
                }
            }, new Realm.Transaction.OnError() {
                @Override
                public void onError(Throwable error) {
                    callback.onError(error);
                }
            });
        }
    }

    public static void insertOrUpdateSemestersInDB(final List<Semester> semesters, final Realm.Callback callback) {
        if (semesters == null) {
            callback.onError(new RuntimeException("semesters list is empty. couldn't insert in to realm."));
            return;
        }
        try (Realm r = Realm.getDefaultInstance()) {
            r.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(@NonNull Realm realm) {
                    realm.insertOrUpdate(semesters);
                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    callback.onSuccess(r);
                }
            }, new Realm.Transaction.OnError() {
                @Override
                public void onError(Throwable error) {
                    callback.onError(error);
                }
            });
        }
    }


    public static void insertOrUpdateNoteOverviewInDB(final List<NoteOverview> noteOverviewList, final Realm.Callback callback) {
        if (noteOverviewList == null ) {
            callback.onError(new RuntimeException("Notes Overview list is empty. couldn't insert in to realm."));
            return;
        }
        try (Realm r = Realm.getDefaultInstance()) {
            r.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(@NonNull Realm realm) {
                    realm.insertOrUpdate(noteOverviewList);
                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    callback.onSuccess(r);
                }
            }, new Realm.Transaction.OnError() {
                @Override
                public void onError(Throwable error) {
                    callback.onError(error);
                }
            });
        }
    }

    public static void insertOrUpdateSyllabusInDB(final List<Syllabus> syllabusList, final Realm.Callback callback) {
        if (syllabusList == null || syllabusList.isEmpty()) return;
        try(final Realm realm = Realm.getDefaultInstance()) {

            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(@NonNull Realm realm) {

                    RealmResults<Syllabus> syllabusRealmResults = realm.where(Syllabus.class).equalTo("courseID", syllabusList.get(0).getCourseID()).findAll();
                    syllabusRealmResults.deleteAllFromRealm();
                    realm.insertOrUpdate(syllabusList);

                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    callback.onSuccess(realm);
                }
            }, new Realm.Transaction.OnError() {
                @Override
                public void onError(@NonNull Throwable error) {
                    callback.onError(error);
                }
            });
        }
    }
}
