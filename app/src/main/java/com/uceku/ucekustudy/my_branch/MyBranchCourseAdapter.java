package com.uceku.ucekustudy.my_branch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.uceku.ucekustudy.R;
import com.uceku.ucekustudy.models.Course;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

public class MyBranchCourseAdapter extends RealmRecyclerViewAdapter<Course, MyBranchCourseAdapter.MyViewHolder> {
    private Context context;
    private OrderedRealmCollection<Course> courseOrderedRealmCollection;
    private CourseItemClickListener courseItemClickListener;

    MyBranchCourseAdapter(Context context, OrderedRealmCollection<Course> courses, CourseItemClickListener courseItemClickListener)  {
        super(context, courses,true);
        this.context = context;
        this.courseOrderedRealmCollection = courses;
        this.courseItemClickListener = courseItemClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView ;

            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.course_view_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        if (courseOrderedRealmCollection != null) holder.onBind(holder);
    }

    @Override
    public int getItemCount() {
        return courseOrderedRealmCollection == null ? 0 : courseOrderedRealmCollection.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView subjectNameTV;
        private TextView attachmentTV;
        private ImageView attachmentIV;
        private View root;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            subjectNameTV = itemView.findViewById(R.id.subject_name_TV);
            attachmentTV = itemView.findViewById(R.id.attachmentTV);
            attachmentIV = itemView.findViewById(R.id.attachmentIV);
            root = itemView.findViewById(R.id.root);
        }

        public void onBind(MyViewHolder holder) {
            attachmentIV.setVisibility(View.GONE);
            attachmentTV.setVisibility(View.GONE);
            int position = holder.getAdapterPosition();
            final Course course = getItem(position);
            if (course != null) {
                subjectNameTV.setText(course.getName());
                root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (courseItemClickListener != null) courseItemClickListener.onClick(course);
                    }
                });
            }

        }

    }
}
