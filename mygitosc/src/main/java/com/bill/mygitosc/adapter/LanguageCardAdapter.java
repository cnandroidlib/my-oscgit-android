package com.bill.mygitosc.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.bill.mygitosc.R;
import com.bill.mygitosc.bean.Language;
import com.bill.mygitosc.common.LanguageFilter;
import com.bill.mygitosc.ui.ViewLanguageProjectActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liaobb on 2015/7/30.
 */
public class LanguageCardAdapter extends RecyclerView.Adapter<LanguageCardAdapter.LanguageViewHolder> implements Filterable {

    private List<Language> mData;
    private List<Language> mOriginData;
    private Context context;

    private View.OnClickListener languageClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Language language = (Language) v.getTag();
            Intent intent = new Intent(context, ViewLanguageProjectActivity.class);
            intent.putExtra(ViewLanguageProjectActivity.LANGUAGE_ID, language.getId());
            intent.putExtra(ViewLanguageProjectActivity.LANGUAGE_NAME, language.getName());
            context.startActivity(intent);
        }
    };

    public LanguageCardAdapter(Context context) {
        mData = new ArrayList<Language>();
        mOriginData = new ArrayList<Language>();
        this.context = context;
    }

    /*public LanguageCardAdapter(Context context, List<Language> languageList) {
        this.mDatas = languageList;
        this.context = context;
    }*/

    @Override
    public Filter getFilter() {
        return new LanguageFilter(this, mOriginData);
    }


    @Override
    public LanguageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new LanguageViewHolder(LayoutInflater.from(context).inflate(R.layout.recycleview_language_item, parent, false));
    }

    @Override
    public void onBindViewHolder(LanguageViewHolder holder, int position) {
        Language language = mData.get(position);
        holder.languageName.setText(language.getName());
        holder.languageThemeNum.setText(context.getString(R.string.theme_num_hint, language.getProjects_count()));

        holder.itemView.setTag(language);
        holder.itemView.setOnClickListener(languageClickListener);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void addAllDataSets(List<Language> languages) {
        mData.clear();
        mData.addAll(languages);
        mOriginData.clear();
        mOriginData.addAll(languages);
        notifyDataSetChanged();
    }

    public void addViewDatasets(List<Language> languages){
        mData.clear();
        mData.addAll(languages);
        notifyDataSetChanged();
    }


    class LanguageViewHolder extends RecyclerView.ViewHolder {

        TextView languageName;
        TextView languageThemeNum;

        public LanguageViewHolder(View itemView) {
            super(itemView);
            languageName = (TextView) itemView.findViewById(R.id.language_textview);
            languageThemeNum = (TextView) itemView.findViewById(R.id.languageThemeNum_textview);
        }
    }
}