package com.bill.mygitosc.common;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;

/**
 * Created by liaobb on 2015/7/23.
 */
public class TypefaceUtils {
    private static Typeface typeface;

    private static Typeface getTypeface(Context context) {
        if (typeface == null) {
            typeface = Typeface.createFromAsset(context.getAssets(), "icons.ttf");
        }
        return typeface;
    }

    public static void setIconText(Context context, TextView tv_watch, String text) {
        tv_watch.setText(text);
        tv_watch.setTypeface(TypefaceUtils.getTypeface(context));
    }
}
