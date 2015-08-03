package com.bill.mytest;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liaobb on 2015/8/2.
 */
public abstract class BaseRecycleViewAdapter<T> extends RecyclerView.Adapter {
    protected List<T> list;
    protected Context context;

    public BaseRecycleViewAdapter(Context context) {
        this.list = new ArrayList<T>();
        this.context = context;
    }

    public void resetDataSet(List<T> newList) {
        list.clear();
        list.addAll(newList);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list.size() + 1;
    }
}
