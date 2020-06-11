package com.uceku.ucekustudy.my_department_selector;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.uceku.ucekustudy.R;
import com.uceku.ucekustudy.models.Department;
import com.uceku.ucekustudy.utility.Utility;

import java.util.ArrayList;
import java.util.List;

public class BranchGridAdapter extends BaseAdapter {

    private List<Department> departmentList = new ArrayList<>();

    public BranchGridAdapter(List<Department> departmentList) {
        this.departmentList = departmentList;
    }

    @Override
    public int getCount() {
        return departmentList != null ? departmentList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return departmentList != null ? departmentList.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.branch_grid_row, parent, false);
        }
        ImageView image = convertView.findViewById(R.id.branch_img_IV);
        TextView name = convertView.findViewById(R.id.branch_name_tv);

        Department department = (Department) getItem(position);
        int img_drawable = 0;
        if (department.getShortName() != null) {
            name.setText(department.getFullName());

            switch (department.getShortName()) {
                case Utility.CSE:
                    img_drawable = R.drawable.ic_graduates_pale;
                    break;
                case Utility.EEE:
                    img_drawable = R.drawable.ic_graduates_pale;
                    break;
                case Utility.ECE:
                    img_drawable = R.drawable.ic_graduates_pale;
                    break;
                case Utility.MIN:
                    img_drawable = R.drawable.ic_graduates_pale;
                    break;
                case Utility.IT:
                    img_drawable = R.drawable.ic_graduates_pale;
                    break;
                default:
                    img_drawable = R.drawable.ic_graduates_pale;
            }
        }

        image.setImageResource(img_drawable);
        return convertView;
    }

    public void updateBranchList(List<Department> departments) {
        if (departments == departmentList) {
            notifyDataSetChanged();
            return;
        }
        departmentList.clear();
        departmentList.addAll(departments);
        notifyDataSetChanged();
    }
}
