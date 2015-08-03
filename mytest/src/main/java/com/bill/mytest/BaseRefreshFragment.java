package com.bill.mytest;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liaobb on 2015/8/2.
 */
public abstract class BaseRefreshFragment<T> extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private String cache_key = "projects";

    private RecyclerView recyclerView;
    private BaseRecycleViewAdapter adapter;
    private SwipeRefreshLayout refreshLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recycler_view_layout, container, false);

        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresher);
        refreshLayout.setOnRefreshListener(this);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = getAdapter();
        recyclerView.setAdapter(adapter);
        return view;
    }


    public abstract BaseRecycleViewAdapter getAdapter();


    @Override
    public void onRefresh() {
        initData();
        //adapter.resetDataSet(initData());
        refreshLayout.setRefreshing(false);
    }

    public void initData() {
        if (CacheManager.isExistDataCache(getActivity(), cache_key)) {
            Log.d("bill.lia","read data from cache");
            new ReadCacheTask(getActivity()).execute(cache_key);
        } else {
            Log.d("bill.lia","init data");
            List<Project> projects = new ArrayList<>();
            projects.add(new Project("binbin", "24"));
            projects.add(new Project("panpan", "23"));
            adapter.resetDataSet(projects);
            new SaveCacheTask(getActivity(), (Serializable) projects, cache_key).execute();
        }
    }

    private void executeOnLoadDataSuccess(Serializable list) {
        List<T> newList = (List<T>) list;
        adapter.resetDataSet(newList);
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
                executeOnLoadDataSuccess(list);
            } else {
            }
        }

    }
}
