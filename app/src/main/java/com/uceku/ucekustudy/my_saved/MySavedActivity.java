package com.uceku.ucekustudy.my_saved;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.tabs.TabLayoutMediator;
import com.uceku.ucekustudy.R;
import com.uceku.ucekustudy.my_course.MyCourseViewPagerAdapter;
import com.uceku.ucekustudy.my_saved.ui.main.SectionsPagerAdapter;
import com.uceku.ucekustudy.utility.Contract;

public class MySavedActivity extends AppCompatActivity {

    private ViewPager2 mViewPager;
    private MySavedContentViewPagerAdapter mViewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_saved);

        mViewPager = findViewById(R.id.view_pager);
        mViewPagerAdapter = new MySavedContentViewPagerAdapter(this, Contract.getFragmentEnums());
        mViewPager.setAdapter(mViewPagerAdapter);
        TabLayout tabLayout = findViewById(R.id.tabs);
        new TabLayoutMediator(tabLayout, mViewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(Contract.getFragmentName(position));
            }
        }
        ).attach();


    }

    public void onClickBackButton(View view) {
        onBackPressed();
    }
}