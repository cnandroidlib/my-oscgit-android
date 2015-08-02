package com.bill.mygitosc.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.bill.mygitosc.R;
import com.bill.mygitosc.common.Utils.ProjectType;
import com.bill.mygitosc.ui.base.BaseTabViewpageFragment;

import java.util.List;

/**
 * Created by liaobb on 2015/7/22.
 */
public class MySelfInfoTabFragment extends BaseTabViewpageFragment {
    public static String USER_ID="user_id";
    private int userID;

    public static MySelfInfoTabFragment newInstance(int userid) {
        MySelfInfoTabFragment fragment = new MySelfInfoTabFragment();
        Bundle args = new Bundle();
        args.putInt(USER_ID, userid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userID = getArguments().getInt(USER_ID);
        }
    }

    @Override
    protected void initViewpagerFragmentList(List<Fragment> viewpagerFragmentList, List<String> mTitles) {
        //int userID = AppContext.getInstance().getSession().getId();
        mTitles.add(getString(R.string.tab_myself_news));
        viewpagerFragmentList.add(EventsRefreshFragment.newInstance(userID));

        mTitles.add(getString(R.string.tab_myself_project));
        viewpagerFragmentList.add(ProjectsRefreshFragment.newInstance(userID, ProjectType.MyProject.getProjectType()));

        mTitles.add(getString(R.string.tab_myself_star));
        viewpagerFragmentList.add(ProjectsRefreshFragment.newInstance(userID, ProjectType.StaredProject.getProjectType()));

        mTitles.add(getString(R.string.tab_myself_watch));
        viewpagerFragmentList.add(ProjectsRefreshFragment.newInstance(userID, ProjectType.WatchedProject.getProjectType()));
    }
}