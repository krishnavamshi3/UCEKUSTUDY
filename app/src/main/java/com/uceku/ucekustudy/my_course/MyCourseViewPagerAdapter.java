package com.uceku.ucekustudy.my_course;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.uceku.ucekustudy.my_course.mycourse_fragments.notes.CourseNotesFragment;
import com.uceku.ucekustudy.my_course.mycourse_fragments.OnCourseFragmentInteractionListener;

import java.util.ArrayList;
import java.util.List;


public class MyCourseViewPagerAdapter extends FragmentStateAdapter {

    private static final String TAG = MyCourseViewPagerAdapter.class.getSimpleName();

    private int courseId;

    List<FragmentEnum> fragmentEnumList = new ArrayList<>();



    public MyCourseViewPagerAdapter(@NonNull FragmentActivity fragmentActivity,
                                    int courseId,
                                    List<FragmentEnum> fragmentEnumList
    ) {
        super(fragmentActivity);
        this.fragmentEnumList = fragmentEnumList;
        this.courseId = courseId;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment = null;
        switch (fragmentEnumList.get(position)) {
            case NOTE_FRAGMENT:
                fragment = new CourseNotesFragment(courseId);
                break;
            case BOOKS:
                Log.d(TAG, " => CreateFragment BOOKS.");
                break;
            case PREVIOUS_PAPER:
                break;
            default:
                fragment = new CourseNotesFragment(courseId);

        }
        assert fragment != null;
        return fragment;
    }

    @Override
    public int getItemCount() {
        return fragmentEnumList != null ? fragmentEnumList.size() : 0;
    }

}
