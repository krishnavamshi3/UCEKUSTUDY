package com.uceku.ucekustudy.my_saved;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.uceku.ucekustudy.R;
import com.uceku.ucekustudy.Routes;
import com.uceku.ucekustudy.models.CourseContentType;
import com.uceku.ucekustudy.models.NoteOverview;
import com.uceku.ucekustudy.my_course.mycourse_fragments.OnCourseFragmentInteractionListener;
import com.uceku.ucekustudy.my_course.mycourse_fragments.notes.OnNoteListItemInteractionListener;
import com.uceku.ucekustudy.realm_db.Reads;
import com.uceku.ucekustudy.utility.DocType;

import javax.annotation.Nonnull;

import io.realm.Realm;
import io.realm.RealmResults;


/**
 * A fragment representing a list of Notes Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnCourseFragmentInteractionListener}
 * interface.
 */
public class MySavedNotesFragment extends Fragment implements OnSavedNoteListItemInteractionListener {

    private static final String TAG = MySavedNotesFragment.class.getSimpleName();

    private RecyclerView mRecyclerView;

    private RealmResults<NoteOverview> mNoteOverviewRealmResults;

    private Realm realm;

    private ImageView infoImageView;
    private TextView infoTextView;
    private RelativeLayout infoRootLayout;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MySavedNotesFragment() {
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
        mRecyclerView.setAdapter(new MySavedNotesRVAdapter(getContext(), null, this));

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

        realm = Realm.getDefaultInstance();

        mNoteOverviewRealmResults = Reads.getAllStarredNotes(realm);
        if (mNoteOverviewRealmResults == null || mNoteOverviewRealmResults.isEmpty()) {
            showNoSavedContentOnUI();
        } else {
            showSavedContentOnUI();
            mRecyclerView.setAdapter(new MySavedNotesRVAdapter(getContext(), mNoteOverviewRealmResults, this));

        }

    }

    @Override
    public void onStop() {
        super.onStop();
        if (mNoteOverviewRealmResults != null) mNoteOverviewRealmResults.removeAllChangeListeners();
        realm.close();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public void onNoteItemClick(NoteOverview noteOverview) {
        NoteOverview noteUnManaged = realm.copyFromRealm(noteOverview);
        Routes.startMyCourseContentActivity(getActivity(), DocType.PDF, CourseContentType.NOTES.ordinal(), noteUnManaged.getId());

    }

    @Override
    public void onNoteItemDeleteClick(NoteOverview noteOverview) {
        final NoteOverview noteUnManaged = realm.copyFromRealm(noteOverview);
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                noteUnManaged.setStarred(!noteUnManaged.isStarred());
                realm.insertOrUpdate(noteUnManaged);
            }
        });
    }

    private void showSavedContentOnUI() {
        mRecyclerView.setVisibility(View.VISIBLE);
        infoRootLayout.setVisibility(View.GONE);
    }

    private void showNoSavedContentOnUI() {
        infoRootLayout.setVisibility(View.VISIBLE);
        infoImageView.setImageResource(R.drawable.ic_error_outline_black_24dp);
        infoTextView.setText("Couldn't find a starred note.\nTo have a quick view, click on star icon on Notes.");


        mRecyclerView.setVisibility(View.GONE);
    }

}
