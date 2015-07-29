package com.bill.mygitosc.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.bill.mygitosc.R;
import com.bill.mygitosc.bean.Project;
import com.bill.mygitosc.bean.StarWatchOptionResult;
import com.bill.mygitosc.common.AppContext;
import com.bill.mygitosc.common.HttpUtils;
import com.bill.mygitosc.common.TypefaceUtils;
import com.bill.mygitosc.gson.GsonRequest;

import java.util.HashMap;
import java.util.Map;

import butterknife.InjectView;
import butterknife.OnClick;

public class ViewProjectInfoActivity extends BaseActivity {
    public static String VIEW_PROJECT_INFO = "view_project_info";

    @InjectView(R.id.project_name)
    TextView projectName;
    @InjectView(R.id.project_description)
    TextView projectDescription;

    @InjectView(R.id.project_star_stared)
    TextView projectStarStared;
    @InjectView(R.id.project_star_text)
    TextView projectStarText;
    @InjectView(R.id.project_starnum)
    TextView projectStarnum;
    @InjectView(R.id.project_watch_stared)
    TextView projectWatchStared;
    @InjectView(R.id.project_watch_text)
    TextView projectWatchText;
    @InjectView(R.id.project_watchnum)
    TextView projectWatchnum;

    @InjectView(R.id.star_watch_linearlayout)
    View starWatchLinearLayout;

    @InjectView(R.id.project_code_listview)
    ListView projectCodeListView;

