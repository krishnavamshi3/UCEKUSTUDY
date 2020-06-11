package com.uceku.ucekustudy.my_course.mycourse_fragments;

import com.uceku.ucekustudy.models.NoteOverview;

/**
 * This interface must be implemented by activities that contain this
 * fragment to allow an interaction in this fragment to be communicated
 * to the activity and potentially other fragments contained in that
 * activity.
 * <p/>
 * See the Android Training lesson <a href=
 * "http://developer.android.com/training/basics/fragments/communicating.html"
 * >Communicating with Other Fragments</a> for more information.
 */
public interface OnCourseFragmentInteractionListener {

    void onNoteItemViewClick(NoteOverview noteOverview);

    void onNoteStarredClick(NoteOverview noteOverview);

    void onPreviousPaperItemViewClick(NoteOverview noteOverview);

    void onPreviousPaperStarredClick(NoteOverview noteOverview);

    void onBookItemViewClick(NoteOverview noteOverview);

    void onBookStarredClick(NoteOverview noteOverview);

}
