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
import com.bill.mygitosc.adapter.EventAdapter;
import com.bill.mygitosc.adapter.ProjectAdapter;
import com.bill.mygitosc.bean.Project;
import com.bill.mygitosc.bean.SelfEvent;
import com.bill.mygitosc.common.AppContext;
import com.bill.mygitosc.common.HttpUtils;
import com.bill.mygitosc.common.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * Created by liaobb on 2015/7/27.
 */
public class SelfEventsRefreshFragment extends BaseSwipeRefreshFragment {
    private static String UserID = "user_id";
    private int userID;

    private StringRequest eventListRequest;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userID = getArguments().getInt(UserID);
        }
    }

    public static SelfEventsRefreshFragment newInstance(int userID) {
        SelfEventsRefreshFragment fragment = new SelfEventsRefreshFragment();
        Bundle args = new Bundle();
        args.putInt(UserID, userID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void requestData(final int page) {
        setSwipeRefreshLayout(true);
        Log.d(AppContext.TAG, "requestData:" + getSelfEventURL(userID, page));

        RequestQueue mQueue = Volley.newRequestQueue(getActivity());
        final Gson gson = new Gson();

        eventListRequest = new StringRequest(getSelfEventURL(userID, page),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        List<SelfEvent> newEventList = gson.fromJson(response, new TypeToken<List<SelfEvent>>() {
                        }.getType());
                        if (newEventList.size() > 0) {
                            setRetryBut(View.GONE, "");
                            if (mDataAdapter instanceof EventAdapter) {
                                if (page == 1) {
                                    ((EventAdapter) mDataAdapter).resetDataSet(newEventList);
                                } else {
                                    ((EventAdapter) mDataAdapter).addDataSet(newEventList);
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
        mQueue.add(eventListRequest);
    }

    private String getSelfEventURL(int userID, int page) {
        if (AppContext.getInstance().getSession() != null && userID == AppContext.getInstance().getSession().getId()) {
            return HttpUtils.getMySelfEventURL(page);
        }
        return HttpUtils.getSelfEventURL(userID, page);
    }


    @Override
    protected void initRecycleViewAdapter() {
        mDataAdapter = new EventAdapter(getActivity(), mData);
    }

    @Override
    public void onDestroy() {
        setSwipeRefreshLayout(false);
        eventListRequest.cancel();
        super.onDestroy();
    }
}
