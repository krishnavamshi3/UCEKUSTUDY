package com.uceku.ucekustudy.my_department_selector;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.novoda.merlin.Bindable;
import com.novoda.merlin.Connectable;
import com.novoda.merlin.Disconnectable;
import com.novoda.merlin.Merlin;
import com.novoda.merlin.NetworkStatus;
import com.uceku.ucekustudy.network.NetworkChangeListener;
import com.uceku.ucekustudy.network.NetworkConfig;
import com.uceku.ucekustudy.R;
import com.uceku.ucekustudy.Routes;
import com.uceku.ucekustudy.constants.SharedPref;
import com.uceku.ucekustudy.firebase.Firestore;
import com.uceku.ucekustudy.models.Department;
import com.uceku.ucekustudy.realm_db.Reads;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

/**
 * Activity to show the Departments to select.
 * Source : Firebase Firestore (Primary) and Realm Local Database (Secondary)
 * Workflow : OnStart Of Activity :
 * Fetch Departments from Realm Database. if No Records exists - Fetch From Firestore and dump in Realm Database.
 * * Realm database automatically show the records on UI but we are updating manually.
 * Error Messages :
 * On Internet Failure while fetching - activity will show as no internet info message.
 * On No Records Found - activity will show as no records found message.
 * OnStop of Activity :
 * close all the instances of the resources used. eg : realm, firestore, recyclerview etc...
 */
public class DepartmentSelectorActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, Connectable, Disconnectable, Bindable {
    private static final String TAG = DepartmentSelectorActivity.class.getSimpleName();
    BranchGridAdapter mGridAdapter;
    GridView mGridView;
    List<Department> departmentList = new ArrayList<>();

    Realm realm;
    LinearLayout infoLinearLayout;
    ImageView infoImageView;
    TextView infoTextView;

