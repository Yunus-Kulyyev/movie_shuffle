package com.graspery.www.spicemeup.Dialogs;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.graspery.www.spicemeup.CustomAdapters.AvailabilityListAdapter;
import com.graspery.www.spicemeup.CustomAdapters.CastRecyclerViewAdapter;
import com.graspery.www.spicemeup.Firebase.FirebaseDatabaseHelper;
import com.graspery.www.spicemeup.Models.ArchiveModelMovie;
import com.graspery.www.spicemeup.Activities.LoginActivity;
import com.graspery.www.spicemeup.R;
import com.graspery.www.spicemeup.Utility.OkhttpHelperSingleton;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.model.MovieDb;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.GONE;

public class MovieInfoDialog extends BottomSheetDialog implements View.OnClickListener {

    CheckBox watchListButton;
    CheckBox alreadyWatchedListButton;
    RelativeLayout watchListRelative;
    RelativeLayout alreadyWatchedRelative;
    boolean watchListBool;
    boolean alreadyListBool;

    Activity c;
    MovieDb movie;
    ImageView moviePoster;
    boolean isImageFitToScreen;

    TextView movieOverview;
    TextView movieTitle;
    TextView movieRelease;
    TextView movieRating;
    TextView movieVotes;

    TextView budget;
    TextView budgetTitle;
    TextView revenue;
    TextView revenueTitle;
    TextView net;
    TextView netTitle;
    TextView tagline;
    TextView movieName;
    TextView movieRuntime;

    LinearLayout castList;
    YouTubePlayerView youTubePlayerView;

    FirebaseDatabaseHelper mFirebaseDatabaseHelper;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    DatabaseReference reference;

    private SliderLayout mSliderLayout;
    private HashMap<String, String> movieImageDetails;

    SharedPreferences mSharedPreferences;
    int type;
    ArchiveModelMovie archMovie;

    RecyclerView recyclerView;
    private ListView availListview;
    private LinearLayout availLinear;

    private OkhttpHelperSingleton mOkhttpHelperSingleton;
    private ProgressBar mProgressBar;

    public MovieInfoDialog(Activity a, MovieDb movie) {
        super(a, R.style.SheetDialog);
        // TODO Auto-generated constructor stub
        this.c = a;
        type = 0;
        this.movie = movie;
    }

    public MovieInfoDialog(Context context, ArchiveModelMovie archMovie) {
        super(context,R.style.SheetDialog);
        this.c = (Activity) context;
        this.archMovie = archMovie;
        type = 1;
    }

    private final class ReadOneMovie extends AsyncTask<Void, Void, String> {
        ArchiveModelMovie movieDetails;
        MovieDb formattedMovie;

        public ReadOneMovie(ArchiveModelMovie movieDetails) {
            this.movieDetails = movieDetails;
        }

        @Override
        protected String doInBackground(Void... params) {
            TmdbApi mTmdbApi = new TmdbApi(c.getResources().getString(R.string.tmdb_key));
            TmdbMovies movies = mTmdbApi.getMovies();

            formattedMovie = movies.getMovie(Integer.parseInt(movieDetails.getId()),
                    "en", TmdbMovies.MovieMethod.credits,TmdbMovies.MovieMethod.reviews, TmdbMovies.MovieMethod.similar,
                    TmdbMovies.MovieMethod.images, TmdbMovies.MovieMethod.videos);

            return "Executed";
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);

            //loadingProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String result) {
            //loadingProgress.setVisibility(GONE);
            movie = formattedMovie;
            setUp();
            mProgressBar.setVisibility(GONE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setContentView(R.layout.movie_info_dialog);

        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);

        mSharedPreferences = c.getSharedPreferences("grasperySettings", MODE_PRIVATE);
        mOkhttpHelperSingleton = OkhttpHelperSingleton.getInstance();

        mProgressBar = findViewById(R.id.movie_info_progress_bar);

        if(type == 0) {
            setUp();
        } else if(type == 1){
            ReadOneMovie readOneMovie = new ReadOneMovie(archMovie);
            readOneMovie.execute();
        }

        availLinear = findViewById(R.id.availability_linear);
        availListview = findViewById(R.id.availability_listview);
    }

