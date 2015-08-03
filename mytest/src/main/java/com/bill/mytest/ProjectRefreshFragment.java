package com.bill.mytest;

/**
 * Created by liaobb on 2015/8/2.
 */
public class ProjectRefreshFragment extends BaseRefreshFragment<Project> {
    @Override
    public BaseRecycleViewAdapter getAdapter() {
        return new ProjectAdapter(getActivity());
    }

}
