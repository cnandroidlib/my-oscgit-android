package com.bill.mygitosc.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bill.mygitosc.R;
import com.bill.mygitosc.adapter.ProjectAdapter;
import com.bill.mygitosc.bean.Project;
import com.bill.mygitosc.common.AppContext;
import com.bill.mygitosc.common.HttpUtils;
import com.bill.mygitosc.common.Utils.ProjectType;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * Created by liaobb on 2015/7/23.
 */
public class ProjectRefreshFragment extends BaseSwipeRefreshFragment<Project> {
    public static final String FIND_ITEM_PARAM = "find_item_param";
    public static final String FIND_USER_PARAM = "find_user_param";

    protected String currenFindProjectType;
    private int currentUserID;

    private StringRequest projectListRequest;

    public static ProjectRefreshFragment newInstance(int userid, String projectType) {
        ProjectRefreshFragment fragment = new ProjectRefreshFragment();
        Bundle args = new Bundle();
        args.putString(FIND_ITEM_PARAM, projectType);
        args.putInt(FIND_USER_PARAM, userid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currenFindProjectType = getArguments().getString(FIND_ITEM_PARAM);
            currentUserID = getArguments().getInt(FIND_USER_PARAM);
        }
    }

    @Override
    protected void requestData(final int page) {
        setSwipeRefreshLayout(true);
        Log.d(AppContext.TAG, "requestData:" + getProjectURL(currenFindProjectType, page));

        RequestQueue mQueue = Volley.newRequestQueue(getActivity());
        final Gson gson = new Gson();

        projectListRequest = new StringRequest(getProjectURL(currenFindProjectType, page),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        List<Project> newProjectList = gson.fromJson(response, new TypeToken<List<Project>>() {
                        }.getType());
                        if (newProjectList.size() > 0) {
                            setRetryBut(View.GONE, "");
                            if (mDataAdapter instanceof ProjectAdapter) {
                                if (!ProjectType.StaredProject.getProjectType().equals(currenFindProjectType)
                                        && !ProjectType.WatchedProject.getProjectType().equals(currenFindProjectType)) {
                                    if (page == 1) {
                                        ((ProjectAdapter) mDataAdapter).resetDataSet(newProjectList);
                                    } else {
                                        ((ProjectAdapter) mDataAdapter).addDataSet(newProjectList);
                                        setCurrentPage(page);
                                    }
                                } else {
                                    ((ProjectAdapter) mDataAdapter).resetDataSet(newProjectList);
                                }
                            }
                        } else {
                            if (mDataAdapter.getItemCount() == 0) {
                                setRetryBut(View.VISIBLE, getString(R.string.no_data_but_hint));
                            }
                        }
                        setSwipeRefreshLayout(false);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (mDataAdapter.getItemCount() == 0) {
                            setRetryBut(View.VISIBLE, getString(R.string.request_data_error_but_hint));
                        } else {
                            Toast.makeText(getActivity(), getString(R.string.request_data_error_hint), 500).show();
                        }
                        setSwipeRefreshLayout(false);
                    }

                });
        mQueue.add(projectListRequest);
    }

    @Override
    protected void initRecycleViewAdapter() {
        mDataAdapter = new ProjectAdapter(getActivity(), mData);
        if (currentUserID != -1) {
            ((ProjectAdapter) mDataAdapter).setPortraitClickable(false);
        } else {
            ((ProjectAdapter) mDataAdapter).setPortraitClickable(true);
        }
    }

    protected String getProjectURL(String projectType, int page) {
        if (currentUserID != -1) {
            if (currenFindProjectType.equals(ProjectType.MyProject.getProjectType())) {
                return HttpUtils.getSelfProjectsURL(currentUserID, page);
            } else {
                return HttpUtils.getSelfOthersProjectURL(currentUserID, currenFindProjectType);
            }
        }
        return HttpUtils.getProjectsURL(projectType, page);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        setSwipeRefreshLayout(false);
        projectListRequest.cancel();
        super.onDestroy();
    }
}
