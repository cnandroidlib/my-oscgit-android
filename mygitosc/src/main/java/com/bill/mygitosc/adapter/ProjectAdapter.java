package com.bill.mygitosc.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.bill.mygitosc.R;
import com.bill.mygitosc.bean.Project;
import com.bill.mygitosc.common.AppContext;
import com.bill.mygitosc.common.BitmapCache;
import com.bill.mygitosc.common.TypefaceUtils;
import com.bill.mygitosc.common.Utils;
import com.bill.mygitosc.ui.activity.ViewProjectInfoActivity;
import com.bill.mygitosc.ui.activity.ViewSelfInfoActivity;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by liaobb on 2015/7/21.
 */
public class ProjectAdapter extends BaseStateRecyclerAdapter<Project> {
    private ImageLoader mImageLoader;
    //private ImageLoader.ImageListener listener;
    private boolean portraitClickable;
    private boolean noPictureMode;
    private RequestQueue mQueue;

    private View.OnClickListener projectClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Project project = (Project) v.getTag();
            Intent intent = new Intent(context, ViewProjectInfoActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(ViewProjectInfoActivity.VIEW_PROJECT_INFO, project);
            intent.putExtras(bundle);
            context.startActivity(intent);
        }
    };

    public ProjectAdapter(Context context) {
        super(context);
        mQueue = Volley.newRequestQueue(context);
        mImageLoader = new ImageLoader(mQueue, new BitmapCache());
        noPictureMode = Utils.checkNoPic(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == BaseStateRecyclerAdapter.TYPE_ITEM) {
            return new ProjectViewHolder(LayoutInflater.from(context).inflate(R.layout.recycleview_project_item, parent, false));
        } else {
            return new FootViewHolder(LayoutInflater.from(context).inflate(R.layout.foot_item, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ProjectViewHolder) {
            ProjectViewHolder projectHolder = (ProjectViewHolder) holder;

            Project project = mData.get(position);

            projectHolder.tv_title.setText(project.getOwner().getName() + " / " + project.getName());
            String description = project.getDescription();
            if (TextUtils.isEmpty(description)) {
                description = context.getString(R.string.no_description_hint);
            }
            projectHolder.tv_description.setText(description);

            TypefaceUtils.setIconText(context, projectHolder.tv_watch, context.getString(R.string.sem_watch) + " " + project.getWatches_count());
            TypefaceUtils.setIconText(context, projectHolder.tv_star, context.getString(R.string.sem_star) + " " + project.getStars_count());
            TypefaceUtils.setIconText(context, projectHolder.tv_fork, context.getString(R.string.sem_fork) + " " + project.getForks_count());

            String language = project.getLanguage();
            if (TextUtils.isEmpty(language)) {
                projectHolder.tv_lanuage.setVisibility(View.GONE);
            } else {
                TypefaceUtils.setIconText(context, projectHolder.tv_lanuage, context.getString(R.string.sem_tag) + " " + project.getLanguage());
            }

            if (!TextUtils.isEmpty(project.getLast_push_at())) {
                TypefaceUtils.setIconText(context, projectHolder.tv_createtime, context.getString(R.string.fa_clock_o) + " " +
                        Utils.friendlyFormat(context, project.getLast_push_at()));
            } else if (!TextUtils.isEmpty(project.getCreated_at())) {
                TypefaceUtils.setIconText(context, projectHolder.tv_createtime, context.getString(R.string.fa_clock_o) + " " +
                        Utils.friendlyFormat(context, project.getCreated_at()));
            }

            projectHolder.setUserID(project.getOwner().getId());
            projectHolder.setUserName(project.getOwner().getName());

            if (!noPictureMode) {
                String portraitURL = project.getOwner().getNew_portrait();
                if (portraitURL.endsWith("portrait.gif")) {
                    projectHolder.iv_portrait.setImageResource(R.drawable.mini_avatar);
                } else {
                    ImageLoader.ImageListener listener = ImageLoader.getImageListener(projectHolder.iv_portrait,
                            R.drawable.mini_avatar, R.drawable.mini_avatar);
                    mImageLoader.get(portraitURL, listener);
                }
            }

            projectHolder.itemView.setTag(project);
            projectHolder.itemView.setOnClickListener(projectClickListener);
        } else if (holder instanceof FootViewHolder) {
            FootViewHolder footViewHolder = (FootViewHolder) holder;
            footViewHolder.foot_hint.setText(getStateDescription());
            footViewHolder.foot_progressBar.setVisibility(getProgressBarVisiable());
            AppContext.log(getStateDescription());
        }
    }

    public void setPortraitClickable(boolean portraitClickable) {
        this.portraitClickable = portraitClickable;
    }


    class ProjectViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CircleImageView iv_portrait;

        TextView tv_title;
        TextView tv_description;

        TextView tv_watch;
        TextView tv_star;
        TextView tv_fork;
        TextView tv_lanuage;
        TextView tv_createtime;

        int userID;
        String userName;

        public ProjectViewHolder(View itemView) {
            super(itemView);
            iv_portrait = (CircleImageView) itemView.findViewById(R.id.iv_portrait);
            if (portraitClickable) {
                iv_portrait.setOnClickListener(this);
            }
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            tv_description = (TextView) itemView.findViewById(R.id.tv_description);
            tv_watch = (TextView) itemView.findViewById(R.id.tv_watch);
            tv_star = (TextView) itemView.findViewById(R.id.tv_star);
            tv_fork = (TextView) itemView.findViewById(R.id.tv_fork);
            tv_lanuage = (TextView) itemView.findViewById(R.id.tv_language);
            tv_createtime = (TextView) itemView.findViewById(R.id.tv_createtime);
        }

        public void setUserID(int userID) {
            this.userID = userID;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, ViewSelfInfoActivity.class);
            intent.putExtra(ViewSelfInfoActivity.UserID, userID);
            intent.putExtra(ViewSelfInfoActivity.UserName, userName);
            context.startActivity(intent);
        }
    }
}
