package com.uceku.ucekustudy.my_course;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.uceku.ucekustudy.R;
import com.uceku.ucekustudy.Routes;
import com.uceku.ucekustudy.constants.IntentConstants;
import com.uceku.ucekustudy.firebase.Firestore;
import com.uceku.ucekustudy.models.Course;
import com.uceku.ucekustudy.models.CourseContentType;
import com.uceku.ucekustudy.models.Syllabus;
import com.uceku.ucekustudy.realm_db.Reads;
import com.uceku.ucekustudy.realm_db.Writes;
import com.uceku.ucekustudy.utility.Contract;
import com.uceku.ucekustudy.utility.DocType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.realm.Realm;

public class CourseActivity extends AppCompatActivity {

    private int mCourseId;

    private ViewPager2 mViewPager;
    private MyCourseViewPagerAdapter mViewPagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mycourse);

        // getIntent in case this activity is called from other Source
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(IntentConstants.COURSE_ID)) {
            mCourseId = intent.getIntExtra(IntentConstants.COURSE_ID, -1);
        } else if (savedInstanceState != null) {
            mCourseId = savedInstanceState.getInt(IntentConstants.COURSE_ID, -1);
        }


        //getting the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);

        //placing toolbar in place of actionbar
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mViewPager = findViewById(R.id.pager);
        mViewPagerAdapter = new MyCourseViewPagerAdapter(this, mCourseId, Contract.getFragmentEnums());
        mViewPager.setAdapter(mViewPagerAdapter);
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        new TabLayoutMediator(tabLayout, mViewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(Contract.getFragmentName(position));
            }
        }
        ).attach();


    }

    @Override
    protected void onStart() {
        super.onStart();
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                try (Realm realm = Realm.getDefaultInstance()) {
                    Course course = realm.where(Course.class).equalTo("id", mCourseId).findFirst();
                    if (getSupportActionBar() != null && course != null)
                        getSupportActionBar().setTitle(course.getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onFullSyllabusViewClick(View view) {
         Syllabus mSyllabus = Reads.getCourseSyllabus(mCourseId);
         if (mSyllabus == null) {
             final Snackbar bar = Snackbar.make(findViewById(R.id.fullSyllabusviewIconIB), "fetching syllabus...", Snackbar.LENGTH_INDEFINITE);
             bar.show();
             Firestore.getSyllabusQuerySnapshot(mCourseId)
                     .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                         @Override
                         public void onComplete(@NonNull Task<QuerySnapshot> task) {
                             List<Syllabus> _syllabusList = new ArrayList<>();
                             for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                 _syllabusList.add(Syllabus.newSyllabusObject(document.getData()));
                             }
                             Writes.insertOrUpdateSyllabusInDB(_syllabusList, new Realm.Callback() {
                                 @Override
                                 public void onSuccess(@NonNull Realm realm) {
                                     bar.dismiss();
                                     viewSyllabus();
                                 }
                             });
                         }
                     });
         } else {
             viewSyllabus();
         }

    }

    public void viewSyllabus() {
        Syllabus syllabus = Reads.getCourseSyllabus(mCourseId);
        if (syllabus == null) {
            Toast.makeText(this, "couldn't load syllabus. try again after some time.", Toast.LENGTH_SHORT).show();
        } else {
            Routes.startMyCourseContentActivity(CourseActivity.this, DocType.PDF, CourseContentType.SYLLABUS.ordinal(), syllabus.getId());

        }
    }



    public void choosePDFOpenIntent(String url) {
        Intent target = new Intent(Intent.ACTION_VIEW);
        target.setDataAndType(Uri.parse(url), "application/pdf");
        target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        Intent intent = Intent.createChooser(target, "Open File");
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // Instruct the user to install a PDF reader here, or something
            Toast.makeText(this, "There is no app to open pdf! Please install one.", Toast.LENGTH_SHORT).show();
        }
    }

}
