package com.bill.mygitosc.ui.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.bill.mygitosc.R;
import com.bill.mygitosc.bean.ReadMe;
import com.bill.mygitosc.common.HttpUtils;
import com.bill.mygitosc.gson.GsonRequest;
import com.bill.mygitosc.ui.base.BaseActivity;

import org.apache.http.protocol.HTTP;

import butterknife.InjectView;

public class ProjectReadMeActivity extends BaseActivity {

    @InjectView(R.id.readme_load_error_button)
    Button readmeLoadButton;
    @InjectView(R.id.webView)
    WebView webView;

    public String linkCss = "<link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/readme_style.css\">";

    public static String PROJECT_ID = "project_id";
    private int projectID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent() != null) {
            projectID = getIntent().getIntExtra(PROJECT_ID, 0);
        }

        initView();
        loadData();
    }

    @Override
    protected void initToolbar() {
        super.initToolbar();
        toolbar.setTitle("README.md");
    }

    private void loadData() {
        RequestQueue mQueue = Volley.newRequestQueue(this);

        GsonRequest<ReadMe> gsonRequest = new GsonRequest<ReadMe>(HttpUtils.getReadmeURL(projectID), ReadMe.class,
                new Response.Listener<ReadMe>() {
                    @Override
                    public void onResponse(ReadMe response) {
                        if (response != null && response.getContent() != null) {
                            webView.setVisibility(View.VISIBLE);
                            readmeLoadButton.setVisibility(View.GONE);
                            String body = linkCss + "<div class='markdown-body'>" + response.getContent() + "</div>";
                            webView.loadDataWithBaseURL(null, body, "text/html", HTTP.UTF_8, null);
                        } else {
                            webView.setVisibility(View.GONE);
                            readmeLoadButton.setVisibility(View.VISIBLE);
                            readmeLoadButton.setText(getString(R.string.no_readme_hint));
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                webView.setVisibility(View.GONE);
                readmeLoadButton.setVisibility(View.VISIBLE);
                readmeLoadButton.setText(getString(R.string.request_data_error_but_hint));
            }
        });
        mQueue.add(gsonRequest);
    }

    private void initView() {
        readmeLoadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData();
            }
        });
    }


    @Override
    protected int getLayoutView() {
        return R.layout.activity_project_read_me;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
