package com.bill.mygitosc.fragment;

import android.os.Bundle;

import com.bill.mygitosc.adapter.BaseStateRecyclerAdapter;
import com.bill.mygitosc.adapter.ProjectAdapter;
import com.bill.mygitosc.bean.Project;
import com.bill.mygitosc.utils.HttpUtils;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by liaobb on 2015/7/23.
 */
public class ProjectsRefreshFragment extends BaseSwipeRefreshFragment<Project> {
    public static final String PROJECT_TYPE = "project_type";
    public static final String FIND_USER_PARAM = "find_user_param";

    protected String currentProjectType;
    private int currentUserID;

    public static ProjectsRefreshFragment newInstance(int userid, String projectType) {
        ProjectsRefreshFragment fragment = new ProjectsRefreshFragment();
        Bundle args = new Bundle();
        args.putString(PROJECT_TYPE, projectType);
        args.putInt(FIND_USER_PARAM, userid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentProjectType = getArguments().getString(PROJECT_TYPE);
            currentUserID = getArguments().getInt(FIND_USER_PARAM);
        }
    }

    @Override
    protected boolean itemCompareTo(List<Project> list, Project item) {
        int size = list.size();
        for (int i = 0; i < size; i++) {
            if (item.getId() == list.get(i).getId()) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected String getItemType() {
        return currentProjectType;
    }

    @Override
    protected Type getGsonArrayType() {
        return new TypeToken<List<Project>>() {
        }.getType();
    }

    @Override
    protected BaseStateRecyclerAdapter getRecyclerAdapter() {
        BaseStateRecyclerAdapter mDataAdapter = new ProjectAdapter(getActivity());
        if (currentUserID != -1) {
            ((ProjectAdapter) mDataAdapter).setPortraitClickable(false);
        } else {
            ((ProjectAdapter) mDataAdapter).setPortraitClickable(true);
        }
        return mDataAdapter;
    }

    @Override
    protected String getItemURL(int page) {
        if (currentUserID != -1) {
            return HttpUtils.getSelfProjectsURL(currentUserID, currentProjectType, page);
        } else {
            if (isLanguageProject(currentProjectType)) {
                return HttpUtils.getLanguageProjectsURL(currentProjectType, page);
            } else {
                return HttpUtils.getProjectsURL(currentProjectType, page);
            }
        }
    }

    @Override
    protected String getCacheKey() {
        if (currentUserID == -1) {
            if (isLanguageProject(currentProjectType)) {
                return "project_language_" + currentProjectType + "_";
            }
            return "project_all_" + currentProjectType + "_";
        } else {

            return "project_" + currentProjectType + "_" + currentUserID + "_";
        }
    }


    private boolean isLanguageProject(String projectType) {
        if ("featured".equals(projectType) || "popular".equals(projectType) || "latest".equals(projectType)) {
            return false;
        }
        return true;
    }


}
