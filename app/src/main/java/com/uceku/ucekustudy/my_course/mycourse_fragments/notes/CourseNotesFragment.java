package com.uceku.ucekustudy.my_course.mycourse_fragments.notes;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.uceku.ucekustudy.Routes;
import com.uceku.ucekustudy.models.CourseContentType;
import com.uceku.ucekustudy.models.Syllabus;
import com.uceku.ucekustudy.network.NetworkConfig;
import com.uceku.ucekustudy.R;
import com.uceku.ucekustudy.firebase.Firestore;
import com.uceku.ucekustudy.models.NoteOverview;
import com.uceku.ucekustudy.my_course.mycourse_fragments.OnCourseFragmentInteractionListener;
import com.uceku.ucekustudy.realm_db.Reads;
import com.uceku.ucekustudy.realm_db.Writes;
import com.uceku.ucekustudy.utility.DocType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nonnull;

import io.realm.Realm;
import io.realm.RealmResults;


/**
 * A fragment representing a list of Notes Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnCourseFragmentInteractionListener}
 * interface.
 */
public class CourseNotesFragment extends Fragment implements Connectable, Disconnectable, Bindable, OnNoteListItemInteractionListener {

    private static final String TAG = CourseNotesFragment.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private ImageView infoImageView;
    private TextView infoTextView;
    private RelativeLayout infoRootLayout;

    private int mCourseId;
    private RealmResults<NoteOverview> mNoteOverviewRealmResults;

    private Merlin merlin;
    private NetworkStatus mNetworkStatus;
    private Realm realm;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CourseNotesFragment() {
    }

    public CourseNotesFragment(int courseId) {
        this.mCourseId = courseId;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course_notes_list, container, false);
        mRecyclerView = view.findViewById(R.id.notes_recycler_view);
        mRecyclerView.setAdapter(new MyCourseNotesRecyclerViewAdapter(getContext(), null, this));

        infoImageView = view.findViewById(R.id.no_sem_icon_iv);
        infoTextView = view.findViewById(R.id.no_sem_text_tv);

        infoRootLayout = view.findViewById(R.id.no_recyclerview_rl);

