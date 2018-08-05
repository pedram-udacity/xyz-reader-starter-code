package com.example.xyzreader.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.xyzreader.R;

import java.util.ArrayList;

public class BodyTextAdapter extends ArrayAdapter<String> {

    public BodyTextAdapter(@NonNull Context context, ArrayList<String> aStringArrayAdapter) {
        super(context, 0, aStringArrayAdapter);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String body_text = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_body_text, parent, false);
        }

        TextView body_text_tv = (TextView) convertView.findViewById(R.id.body_text_tv);
        body_text_tv.setText(body_text);


        return convertView;    }
}
