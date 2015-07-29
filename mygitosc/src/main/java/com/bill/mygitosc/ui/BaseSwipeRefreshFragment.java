package com.bill.mygitosc.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.bill.mygitosc.R;
import com.bill.mygitosc.common.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;


public abstract class BaseSwipeRefreshFragment<T> extends Fragment implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private Button retryRefreshBut;

    protected List<T> mData;
    protected RecyclerView.Adapter mDataAdapter;

    private LinearLayoutManager linearLayoutManager;
    private int lastVisiableItem;

    private int currentPage = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mData = new ArrayList<T>();
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
        requestData(currentPage);

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
                            requestData(currentPage + 1);
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

        retryRefreshBut = (Button) view.findViewById(R.id.recycle_hint_button);
        retryRefreshBut.setOnClickListener(this);
    }

    @Override
    public void onRefresh() {
        currentPage = 1;
        requestData(currentPage);
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

    protected void setRetryBut(int visibility, String text) {
        retryRefreshBut.setVisibility(visibility);
        retryRefreshBut.setText(text);
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    protected abstract void requestData(int page);

    protected abstract void initRecycleViewAdapter();

    @Override
    public void onClick(View v) {

    }
}
