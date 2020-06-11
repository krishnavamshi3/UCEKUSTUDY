package com.uceku.ucekustudy.models;

import java.util.Map;

import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

@RealmClass
public class NoteOverview implements RealmModel {
    private String noteName;
    private String noteAuthor;
    private String noteCloudFullFileUrl;
    private String noteLocalFileUrl;
    private String noteCloudThumbnailUrl;
    private String noteLocalThumbnailUrl;
    private int noteRecommendedCount;
    private boolean recommended;
    private boolean recommendedByTeacher;

    private int departmentId;
    private int semId;
    private int courseId;

    private long enqueuedDownloadId;
    private boolean saved;
    private int downloadStatus;

    private boolean starred;

    @PrimaryKey
    private int id;


    public NoteOverview() {
    }

    public NoteOverview(String noteName,
                        String noteAuthor,
                        String noteCloudFullFileUrl,
                        String noteLocalFileUrl,
                        String noteCloudThumbnailUrl,
                        String noteLocalThumbnailUrl,
                        int noteRecommendedCount,
                        boolean recommended,
                        boolean recommendedByTeacher,
                        int departmentId,
                        int semId,
                        int courseId,
                        int id,
                        int enqueuedDownloadId,
                        boolean saved,
                        int downloading,
                        boolean starred) {
        this.noteName = noteName;
        this.noteAuthor = noteAuthor;
        this.noteCloudFullFileUrl = noteCloudFullFileUrl;
        this.noteLocalFileUrl = noteLocalFileUrl;
        this.noteCloudThumbnailUrl = noteCloudThumbnailUrl;
        this.noteLocalThumbnailUrl = noteLocalThumbnailUrl;
        this.noteRecommendedCount = noteRecommendedCount;
        this.recommended = recommended;
        this.recommendedByTeacher = recommendedByTeacher;
        this.departmentId = departmentId;
        this.semId = semId;
        this.courseId = courseId;
        this.id = id;
        this.enqueuedDownloadId = enqueuedDownloadId;
        this.saved = saved;
        this.downloadStatus = downloadStatus;
        this.starred = starred;
    }

    public String getNoteName() {
        return noteName;
    }

    public void setNoteName(String noteName) {
        this.noteName = noteName;
    }

    public String getNoteAuthor() {
        return noteAuthor;
    }

    public void setNoteAuthor(String noteAuthor) {
        this.noteAuthor = noteAuthor;
    }

    public String getNoteCloudFullFileUrl() {
        return noteCloudFullFileUrl;
    }

    public void setNoteCloudFullFileUrl(String noteCloudFullFileUrl) {
        this.noteCloudFullFileUrl = noteCloudFullFileUrl;
    }

    public String getNoteLocalFileUrl() {
        return noteLocalFileUrl;
    }

    public void setNoteLocalFileUrl(String noteLocalFileUrl) {
        this.noteLocalFileUrl = noteLocalFileUrl;
    }

    public String getNoteCloudThumbnailUrl() {
        return noteCloudThumbnailUrl;
    }

    public void setNoteCloudThumbnailUrl(String noteCloudThumbnailUrl) {
        this.noteCloudThumbnailUrl = noteCloudThumbnailUrl;
    }

    public String getNoteLocalThumbnailUrl() {
        return noteLocalThumbnailUrl;
    }

    public void setNoteLocalThumbnailUrl(String noteLocalThumbnailUrl) {
        this.noteLocalThumbnailUrl = noteLocalThumbnailUrl;
    }

    public int getNoteRecommendedCount() {
        return noteRecommendedCount;
    }

    public void setNoteRecommendedCount(int noteRecommendedCount) {
        this.noteRecommendedCount = noteRecommendedCount;
    }

    public boolean isRecommended() {
        return recommended;
    }

    public void setRecommended(boolean recommended) {
        this.recommended = recommended;
    }

    public boolean isRecommendedByTeacher() {
        return recommendedByTeacher;
    }

    public void setRecommendedByTeacher(boolean recommendedByTeacher) {
        this.recommendedByTeacher = recommendedByTeacher;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public int getSemId() {
        return semId;
    }

    public void setSemId(int semId) {
        this.semId = semId;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getEnqueuedDownloadId() {
        return enqueuedDownloadId;
    }

    public void setEnqueuedDownloadId(long enqueuedDownloadId) {
        this.enqueuedDownloadId = enqueuedDownloadId;
    }

    public boolean isSaved() {
        return saved;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }

    public int getDownloadStatus() {
        return downloadStatus;
    }

    public void setDownloading(int downloadStatus) {
        this.downloadStatus = downloadStatus;
    }

    public void setDownloadStatus(int downloadStatus) {
        this.downloadStatus = downloadStatus;
    }

    public boolean isStarred() {
        return starred;
    }

    public void setStarred(boolean starred) {
        this.starred = starred;
    }

    public static NoteOverview newNoteOverviewObject(Map<String, Object> data) {
        return new NoteOverview(
                String.valueOf(data.get("name")),
                String.valueOf(data.get("author")),
                String.valueOf(data.get("fileURL")),
                "",
                "",
                "",
                0,
                Boolean.parseBoolean(String.valueOf(data.get("suggested"))),
                Boolean.parseBoolean(String.valueOf(data.get("suggestedByTeacher"))),
                Integer.parseInt(String.valueOf(data.get("branchId"))),
                Integer.parseInt(String.valueOf(data.get("semId"))),
                Integer.parseInt(String.valueOf(data.get("courseId"))),
                Integer.parseInt(String.valueOf(data.get("id"))),
                -1,
                false,
                DownloadStatus.NONE.ordinal(),
                false
        );

    }

}
