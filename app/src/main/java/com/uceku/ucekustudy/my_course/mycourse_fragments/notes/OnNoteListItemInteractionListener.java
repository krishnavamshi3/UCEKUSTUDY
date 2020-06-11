package com.uceku.ucekustudy.my_course.mycourse_fragments.notes;

import com.uceku.ucekustudy.models.NoteOverview;

public interface OnNoteListItemInteractionListener {
    void onNoteItemClick(NoteOverview noteOverview);
    void onNoteItemStarredClick(NoteOverview noteOverview);
}
