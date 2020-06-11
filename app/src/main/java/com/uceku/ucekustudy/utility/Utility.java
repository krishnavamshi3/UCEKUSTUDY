package com.uceku.ucekustudy.utility;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Utility {

    String pdfUri = "https://www.iitm.ac.in/sites/default/files/notices/application_format_for_institute_pdf.pdf";


    // This must be public. Do not change the  accessor.
    public static final String CSE = "Computer Science and Engineering";
    public static final String EEE = "Electrical and Electronics Engineering";
    public static final String MIN = "Mining Engineering";
    public static final String ECE = "Electronics and Communications Engineering";
    public static final String IT = "Information Technology";
    public static Map<String, String> branchFullShortNameMap = new HashMap<>();

    static {
        Utility.branchFullShortNameMap.put(Utility.CSE, "CSE");
        Utility.branchFullShortNameMap.put(Utility.EEE, "EEE");
        Utility.branchFullShortNameMap.put(Utility.MIN, "MIN");
        Utility.branchFullShortNameMap.put(Utility.ECE, "ECE");
        Utility.branchFullShortNameMap.put(Utility.IT, "IT");
    }

    public static boolean isActivityForIntentAvailable(Context context, Intent intent) {
        final PackageManager packageManager = context.getPackageManager();
        List list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }


}
