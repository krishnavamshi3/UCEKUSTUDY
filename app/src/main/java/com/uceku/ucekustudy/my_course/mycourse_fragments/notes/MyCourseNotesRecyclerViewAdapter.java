package com.uceku.ucekustudy.my_course.mycourse_fragments.notes;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.uceku.ucekustudy.R;
import com.uceku.ucekustudy.models.NoteOverview;
import com.uceku.ucekustudy.my_course.mycourse_fragments.OnCourseFragmentInteractionListener;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

/**
 * {@link RecyclerView.Adapter} that can display a {@link NoteOverview Items} and makes a call to the
 * specified {@link OnCourseFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyCourseNotesRecyclerViewAdapter extends RealmRecyclerViewAdapter<NoteOverview, MyCourseNotesRecyclerViewAdapter.ViewHolder> {

    private final OrderedRealmCollection<NoteOverview> noteOverviewOrderedRealmCollection;
    private final OnNoteListItemInteractionListener mListener;

    MyCourseNotesRecyclerViewAdapter(Context context, OrderedRealmCollection<NoteOverview> noteOverviewOrderedRealmCollection, OnNoteListItemInteractionListener listener) {
        super(context, noteOverviewOrderedRealmCollection,true);
        this.noteOverviewOrderedRealmCollection = noteOverviewOrderedRealmCollection;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.course_notes_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.onBind(holder);
    }

    @Override
    public int getItemCount() {
        return noteOverviewOrderedRealmCollection == null ? 0 : noteOverviewOrderedRealmCollection.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTV;
        private TextView uploaderTV;
        private ImageButton notesStarredBtn;
        private CardView rootView;

        ViewHolder(View view) {
            super(view);
            rootView = view.findViewById(R.id.notes_overview_root);
            titleTV = view.findViewById(R.id.notes_title);
            uploaderTV = view.findViewById(R.id.notes_uploader_name_tv);
            notesStarredBtn = view.findViewById(R.id.notes_starred_ib);
        }

        void onBind(final ViewHolder holder) {
            final NoteOverview noteManaged = getItem(holder.getAdapterPosition());

            if(noteManaged != null) {
                titleTV.setText(noteManaged.getNoteName());
                uploaderTV.setText(noteManaged.getNoteAuthor());
            }


            if (noteManaged != null && noteManaged.isStarred()) {
                notesStarredBtn.setImageResource(R.drawable.ic_star_primary_24dp);
            } else if (noteManaged != null && !noteManaged.isStarred()) {
                notesStarredBtn.setImageResource(R.drawable.ic_star_border_primary_24dp);
            }
            rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) mListener.onNoteItemClick(noteManaged);
                }
            });

            notesStarredBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) mListener.onNoteItemStarredClick(noteManaged);
                }
            });

        }
    }
}
