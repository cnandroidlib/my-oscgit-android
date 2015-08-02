package com.bill.mygitosc.ui.fragment;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.text.TextUtils;

import com.bill.mygitosc.R;
import com.bill.mygitosc.ui.activity.SettingActivity;
import com.jenzz.materialpreference.CheckBoxPreference;
import com.jenzz.materialpreference.Preference;
import com.jenzz.materialpreference.SwitchPreference;

import java.util.List;

/**
 * Created by liaobb on 2015/7/8.
 */
public class SettingsFragment extends PreferenceFragment {
    private Context context;

    private SwitchPreference rightHandModeSwitch;
    private CheckBoxPreference noPictureModeCheckBox;
    private Preference feedbackPreference;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private boolean rightHandMode;
    private boolean noPictureMode;

    public SettingsFragment(){
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefs);

        context = getActivity();
        sharedPreferences = context.getSharedPreferences(SettingActivity.PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        rightHandMode = sharedPreferences.getBoolean(getString(R.string.right_hand_mode_key), false);
        rightHandModeSwitch = (SwitchPreference) findPreference(getString(R.string.right_hand_mode_key));
        rightHandModeSwitch.setChecked(rightHandMode);

        noPictureMode = sharedPreferences.getBoolean(getString(R.string.no_picture_mode_key), false);
        noPictureModeCheckBox = (CheckBoxPreference) findPreference(getString(R.string.no_picture_mode_key));
        noPictureModeCheckBox.setChecked(noPictureMode);

        feedbackPreference = (Preference) findPreference(getString(R.string.advice_feedback_key));

        initFeedbackPreference();
    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, android.preference.Preference preference) {
        if (preference == null)
            return super.onPreferenceTreeClick(preferenceScreen, preference);
        String key = preference.getKey();
        if (TextUtils.equals(key, getString(R.string.right_hand_mode_key))) {
            rightHandMode = !rightHandMode;
            editor.putBoolean(getString(R.string.right_hand_mode_key), rightHandMode).commit();
        } else if(TextUtils.equals(key, getString(R.string.no_picture_mode_key))){
            noPictureMode = !noPictureMode;
            editor.putBoolean(getString(R.string.no_picture_mode_key), noPictureMode).commit();
        } else if (TextUtils.equals(key, getString(R.string.give_favor_key))){
            giveFavor();
        }

        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    private void initFeedbackPreference() {
        Uri uri = Uri.parse("mailto:1250440341@qq.com");
        final Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        PackageManager pm = getActivity().getPackageManager();
        List<ResolveInfo> infos = pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (infos == null || infos.size() <= 0) {
            feedbackPreference.setSummary(getString(R.string.no_email_app_tip));
            return;
        }
        feedbackPreference.setOnPreferenceClickListener(new android.preference.Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(android.preference.Preference preference) {
                startActivity(intent);
                return true;
            }
        });
    }

    private void giveFavor(){
        try{
            Uri uri = Uri.parse("market://details?id="+ getActivity().getPackageName());
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }catch(ActivityNotFoundException e){
            e.printStackTrace();
        }
    }

}
