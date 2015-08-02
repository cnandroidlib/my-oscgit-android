package com.bill.mygitosc.ui.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bill.mygitosc.R;
import com.bill.mygitosc.adapter.LanguageCardAdapter;
import com.bill.mygitosc.bean.Language;
import com.bill.mygitosc.cache.CacheManager;
import com.bill.mygitosc.common.AppContext;
import com.bill.mygitosc.common.HttpUtils;
import com.bill.mygitosc.ui.base.BaseActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by liaobb on 2015/7/30.
 */
public class LanguageCardFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private String LANGUAGE_CACHE_KEY = "language_list_cache";

    private SearchView searchView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private LanguageCardAdapter languageCardAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.language_recycleview_layout, container, false);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresher);
        if (getActivity() instanceof BaseActivity) {
            swipeRefreshLayout.setColorSchemeColors(((BaseActivity) getActivity()).getColorPrimary());
        }
        swipeRefreshLayout.setOnRefreshListener(this);

        recyclerView = (RecyclerView) view.findViewById(R.id.language_recyclerview);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        languageCardAdapter = new LanguageCardAdapter(getActivity());
        recyclerView.setAdapter(languageCardAdapter);

        requestData(false);
        return view;
    }

    private void requestData(boolean refreshFlag) {
        if (refreshFlag) {
            swipeRefreshLayout.setEnabled(false);

            RequestQueue mQueue = Volley.newRequestQueue(getActivity());
            final Gson gson = new Gson();

            Log.d(AppContext.TAG, HttpUtils.getLanguageListURL());

            StringRequest stringRequest = new StringRequest(HttpUtils.getLanguageListURL(),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            List<Language> newLanguageList = gson.fromJson(response, new TypeToken<List<Language>>() {
                            }.getType());
                            languageCardAdapter.addAllDataSets(newLanguageList);
                            setSwipeRefreshLayout(false);
                            new SaveCacheTask(getActivity(), (Serializable) newLanguageList, LANGUAGE_CACHE_KEY).execute();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d(AppContext.TAG, "language error");
                            setSwipeRefreshLayout(false);
                        }
                    });
            mQueue.add(stringRequest);
        } else {
            new ReadCacheTask(getActivity()).execute(LANGUAGE_CACHE_KEY);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_language_fragment, menu);
        menu.findItem(R.id.action_search).setVisible(false);
        menu.findItem(R.id.action_remind).setVisible(false);

        MenuItem searchItem = menu.findItem(R.id.action_search_language);
        initSearchView(searchItem);
    }

    private void initSearchView(MenuItem searchItem) {
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        //searchView.setSubmitButtonEnabled(true);//是否显示确认搜索按钮
        //searchView.setIconified(false);
        searchView.setIconifiedByDefault(false);//设置展开后图标的样式,这里只有两种,一种图标在搜索框外,一种在搜索框内

        searchView.setQueryHint(getString(R.string.search_language_hint));
        SearchView.SearchAutoComplete textView = (SearchView.SearchAutoComplete) searchView.findViewById(R.id.search_src_text);
        textView.setTextColor(Color.WHITE);
        textView.setHintTextColor(getActivity().getResources().getColor(R.color.no_select_white));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.d(AppContext.TAG, "onQueryTextChange:" + s);
                languageCardAdapter.getFilter().filter(s);
                return true;
            }
        });
    }

    @Override
    public void onRefresh() {
        requestData(true);
    }

    private void setSwipeRefreshLayout(boolean result) {
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

    private void executeOnLoadDataSuccess(Serializable list) {
        List<Language> languages = (List<Language>) list;
        languageCardAdapter.addAllDataSets(languages);
    }


    private class SaveCacheTask extends AsyncTask<Void, Void, Void> {
        private final WeakReference<Context> mContext;
        private final Serializable seri;
        private final String key;

        private SaveCacheTask(Context context, Serializable seri, String key) {
            mContext = new WeakReference<Context>(context);
            this.seri = seri;
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
                executeOnLoadDataSuccess(list);
            } else {
                //executeOnLoadDataError(null);
            }
        }
    }
}

