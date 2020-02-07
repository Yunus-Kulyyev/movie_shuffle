package com.graspery.www.spicemeup.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubePlayerView;
import com.graspery.www.spicemeup.CustomAdapters.GenreListViewAdapter;
import com.graspery.www.spicemeup.R;
import com.graspery.www.spicemeup.Utility.AppStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class NetflixDialog extends Dialog {

    Activity c;

    ListView genresListView;
    String[] genres;
    ArrayAdapter<String> listAdapter;

    public NetflixDialog(Activity a) {
        super(a,android.R.style.Theme_DeviceDefault_NoActionBar);
        // TODO Auto-generated constructor stub
        this.c = a;
    }


    @Override
    public void onBackPressed() {
        this.c.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.netflix_dialog_layout);

        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);

        genres = c.getResources().getStringArray(R.array.genre_titles);
        List<String> genreList = Arrays.asList(genres);

        genresListView = findViewById(R.id.listview_genres);
        GenreListViewAdapter customAdapter = new GenreListViewAdapter(c, R.layout.genre_row_layout, genreList);
        genresListView.setAdapter(customAdapter);

        //listAdapter = new ArrayAdapter<String>(c, R.layout.support_simple_spinner_dropdown_item, genres);
        //genresListView.setAdapter(listAdapter);
        genresListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (AppStatus.getInstance(c).isOnline()) {
                    SharedPreferences.Editor editor = c.getSharedPreferences("grasperySettings", MODE_PRIVATE).edit();
                    editor.putString("genre", genres[position]);
                    editor.apply();
                    dismiss();
                } else {
                    Toast.makeText(c, "No internet connection. Please connect to Wifi or Data and try again.",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}
