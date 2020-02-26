package com.graspery.www.spicemeup.Activities;

import androidx.appcompat.app.AppCompatActivity;
import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.model.Discover;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.core.MovieResultsPage;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.graspery.www.spicemeup.CustomAdapters.MovieOverviewAdapter;
import com.graspery.www.spicemeup.Dialogs.MovieInfoDialog;
import com.graspery.www.spicemeup.Dialogs.NetflixDialog;
import com.graspery.www.spicemeup.Dialogs.ProfileSettingsDialog;
import com.graspery.www.spicemeup.Dialogs.SettingsDialog;
import com.graspery.www.spicemeup.Dialogs.YearCounterDialog;
import com.graspery.www.spicemeup.Firebase.FirebaseDatabaseHelper;
import com.graspery.www.spicemeup.R;
import com.graspery.www.spicemeup.Utility.AppStatus;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;


import static android.view.View.GONE;

public class NetflixActivity extends AppCompatActivity implements View.OnClickListener {

    List<MovieDb> chosenMovies;
    ProgressBar loadingProgress;
    HashMap<String, Integer> genres;
    TmdbApi mTmdbApi;
    private final String TMBD_POSTER_LINK = "https://image.tmdb.org/t/p/w600_and_h900_bestv2";
    SharedPreferences prefs;

    RelativeLayout profileBtn;
    RelativeLayout settingsBtn;

    //RelativeLayout topNavigation;
    //TextView yearTitle;

    int offSetIndex;

    Button shuffleBtn;
    Vibrator vibrate;

    int totalPageNumbers = 0;
    int pageNumber = 1;
    int shuffleCount = 0;

    FirebaseDatabaseHelper mFirebaseDatabaseHelper;

    MovieInfoDialog movieInfoDialog;

    private ListView movieOverviewListView;
    private SwipyRefreshLayout mSwipyRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_netflix);

        initialize();
    }

    private void initalizeGenres() {
        genres = new HashMap<>();
        genres.put(getResources().getString(R.string.action), 28);
        genres.put(getResources().getString(R.string.adventure), 12);
        genres.put(getResources().getString(R.string.animation), 16);
        genres.put(getResources().getString(R.string.comedy), 35);
        genres.put(getResources().getString(R.string.crime), 80);
        genres.put(getResources().getString(R.string.documentary), 99);
        genres.put(getResources().getString(R.string.drama), 18);
        genres.put(getResources().getString(R.string.family), 10751);
        genres.put(getResources().getString(R.string.fantasy), 14);
        genres.put(getResources().getString(R.string.history), 36);
        genres.put(getResources().getString(R.string.horror), 27);
        genres.put(getResources().getString(R.string.music), 10402);
        genres.put(getResources().getString(R.string.mystery), 9648);
        genres.put(getResources().getString(R.string.romance), 10749);
        genres.put(getResources().getString(R.string.science_fiction), 878);
        genres.put(getResources().getString(R.string.tv_movie), 10770);
        genres.put(getResources().getString(R.string.thriller), 53);
        genres.put(getResources().getString(R.string.war), 10752);
        genres.put(getResources().getString(R.string.western), 37);
    }

    private void initialize() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels + getNavigationBarHeight() - 300;
        prefs = getSharedPreferences("grasperySettings", MODE_PRIVATE);
        loadingProgress = findViewById(R.id.loading_movies_progress_bar);
        chosenMovies = new ArrayList<>();

        mFirebaseDatabaseHelper = new FirebaseDatabaseHelper(this);

        settingsBtn = findViewById(R.id.settings_button);
        settingsBtn.setOnClickListener(this);

        profileBtn = findViewById(R.id.profile_button);
        profileBtn.setOnClickListener(this);

        movieOverviewListView = findViewById(R.id.movie_overview_listview);
