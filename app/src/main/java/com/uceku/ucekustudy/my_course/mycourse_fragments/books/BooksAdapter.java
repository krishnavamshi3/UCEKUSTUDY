package com.uceku.ucekustudy.my_course.mycourse_fragments.books;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.uceku.ucekustudy.R;

public class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.BookHolder> {

    @NonNull
    @Override
    public BookHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.book_item_layout, parent, false);

        return new BookHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull BookHolder holder, int position) {
        holder.onBind(holder);
    }

    @Override
    public int getItemCount() {
        return 5;
    }

    class BookHolder extends RecyclerView.ViewHolder {
        private ImageView bookImageView;
        private ImageButton bookDownloadIB;
        private TextView bookName;

        public BookHolder(@NonNull View itemView) {
            super(itemView);
            bookDownloadIB = itemView.findViewById(R.id.book_download_IB);
            bookName = itemView.findViewById(R.id.book_title_tv);
            bookImageView = itemView.findViewById(R.id.book_iv);
        }

        public void onBind(BookHolder holder) {

        }
    }
}
