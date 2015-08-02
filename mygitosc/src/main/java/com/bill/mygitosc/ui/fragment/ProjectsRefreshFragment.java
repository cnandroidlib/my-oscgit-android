package com.bill.mygitosc.ui.fragment;

import android.os.Bundle;

import com.android.volley.toolbox.StringRequest;
import com.bill.mygitosc.R;
import com.bill.mygitosc.adapter.ProjectAdapter;
import com.bill.mygitosc.bean.Project;
import com.bill.mygitosc.common.AppContext;
import com.bill.mygitosc.common.HttpUtils;
import com.bill.mygitosc.ui.base.BaseSwipeRefreshFragment;

import java.io.Serializable;
import java.util.List;

/**
 * Created by liaobb on 2015/7/23.
 */
public class ProjectsRefreshFragment extends BaseSwipeRefreshFragment<Project> {
    public static final String PROJECT_TYPE = "project_type";
    public static final String FIND_USER_PARAM = "find_user_param";

    protected String currentProjectType;
    private int currentUserID;

    private StringRequest projectListRequest;

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
    protected String getProjectType() {
        return currentProjectType;
    }

    @Override
    protected void executeOnLoadDataSuccess(Serializable list) {
        List<Project> newProjectList = (List<Project>) list;
        AppContext.log(currentProjectType + ": " + newProjectList.size());
        ((ProjectAdapter) mDataAdapter).addDataSetToEnd(newProjectList);
    }

    /*@Override
    protected void requestDataFromNetwork(final int page) {
        setSwipeRefreshLayout(true);

        RequestQueue mQueue = Volley.newRequestQueue(getActivity());
        final Gson gson = new Gson();
        AppContext.log(getProjectURL(currentProjectType, page));

        projectListRequest = new StringRequest(getProjectURL(currentProjectType, page),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        List<Project> newProjectList = gson.fromJson(response, new TypeToken<List<Project>>() {
                        }.getType());
                        if (newProjectList.size() > 0) {
                            if (mDataAdapter instanceof ProjectAdapter) {
                                AppContext.log("haha");
                                if (page == 1) {
                                    AppContext.log("heee");
                                    if (mDataAdapter.getItemCount() <= AppContext.PAGE_SIZE) {
                                        ((ProjectAdapter) mDataAdapter).resetDataSet(newProjectList);
                                    } else {
                                        AppContext.log("hiiii");
                                        for (int i = 0; i < newProjectList.size(); i++) {
                                            if (compareTo(((ProjectAdapter) mDataAdapter).getmDatas(), newProjectList.get(i))) {
                                                newProjectList.remove(i);
                                                i--;
                                            }
                                        }
                                        ((ProjectAdapter) mDataAdapter).addDataSet(newProjectList);
                                        Toast.makeText(getActivity(), ("add " + newProjectList.size() + " item"), 500).show();
                                    }
                                } else {
                                    ((ProjectAdapter) mDataAdapter).addDataSet(newProjectList);
                                }
                                setCurrentPage(page);
                                new SaveCacheTask(getActivity(), (Serializable) newProjectList, getCacheKey() + page).execute();
                            }
                        } else {
                            if (mDataAdapter.getItemCount() == 0) {
                            }
                        }
                        setSwipeRefreshLayout(false);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (mDataAdapter.getItemCount() == 0) {
                        } else {
                            Toast.makeText(getActivity(), getString(R.string.request_data_error_hint), 500).show();
                        }
                        setSwipeRefreshLayout(false);
                    }

                });
        mQueue.add(projectListRequest);
    }*/


    @Override
    protected void initRecycleViewAdapter() {
        mDataAdapter = new ProjectAdapter(getActivity(), R.layout.recycleview_project_item);
        if (currentUserID != -1) {
            ((ProjectAdapter) mDataAdapter).setPortraitClickable(false);
        } else {
            ((ProjectAdapter) mDataAdapter).setPortraitClickable(true);
        }
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

    /*protected String getItemURL(String projectType, int page) {
        if (currentUserID != -1) {
            return HttpUtils.getSelfProjectsURL(currentUserID, currentProjectType, page);
        } else {
            if (isLanguageProject(projectType)) {
                return HttpUtils.getLanguageProjectsURL(projectType, page);
            } else {
                return HttpUtils.getProjectsURL(projectType, page);
            }
        }
    }*/

    private boolean isLanguageProject(String projectType) {
        if ("featured".equals(projectType) || "popular".equals(projectType) || "latest".equals(projectType)) {
            return false;
        }
        return true;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        setSwipeRefreshLayout(false);
        if (projectListRequest != null) {
            projectListRequest.cancel();
        }
        super.onDestroy();
    }
}
