package com.uceku.ucekustudy.my_branch;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.novoda.merlin.Bindable;
import com.novoda.merlin.Connectable;
import com.novoda.merlin.Disconnectable;
import com.novoda.merlin.Merlin;
import com.novoda.merlin.NetworkStatus;
import com.uceku.ucekustudy.R;
import com.uceku.ucekustudy.Routes;
import com.uceku.ucekustudy.constants.IntentConstants;
import com.uceku.ucekustudy.constants.SharedPref;
import com.uceku.ucekustudy.firebase.Firestore;
import com.uceku.ucekustudy.models.Course;
import com.uceku.ucekustudy.models.Semester;
import com.uceku.ucekustudy.realm_db.Reads;
import com.uceku.ucekustudy.realm_db.Writes;
import com.uceku.ucekustudy.utility.Contract;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Menu option to the left : select semesters, CSE blog, options to change defaults or settings.
 * Activity to show the overview of the selected Department.
 * <p>
 * Workflow :
 * Select the semester from Menu and load the courses from firestore with semester and branch Id's.
 * loading the courses :
 * load from realm database. if empty : fetch from firestore.
 * * Realm database automatically show the records on UI
 * Error Messages :
 * On Internet Failure while fetching - activity will show as no internet info message.
 * On No Records Found - activity will show as no records found message.
 * OnStop of Activity :
 * close all the instances of the resources used. eg : realm, firestore, recyclerview etc...
 */
