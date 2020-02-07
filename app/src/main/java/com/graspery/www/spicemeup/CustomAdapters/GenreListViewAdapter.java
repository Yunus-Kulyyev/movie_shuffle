package com.graspery.www.spicemeup.CustomAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.graspery.www.spicemeup.CustomViews.NormalTextView;
import com.graspery.www.spicemeup.R;

import java.util.List;

public class GenreListViewAdapter extends ArrayAdapter<String> {
    private int resourceLayout;
    private Context mContext;

    public GenreListViewAdapter(Context context, int resource, List<String> items) {
        super(context, resource, items);
        this.resourceLayout = resource;
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(mContext);
            v = vi.inflate(resourceLayout, null);
        }

        String genre = getItem(position);

        if (genre != null) {
            NormalTextView genreTitle = v.findViewById(R.id.genre_title);

            if (genreTitle != null) {
                genreTitle.setText(genre);
            }
        }

        return v;
    }
}
