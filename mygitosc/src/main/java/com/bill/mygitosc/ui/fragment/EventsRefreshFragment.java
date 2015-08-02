package com.bill.mygitosc.ui.fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import com.android.volley.toolbox.StringRequest;
import com.bill.mygitosc.adapter.EventAdapter;
import com.bill.mygitosc.bean.Event;
import com.bill.mygitosc.cache.CacheManager;
import com.bill.mygitosc.common.AppContext;
import com.bill.mygitosc.common.HttpUtils;
import com.bill.mygitosc.ui.base.BaseSwipeRefreshFragment;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by liaobb on 2015/7/27.
 */
public class EventsRefreshFragment extends BaseSwipeRefreshFragment {
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

    @Override
    protected String getProjectType() {
        return "event";
    }

    @Override
    protected void executeOnLoadDataSuccess(Serializable list) {
        List<Event> newEvents = (List<Event>) list;
        ((EventAdapter) mDataAdapter).addDataSet(newEvents);
    }

    public static EventsRefreshFragment newInstance(int userID) {
        EventsRefreshFragment fragment = new EventsRefreshFragment();
        Bundle args = new Bundle();
        args.putInt(UserID, userID);
        fragment.setArguments(args);
        return fragment;
    }

    /*@Override
    protected void requestDataFromNetwork(final int page) {
        setSwipeRefreshLayout(true);

        RequestQueue mQueue = Volley.newRequestQueue(getActivity());
        final Gson gson = new Gson();

        eventListRequest = new StringRequest(getSelfEventURL(userID, page),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        List<Event> newEventList = gson.fromJson(response, new TypeToken<List<Event>>() {
                        }.getType());
                        if (newEventList.size() > 0) {
                            if (mDataAdapter instanceof EventAdapter) {
                                if (page == 1) {
                                    ((EventAdapter) mDataAdapter).resetDataSet(newEventList);
                                } else {
                                    ((EventAdapter) mDataAdapter).addDataSet(newEventList);
                                }

                                setCurrentPage(page);
                                EventsSaveCacheTask saveCacheTask = new EventsSaveCacheTask(getActivity(), (Serializable) newEventList, getCacheKey() + page);
                                saveCacheTask.execute();
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
        mQueue.add(eventListRequest);
    }*/

    @Override
    protected String getCacheKey() {
        return "event_self_" + userID + "_";
    }

    @Override
    protected String getItemURL(int page) {
        if (AppContext.getInstance().getSession() != null && userID == AppContext.getInstance().getSession().getId()) {
            return HttpUtils.getMySelfEventURL(page);
        }
        return HttpUtils.getSelfEventURL(userID, page);
    }

    /*private String getSelfEventURL(int userID, int page) {
        if (AppContext.getInstance().getSession() != null && userID == AppContext.getInstance().getSession().getId()) {
            return HttpUtils.getMySelfEventURL(page);
        }
        return HttpUtils.getSelfEventURL(userID, page);
    }*/


    @Override
    protected void initRecycleViewAdapter() {
        mDataAdapter = new EventAdapter(getActivity());
    }

    @Override
    public void onDestroy() {
        setSwipeRefreshLayout(false);
        if (eventListRequest != null) {
            eventListRequest.cancel();
        }
        super.onDestroy();
    }

    public class EventsSaveCacheTask extends AsyncTask<Void, Void, Void> {
        private final WeakReference<Context> mContext;
        private final Serializable seri;
        private final String key;

        public EventsSaveCacheTask(Context context, Serializable serializable, String key) {
            mContext = new WeakReference<Context>(context);
            this.seri = serializable;
            this.key = key;
        }

        @Override
        protected Void doInBackground(Void... params) {

            CacheManager.saveObject(mContext.get(), seri, key);
            return null;
        }
    }
}
