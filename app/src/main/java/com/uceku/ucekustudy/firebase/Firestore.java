package com.uceku.ucekustudy.firebase;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class Firestore {
    private final static String FIRESTORE_COLLECTION_BRANCH = "branch";
    public static final String FIRESTORE_COLLECTION_SEMESTER = "SEMESTER";
    public static final String FIRESTORE_COLLECTION_COURSE= "COURSE";
    private static final String FIRESTORE_COLLECTION_NOTES = "NOTES";
    private static final String FIRESTORE_COLLECTION_SYLLABUS = "SYLLABUS";

    public static void getAllFireStoreCollections(){

    }

    public static Query getAllBranchQuery() {
         return FirebaseFirestore.getInstance().collection(Firestore.FIRESTORE_COLLECTION_BRANCH);
    }

    public static Query getAllSemesterQuery() {
        return FirebaseFirestore.getInstance().collection(Firestore.FIRESTORE_COLLECTION_SEMESTER);
    }

    public static Task<QuerySnapshot> getAllCoursesQuerySnapshot(int departmentId, int semesterId) {
        return FirebaseFirestore.getInstance().collection(Firestore.FIRESTORE_COLLECTION_COURSE)
                .whereEqualTo("branchId", String.valueOf(departmentId))
                .whereEqualTo("semId", String.valueOf(semesterId))
                .get();
    }

    public static Query getAllNotesCollectionQuery() {
        return FirebaseFirestore.getInstance().collection(Firestore.FIRESTORE_COLLECTION_NOTES);
    }

    public static Query getSyllabusCollectionQuery() {
        return FirebaseFirestore.getInstance().collection(Firestore.FIRESTORE_COLLECTION_SYLLABUS);
    }

    public static Task<QuerySnapshot> getSyllabusQuerySnapshot(int courseId) {
        return FirebaseFirestore.getInstance().collection(Firestore.FIRESTORE_COLLECTION_SYLLABUS)
                .whereEqualTo("courseId", String.valueOf(courseId))
                .get();
    }
}
