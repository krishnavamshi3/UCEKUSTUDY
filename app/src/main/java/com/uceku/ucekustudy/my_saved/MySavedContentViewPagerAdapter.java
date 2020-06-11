package com.uceku.ucekustudy.my_saved;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.uceku.ucekustudy.my_course.FragmentEnum;
import com.uceku.ucekustudy.my_course.mycourse_fragments.notes.CourseNotesFragment;

import java.util.ArrayList;
import java.util.List;


public class MySavedContentViewPagerAdapter extends FragmentStateAdapter {

    private static final String TAG = MySavedContentViewPagerAdapter.class.getSimpleName();


    List<FragmentEnum> fragmentEnumList = new ArrayList<>();



    public MySavedContentViewPagerAdapter(@NonNull FragmentActivity fragmentActivity,
                                          List<FragmentEnum> fragmentEnumList
    ) {
        super(fragmentActivity);
        this.fragmentEnumList = fragmentEnumList;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment = null;
        switch (fragmentEnumList.get(position)) {
            case NOTE_FRAGMENT:
                fragment = new MySavedNotesFragment();
                break;
            case BOOKS:
                Log.d(TAG, " => CreateFragment BOOKS.");
                break;
            case PREVIOUS_PAPER:
                break;
            default:
                fragment = new MySavedNotesFragment();

        }
        assert fragment != null;
        return fragment;
    }

    @Override
    public int getItemCount() {
        return fragmentEnumList != null ? fragmentEnumList.size() : 0;
    }

}