public class MyBranchActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, Connectable, Disconnectable, Bindable, CourseItemClickListener {
    private static final String TAG = MyBranchActivity.class.getSimpleName();
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private TextView semesterTV;
    private RecyclerView mRecyclerView;
    private MyBranchCourseAdapter myBranchCourseAdapter;
    private RelativeLayout infoRootLayout;
    private RealmResults courseRealmCollection;
    Merlin merlin;
    NetworkStatus mNetworkStatus;
    Realm realm;
    private int departmentId = -1;
    private int semesterId = -1;
    private ImageView infoImageView;
    private TextView infoTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_branch);
        merlin = new Merlin.Builder().withAllCallbacks().build(getBaseContext());
        merlin.registerConnectable(this);
        merlin.registerDisconnectable(this);
        merlin.registerBindable(this);
        merlin.bind();

        // getIntent in case this activity is called from other Source
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(IntentConstants.DEPARTMENT_ID)) {
            departmentId = intent.getIntExtra(IntentConstants.DEPARTMENT_ID, -1);
            semesterId = intent.getIntExtra(IntentConstants.SEMESTER_ID, -1);
        } else if (savedInstanceState != null) {
            departmentId = savedInstanceState.getInt("DEPARTMENT_ID", -1);
            semesterId = savedInstanceState.getInt("SEMESTER_ID", -1);
        }

        infoRootLayout = findViewById(R.id.no_recyclerview_rl);
        infoImageView = findViewById(R.id.no_sem_icon_iv);
        infoTextView = findViewById(R.id.no_sem_text_tv);

        semesterTV = findViewById(R.id.sem_header_TV);
        mRecyclerView = findViewById(R.id.sem_subjects_rv);

    }

    @Override
    protected void onStart() {
        super.onStart();

        realm = Realm.getDefaultInstance();

        // Set Toolbar.
        setToolbar();

        setSideBar();

        setRecyclerView();

        if (semesterId == -1) {
            showSelectSemesterMsgOnUI();
        } else {
            loadSemesterOverview(semesterId, false);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!realm.isClosed()) realm.close();
        myBranchCourseAdapter = null;
        courseRealmCollection = null;

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("DEPARTMENT_ID", departmentId);
        outState.putInt("SEMESTER_ID", semesterId);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        merlin.unbind();
    }

    @Override
    public void onClick(Course course) {
        Routes.routeSubjectViewActivity(MyBranchActivity.this, course.getId());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.actions_branch_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        } else if (item.getItemId() == R.id.action_refresh) {
            onRefresh();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.nav_sem1 || itemId == R.id.nav_sem2 || itemId == R.id.nav_sem3 ||
                itemId == R.id.nav_sem4
                //|| itemId == R.id.nav_sem5 || itemId == R.id.nav_sem6 ||
                //itemId == R.id.nav_sem7 || itemId == R.id.nav_sem8
        ) {
            onSelectSemesterFromNav(item.getTitle().toString().toLowerCase());

        } else if (itemId == R.id.nav_saved) {
            onMySavedSelectedAction();
        } else if (itemId == R.id.nav_change_branch) {
            onChangeDepartment();
        } else if (itemId == R.id.nav_contact_us) {
            onSendMail();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void onChangeDepartment() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                SharedPref.clearSelectedDepartmentValues(MyBranchActivity.this.getBaseContext());
                finish();
                Routes.routeDepartmentSelectorActivity(MyBranchActivity.this);
            }
        });
    }

    private void onMySavedSelectedAction() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                Routes.routeSavedActivity(MyBranchActivity.this);
            }
        });

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onConnect() {
        mNetworkStatus = NetworkStatus.newAvailableInstance();
    }

    @Override
    public void onDisconnect() {
        mNetworkStatus = NetworkStatus.newUnavailableInstance();
    }

    @Override
    public void onBind(NetworkStatus networkStatus) {
        mNetworkStatus = networkStatus;
    }


    // Setting Data on UI or UI related Logic.
    private void setToolbar() {
        //getting the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);

        //placing toolbar in place of actionbar
        setSupportActionBar(toolbar);

        // Set Title
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                Realm realm = Realm.getDefaultInstance();
                String departmentName = Reads.getDepartmentNameFromId(departmentId, realm);
                realm.close();
                if (getSupportActionBar() != null) getSupportActionBar().setTitle(departmentName);
            }
        });
    }

    /**
     * Setting SideBar.
     */
    private void setSideBar() {
        drawerLayout = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.openDrawerContentDescRes, R.string.closeDrawerContentDescRes);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);


    }

    private void setRecyclerView() {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(), DividerItemDecoration.VERTICAL));
        myBranchCourseAdapter = new MyBranchCourseAdapter(this.getBaseContext(), null, this);
        mRecyclerView.setAdapter(myBranchCourseAdapter);
    }

    private void setSemesterNameForSemId(final int semesterId) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                Realm realm = Realm.getDefaultInstance();
                String semesterName = Reads.getSemesterNameFromId(semesterId, realm);
                realm.close();
                semesterTV.setText(semesterName);
            }
        });
    }

    private void showSelectSemesterMsgOnUI() {
        infoRootLayout.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
        infoImageView.setImageResource(R.drawable.ic_graduates_pale);
        infoTextView.setText(R.string.select_semester_info_msg);
    }

    private void showFetchingFromFireStoreOnUI() {
        infoRootLayout.setVisibility(View.VISIBLE);
        infoImageView.setImageResource(R.drawable.ic_linear_scale_primarycolor_24dp);
        infoTextView.setText(R.string.fetching_courses_info_msg);

        mRecyclerView.setVisibility(View.GONE);
    }

    private void showSemesterOverviewOnUI() {
        setSemesterNameForSemId(semesterId);
        mRecyclerView.setVisibility(View.VISIBLE);
        infoRootLayout.setVisibility(View.GONE);
    }

    private void showErrorFetchingCoursesFromFireStoreOnUI() {
        infoRootLayout.setVisibility(View.VISIBLE);
        infoImageView.setImageResource(R.drawable.ic_error_outline_black_24dp);
        infoTextView.setText(R.string.error_fetching_departments_from_server_info);


        mRecyclerView.setVisibility(View.GONE);
    }

    private void showNetworkIssueOnUI() {
        infoRootLayout.setVisibility(View.VISIBLE);
        infoImageView.setImageResource(R.drawable.ic_portable_wifi_off_black_24dp);
        infoTextView.setText(R.string.please_check_network_message);

        mRecyclerView.setVisibility(View.GONE);
    }

    private void updateSemesterOnUI() {
        showSemesterOverviewOnUI();
        courseRealmCollection = null;
        courseRealmCollection = Reads.getQueryForAllCourses(departmentId, semesterId, realm).findAll();
        myBranchCourseAdapter = new MyBranchCourseAdapter(getBaseContext(), (OrderedRealmCollection<Course>) courseRealmCollection, this);
        mRecyclerView.setAdapter(myBranchCourseAdapter);
    }

    // Setting Data on UI or UI related Logic.


    // Functionality

    private void loadSemesterOverview(final int semesterId, final boolean forceFetchFromServer) {
        if (semesterId == -1) {
            showSelectSemesterMsgOnUI();
            return;
        }
        final boolean isSemesterDBEmpty = Reads.isSemesterDBEmpty(realm, semesterId);
        final boolean isCourseDBEmptyForBranchAndSem = Reads.isCoursesEmptyForDepartmentAndSemester(getBaseContext(), departmentId, semesterId, realm);
        if (forceFetchFromServer || isSemesterDBEmpty || isCourseDBEmptyForBranchAndSem) {
            if ((mNetworkStatus == null || !mNetworkStatus.isAvailable()) && isSemesterDBEmpty) {
                showNetworkIssueOnUI();
                return;
            } else if ((mNetworkStatus == null || !mNetworkStatus.isAvailable())
                    && !isSemesterDBEmpty && !isCourseDBEmptyForBranchAndSem) {
                Toast.makeText(this, R.string.please_check_network_message, Toast.LENGTH_SHORT).show();
                return;
            }

            showFetchingFromFireStoreOnUI();
            fetchSemestersFromFireBaseAndStoreInDB(new SemesterFetchListener() {
                @Override
                public void onSuccess() {
                    setSemesterNameForSemId(semesterId);
                    loadCoursesForSemester(semesterId, forceFetchFromServer, new CourseFetchListener() {
                        @Override
                        public void onSuccess() {
                            updateSemesterOnUI();

                        }

                        @Override
                        public void onFailure(Exception e) {
                            if (isCourseDBEmptyForBranchAndSem)
                                showErrorFetchingCoursesFromFireStoreOnUI();
                            else
                                Toast.makeText(MyBranchActivity.this, R.string.error_fetching_courses_from_server_info, Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onFailure(Exception e) {
                    if (isSemesterDBEmpty && isCourseDBEmptyForBranchAndSem)
                        showErrorFetchingCoursesFromFireStoreOnUI();
                    else
                        Toast.makeText(MyBranchActivity.this, R.string.error_fetching_departments_from_server_info, Toast.LENGTH_SHORT).show();

                }
            });
        } else {
            updateSemesterOnUI();
        }
    }


    private void updateSelectedBranchAndSemesterInfo() {
        SharedPref.updateSelectedBranchAndSemesterInfo(getBaseContext(), departmentId, semesterId);
    }


    private void loadCoursesForSemester(int semesterId, boolean forceFetchFromServer, final CourseFetchListener courseFetchListener) {
        final boolean isCoursesDBEmptyForSemester = Reads.isCoursesEmptyForDepartmentAndSemester(getBaseContext(), departmentId, semesterId, realm);
        if (forceFetchFromServer || isCoursesDBEmptyForSemester) {
            if ((mNetworkStatus == null || !mNetworkStatus.isAvailable()) && isCoursesDBEmptyForSemester) {
                showNetworkIssueOnUI();
                return;
            } else if ((mNetworkStatus == null || !mNetworkStatus.isAvailable()) && !isCoursesDBEmptyForSemester) {
                Toast.makeText(this, R.string.please_check_network_message, Toast.LENGTH_SHORT).show();
                return;
            }

            showFetchingFromFireStoreOnUI();
            fetchCoursesFromServerAndStoreInDB(
                    semesterId,
                    new CourseFetchListener() {
                        @Override
                        public void onSuccess() {
                            if (courseFetchListener != null) courseFetchListener.onSuccess();
                        }

                        @Override
                        public void onFailure(Exception e) {
                            if (courseFetchListener != null)
                                courseFetchListener.onFailure(new Exception());
                        }
                    }
            );
        }

    }


    private void fetchCoursesFromServerAndStoreInDB(int semesterId, final CourseFetchListener courseFetchListener) {
        Firestore.getAllCoursesQuerySnapshot(departmentId, semesterId)
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Course> courseList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                courseList.add(Course.newCourseObject(document.getData()));
                            }
                            Writes.insertOrUpdateCoursesInDB(courseList, new Realm.Callback() {
                                @Override
                                public void onSuccess(@NonNull Realm realm) {
                                    if (courseFetchListener != null)
                                        courseFetchListener.onSuccess();
                                }

                                @Override
                                public void onError(@NonNull Throwable exception) {
                                    super.onError(exception);
                                    if (courseFetchListener != null)
                                        courseFetchListener.onFailure(new Exception());

                                }
                            });
                        } else {
                            if (courseFetchListener != null)
                                courseFetchListener.onFailure(new Exception());
                        }

                    }
                });


    }

    private void fetchSemestersFromFireBaseAndStoreInDB(final SemesterFetchListener listener) {
        Firestore.getAllSemesterQuery()
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Semester> semesters = new ArrayList<>();
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                semesters.add(Semester.newSemesterObject(document.getData()));
                            }

                            Writes.insertOrUpdateSemestersInDB(semesters, new Realm.Callback() {
                                @Override
                                public void onSuccess(@NonNull Realm realm) {
                                    if (listener != null) listener.onSuccess();
                                }

                                @Override
                                public void onError(@NonNull Throwable exception) {
                                    super.onError(exception);
                                    if (listener != null) listener.onFailure(new Exception());
                                }
                            });
                        } else {
                            Log.d(TAG, "Error getting Departments: ", task.getException());
                            if (listener != null) listener.onFailure(new Exception());
                        }
                    }
                });
    }


    // User Actions
    private void onRefresh() {
        loadSemesterOverview(semesterId, true);
    }

    private void onSelectSemesterFromNav(String navItemTitle) {
        semesterId = Contract.getContractSemIdForMenuSem(navItemTitle);
        updateSelectedBranchAndSemesterInfo();
        loadSemesterOverview(semesterId, false);
    }

    private void onSendMail() {

        try {
            Intent send = new Intent(Intent.ACTION_SENDTO);
            String uriText = "mailto:" + Uri.encode("ucekustudy@gmail.com") +
                    "?subject=" + Uri.encode("UCEKUSTUDY Mobile App : ") +
                    "&body=" + Uri.encode("");
            Uri uri = Uri.parse(uriText);

            send.setData(uri);
            startActivity(Intent.createChooser(send, "Send mail..."));
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Couldn't open any mail app.", Toast.LENGTH_SHORT).show();
        }

    }

}
