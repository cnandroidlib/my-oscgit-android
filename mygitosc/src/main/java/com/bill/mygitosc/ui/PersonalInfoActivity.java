package com.bill.mygitosc.ui;

import android.os.Bundle;

import com.bill.mygitosc.R;

public class PersonalInfoActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initToolbar() {
        super.initToolbar();
        toolbar.setTitle(getString(R.string.personal_info_title));
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_personal_info;
    }

}
