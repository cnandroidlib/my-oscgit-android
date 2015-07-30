package com.bill.mygitosc.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.bill.mygitosc.R;
import com.bill.mygitosc.bean.SelfEvent;
import com.bill.mygitosc.ui.BaseActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by liaobb on 2015/7/24.
 */
public class Utils {
    public static final int LOGIN_SUCCESS_EVNET = 0;

    public static boolean ignoreCaseContain(String s1, String s2) {
        return s1.toLowerCase().indexOf(s2.toLowerCase()) >= 0;
    }

    private static String getEventsTitle(SelfEvent event) {
        String title = "";
        if (event.getEvents().getIssue() != null) {
            title = " #" + event.getEvents().getIssue().getIid();
        }

        if (event.getEvents().getPull_request() != null) {
            title = " #" + event.getEvents().getPull_request().getIid();
        }
        return title;
    }


    public static String parseEventTitle(Context context, String author_name, String pAuthor_And_pName, SelfEvent event) {
        author_name = "(" + author_name + ")";
        pAuthor_And_pName = "<" + pAuthor_And_pName + ">";
        String title = "";
        String eventTitle = "";
        int action = event.getAction();
        switch (action) {
            case SelfEvent.EVENT_TYPE_CREATED:// 创建了issue
                eventTitle = event.getTarget_type() + getEventsTitle(event);
                title = context.getString(R.string.in_project_title) + pAuthor_And_pName + context.getString(R.string.create_project_title) + eventTitle;
                break;
            case SelfEvent.EVENT_TYPE_UPDATED:// 更新项目
                title = context.getString(R.string.update_project_title) + pAuthor_And_pName;
                break;
            case SelfEvent.EVENT_TYPE_CLOSED:// 关闭项目
                eventTitle = event.getTarget_type() + getEventsTitle(event);
                title = context.getString(R.string.close_project_title) + pAuthor_And_pName + " -- " + eventTitle;
                break;
            case SelfEvent.EVENT_TYPE_REOPENED:// 重新打开了项目
                eventTitle = event.getTarget_type() + getEventsTitle(event);
                title = context.getString(R.string.reopen_project_title) + pAuthor_And_pName + " -- " + eventTitle;
                break;
            case SelfEvent.EVENT_TYPE_PUSHED:// push
                eventTitle = event.getData().getRef()
                        .substring(event.getData().getRef().lastIndexOf("/") + 1);
                title = context.getString(R.string.pull_project_title) + pAuthor_And_pName + " -- " + eventTitle + context.getString(R.string.branch_title);
                break;
            case SelfEvent.EVENT_TYPE_COMMENTED:// 评论
                if (event.getEvents().getIssue() != null) {
                    eventTitle = "Issues";
                } else if (event.getEvents().getPull_request() != null) {
                    eventTitle = "PullRequest";
                }
                eventTitle = eventTitle + getEventsTitle(event);
                title = context.getString(R.string.comment_project_title) + pAuthor_And_pName + " -- " + eventTitle;
                break;
            case SelfEvent.EVENT_TYPE_MERGED:// 合并
                eventTitle = event.getTarget_type() + getEventsTitle(event);
                title = context.getString(R.string.accept_project_title) + pAuthor_And_pName + " -- " + eventTitle;
                break;
            case SelfEvent.EVENT_TYPE_JOINED:// # User joined project
                title = context.getString(R.string.enter_project_title) + pAuthor_And_pName;
                break;
            case SelfEvent.EVENT_TYPE_LEFT:// # User left project
                title = context.getString(R.string.leave_project_title) + pAuthor_And_pName;
                break;
            case SelfEvent.EVENT_TYPE_FORKED:// fork了项目
                title = context.getString(R.string.fork_project_title) + pAuthor_And_pName;
                break;
            default:
                title = context.getString(R.string.update_project_default_title);
                break;
        }
        title = author_name + " " + title;
        return title;
    }

    public static SimpleDateFormat defaultDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
    public static SimpleDateFormat hourMinSSDateFormat = new SimpleDateFormat("HH:mm:ss");
    public static SimpleDateFormat hourMinDateFormat = new SimpleDateFormat("HH:mm");

    /*2015-07-28T00:14:27+08:00*/
    public static String friendlyFormat(Context context, String updated_at) {
        int index = updated_at.lastIndexOf(':');
        //updated_at
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < updated_at.length(); i++) {
            if (i != index)
                stringBuilder.append(updated_at.charAt(i));
        }
        Date date = null;
        try {
            date = defaultDateFormat.parse(stringBuilder.toString());
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d(AppContext.TAG, "date parse error");
        }
        if (date == null) {
            return "Unknown";
        }
        Date now = new Date(System.currentTimeMillis());
        if (now.getYear() == date.getYear()) {
            if (now.getMonth() == date.getMonth()) {
                if (now.getDate() == date.getDate()) {
                    return context.getString(R.string.today, hourMinDateFormat.format(date));
                    //return hourMinDateFormat.format(date);
                } else if (now.getDate() - date.getDate() == 1) {
                    return context.getString(R.string.first_before_dat, hourMinDateFormat.format(date));
                } else if (now.getDate() - date.getDate() == 2) {
                    return context.getString(R.string.secode_before_dat, hourMinDateFormat.format(date));
                } else {
                    return String.format(context.getString(R.string.before_day), now.getDate() - date.getDate(), hourMinDateFormat.format(date));
                }
            } else {
                return context.getString(R.string.before_month, now.getMonth() - date.getMonth());
            }
        } else {
            return context.getString(R.string.before_year, now.getYear() - date.getYear());
        }
    }

    public enum ProjectType {
        FeaturedProject("featured"), PopularProject("popular"), LatestProject("latest"),
        MyProject("projects"), StaredProject("stared_projects"), WatchedProject("watched_projects");

        private String projectType;

        private ProjectType(String type) {
            projectType = type;
        }

        public String getProjectType() {
            return projectType;
        }
    }

    public static boolean checkNoPic(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(BaseActivity.PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(context.getString(R.string.no_picture_mode_key), false);
    }
}
