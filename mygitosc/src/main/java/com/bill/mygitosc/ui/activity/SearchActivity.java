package com.bill.mygitosc.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bill.mygitosc.R;
import com.bill.mygitosc.adapter.ProjectAdapter;
import com.bill.mygitosc.bean.Project;
import com.bill.mygitosc.common.AppContext;
import com.bill.mygitosc.common.DividerItemDecoration;
import com.bill.mygitosc.common.HttpUtils;
import com.bill.mygitosc.ui.base.BaseActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import butterknife.InjectView;

public class SearchActivity extends BaseActivity implements SearchView.OnQueryTextListener, View.OnClickListener {
    @InjectView(R.id.project_searchview)
    SearchView searchView;
    @InjectView(R.id.search_recycleView)
    RecyclerView recyclerView;
    @InjectView(R.id.search_result_reload_button)
    Button reSearchButton;
    @InjectView(R.id.searching_project_progressbar)
    ProgressBar searchingProgressbar;

    private InputMethodManager inputMethodManager;
    private ProjectAdapter mDataAdapter;
    private LinearLayoutManager linearLayoutManager;
    private int lastVisiableItem;
    private int currentPage = 1;

    private String searchKey;
    private boolean searchingFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        searchView.setOnQueryTextListener(this);
        //searchView.setIconified(false);
        searchView.setIconifiedByDefault(false);
        searchView.setFocusable(true);
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        reSearchButton.setOnClickListener(this);

        initRecycleView();
    }

    private void initRecycleView() {
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        mDataAdapter = new ProjectAdapter(this,R.layout.recycleview_project_item);
        recyclerView.setAdapter(mDataAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (lastVisiableItem + 1 == mDataAdapter.getItemCount()) {
                        requestData(searchKey, currentPage + 1);
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

    private void requestData(String searchKey, final int page) {
        if (page == 1) {
            currentPage = 1;
        }
        if (searchingFlag) {
            return;
        } else {
            searchingFlag = true;
        }
        setSearchResultHint(true, null);

        RequestQueue mQueue = Volley.newRequestQueue(this);
        final Gson gson = new Gson();

        Log.d(AppContext.TAG, HttpUtils.getSearchProjectURL(searchKey, page));
        StringRequest projectListRequest = new StringRequest(HttpUtils.getSearchProjectURL(searchKey, page),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(AppContext.TAG, "search success");
                        List<Project> newProjectList = gson.fromJson(response, new TypeToken<List<Project>>() {
                        }.getType());
                        if (newProjectList.size() > 0) {
                            //setRetryBut(View.GONE, "");
                            if (page == 1) {
                                mDataAdapter.resetDataSet(newProjectList);
                            } else {
                                mDataAdapter.addDataSetToEnd(newProjectList);
                                currentPage++;
                            }
                            setSearchResultHint(false, null);
                        } else {
                            if (mDataAdapter.getItemCount() == 0) {
                                setSearchResultHint(false, getString(R.string.no_data_but_hint));
                            } else {
                                setSearchResultHint(false, null);
                            }
                        }
                        searchingFlag = false;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(AppContext.TAG, "search error");
                        if (mDataAdapter.getItemCount() == 0) {
                            setSearchResultHint(false, getString(R.string.request_data_error_but_hint));
                        } else {
                            setSearchResultHint(false, null);
                            Toast.makeText(SearchActivity.this, getString(R.string.request_data_error_hint), 500).show();
                        }
                        searchingFlag = false;
                    }
                });
        projectListRequest.setRetryPolicy(new DefaultRetryPolicy(5 * 1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mQueue.add(projectListRequest);
    }

    private void setSearchResultHint(boolean showProgress, String message) {
        if (showProgress) {
            searchingProgressbar.setVisibility(View.VISIBLE);
        } else {
            searchingProgressbar.setVisibility(View.GONE);
        }
        if (message != null) {
            reSearchButton.setVisibility(View.VISIBLE);
            reSearchButton.setText(message);
        } else {
            reSearchButton.setVisibility(View.GONE);
        }
    }

    @Override
    protected void initToolbar() {
        super.initToolbar();
        toolbar.setTitle(getString(R.string.project_search_title));
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_search;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        searchKey = query;
        mDataAdapter.clear();
        requestData(query, 1);
        inputMethodManager.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return true;
    }

    @Override
    public void onClick(View v) {
        requestData(searchKey, 1);
    }
}
