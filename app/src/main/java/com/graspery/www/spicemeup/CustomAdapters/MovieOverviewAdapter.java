package com.graspery.www.spicemeup.CustomAdapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.graspery.www.spicemeup.CustomViews.NormalTextView;
import com.graspery.www.spicemeup.Dialogs.MovieInfoDialog;
import com.graspery.www.spicemeup.Platforms.NetflixActivity;
import com.graspery.www.spicemeup.R;

import java.util.List;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.model.MovieDb;

import static android.view.View.GONE;

public class MovieOverviewAdapter extends ArrayAdapter<MovieDb> {
    private final String TMBD_POSTER_LINK = "https://image.tmdb.org/t/p/w600_and_h900_bestv2";
    private int resourceLayout;
    private Context mContext;
    private int height;

    private Animation animFadeIn;

    private LinearLayout movieLinear;
    private ImageView poster;
    private TextView movieTitle;
    private TextView movieDescr;
    private TextView readMore;

    private MovieInfoDialog movieInfoDialog;
    private ProgressBar progressBar;

    public MovieOverviewAdapter(Context context, int resource, List<MovieDb> items, int height, ProgressBar progressBar) {

        super(context, resource, items);
        this.resourceLayout = resource;
        this.mContext = context;
        this.height = height;
        this.progressBar = progressBar;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(mContext);

            if((position+1)%2 == 0) {
                v = vi.inflate(R.layout.movie_overview_left_sided, null);
            } else {
                v = vi.inflate(resourceLayout, null);
            }
        }

        MovieDb movie = getItem(position);

        if (movie != null) {
            animFadeIn = AnimationUtils.loadAnimation(mContext, R.anim.fadein);
            animFadeIn.setFillAfter(true);

            movieLinear = v.findViewById(R.id.movie_linear_at_row);
            //movieLinear.setOnClickListener(this);
            movieLinear.getLayoutParams().height = height/3;
            movieLinear.requestLayout();

            poster = v.findViewById(R.id.poster_at_row);
            movieTitle = v.findViewById(R.id.movie_title_at_row);
            movieDescr = v.findViewById(R.id.movie_descr_at_row);
            readMore = v.findViewById(R.id.read_more_at_row);

            poster.setImageBitmap(null);
            Glide.with(mContext)
                    .load(TMBD_POSTER_LINK + movie.getPosterPath()).asBitmap()
                    .into(imageUpload(poster));

            movieTitle.setText(movie.getOriginalTitle());
            movieDescr.setText(movie.getOverview());

            movieLinear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ReadOneMovie readOneMovie = new ReadOneMovie(movie);
                    readOneMovie.execute();
                }
            });

        }

        return v;
    }

    TmdbApi mTmdbApi;
    private final class ReadOneMovie extends AsyncTask<Void, Void, String> {
        MovieDb movieDetails;
        MovieDb formattedMovie;

        public ReadOneMovie(MovieDb movieDetails) {
            this.movieDetails = movieDetails;
        }

        @Override
        protected String doInBackground(Void... params) {
            mTmdbApi = new TmdbApi(mContext.getResources().getString(R.string.tmdb_key));

            TmdbMovies movies = mTmdbApi.getMovies();

            formattedMovie = movies.getMovie(movieDetails.getId(),
                    "en", TmdbMovies.MovieMethod.credits,TmdbMovies.MovieMethod.reviews, TmdbMovies.MovieMethod.similar,
                    TmdbMovies.MovieMethod.images, TmdbMovies.MovieMethod.videos);

            return "Executed";
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String result) {
            progressBar.setVisibility(GONE);

            if(movieInfoDialog != null && movieInfoDialog.isShowing()) {
                movieInfoDialog.dismiss();
            }


            movieInfoDialog = new MovieInfoDialog((Activity)mContext, formattedMovie);
            movieInfoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            View view = View.inflate(mContext, R.layout.movie_info_dialog, null);
            movieInfoDialog.setContentView(view);
            BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(((View) view.getParent()));
            bottomSheetBehavior.setPeekHeight((int)(height/(1.1)));
            movieInfoDialog.show();
        }
    }

    private SimpleTarget<Bitmap> imageUpload(ImageView poster) {
        return new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                //animFadeIn.reset();
                //poster3.clearAnimation();
                poster.startAnimation(animFadeIn);
                poster.setImageBitmap(getRoundedCornerBitmap(resource, 40));
            }
            @Override
            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                animFadeIn.reset();
                //poster3.clearAnimation();
                poster.startAnimation(animFadeIn);
                poster.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_launcher_foreground));
            }
        };
    }

    public Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }
}
