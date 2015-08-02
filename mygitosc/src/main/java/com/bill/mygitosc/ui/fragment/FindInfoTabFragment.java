package com.bill.mygitosc.ui.fragment;

import android.support.v4.app.Fragment;

import com.bill.mygitosc.R;
import com.bill.mygitosc.common.Utils.ProjectType;
import com.bill.mygitosc.ui.base.BaseTabViewpageFragment;

import java.util.List;

/**
 * Created by liaobb on 2015/7/22.
 */
public class FindInfoTabFragment extends BaseTabViewpageFragment {
    @Override
    protected void initViewpagerFragmentList(List<Fragment> viewpagerFragmentList, List<String> mTitles) {

        mTitles.add(getString(R.string.tab_find_featured_project));
        viewpagerFragmentList.add(ProjectsRefreshFragment.newInstance(-1, ProjectType.FeaturedProject.getProjectType()));

        mTitles.add(getString(R.string.tab_find_popular_project));
        viewpagerFragmentList.add(ProjectsRefreshFragment.newInstance(-1, ProjectType.PopularProject.getProjectType()));

        mTitles.add(getString(R.string.tab_find_latest_project));
        viewpagerFragmentList.add(ProjectsRefreshFragment.newInstance(-1, ProjectType.LatestProject.getProjectType()));
    }
}
