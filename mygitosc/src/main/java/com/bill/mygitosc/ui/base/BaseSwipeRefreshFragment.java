package com.bill.mygitosc.ui.base;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bill.mygitosc.R;
import com.bill.mygitosc.adapter.BaseRecyclerAdapter;
import com.bill.mygitosc.adapter.BaseStateRecyclerAdapter;
import com.bill.mygitosc.cache.CacheManager;
import com.bill.mygitosc.common.AppContext;
import com.bill.mygitosc.common.DividerItemDecoration;
import com.bill.mygitosc.common.FootViewState;
import com.google.gson.Gson;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.List;


public abstract class BaseSwipeRefreshFragment<T> extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private BaseStateRecyclerAdapter mDataAdapter;
    private LinearLayoutManager linearLayoutManager;
    private int lastVisiableItem;
    private int currentPage;

    private StringRequest listRequest;
    private boolean requestingFlag;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentPage = 0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recycle_view_layout, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresher);
        if (getActivity() instanceof BaseActivity) {
            swipeRefreshLayout.setColorSchemeColors(((BaseActivity) getActivity()).getColorPrimary());
        }
        swipeRefreshLayout.setOnRefreshListener(this);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycleView);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        mDataAdapter = getRecyclerAdapter();
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mDataAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

        requestData(currentPage + 1, false);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (lastVisiableItem + 1 == mDataAdapter.getItemCount()) {
                        if (!requestingFlag) {
                            requestData(currentPage + 1, false);
                        }
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisiableItem = linearLayoutManager.findLastVisibleItemPosition();
            }
        });

    }

    private void requestData(int page, boolean refreshFlag) {
        requestingFlag = true;
        if (mDataAdapter.getItemCount() != 0) {
            if(refreshFlag){
                mDataAdapter.setState(BaseStateRecyclerAdapter.STATE_FULL);
                mDataAdapter.notifyDataSetChanged();
            }else{
                mDataAdapter.setState(BaseStateRecyclerAdapter.STATE_MORE);
                mDataAdapter.notifyDataSetChanged();
            }

        }else{

        }
        String cacheKey = getCacheKey() + page;

        if (isReadCacheData(refreshFlag, page, cacheKey)) {
            AppContext.log("requestDataFromCache " + cacheKey);
            requestDataFromCache(cacheKey);
        } else {
            AppContext.log("requestDataFromNetwork " + getItemType() + " page:" + page);
            requestDataFromNetwork(page);
        }
    }

    private boolean isReadCacheData(boolean refreshFlag, int page, String cacheKey) {
        if (CacheManager.isExistDataCache(getActivity(), cacheKey) && !refreshFlag && page == 1) {
            return true;
        } else if (CacheManager.isExistDataCache(getActivity(), cacheKey) &&
                !CacheManager.isCacheDataFailure(getActivity(), cacheKey) && page != 1) {
            return true;
        }
        return false;
    }

    private void requestDataFromCache(String key) {
        new ReadCacheTask(getActivity()).execute(key);
    }

    private void requestDataFromNetwork(final int page) {
        RequestQueue mQueue = Volley.newRequestQueue(getActivity());
        final Gson gson = new Gson();
        AppContext.log(getItemURL(page));

        listRequest = new StringRequest(getItemURL(page),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        List<T> newList = gson.fromJson(response, getGsonArrayType());
                        loadDataComplete(newList);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loadDataComplete(null);
                    }
                });
        mQueue.add(listRequest);
    }


    @Override
    public void onRefresh() {
        if (!requestingFlag) {
            currentPage = 0;
            requestData(currentPage + 1, true);
            swipeRefreshLayout.setEnabled(false);
        }
    }

    private class SaveCacheTask extends AsyncTask<Void, Void, Void> {
        private final WeakReference<Context> mContext;
        private final Serializable seri;
        private final String key;

        private SaveCacheTask(Context context, Serializable serializable, String key) {
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

    private class ReadCacheTask extends AsyncTask<String, Void, Serializable> {
        private final WeakReference<Context> mContext;

        private ReadCacheTask(Context context) {
            mContext = new WeakReference<Context>(context);
        }

        @Override
        protected Serializable doInBackground(String... params) {
            Serializable seri = CacheManager.readObject(mContext.get(),
                    params[0]);
            if (seri == null) {
                return null;
            } else {
                return seri;
            }
        }

        @Override
        protected void onPostExecute(Serializable list) {
            super.onPostExecute(list);
            if (list != null) {
                readCacheListSuccess(list);
            } else {
                readCacheListSuccess(null);
            }
        }
    }

    private void readCacheListSuccess(Serializable serializable) {
        if (serializable == null) {
            requestData(currentPage + 1, true);
            return;
        }
        List<T> list = (List<T>) serializable;
        AppContext.log("readCacheListSuccess " + getItemType() + " num:" + list.size());
        if (list.size() == 0) {
            requestData(currentPage + 1, true);
        } else {
            loadDataComplete(list);
        }
    }

    private void loadDataComplete(List<T> list) {
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
            swipeRefreshLayout.setEnabled(true);
        }
        requestingFlag = false;

        if (list == null) {
            Toast.makeText(getActivity(), getString(R.string.request_data_error_hint), 500).show();
            mDataAdapter.setState(BaseStateRecyclerAdapter.STATE_ERROR);
            mDataAdapter.notifyDataSetChanged();
            return;
        }
        if (list.size() > 0) {
            if (currentPage == 0) {
                if (list.size() < AppContext.PAGE_SIZE) {
                    mDataAdapter.resetDataSet(list);
                } else {
                    for (int i = 0; i < list.size(); i++) {
                        if (itemCompareTo(mDataAdapter.getDataSet(), list.get(i))) {
                            list.remove(i);
                            i--;
                        }
                    }
                    mDataAdapter.addDataSetToStart(list);
                    Toast.makeText(getActivity(), ("add " + list.size() + " item"), 500).show();
                }
                mDataAdapter.setState(BaseStateRecyclerAdapter.STATE_FULL);
            } else {
                mDataAdapter.setState(BaseStateRecyclerAdapter.STATE_MORE);
                mDataAdapter.addDataSetToEnd(list);
            }
            currentPage++;
            new SaveCacheTask(getActivity(), (Serializable) list, getCacheKey() + currentPage).execute();
        } else {
            AppContext.log("no data:" + mDataAdapter.getItemCount());
            if (mDataAdapter.getItemCount() == 1) {
                mDataAdapter.setState(BaseStateRecyclerAdapter.STATE_EMPTY);
                mDataAdapter.notifyDataSetChanged();
            } else {
                mDataAdapter.setState(BaseStateRecyclerAdapter.STATE_FULL);
                mDataAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onDestroy() {
        if (listRequest != null) {
            listRequest.cancel();
        }
        super.onDestroy();
    }

    protected abstract boolean itemCompareTo(List<T> list, T item);

    protected abstract String getCacheKey();

    protected abstract BaseStateRecyclerAdapter getRecyclerAdapter();

    protected abstract String getItemURL(int page);

    protected abstract String getItemType();

    protected abstract java.lang.reflect.Type getGsonArrayType();
}
