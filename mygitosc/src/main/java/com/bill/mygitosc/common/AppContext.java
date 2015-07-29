package com.bill.mygitosc.common;

import android.app.Application;

import com.bill.mygitosc.R;
import com.bill.mygitosc.bean.Session;

/**
 * Created by liaobb on 2015/7/22.
 */
public class AppContext extends Application {
    public static String TAG = "bill.liao";
    private int currentTheme;
    private Session session;
    private boolean loginFlag;

    private static AppContext appContext;

    @Override
    public void onCreate() {
        super.onCreate();
        setDefaultTheme();
        setSession(null);
        appContext = this;
    }

    public static AppContext getInstance() {
        return appContext;
    }

    public int getCurrentTheme() {
        return currentTheme;
    }

    public void setGreenTheme() {
        currentTheme = R.style.GreenAppBaseTheme;
    }

    public void setDefaultTheme() {
        currentTheme = R.style.AppBaseTheme;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
        if (session == null) {
            loginFlag = false;
        } else {
            loginFlag = true;
        }
    }

    public boolean getLoginFlag() {
        return loginFlag;
    }
}
