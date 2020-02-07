package com.graspery.www.spicemeup.CustomAdapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.graspery.www.spicemeup.R;

import java.util.HashMap;
import java.util.List;

public class AvailabilityListAdapter extends ArrayAdapter<HashMap<String, String>> {
    private int resourceLayout;
    private Context mContext;
    private ImageView share;
    private TextView watchNow;
    private TextView provider;

    public AvailabilityListAdapter(Context context, int resource, List<HashMap<String, String>> items) {
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


        HashMap<String, String> movie = getItem(position);

        if (movie != null) {
            provider = v.findViewById(R.id.provider_name);
            watchNow = v.findViewById(R.id.watch_now);
            share = v.findViewById(R.id.share_btn);

            for(String key : movie.keySet()) {
                /*if(key.equals("Amazon Instant Video")) {
                    availImageButton.setImageDrawable(mContext.getDrawable(R.drawable.amazon_instant));
                    availImageButton.setBackground(mContext.getDrawable(R.drawable.amazon_ripple_button));
                } else if(key.equals("Google Play")) {
                    availImageButton.setImageDrawable(mContext.getDrawable(R.drawable.google_play));
                    availImageButton.setBackground(mContext.getDrawable(R.drawable.hulu_ripple_button));
                } else if(key.equals("Netflix")) {
                    availImageButton.setImageDrawable(mContext.getDrawable(R.drawable.netflix));
                    availImageButton.setBackground(mContext.getDrawable(R.drawable.netflix_ripple_button));
                } else if(key.equals("iTunes")) {
                    availImageButton.setImageDrawable(mContext.getDrawable(R.drawable.itunes));
                    availImageButton.setBackground(mContext.getDrawable(R.drawable.hbo_ripple_button));
                } else if(key.equals("HBO")) {
                    availImageButton.setImageDrawable(mContext.getDrawable(R.drawable.hbo));
                    availImageButton.setBackground(mContext.getDrawable(R.drawable.hbo_ripple_button));
                } else if(key.equals("Hulu")) {
                    availImageButton.setImageDrawable(mContext.getDrawable(R.drawable.hulu));
                    availImageButton.setBackground(mContext.getDrawable(R.drawable.hulu_ripple_button));
                } else if(key.equals("Amazon Prime Video")) {
                    availImageButton.setImageDrawable(mContext.getDrawable(R.drawable.amazon_prime));
                    availImageButton.setBackground(mContext.getDrawable(R.drawable.amazon_ripple_button));
                }
                else {
                    availImageButton.setImageDrawable(mContext.getDrawable(R.drawable.ic_launcher_foreground));
                }*/

                /*availImageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(movie.get(key)));
                        mContext.startActivity(i);
                    }
                });*/
                provider.setText(key);
                watchNow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(movie.get(key)));
                        mContext.startActivity(i);
                    }
                });

                share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(Intent.ACTION_SEND);
                        i.setType("text/plain");
                        i.putExtra(Intent.EXTRA_SUBJECT, "Sharing URL");
                        i.putExtra(Intent.EXTRA_TEXT, movie.get(key));
                        mContext.startActivity(Intent.createChooser(i, "Share URL"));
                    }
                });
            }
        }

        return v;
    }
}