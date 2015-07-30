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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * Created by liaobb on 2015/7/30.
 */
public class LanguageProjectFragment extends BaseSwipeRefreshFragment {
    private static String LANGUAGE_ID = "language_id";
    private int currentLanguageID;

    private StringRequest currentRequest;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentLanguageID = getArguments().getInt(LANGUAGE_ID);
        }
    }

    public static LanguageProjectFragment newInstance(int languageID) {
        LanguageProjectFragment fragment = new LanguageProjectFragment();
        Bundle args = new Bundle();
        args.putInt(LANGUAGE_ID, languageID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void requestData(final int page) {
        if (currentLanguageID == -1) {
            return;
        }
        setSwipeRefreshLayout(true);
        Log.d(AppContext.TAG, "requestData:" + HttpUtils.getLanguageURL(currentLanguageID, page));

        RequestQueue mQueue = Volley.newRequestQueue(getActivity());
        final Gson gson = new Gson();

        currentRequest = new StringRequest(HttpUtils.getLanguageURL(currentLanguageID, page),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        List<Project> newProjectList = gson.fromJson(response, new TypeToken<List<Project>>() {
                        }.getType());
                        if (newProjectList.size() > 0) {
                            if (mDataAdapter instanceof ProjectAdapter) {
                                if (page == 1) {
                                    ((ProjectAdapter) mDataAdapter).resetDataSet(newProjectList);
                                } else {
                                    ((ProjectAdapter) mDataAdapter).addDataSet(newProjectList);
                                    setCurrentPage(page);
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
        mQueue.add(currentRequest);
    }

    @Override
    protected void initRecycleViewAdapter() {
        mDataAdapter = new ProjectAdapter(getActivity(), mData);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        currentRequest.cancel();
    }
}
