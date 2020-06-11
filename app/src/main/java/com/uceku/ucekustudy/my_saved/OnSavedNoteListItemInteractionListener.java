package com.uceku.ucekustudy.my_saved;

import com.uceku.ucekustudy.models.NoteOverview;

public interface OnSavedNoteListItemInteractionListener {
    void onNoteItemClick(NoteOverview noteOverview);
    void onNoteItemDeleteClick(NoteOverview noteOverview);
}
