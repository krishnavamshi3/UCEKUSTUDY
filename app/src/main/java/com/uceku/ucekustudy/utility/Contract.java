package com.uceku.ucekustudy.utility;

import com.uceku.ucekustudy.my_course.FragmentEnum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Contract {

    // branch Names
    private static final String CSE = "CSE";
    private static final String EEE = "EEE";
    private static final String ECE = "ECE";

    // branch Ids
    private static final Integer CSE_ID = 0;
    private static final Integer ECE_ID = 1;
    private static final Integer EEE_ID = 2;

    // semester Names
    private static final String SEM1 = "SEM1";
    private static final String SEM2 = "SEM2";
    private static final String SEM3 = "SEM3";
    private static final String SEM4 = "SEM4";
    private static final String SEM5 = "SEM5";
    private static final String SEM6 = "SEM6";
    private static final String SEM7 = "SEM7";
    private static final String SEM8 = "SEM8";

    // semester Ids
    private static final Integer SEM1_ID = 0;
    private static final Integer SEM2_ID = 1;
    private static final Integer SEM3_ID = 2;
    private static final Integer SEM4_ID = 3;
    private static final Integer SEM5_ID = 4;
    private static final Integer SEM6_ID = 5;
    private static final Integer SEM7_ID = 6;
    private static final Integer SEM8_ID = 7;


    // branch_id
    private static final Map<String, Integer> branchIdMap = new HashMap<>();
    private static final Map<String, Integer> semesterIdMap = new HashMap<>();
    private static final Map<String, String> resourceSemToContractSem = new HashMap<>();

    private static final Map<FragmentEnum, String> fragmentNames = new HashMap<>();

    private static final Map<Integer, FragmentEnum> fragmentPositionNameMap = new HashMap<>();

    static {
        branchIdMap.put(CSE, CSE_ID);
        branchIdMap.put(ECE, ECE_ID);
        branchIdMap.put(EEE, EEE_ID);

        semesterIdMap.put(SEM1, SEM1_ID);
        semesterIdMap.put(SEM2, SEM2_ID);
        semesterIdMap.put(SEM3, SEM3_ID);
        semesterIdMap.put(SEM4, SEM4_ID);
        semesterIdMap.put(SEM5, SEM5_ID);
        semesterIdMap.put(SEM6, SEM6_ID);
        semesterIdMap.put(SEM7, SEM7_ID);
        semesterIdMap.put(SEM8, SEM8_ID);

        resourceSemToContractSem.put("sem1", SEM1);
        resourceSemToContractSem.put("sem2", SEM2);
        resourceSemToContractSem.put("sem3", SEM3);
        resourceSemToContractSem.put("sem4", SEM4);
        resourceSemToContractSem.put("sem5", SEM5);
        resourceSemToContractSem.put("sem6", SEM6);
        resourceSemToContractSem.put("sem7", SEM7);
        resourceSemToContractSem.put("sem8", SEM8);

        fragmentPositionNameMap.put(0, FragmentEnum.NOTE_FRAGMENT);
        fragmentPositionNameMap.put(1, FragmentEnum.PREVIOUS_PAPER);
        fragmentPositionNameMap.put(2, FragmentEnum.BOOKS);

        fragmentNames.put(FragmentEnum.NOTE_FRAGMENT, "Notes");
        fragmentNames.put(FragmentEnum.PREVIOUS_PAPER, "Previous Papers");
        fragmentNames.put(FragmentEnum.BOOKS, "Books");
    }


    public static int getBranchIdForBranchName(String branchName) {
        Integer id =  branchIdMap.get(branchName);
        if (id == null) {
            id = -1;
        }
        return id;
    }

    public static int getSemIdForSemName(String semesterName) {
        Integer id =  semesterIdMap.get(semesterName);
        if (id == null) {
            id = -1;
        }
        return id;
    }

    public static String getContractSemValue(String resourceSem) {
        return resourceSemToContractSem.get(resourceSem);
    }

    public static int getContractSemIdForMenuSem(String resourceSem) {
        String semesterName =  resourceSemToContractSem.get(resourceSem);
        if (semesterName == null) return -1;
        return semesterIdMap.get(semesterName);
    }

    public static String getFragmentName(int position) {
        return fragmentNames.get(fragmentPositionNameMap.get(position));
    }

    public static List<FragmentEnum> getFragmentEnums() {
        List<FragmentEnum> values = new ArrayList<FragmentEnum>(fragmentPositionNameMap.values());
        values.clear();
        values.add(FragmentEnum.NOTE_FRAGMENT);
        return values;
    }
}