        return view;
    }

    @Override
    public void onAttach(@Nonnull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onStart() {
        super.onStart();
        merlin = new Merlin.Builder().withAllCallbacks().build(getContext());
        merlin.registerConnectable(this);
        merlin.registerDisconnectable(this);
        merlin.registerBindable(this);
        merlin.bind();

        realm = Realm.getDefaultInstance();

        // fetch from server if noteOverview is empty in database.
        loadNotes(mCourseId, false);

    }

    @Override
    public void onStop() {
        super.onStop();
        if (mNoteOverviewRealmResults != null) mNoteOverviewRealmResults.removeAllChangeListeners();
        realm.close();
        merlin.unbind();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.actions_branch_screen, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.action_refresh) {
            onRefresh();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnect() {
        mNetworkStatus = NetworkStatus.newAvailableInstance();
        NetworkConfig.setNetworkConnected(mNetworkStatus.isAvailable());
    }

    @Override
    public void onDisconnect() {
        mNetworkStatus = NetworkStatus.newUnavailableInstance();
        NetworkConfig.setNetworkConnected(mNetworkStatus.isAvailable());
    }

    @Override
    public void onBind(NetworkStatus networkStatus) {
        mNetworkStatus = networkStatus;
    }

    private void loadNotes(int courseId, boolean forceFetchFromServer) {
        showFetchingFromFireStoreOnUI();
        final boolean isNotesDBEmpty = Reads.isNotesDBEmpty(realm, courseId);

        if (forceFetchFromServer || isNotesDBEmpty) {
            boolean isNetworkConnected = NetworkConfig.isNetworkConnected();
            if (!isNetworkConnected && isNotesDBEmpty) {
                showNetworkIssueOnUI();
                return;
            } else if (!isNetworkConnected) {
                Toast.makeText(getContext(), R.string.please_check_network_message, Toast.LENGTH_SHORT).show();
                return;
            }

            showFetchingFromFireStoreOnUI();
            fetchNotesFromServerAndStoreInDB(new NotesFetchListener() {
                @Override
                public void onSuccess() {
                    showNotesOverviewOnUI();
                    updateNotesRecyclerView();
                }

                @Override
                public void onFailure() {
                    if (isNotesDBEmpty) showErrorFetchingCoursesFromFireStoreOnUI();
                    else
                        Toast.makeText(getContext(), R.string.error_fetching_notes_from_server_info, Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            showNotesOverviewOnUI();
            updateNotesRecyclerView();
        }
    }

    private void updateNotesRecyclerView() {
        mNoteOverviewRealmResults = realm.where(NoteOverview.class).equalTo("courseId", mCourseId).findAll();
        mRecyclerView.setAdapter(new MyCourseNotesRecyclerViewAdapter(getContext(), mNoteOverviewRealmResults, this));
    }

    private void fetchNotesFromServerAndStoreInDB(final NotesFetchListener notesFetchListener) {

        Firestore.getAllNotesCollectionQuery()
                .whereEqualTo("courseId", String.valueOf(mCourseId))
                .get()
                .addOnCompleteListener(
                        new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    List<NoteOverview> noteOverviews = new ArrayList<>();

                                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                        noteOverviews.add(NoteOverview.newNoteOverviewObject(document.getData()));
                                    }

                                    Writes.insertOrUpdateNoteOverviewInDB(noteOverviews, new Realm.Callback() {
                                        @Override
                                        public void onSuccess(@NonNull Realm realm) {
                                            if (notesFetchListener != null)
                                                notesFetchListener.onSuccess();
                                        }

                                        @Override
                                        public void onError(@NonNull Throwable exception) {
                                            super.onError(exception);
                                            if (notesFetchListener != null)
                                                notesFetchListener.onFailure();
                                        }
                                    });
                                } else {
                                    Log.d(TAG, "Error getting Notes: ", task.getException());
                                    if (notesFetchListener != null) notesFetchListener.onFailure();
                                }
                            }
                        }
                );

    }

    // Info methods

    private void showFetchingFromFireStoreOnUI() {
        infoRootLayout.setVisibility(View.VISIBLE);
        infoImageView.setImageResource(R.drawable.ic_linear_scale_primarycolor_24dp);
        infoTextView.setText(R.string.fetching_courses_info_msg);

        mRecyclerView.setVisibility(View.GONE);
    }

    private void showNotesOverviewOnUI() {
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

    // User Actions
    private void onRefresh() {
        loadNotes(mCourseId, true);
        refreshCourseSyllabus(mCourseId);
    }

    private void refreshCourseSyllabus(int mCourseId) {
        Toast.makeText(getContext(), "Updating course syllabus...", Toast.LENGTH_SHORT).show();
        Firestore.getSyllabusQuerySnapshot(mCourseId)
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull final Task<QuerySnapshot> task) {

                        new Handler().post(new Runnable() {
                            @Override
                            public void run() {
                                final List<Syllabus> _syllabusList = new ArrayList<>();
                                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                    _syllabusList.add(Syllabus.newSyllabusObject(document.getData()));
                                }
                                Writes.insertOrUpdateSyllabusInDB(_syllabusList, new Realm.Callback() {
                                    @Override
                                    public void onSuccess(@NonNull Realm realm) {
                                        Toast.makeText(getContext(), "Course syllabus updated!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });

                    }
                });
    }


    @Override
    public void onNoteItemClick(NoteOverview noteOverview) {
        NoteOverview noteUnManaged = realm.copyFromRealm(noteOverview);
        Routes.startMyCourseContentActivity(getActivity(), DocType.PDF, CourseContentType.NOTES.ordinal(), noteUnManaged.getId());

    }

    @Override
    public void onNoteItemStarredClick(final NoteOverview noteOverview) {
        if (realm == null) return;
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@Nonnull Realm realm) {

                noteOverview.setStarred(!noteOverview.isStarred());
                realm.insertOrUpdate(noteOverview);
            }
        });
    }

}
