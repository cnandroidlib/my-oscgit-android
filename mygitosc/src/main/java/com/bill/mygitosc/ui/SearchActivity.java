package com.bill.mygitosc.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodManager;

import com.bill.mygitosc.R;
import com.bill.mygitosc.fragment.SearchProjectFragment;

import butterknife.InjectView;

public class SearchActivity extends BaseActivity implements SearchView.OnQueryTextListener {

    @InjectView(R.id.searchview)
    SearchView searchView;

    private InputMethodManager inputMethodManager;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        searchView.setOnQueryTextListener(this);
        searchView.setIconified(false);
        searchView.setIconifiedByDefault(true);
        searchView.setFocusable(true);
        //searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(this);

        fragmentManager = getSupportFragmentManager();
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
        if (!TextUtils.isEmpty(query)) {
            fragmentManager.beginTransaction().replace(R.id.main_content, SearchProjectFragment.newInstance(query), null).commit();
            inputMethodManager.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
        } else {
            AlertDialog.Builder builder = generateAlterDialog();
            builder.setTitle(getString(R.string.search_dialog_title)).setPositiveButton(getString(R.string.OK), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            builder.show();
        }
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return true;
    }


}
