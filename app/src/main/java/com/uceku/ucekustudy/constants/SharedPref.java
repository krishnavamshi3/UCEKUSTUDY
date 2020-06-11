package com.uceku.ucekustudy.constants;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref {

    public static String spKeyLastSelectedDepartmentId = "selected_Branch_Id_sk";
    public static String spKeyLastSelectedSemesterId = "SELECTED_SEMESTER_ID";


    public static String getSharedPrefFileName() {
        return "com.uceku.ucekustudy.AppSharedPref";
    }


    public static void updateSelectedBranchAndSemesterInfo(Context context, int departmentId, int semesterId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(getSharedPrefFileName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(spKeyLastSelectedSemesterId, semesterId);
        editor.putInt(spKeyLastSelectedDepartmentId, departmentId);
        editor.apply();
    }

    public static void clearSelectedDepartmentValues(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(getSharedPrefFileName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(spKeyLastSelectedSemesterId, -1);
        editor.putInt(spKeyLastSelectedDepartmentId, -1);
        editor.apply();
    }
}
