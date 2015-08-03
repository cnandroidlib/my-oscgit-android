package com.bill.mytest;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by liaobb on 2015/8/2.
 */
public class ProjectAdapter extends BaseRecycleViewAdapter<Project> {
    private static int TYPE_ITEM = 0;
    private static int TYPE_FOOT = 1;

    public ProjectAdapter(Context context) {
        super(context);
    }

    @Override
    public int getItemViewType(int position) {
        if (position + 1 == getItemCount()) {
            return TYPE_FOOT;
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            return new ProjectViewHolder(LayoutInflater.from(context).inflate(R.layout.recycleview_project_item, parent, false));
        } else {
            return new FootViewHolder(LayoutInflater.from(context).inflate(R.layout.foot_item, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ProjectViewHolder) {
            ProjectViewHolder myViewHolder = (ProjectViewHolder) holder;
            Project project = list.get(position);
            myViewHolder.name.setText(project.getName());
            myViewHolder.age.setText(project.getAge());
        } else if (holder instanceof FootViewHolder) {
            FootViewHolder footViewHolder = (FootViewHolder) holder;
            footViewHolder.hint.setText("loading....");
        }
    }

    class ProjectViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView age;

        public ProjectViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            age = (TextView) itemView.findViewById(R.id.age);
        }
    }

    class FootViewHolder extends RecyclerView.ViewHolder {
        TextView hint;

        public FootViewHolder(View itemView) {
            super(itemView);
            hint = (TextView) itemView.findViewById(R.id.hint);
        }
    }
}
