package com.uceku.ucekustudy.my_course;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.uceku.ucekustudy.R;
import com.uceku.ucekustudy.models.PreviousPaper;

import java.util.ArrayList;
import java.util.List;

public class PreviousPaperAdapter extends RecyclerView.Adapter<PreviousPaperAdapter.PreviousPaperHolder> {

    List<PreviousPaper> previousPaperList = new ArrayList<>();

    public PreviousPaperAdapter(List<PreviousPaper> previousPaperList) {
        this.previousPaperList = previousPaperList;
    }

    @NonNull
    @Override
    public PreviousPaperHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.previous_paper_row, parent, false);

        return new PreviousPaperHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PreviousPaperHolder holder, int position) {
        holder.onBind(holder);
    }

    @Override
    public int getItemCount() {
        return 5;
    }

    class PreviousPaperHolder extends RecyclerView.ViewHolder {

        private TextView paperNameTV;
        private ImageButton paperViewIB, paperDownloadIB;

        public PreviousPaperHolder(@NonNull View itemView) {
            super(itemView);
            paperNameTV = itemView.findViewById(R.id.previous_paper_name_tv);
            paperViewIB = itemView.findViewById(R.id.previous_paper_view_IB);
            paperDownloadIB = itemView.findViewById(R.id.previous_paper_download_IB);
        }

        public void onBind(PreviousPaperHolder holder) {
//            PreviousPaper pp = previousPaperList.get(holder.getAdapterPosition());
//            paperNameTV.setText(String.format("%s - %s", pp.getSubject(), pp.getYear()));
            paperNameTV.setText("Operating System - 1996 " + " (Revaluation) ");
        }
    }
}