    private Project currentProject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().getExtras() != null) {
            currentProject = (Project) getIntent().getSerializableExtra(VIEW_PROJECT_INFO);
        }
        toolbar.setTitle(currentProject.getName());
        toolbar.setSubtitleTextColor(getResources().getColor(android.R.color.white));
        toolbar.setSubtitle(currentProject.getOwner().getName());
        initView();
    }

    private void initView() {
        projectName.setText(currentProject.getName());
        projectDescription.setText(currentProject.getDescription());
        projectStarnum.setText(currentProject.getStars_count() + "");
        projectWatchnum.setText(currentProject.getWatches_count() + "");
        setStared(currentProject.isStared());
        setWatched(currentProject.isWatched());
        ArrayAdapter<String> projectCode = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        projectCode.add(getString(R.string.project_code_readme));
        projectCode.add(getString(R.string.project_code_codes));
        projectCode.add(getString(R.string.project_code_commits));
        projectCode.add(getString(R.string.project_code_issues));
        projectCodeListView.setAdapter(projectCode);
        projectCodeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    Intent intent = new Intent(ViewProjectInfoActivity.this, ProjectReadMeActivity.class);
                    intent.putExtra(ProjectReadMeActivity.PROJECT_ID, currentProject.getId());
                    startActivity(intent);
                } else {
                    Toast.makeText(ViewProjectInfoActivity.this, getString(R.string.project_code_hint), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (AppContext.getInstance().getLoginFlag()) {
            RequestQueue mQueue = Volley.newRequestQueue(this);

            GsonRequest<Project> gsonRequest = new GsonRequest<Project>(HttpUtils.getProjectURl(currentProject.getId()), Project.class,
                    new Response.Listener<Project>() {
                        @Override
                        public void onResponse(Project response) {
                            currentProject = response;
                            setStared(currentProject.isStared());
                            setWatched(currentProject.isWatched());
                            starWatchLinearLayout.setVisibility(View.VISIBLE);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    starWatchLinearLayout.setVisibility(View.VISIBLE);
                }
            });
            mQueue.add(gsonRequest);
        } else {
            starWatchLinearLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //starWatchLinearLayout.setVisibility(View.GONE);
    }

    /*@Override
    protected void initToolbar() {
        toolbar.setTitle(currentProject.getName());
        toolbar.setSubtitleTextColor(getResources().getColor(android.R.color.white));
        toolbar.setSubtitle(currentProject.getOwner().getName());
        super.initToolbar();
    }*/

    @Override
    protected int getLayoutView() {
        return R.layout.activity_view_project_info;
    }

    private void setStared(boolean stared) {
        int textRes;
        if (stared) {
            textRes = R.string.sem_star;
            projectStarText.setText("unstar");
        } else {
            textRes = R.string.sem_empty_star;
            projectStarText.setText("star");
        }
        TypefaceUtils.setIconText(this, projectStarStared, getString(textRes));
    }

    private void setWatched(boolean watched) {
        int textRes;
        if (watched) {
            textRes = R.string.sem_watch;
            projectWatchText.setText("unwatch");
        } else {
            textRes = R.string.sem_empty_watch;
            projectWatchText.setText("watch");
        }
        TypefaceUtils.setIconText(this, projectWatchStared, getString(textRes));
    }

    @OnClick({R.id.ll_star, R.id.ll_watch})
    public void onClickItem(View v) {

        switch (v.getId()) {
            case R.id.ll_star:
                starOption();
                break;
            case R.id.ll_watch:
                watchOption();
                break;
        }
    }

    private void starOption() {
        if (!AppContext.getInstance().getLoginFlag()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            return;
        }

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(true);
        mProgressDialog.setCanceledOnTouchOutside(false);

        String message = "";
        String url;
        if (currentProject.isStared()) {
            message = getString(R.string.unstar_ing_hint);
            url = HttpUtils.unStarProject(currentProject.getId());
        } else {
            message = getString(R.string.star_ing_hint);
            url = HttpUtils.starProject(currentProject.getId());
        }
        mProgressDialog.setMessage(message);
        mProgressDialog.show();

        RequestQueue mQueue = Volley.newRequestQueue(this);
        Map<String, String> map = new HashMap<String, String>();
        map.put("private_token", AppContext.getInstance().getSession().getPrivate_token());

        GsonRequest<StarWatchOptionResult> gsonRequest = new GsonRequest<StarWatchOptionResult>(map, url, StarWatchOptionResult.class,
                new Response.Listener<StarWatchOptionResult>() {
                    @Override
                    public void onResponse(StarWatchOptionResult response) {
                        mProgressDialog.dismiss();
                        String resMsg;
                        if (response.getCount() > currentProject.getStars_count()) {
                            setStared(true);
                            currentProject.setStared(true);
                            resMsg = getString(R.string.star_success_hint);
                        } else {
                            setStared(false);
                            currentProject.setStared(false);
                            resMsg = getString(R.string.unstar_success_hint);
                        }
                        currentProject.setStars_count(response.getCount());
                        projectStarnum.setText(response.getCount() + "");
                        Toast.makeText(ViewProjectInfoActivity.this, resMsg, Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                Toast.makeText(ViewProjectInfoActivity.this, getString(R.string.request_data_error_hint), Toast.LENGTH_SHORT).show();
            }
        });
        mQueue.add(gsonRequest);
    }

    private void watchOption() {
        if (!AppContext.getInstance().getLoginFlag()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            return;
        }

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(true);
        mProgressDialog.setCanceledOnTouchOutside(false);

        String message = "";
        String url;
        if (currentProject.isWatched()) {
            message = getString(R.string.unwatch_ing_hint);
            url = HttpUtils.unWatchProject(currentProject.getId());
        } else {
            message = getString(R.string.watch_ing_hint);
            url = HttpUtils.watchProject(currentProject.getId());
        }
        mProgressDialog.setMessage(message);
        mProgressDialog.show();

        RequestQueue mQueue = Volley.newRequestQueue(this);
        Map<String, String> map = new HashMap<String, String>();
        map.put("private_token", AppContext.getInstance().getSession().getPrivate_token());

        GsonRequest<StarWatchOptionResult> gsonRequest = new GsonRequest<StarWatchOptionResult>(map, url, StarWatchOptionResult.class,
                new Response.Listener<StarWatchOptionResult>() {
                    @Override
                    public void onResponse(StarWatchOptionResult response) {
                        mProgressDialog.dismiss();
                        String resMsg;
                        if (response.getCount() > currentProject.getWatches_count()) {
                            setWatched(true);
                            currentProject.setWatched(true);
                            resMsg = getString(R.string.watch_success_hint);
                        } else {
                            setWatched(false);
                            currentProject.setWatched(false);
                            resMsg = getString(R.string.unwatch_success_hint);
                        }
                        currentProject.setWatches_count(response.getCount());
                        projectWatchnum.setText(response.getCount() + "");
                        Toast.makeText(ViewProjectInfoActivity.this, resMsg, Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                Toast.makeText(ViewProjectInfoActivity.this, getString(R.string.request_data_error_hint), Toast.LENGTH_SHORT).show();
            }
        });
        mQueue.add(gsonRequest);
    }
}