    private void setUp() {
        recyclerView = findViewById(R.id.cast_recycler_view);
        mFirebaseDatabaseHelper = new FirebaseDatabaseHelper(c);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("movies");

        mSliderLayout = findViewById(R.id.slider);
        movieImageDetails = new HashMap<>();

        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        budget = findViewById(R.id.budget_textview);
        budgetTitle = findViewById(R.id.budget_title);
        revenue = findViewById(R.id.revenue_textview);
        revenueTitle = findViewById(R.id.revenue_title);
        net = findViewById(R.id.net_textview);
        netTitle = findViewById(R.id.net_title);
        movieRuntime = findViewById(R.id.movie_runtime);

        try {
            budget.setText(formatter.format(movie.getBudget()));
            revenue.setText(formatter.format(movie.getRevenue()));

            if (movie.getRevenue() - movie.getBudget() < 0) {
                long netValue = -1 * (movie.getRevenue() - movie.getBudget());

                net.setText("- " + formatter.format(netValue));
                net.setTextColor(Color.RED);
            } else {
                long netValue = (movie.getRevenue() - movie.getBudget());

                net.setText("+ " + formatter.format(netValue));
                net.setTextColor(Color.GREEN);
            }

            if (movie.getBudget() == 0) {
                budget.setVisibility(View.GONE);
                net.setVisibility(View.GONE);
                budgetTitle.setVisibility(View.GONE);
                netTitle.setVisibility(View.GONE);
            }
            if (movie.getRevenue() == 0) {
                revenue.setVisibility(View.GONE);
                net.setVisibility(View.GONE);
                revenueTitle.setVisibility(View.GONE);
                netTitle.setVisibility(View.GONE);
            }
        } catch (NullPointerException e) {

        }


        movieTitle = findViewById(R.id.movie_title);
        movieTitle.setText(movie.getOriginalTitle());
        //movieTitle.setText(movie.getTagline());
        tagline = findViewById(R.id.tagline);
        if(movie.getTagline() != null) {
            tagline.setText(movie.getTagline());
        } else {
            tagline.setVisibility(GONE);
        }

        movieName = findViewById(R.id.movie_name);
        movieName.setText(movie.getOriginalTitle());

        movieRelease = findViewById(R.id.movie_release);
        movieRelease.setText("Release: " + movie.getReleaseDate());

        movieRating = findViewById(R.id.movie_rating);
        movieRating.setText(movie.getVoteAverage()+"");

        movieVotes = findViewById(R.id.movie_votes);
        movieVotes.setText("Votes: " + movie.getVoteCount() + "");

        movieOverview = findViewById(R.id.movie_overview);
        movieOverview.setText(movie.getOverview());

        moviePoster = findViewById(R.id.movie_poster);
        Glide.with(c)
                .load("https://image.tmdb.org/t/p/w600_and_h900_bestv2" + movie.getPosterPath())
                .into(moviePoster);

        ImageView backbtn = findViewById(R.id.back_btn);
        backbtn.setOnClickListener(this);

        watchListBool = false;
        watchListButton = findViewById(R.id.watch_list_radio);
        watchListButton.setOnClickListener(this);
        watchListRelative = findViewById(R.id.watch_list_relative);
        watchListRelative.setOnClickListener(this);

        alreadyListBool = false;
        alreadyWatchedListButton = findViewById(R.id.already_watched_radio);
        alreadyWatchedListButton.setOnClickListener(this);
        alreadyWatchedRelative = findViewById(R.id.already_watched_relative);
        alreadyWatchedRelative.setOnClickListener(this);

        //logMovieDetails();

        readImages();

        playVideo();

        //setCastListView();

        showCast();

        if(mFirebaseDatabaseHelper.isRegisteredUser()) {
            databaseSearch(movie.getId()+"");
        }

        checkAvailabilityOnFirebase();

        castImages();


        //Set the runtime
        if(movie.getRuntime() != 0) {
            int hours = movie.getRuntime() / 60; //since both are ints, you get an int
            int minutes = movie.getRuntime() % 60;
            movieRuntime.setText("Runtime: " + hours + "h " + minutes + "min");
        } else {
            movieRuntime.setVisibility(GONE);
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.watch_list_radio || v.getId() == R.id.watch_list_relative) {
            watchListTrigger();
        } else if(v.getId() == R.id.already_watched_radio || v.getId() == R.id.already_watched_relative) {
            alreadyWatchedListTrigger();
        } else if(v.getId() == R.id.back_btn) {
            dismiss();
        }
    }

