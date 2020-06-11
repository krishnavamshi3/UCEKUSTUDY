package com.uceku.ucekustudy;

import android.app.Activity;
import android.content.Intent;

import com.uceku.ucekustudy.my_course_content.MyCourseContentActivity;
import com.uceku.ucekustudy.my_department_selector.DepartmentSelectorActivity;
import com.uceku.ucekustudy.constants.IntentConstants;
import com.uceku.ucekustudy.my_branch.MyBranchActivity;
import com.uceku.ucekustudy.my_course.CourseActivity;
import com.uceku.ucekustudy.my_saved.MySavedActivity;
import com.uceku.ucekustudy.utility.DocType;

public class Routes {
    public static void routeMainActivity(Activity activity) {
        activity.startActivity(new Intent(activity, DepartmentSelectorActivity.class));
    }

    public static void routeSelectedBranchActivity(Activity baseContext, int selectedDepartmentId, int selectedSemesterId) {
        if (selectedDepartmentId == -1) {
            throw new RuntimeException("You are trying to navigate to SelectedBranchActivity. " +
                    "But the branch id is invalid");
        }
        Intent intent = new Intent(baseContext, MyBranchActivity.class);
        intent.putExtra(IntentConstants.DEPARTMENT_ID, selectedDepartmentId);
        intent.putExtra(IntentConstants.SEMESTER_ID, selectedSemesterId);
        baseContext.startActivity(intent);
    }

    public static void routeSubjectViewActivity(Activity activity, int courseId) {
        Intent intent = new Intent(activity, CourseActivity.class);
        intent.putExtra(IntentConstants.COURSE_ID, courseId);
        activity.startActivity(intent);
    }

    public static void startMyCourseContentActivity(Activity activity, DocType docType, int contentTypeOrdinal, int contentID) {
        Intent intent = new Intent(activity, MyCourseContentActivity.class);
        intent.putExtra(IntentConstants.DOCTYPE, docType);
        intent.putExtra(IntentConstants.CONTENTTYPE, contentTypeOrdinal);
        intent.putExtra(IntentConstants.CONTENTID, contentID);
        activity.startActivity(intent);
    }

    public static void routeSavedActivity(Activity activity) {
        activity.startActivity(new Intent(activity, MySavedActivity.class));
    }

    public static void routeDepartmentSelectorActivity(Activity activity) {
        activity.startActivity(new Intent(activity, DepartmentSelectorActivity.class));
    }
}
