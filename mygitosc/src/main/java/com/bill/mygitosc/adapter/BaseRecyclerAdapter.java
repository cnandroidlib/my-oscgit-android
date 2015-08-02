package com.bill.mygitosc.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liaobb on 2015/8/2.
 */
public class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<BaseViewHolder> {

    protected List<T> mData;
    protected Context context;
    private int layoutID;

    public BaseRecyclerAdapter(Context context, int layoutID) {
        this.context = context;
        this.layoutID=layoutID;
        this.mData = new ArrayList<T>();
    }

    public BaseRecyclerAdapter(Context context, int layoutID, List<T> list) {
        this.context = context;
        this.layoutID=layoutID;
        this.mData = list;
    }


    public void clear() {
        mData.clear();
        notifyDataSetChanged();
    }

    public void addDataSetToStart(List<T> list) {
        mData.addAll(0, list);
        notifyDataSetChanged();
    }

    public void addDataSetToEnd(List<T> list) {
        mData.addAll(list);
        notifyDataSetChanged();
    }

    public void resetDataSet(List<T> list) {
        mData.clear();
        mData.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseViewHolder(LayoutInflater.from(context).inflate(layoutID, parent, false));
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

}