    private void setCastListView() {
        castList = findViewById(R.id.cast_layout);

        for(int i = 0; i < movie.getCast().size(); i++) {
            TextView castText = new TextView(c);
            castText.setText(movie.getCast().get(i).getCharacter() + " - " + movie.getCast().get(i).getName());
            castText.setGravity(Gravity.CENTER);
            castText.setTextSize(16);
            castText.setTextColor(Color.WHITE);

            castList.addView(castText);
        }
    }

    private void castImages() {
        int looperSize = 0;
        if(movie.getCast().size() < 5) {
            looperSize = movie.getCast().size();
        } else if(movie.getCast().size() > 10){
            looperSize = 10;
        } else {
            looperSize = 5;
        }

        for(int i = 0; i < looperSize; i++) {
            new ReadCastImages(movie.getCast().get(i).getId()+"", movie.getCast().get(i).getName(), looperSize).execute();
        }
    }

    ArrayList<String> castInfoJson;
    //ArrayList<String[]> castImages;
    RecyclerView.LayoutManager RecyclerViewLayoutManager;
    LinearLayoutManager HorizontalLayout;

    private class ReadCastImages extends AsyncTask<Void, Void, String> {
        String castId;
        String name;
        int position;

        public ReadCastImages(String castId, String name, int position) {
            this.castId = castId;
            this.name = name;
            this.position = position;
            castInfoJson = new ArrayList<>();
            //castImages = new ArrayList<>();
        }

        @Override
        protected String doInBackground(Void... params) {
            Request request = new Request.Builder()
                    .url("https://api.themoviedb.org/3/person/" + castId + "?api_key=" + c.getString(R.string.tmdb_key) + "&language=en-US")
                    .build();
            /*Request request = new Request.Builder()
                    .url("https://api.themoviedb.org/3/person/" + castId + "/images?api_key=" + c.getString(R.string.tmdb_key))
                    .build();*/

            try {
                Response response = mOkhttpHelperSingleton.getOkHttpClient().newCall(request).execute();
                return response.body().string();

            } catch (IOException e) {
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            if(result != null) {
                castInfoJson.add(result);

                if(position == (castInfoJson.size())) {
                    recyclerView.setLayoutManager(new LinearLayoutManager(c));
                    CastRecyclerViewAdapter adapter = new CastRecyclerViewAdapter(c, castInfoJson);
                    RecyclerViewLayoutManager = new LinearLayoutManager(c.getApplicationContext());
                    recyclerView.setLayoutManager(RecyclerViewLayoutManager);
                    HorizontalLayout = new LinearLayoutManager(c, LinearLayoutManager.HORIZONTAL, false);
                    recyclerView.setLayoutManager(HorizontalLayout);
                    recyclerView.setAdapter(adapter);
                }
            }
            /*try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("profiles");

                JSONObject firstOccurObject = jsonArray.getJSONObject(0);

                if(firstOccurObject != null) {
                    String url = firstOccurObject.getString("file_path");
                    castImages.add(new String[]{name, "https://image.tmdb.org/t/p/w600_and_h900_bestv2" + url});


                    if(position == (castImages.size()-1)) {
                        recyclerView.setLayoutManager(new LinearLayoutManager(c));
                        CastRecyclerViewAdapter adapter = new CastRecyclerViewAdapter(c, castImages);
                        RecyclerViewLayoutManager = new LinearLayoutManager(c.getApplicationContext());
                        recyclerView.setLayoutManager(RecyclerViewLayoutManager);
                        HorizontalLayout = new LinearLayoutManager(c, LinearLayoutManager.HORIZONTAL, false);
                        recyclerView.setLayoutManager(HorizontalLayout);
                        recyclerView.setAdapter(adapter);
                    }
                }


            } catch (JSONException e){

            } catch (NullPointerException e) {

            }*/
        }

    }

    private void showCast() {
        // sample code snippet to set the text content on the ExpandableTextView
        ExpandableTextView expTv1 = findViewById(R.id.expand_text_view);

        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < movie.getCast().size(); i++) {
            stringBuilder.append(movie.getCast().get(i).getCharacter() + " - " + movie.getCast().get(i).getName() +"\n");
        }
        expTv1.setText(stringBuilder.toString());

        ExpandableTextView expTv2 = findViewById(R.id.expand_text_view2);
        StringBuilder stringBuilder2 = new StringBuilder();
        for(int i = 0; i < movie.getCrew().size(); i++) {
            stringBuilder2.append(movie.getCrew().get(i).getJob() + " - " + movie.getCrew().get(i).getName() +"\n");
        }
        expTv2.setText(stringBuilder2.toString());


    }

