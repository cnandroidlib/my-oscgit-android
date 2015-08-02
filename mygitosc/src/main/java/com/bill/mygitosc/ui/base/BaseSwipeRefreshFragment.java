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
import com.bill.mygitosc.adapter.ProjectAdapter;
import com.bill.mygitosc.bean.Project;
import com.bill.mygitosc.cache.CacheManager;
import com.bill.mygitosc.common.AppContext;
import com.bill.mygitosc.common.DividerItemDecoration;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.List;


public abstract class BaseSwipeRefreshFragment<T> extends Fragment implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;

    protected BaseRecyclerAdapter<T> mDataAdapter;

    private LinearLayoutManager linearLayoutManager;
    private int lastVisiableItem;

    private int currentPage;

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

        initRecycleViewAdapter();

        requestData(currentPage + 1, false);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mDataAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (lastVisiableItem + 1 == mDataAdapter.getItemCount()) {
                        if (!swipeRefreshLayout.isRefreshing()) {
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
        String cacheKey = getCacheKey() + page;

        if (isReadCacheData(refreshFlag, page, cacheKey)) {
            AppContext.log("requestDataFromCache " + cacheKey);
            requestDataFromCache(cacheKey);
        } else {
            AppContext.log("requestDataFromNetwork " + getProjectType() + " page:" + page);
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

    @Override
    public void onRefresh() {
        currentPage = 0;
        requestData(currentPage + 1, true);
        setSwipeRefreshLayout(false);
    }

    protected void setSwipeRefreshLayout(boolean result) {
        if (result) {
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(true);
                    swipeRefreshLayout.setEnabled(false);
                }
            });
        } else {
            swipeRefreshLayout.setRefreshing(false);
            swipeRefreshLayout.setEnabled(true);
        }
    }

    public class SaveCacheTask extends AsyncTask<Void, Void, Void> {
        private final WeakReference<Context> mContext;
        private final Serializable seri;
        private final String key;

        public SaveCacheTask(Context context, Serializable serializable, String key) {
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

    public class ReadCacheTask extends AsyncTask<String, Void, Serializable> {
        private final WeakReference<Context> mContext;

        public ReadCacheTask(Context context) {
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
                currentPage++;
                executeOnLoadDataSuccess(list);
            } else {
                //executeOnLoadDataError(null);
            }
        }
    }

    protected void requestDataFromNetwork(final int page) {
        setSwipeRefreshLayout(true);

        RequestQueue mQueue = Volley.newRequestQueue(getActivity());
        final Gson gson = new Gson();
        AppContext.log(getItemURL(page));

        StringRequest listRequest = new StringRequest(getItemURL(page),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        List<T> newList = gson.fromJson(response, new TypeToken<List<T>>() {
                        }.getType());
                        if (newList.size() > 0) {
                                AppContext.log("haha");
                                if (page == 1) {
                                    AppContext.log("heee");
                                    if (mDataAdapter.getItemCount() <= AppContext.PAGE_SIZE) {
                                        mDataAdapter.resetDataSet(newList);
                                    } else {
                                        AppContext.log("hiiii");
                                        /*for (int i = 0; i < newList.size(); i++) {
                                            if (compareTo(((ProjectAdapter) mDataAdapter).getmDatas(), newList.get(i))) {
                                                newList.remove(i);
                                                i--;
                                            }
                                        }*/
                                        mDataAdapter.addDataSetToEnd(newList);
                                        Toast.makeText(getActivity(), ("add " + newList.size() + " item"), 500).show();
                                    }
                                } else {
                                    mDataAdapter.addDataSetToEnd(newList);
                                }
                                setCurrentPage(page);
                                new SaveCacheTask(getActivity(), (Serializable) newList, getCacheKey() + page).execute();
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
        mQueue.add(listRequest);
    }

    /*private boolean compareTo(List<T> list, T item) {
        int size = list.size();
        for (int i = 0; i < size; i++) {
            if (item.getId() == list.get(i).getId()) {
                return true;
            }
        }
        return false;
    }*/

    protected abstract String getProjectType();

    protected abstract void executeOnLoadDataSuccess(Serializable list);

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    //protected abstract void requestDataFromNetwork(int page);

    protected abstract String getCacheKey();

    protected abstract void initRecycleViewAdapter();

    @Override
    public void onClick(View v) {

    }

    protected abstract String getItemURL(int page);
}