/*
        topNavigation = findViewById(R.id.top_navigation);
        topNavigation.setVisibility(View.INVISIBLE);
        yearTitle = findViewById(R.id.top_year_title);*/

        initalizeGenres();
        callGenrePicker();

        offSetIndex = 1;
        shuffleBtn = findViewById(R.id.shuffle_btn);
        shuffleBtn.setOnClickListener(this);

        mSwipyRefreshLayout = findViewById(R.id.swipe_refresher);
        mSwipyRefreshLayout.setDistanceToTriggerSync(height/9);
        mSwipyRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                if(isOnline()) {
                    shuffleAction();
                }
                mSwipyRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void shuffleAction() {
        vibrate = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        if(prefs.getBoolean("vibration",true)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrate.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                //deprecated in API 26
                vibrate.vibrate(50);
            }
        }

        if(offSetIndex == 2) {
            offSetIndex = 1;
            pageNumber++;
            if (pageNumber >= totalPageNumbers) {
                pageNumber = 1;
            }
            chosenMovies.clear();
            new ReadDatabase().execute();
        } else {
            populateSuggestions(offSetIndex);
            offSetIndex++;
        }
    }

    private void callGenrePicker() {
        //startActivity(new Intent(this, MainIntroActivity.class));

        chosenMovies.clear();
        offSetIndex = 1;
        shuffleCount = 0;
        NetflixDialog netflixDialog = new NetflixDialog(NetflixActivity.this);
        netflixDialog.show();
        netflixDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                YearCounterDialog yearCounterDialog = new YearCounterDialog(NetflixActivity.this);
                yearCounterDialog.show();
                yearCounterDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        pageNumber = 1;
                       /* topNavigation.setVisibility(View.VISIBLE);
                        yearTitle.setText(prefs.getInt("year", 2019) + "");*/
                        new ReadDatabase().execute();
                    }
                });
            }
        });
    }

    private boolean isOnline() {
        if (AppStatus.getInstance(this).isOnline()) {
            return true;
        } else {
            Toast.makeText(this, "No internet connection. Please connect to Wifi or Data and try again.",Toast.LENGTH_LONG).show();
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        callGenrePicker();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.shuffle_btn: {
                YoYo.with(Techniques.Shake).pivot(150,150)
                        .duration(300)
                        .repeat(0)
                        .playOn(findViewById(R.id.shuffle_btn));

                if(isOnline()) {
                    shuffleAction();
                }
                break;
            }
            case R.id.settings_button : {
                SettingsDialog settingsDialog = new SettingsDialog(this);
                //settingsDialog.show();

                settingsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                View view = View.inflate(this, R.layout.activity_settings, null);
                settingsDialog.setContentView(view);

                settingsDialog.getWindow()
                        .getAttributes().windowAnimations = R.style.CoolDialogAnimation;

                BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(((View) view.getParent()));
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                bottomSheetBehavior.setPeekHeight(0);
                settingsDialog.show();
                break;
            }
            case R.id.profile_button: {
                if (!mFirebaseDatabaseHelper.isRegisteredUser()) {
                    startActivity(new Intent(this, LoginActivity.class));
                } else {
                    ProfileSettingsDialog profileSettingsDialog = new ProfileSettingsDialog(this);
                    profileSettingsDialog.show();
                }
                break;
            }
        }
    }


    private final class ReadDatabase extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            try {
                mTmdbApi = new TmdbApi(NetflixActivity.this.getResources().getString(R.string.tmdb_key));

                MovieResultsPage discover;

                boolean adult = prefs.getBoolean("adult", true);

                if (!prefs.getString("genre", "Action").equals("Surprise")) {
                    int minYear = prefs.getInt("year", 2019);

                    discover = mTmdbApi.getDiscover()
                            .getDiscover(new Discover().withGenres(genres.get(prefs.getString("genre", "action")) + "")
                                    .includeAdult(adult).primaryReleaseYear(minYear).page(pageNumber));
                } else {
                    int position = new Random().nextInt(genres.size());
                    int counter = 0;
                    String key = "Action";
                    for (String entry : genres.keySet()) {
                        if (counter == position) {
                            key = entry;
                        } else {
                            counter++;
                        }
                    }

                    int minYear = prefs.getInt("year", 2019);

                    discover = new TmdbApi(NetflixActivity.this.getResources().getString(R.string.tmdb_key)).getDiscover()
                            .getDiscover(new Discover().withGenres(genres.get(key) + "")
                                    .includeAdult(adult).year(minYear));
                }

                totalPageNumbers = discover.getTotalPages();
                //MovieDb movie = movies.getMovie(5353, "en");
                List<MovieDb> discoveredMovies = discover.getResults();

                //TmdbMovies movies = mTmdbApi.getMovies();

                Log.d("API SIZE", discover.getResults().size() + "");
                Log.d("Actual SIZE", discoveredMovies.size() + "");

                Collections.shuffle(discoveredMovies);

                for (int i = 0; i < discoveredMovies.size(); i++) {
                    chosenMovies.add(discoveredMovies.get(i));
                }
                //chosenMovies.add(discoveredMovies.get(0));
                //chosenMovies.add(discoveredMovies.get(1));
                //chosenMovies.add(discoveredMovies.get(2));
            /*chosenMovies.add(movies.getMovie(discoveredMovies.get(0).getId(),
                    "en", TmdbMovies.MovieMethod.credits,TmdbMovies.MovieMethod.reviews, TmdbMovies.MovieMethod.similar));
            chosenMovies.add(movies.getMovie(discoveredMovies.get(1).getId(),
                    "en", TmdbMovies.MovieMethod.credits,TmdbMovies.MovieMethod.reviews, TmdbMovies.MovieMethod.similar));
            chosenMovies.add(movies.getMovie(discoveredMovies.get(2).getId(),
                    "en", TmdbMovies.MovieMethod.credits,TmdbMovies.MovieMethod.reviews, TmdbMovies.MovieMethod.similar));
            */
                return discoveredMovies.toString();
            } catch (RuntimeException e) {
                //Toast.makeText(NetflixActivity.this, "Network issue. Please try again later.", Toast.LENGTH_SHORT).show();

                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String result) {
            loadingProgress.setVisibility(GONE);
            if(result != null) {
                populateSuggestions(0);
            }
            else {
                Toast.makeText(NetflixActivity.this, "Network issue. Please try again later.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void populateSuggestions(int offset) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels + getNavigationBarHeight() - 300;

        ArrayList<MovieDb> partitionedMovieList = new ArrayList<>();
        int i = 0;
        if(offset == 0) {
            i = 0;
        } else {
            i = 10;
        }
        for(; i < chosenMovies.size()/(2-offset); i++) {
            partitionedMovieList.add(chosenMovies.get(i));
        }

        MovieOverviewAdapter movieOverviewAdapter = new MovieOverviewAdapter(this, R.layout.movie_overview_row, partitionedMovieList, height, loadingProgress);
        movieOverviewListView.setAdapter(movieOverviewAdapter);

    }

    private int getNavigationBarHeight() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int usableHeight = metrics.heightPixels;
            getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
            int realHeight = metrics.heightPixels;
            if (realHeight > usableHeight)
                return realHeight - usableHeight;
            else
                return 0;
        }
        return 0;
    }
}