    private void playVideo() {

        youTubePlayerView = findViewById(R.id.youtube_player_view);
        //getLifecycle().addObserver(youTubePlayerView);

        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                try {
                    String videoId = movie.getVideos().get(0).getKey();
                    youTubePlayer.loadVideo(videoId, 0);

                    if(!mSharedPreferences.getBoolean("autoplay", true)) {
                        youTubePlayer.pause();
                    } else {
                        youTubePlayer.play();
                    }
                }catch (NullPointerException e){
                    youTubePlayerView.setVisibility(View.GONE);
                } catch (IndexOutOfBoundsException e) {
                    youTubePlayerView.setVisibility(View.GONE);
                }
            }
        });
    }

    private void readImages() {
        for(int i = 0; i < movie.getImages().size(); i++) {
            movieImageDetails.put(movie.getImages().get(i).getFilePath(), movie.getImages().get(i).getFilePath());
        }

        //mSliderLayout.setIndicatorVisibility(PagerIndicator.IndicatorVisibility.Invisible);
        for(String name : movieImageDetails.keySet()){
            TextSliderView textSliderView = new TextSliderView(c);
            // initialize a SliderLayout
            textSliderView
                    .image("https://image.tmdb.org/t/p/w600_and_h900_bestv2" + movieImageDetails.get(name))
                    .setScaleType(BaseSliderView.ScaleType.FitCenterCrop);

            //add your extra information
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString("extra",name);

            mSliderLayout.addSlider(textSliderView);
        }
    }

    private void logMovieDetails() {
//        Log.d("BackdropPath" , movie.getBackdropPath());
        Log.d("PosterPath" , movie.getPosterPath());
//        Log.d("HomePage" , movie.getHomepage());
        Log.d("ImdbID" , movie.getImdbID());
        Log.d("OriginalLanguage" , movie.getOriginalLanguage());
        Log.d("OriginalTitle" , movie.getOriginalTitle());
        Log.d("Overview" , movie.getOverview());
        Log.d("ReleaseDate" , movie.getReleaseDate());
        Log.d("Status" , movie.getStatus());
        Log.d("Tagline" , movie.getTagline());
        Log.d("Title" , movie.getTitle());
        Log.d("Budget" , movie.getBudget() + "");
        Log.d("Cast size" , movie.getCast().size() + "");
       // Log.d("Alternative Titles size" , movie.getAlternativeTitles().size() + "");
        Log.d("Belongs To Collection" , movie.getBelongsToCollection() + "");
        Log.d("Credits" , movie.getCredits().toString());
        Log.d("Crew Size" , movie.getCrew().size() + "");
        Log.d("Genre Size" , movie.getGenres().size() + "");
        Log.d("Image Size" , movie.getImages().get(0).getFilePath() + "");
//        Log.d("Keyword Size" , movie.getKeywords().size() + "");
        Log.d("MovieList Size" , movie.getLists().size() + "");
        Log.d("Media Type" , movie.getMediaType().toString());
        Log.d("Popularity" , movie.getPopularity() + "");
        Log.d("Recommendation Size" , movie.getRecommendations().size() + "");
        Log.d("Countries Size" , movie.getProductionCountries().size() + "");
//        Log.d("Release Size" , movie.getReleases().size() + "");
        Log.d("Revenue" , movie.getRevenue() + "");
        Log.d("Runtime" , movie.getRuntime() + "");
        Log.d("Similar Size" , movie.getSimilarMovies().size() + "");
        Log.d("Reviews Size" , movie.getReviews().size() + "");
//        Log.d("Video Size" , movie.getVideos().get(0).getSite() + "");
        Log.d("Id" , movie.getId() + "");
    }

    private void alreadyWatchedListTrigger() {
        if (!mFirebaseDatabaseHelper.isRegisteredUser()) {
            Toast.makeText(c, "Login to save", Toast.LENGTH_SHORT).show();
            alreadyWatchedListButton.setChecked(false);
            c.startActivity(new Intent(c, LoginActivity.class));
        } else {
            if (!alreadyListBool) {
                mFirebaseDatabaseHelper.addAlreadyWatchedListMovie(movie);
                alreadyListBool = true;
                alreadyWatchedListButton.setChecked(true);
                Toast.makeText(c, "Added to the collection", Toast.LENGTH_SHORT).show();
            } else {
                mFirebaseDatabaseHelper.removeAlreadyWatchedListMovie(movie);
                alreadyListBool = false;
                alreadyWatchedListButton.setChecked(false);
                Toast.makeText(c, "Removed from the collection", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void watchListTrigger() {
        if (!mFirebaseDatabaseHelper.isRegisteredUser()) {
            watchListButton.setChecked(false);
            Toast.makeText(c, "Login to save", Toast.LENGTH_SHORT).show();
            c.startActivity(new Intent(c, LoginActivity.class));
        } else {
            if (!watchListBool) {
                mFirebaseDatabaseHelper.addWatchListMovie(movie);
                watchListBool = true;
                watchListButton.setChecked(true);
                Toast.makeText(c, "Added to the watchlist", Toast.LENGTH_SHORT).show();
            } else {
                mFirebaseDatabaseHelper.removeWatchListMovie(movie);
                watchListBool = false;
                watchListButton.setChecked(false);
                Toast.makeText(c, "Removed from the watchlist", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void databaseSearch(final String id) {
        reference.child("users").child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("already_watched").hasChild(id)) {
                    alreadyWatchedListButton.setChecked(true);
                }
                if(dataSnapshot.child("watch_list").hasChild(id)) {
                    watchListButton.setChecked(true);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    DatabaseReference availabilityReference;
    private void checkAvailabilityOnFirebase() {
        availabilityReference = FirebaseDatabase.getInstance().getReference("movie_availability");
        availabilityReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(movie.getId()+"")) {
                    ArrayList<HashMap<String, String>> movieList = new ArrayList<>();
                    for(DataSnapshot snapshot : dataSnapshot.child(movie.getId() + "").getChildren()) {
                        HashMap<String, String> currMovie = new HashMap<>();
                        currMovie.put(snapshot.getKey(), snapshot.getValue().toString());
                        movieList.add(currMovie);
                    }


                    availabilityReference.child("00000000").child("size").setValue((dataSnapshot.getChildrenCount()-1));
                    populateAvailability(movieList);

                } else {
                    /** Uncomment to Activate Utella API. $0.01 per request if over 1000 requests/month**/
                    //CheckAvailabilityThread checkAvailabilityThread = new CheckAvailabilityThread();
                    //checkAvailabilityThread.execute();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private class CheckAvailabilityThread extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            //OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url("https://utelly-tv-shows-and-movies-availability-v1.p.rapidapi.com/lookup?term=" + movie.getOriginalTitle() + "&country=us")
                    .get()
                    .addHeader("x-rapidapi-host", "utelly-tv-shows-and-movies-availability-v1.p.rapidapi.com")
                    .addHeader("x-rapidapi-key", "0c16cc9525msh198c9d532d110a4p15c2cejsn5b8bc7012a5e")
                    .build();

            try {
                Response response = mOkhttpHelperSingleton.getOkHttpClient().newCall(request).execute();
                return response.body().string();

            } catch (IOException e) {
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("results");

                JSONObject firstOccurObject = null;
                for(int i = 0; i < jsonArray.length(); i++) {
                    if(jsonArray.getJSONObject(i).getString("name").toLowerCase().trim().equals(movie.getOriginalTitle().toLowerCase().trim())) {
                        firstOccurObject = jsonArray.getJSONObject(i);
                    }
                }

                if(firstOccurObject != null) {
                    JSONArray locations = firstOccurObject.getJSONArray("locations");
                    ArrayList<HashMap<String, String>> availLocations = new ArrayList<>();

                    for (int i = 0; i < locations.length(); i++) {
                        String locTitle = locations.getJSONObject(i).getString("display_name");
                        String locUrl = locations.getJSONObject(i).getString("url");
                        availabilityReference.child(movie.getId() + "").child(locTitle).setValue(locUrl);

                        HashMap<String, String> currentLoc = new HashMap<>();
                        currentLoc.put(locTitle, locUrl);
                        availLocations.add(currentLoc);
                    }

                    populateAvailability(availLocations);
                }
            } catch (JSONException e){

            } catch (NullPointerException e) {

            }
        }
    }

    private void populateAvailability(ArrayList<HashMap<String, String>> res) {
        availLinear.setVisibility(View.VISIBLE);
        AvailabilityListAdapter availabilityListAdapter = new AvailabilityListAdapter(c, R.layout.availability_row, res);
        availListview.setAdapter(availabilityListAdapter);

        int itemHeight = (int) (50 * Resources.getSystem().getDisplayMetrics().density);
        availListview.getLayoutParams().height = itemHeight * res.size();
    }
}