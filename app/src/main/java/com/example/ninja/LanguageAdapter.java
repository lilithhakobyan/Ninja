package com.example.ninja;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class LanguageAdapter extends ArrayAdapter<LanguageItem> {
    public LanguageAdapter(Context context, ArrayList<LanguageItem> languageList) {
        super(context, 0, languageList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.language_item, parent, false);
        }

        LanguageItem currentItem = getItem(position);

        ImageView flagImageView = convertView.findViewById(R.id.imgFlag);
        TextView languageNameTextView = convertView.findViewById(R.id.tvLanguageName);

        if (currentItem != null) {
            flagImageView.setImageResource(currentItem.getFlagResId());
            languageNameTextView.setText(currentItem.getName());
        }

        return convertView;
    }
}