    Merlin merlin;
    private NetworkChangeListener networkChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_branch);

        //getting the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);

        //setting the title@string/select_your_branch
        toolbar.setTitle(getString(R.string.select_your_branch));

        //placing toolbar in place of actionbar
        setSupportActionBar(toolbar);

        setBranchGridView();


        infoLinearLayout = findViewById(R.id.info_root_layout);
        infoImageView = findViewById(R.id.infoIV);
        infoTextView = findViewById(R.id.infoTV);

        merlin = new Merlin.Builder().withAllCallbacks().build(getBaseContext());
        merlin.registerConnectable(this);
        merlin.registerDisconnectable(this);
        merlin.registerBindable(this);
        merlin.bind();

    }

    @Override
    protected void onStart() {
        super.onStart();
        realm = Realm.getDefaultInstance();

        networkChangeListener = new NetworkChangeListener();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        this.registerReceiver(networkChangeListener, filter);
        loadDepartments(false);
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.unregisterReceiver(networkChangeListener);
        if (realm != null && !realm.isClosed()) {
            realm.close();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        merlin.unbind();
    }

    public void setBranchGridView() {
        mGridView = findViewById(R.id.gridview);
        departmentList = new ArrayList<>();
        mGridAdapter = new BranchGridAdapter(departmentList);
        mGridView.setAdapter(mGridAdapter);

        mGridView.setOnItemClickListener(this);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPref = getSharedPreferences(SharedPref.getSharedPrefFileName(), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt(SharedPref.spKeyLastSelectedDepartmentId, departmentList.get(position).getBranchId());
                editor.apply();
                Routes.routeSelectedBranchActivity(DepartmentSelectorActivity.this,
                        departmentList.get(position).getBranchId(), -1);
            }
        });
    }

    @Override
    public void onBind(NetworkStatus networkStatus) {
        if (networkStatus.isAvailable()) NetworkConfig.setNetworkConnected(true);
        else NetworkConfig.setNetworkConnected(false);
    }

    @Override
    public void onConnect() {
        NetworkConfig.setNetworkConnected(true);
    }

    @Override
    public void onDisconnect() {
        NetworkConfig.setNetworkConnected(false);
    }

    private void loadDepartments(boolean forceFetchFromServer) {
        final boolean isDepartmentDBEmpty = Reads.isDepartmentEmpty(realm);


        if (forceFetchFromServer || isDepartmentDBEmpty) {
            boolean isNetworkAvailable = NetworkConfig.isNetworkConnected();
            if (!isNetworkAvailable && isDepartmentDBEmpty) {
                showNetworkIssueOnUI();
                return;
            } else if (!isNetworkAvailable) {
                Toast.makeText(this, R.string.please_check_network_message, Toast.LENGTH_SHORT).show();
                return;
            }

            showFetchingFromFireStoreOnUI();
            fetchDepartmentsFromFirebaseAndStoreInDB(new DepartmentFetchAndStoreListener() {
                @Override
                public void onSuccess() {
                    fetchFromDBAndShowDepartmentsOnUI();
                }

                @Override
                public void onFailure(Exception e) {
                    if (isDepartmentDBEmpty) showErrorFetchingDepartmentsFromFireStoreOnUI();
                    else
                        Toast.makeText(DepartmentSelectorActivity.this, R.string.error_fetching_departments_from_server_info, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            fetchFromDBAndShowDepartmentsOnUI();
        }

    }

    private void fetchFromDBAndShowDepartmentsOnUI() {
        showDepartmentsOnUI();
        updateDepartmentList(Reads.fetchAllDepartmentsUnManaged(realm));
        updateGridView(this.departmentList);
    }

    private void updateDepartmentList(List<Department> departmentList) {
        if (this.departmentList == null) this.departmentList = new ArrayList<>();
        this.departmentList.clear();
        this.departmentList.addAll(departmentList);
    }

    private void updateGridView(List<Department> departments) {
        (mGridAdapter).updateBranchList(departments);
    }

    private void fetchDepartmentsFromFirebaseAndStoreInDB(final DepartmentFetchAndStoreListener listener) {

        Firestore.getAllBranchQuery()
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Department> departments = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                departments.add(Department.newDepartmentObject(document.getData()));
                            }

                            insertOrUpdateCoursesInDB(departments, new Realm.Callback() {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actions_branch_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {
            onRefresh();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onRefresh() {
        loadDepartments(true);
    }

    private void showFetchingFromFireStoreOnUI() {
        infoLinearLayout.setVisibility(View.VISIBLE);
        infoImageView.setImageResource(R.drawable.ic_linear_scale_primarycolor_24dp);
        infoTextView.setText(R.string.fetching_departments_info);

        mGridView.setVisibility(View.GONE);
    }

    private void showDepartmentsOnUI() {
        mGridView.setVisibility(View.VISIBLE);
        infoLinearLayout.setVisibility(View.GONE);
    }

    private void showErrorFetchingDepartmentsFromFireStoreOnUI() {
        infoLinearLayout.setVisibility(View.VISIBLE);
        infoImageView.setImageResource(R.drawable.ic_error_outline_black_24dp);
        infoTextView.setText(R.string.error_fetching_departments_from_server_info);

        mGridView.setVisibility(View.GONE);
    }

    private void showNetworkIssueOnUI() {
        infoLinearLayout.setVisibility(View.VISIBLE);
        infoImageView.setImageResource(R.drawable.ic_portable_wifi_off_black_24dp);
        infoTextView.setText(R.string.please_check_network_message);

        mGridView.setVisibility(View.GONE);
    }

    public void insertOrUpdateCoursesInDB(final List<Department> departmentList, final Realm.Callback callback) {
        try (Realm r = Realm.getDefaultInstance()) {
            r.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(@NonNull Realm realm) {
                    realm.insertOrUpdate(departmentList);
                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    callback.onSuccess(r);
                }
            }, new Realm.Transaction.OnError() {
                @Override
                public void onError(Throwable error) {
                    callback.onError(error);
                }
            });
        }
    }

    private interface DepartmentFetchAndStoreListener {
        void onSuccess();

        void onFailure(Exception e);
    }

}
