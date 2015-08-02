package com.bill.mygitosc.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.bill.mygitosc.R;
import com.bill.mygitosc.bean.Commits;
import com.bill.mygitosc.bean.Event;
import com.bill.mygitosc.common.BitmapCache;
import com.bill.mygitosc.common.Utils;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by liaobb on 2015/7/27.
 */
public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    private ImageLoader mImageLoader;
    private List<Event> mDatas;
    private Context context;
    //private boolean portraitClickable;
    private boolean noPictureMode;

    public EventAdapter(Context context){
        this.context = context;
        this.mDatas = new ArrayList<>();
        RequestQueue mQueue = Volley.newRequestQueue(context);
        mImageLoader = new ImageLoader(mQueue, new BitmapCache());
        noPictureMode = Utils.checkNoPic(context);
    }


    public EventAdapter(Context context, List<Event> mData) {
        this.context = context;
        this.mDatas = mData;
        RequestQueue mQueue = Volley.newRequestQueue(context);
        mImageLoader = new ImageLoader(mQueue, new BitmapCache());
        noPictureMode = Utils.checkNoPic(context);
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //return new EventViewHolder(LayoutInflater.from(context).inflate(R.layout.recycleview_event_item_card, parent, false));
        return new EventViewHolder(LayoutInflater.from(context).inflate(R.layout.recycleview_event_item, parent, false));
    }

    @Override
    public void onBindViewHolder(EventViewHolder holder, int position) {
        Event event = mDatas.get(position);
        String title = Utils.parseEventTitle(context, event
                .getAuthor().getName(), event.getProject().getOwner().getName()
                + "/" + event.getProject().getName(), event);
        holder.ev_title.setText(title);

        if (!TextUtils.isEmpty(event.getUpdated_at())) {
            String updateTime = Utils.friendlyFormat(context, event.getUpdated_at());
            if (!TextUtils.isEmpty(updateTime)) {
                holder.ev_data.setText(updateTime);
            }
        }

        holder.ev_all_commits_list.setVisibility(View.GONE);
        holder.ev_all_commits_list.removeAllViews();
        if (event.getData() != null) {
            List<Commits> commits = event.getData().getCommits();
            if (commits != null && commits.size() > 0) {
                showCommitInfo(holder.ev_all_commits_list, commits);
                holder.ev_all_commits_list.setVisibility(View.VISIBLE);
            }
        }

        if (!noPictureMode) {
            String portraitURL = event.getAuthor().getNew_portrait();
            if (portraitURL.endsWith("portrait.gif")) {
                holder.ev_portrait.setImageResource(R.drawable.mini_avatar);
            } else {
                ImageLoader.ImageListener listener = ImageLoader.getImageListener(holder.ev_portrait,
                        R.drawable.mini_avatar, R.drawable.mini_avatar);
                mImageLoader.get(portraitURL, listener);
            }
        }
    }

    private void showCommitInfo(LinearLayout layout, List<Commits> commits) {
        if (commits.size() >= 2) {
            addCommitItem(layout, commits.get(0));
            addCommitItem(layout, commits.get(1));
        } else {
            for (Commits commit : commits) {
                addCommitItem(layout, commit);
            }
        }
    }

    /**
     * ÃÌº”commitœÓ
     */
    private void addCommitItem(LinearLayout layout, Commits commit) {
        View v = LayoutInflater.from(context).inflate(R.layout.list_item_event_commits, null);
        ((TextView) v.findViewById(R.id.event_commits_listitem_commitid))
                .setText(commit.getId());
        ((TextView) v.findViewById(R.id.event_commits_listitem_username))
                .setText(commit.getAuthor().getName());
        ((TextView) v.findViewById(R.id.event_commits_listitem_message))
                .setText(commit.getMessage());
        layout.addView(v);
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public void addDataSet(List<Event> addNewProjectList) {
        mDatas.addAll(addNewProjectList);
        notifyDataSetChanged();
    }

    public void resetDataSet(List<Event> newProjectList) {
        mDatas.clear();
        mDatas.addAll(newProjectList);
        notifyDataSetChanged();
    }

    /*public void setPortraitClickable(boolean portraitClickable) {
        this.portraitClickable = portraitClickable;
    }*/

    class EventViewHolder extends RecyclerView.ViewHolder {
        CircleImageView ev_portrait;

        TextView ev_title;
        TextView ev_description;
        LinearLayout ev_all_commits_list;
        //TextView ev_client;
        TextView ev_data;

        public EventViewHolder(View itemView) {
            super(itemView);
            ev_portrait = (CircleImageView) itemView.findViewById(R.id.event_portrait);
            /*if (portraitClickable) {
                iv_portrait.setOnClickListener(this);
            }*/
            ev_title = (TextView) itemView.findViewById(R.id.event_title);
            ev_description = (TextView) itemView.findViewById(R.id.event_description);
            ev_all_commits_list = (LinearLayout) itemView.findViewById(R.id.event_all_commits_list);
            //ev_client = (TextView) itemView.findViewById(R.id.event_client);
            ev_data = (TextView) itemView.findViewById(R.id.event_date);
        }
    }
}
