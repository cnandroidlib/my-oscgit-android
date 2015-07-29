package com.bill.mygitosc.common;

import android.content.Context;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.bill.mygitosc.R;

/**
 * Created by liaobb on 2015/7/22.
 */
public class HttpUtils {
    public final static String HOST = "git.oschina.net/";
    private static final String API_VERSION = "api/v3/";// API°æ±¾
    /* public final static String HTTPS = "https://";*/
    public final static String HTTP = "http://";
    public final static String BASE_URL = HTTP + HOST + API_VERSION;
    /*public final static String NO_API_BASE_URL = HTTP + HOST;*/
    public final static String PROJECTS = BASE_URL + "projects/";
    public final static String USER = BASE_URL + "user/";
    public final static String EVENT = BASE_URL + "events/user/";
    public final static String MYEVENT = BASE_URL + "events/";
    public final static String UPLOAD = BASE_URL + "upload";
    public final static String NOTIFICATION = USER + "notifications/";
    public final static String VERSION = BASE_URL + "app_version/new/android";

    public static String getProjectsURL(String projectType, int page) {
        return PROJECTS + projectType + "?page=" + page;
    }

    //http://git.oschina.net/api/v3/projects/485581?private_token=LsvU4AMp6PLVvDMMHUZ7
    public static String getProjectURl(int userID) {
        return PROJECTS + userID + "?private_token=" + AppContext.getInstance().getSession().getPrivate_token();
    }

    public static String getSelfEventURL(int userID, int page) {
        return EVENT + userID + "?page=" + page;
    }

    public static String getMySelfEventURL(int page) {
        return MYEVENT + "?page=" + page + "&private_token=" + AppContext.getInstance().getSession().getPrivate_token();
    }

    public static String getSelfProjectsURL(int userID, int page) {
        return USER + userID + "/projects?page=" + page;
    }

    public static String getSelfOthersProjectURL(int userID, String projectType) {
        return USER + userID + "/" + projectType;
    }


    public static String getLanguageListURL() {
        return PROJECTS + "languages";
    }

    public static String getLanguageURL(int languageID, int page) {
        return PROJECTS + "languages/" + languageID + "/?page=" + page;
    }

    //http://git.oschina.net/api/v3/projects/search/V2ex?page=1
    public static String getSearchProjectURL(String searchKey, int page) {
        return PROJECTS + "search/" + searchKey + "?page=" + page;
    }

    public static String getReadmeURL(int projectId) {
        return PROJECTS + projectId + "/readme";
    }


    public static String getLoginURL() {
        return BASE_URL + "session";
    }

    public static String getRomProjectURL() {
        return PROJECTS + "random?luck=1";
    }

    public static void getPorTraitFormURL(Context context, ImageView imageView, String portraitURL) {
        if (Utils.checkNoPic(context)) {
            RequestQueue mQueue = Volley.newRequestQueue(context);
            ImageLoader mImageLoader = new ImageLoader(mQueue, new BitmapCache());
            ImageLoader.ImageListener listener = ImageLoader.getImageListener(imageView,
                    R.drawable.mini_avatar, R.drawable.mini_avatar);
            mImageLoader.get(portraitURL, listener);
        }
    }

    public static String starProject(int projectId) {
        return PROJECTS + projectId + "/star";
    }

    public static String unStarProject(int projectId) {
        return PROJECTS + projectId + "/unstar";
    }

    public static String watchProject(int projectId) {
        return PROJECTS + projectId + "/watch";
    }

    public static String unWatchProject(int projectId) {
        return PROJECTS + projectId + "/unwatch";
    }
}
