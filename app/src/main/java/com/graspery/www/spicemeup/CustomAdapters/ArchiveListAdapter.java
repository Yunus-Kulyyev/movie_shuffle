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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.graspery.www.spicemeup.CustomViews.NormalTextView;
import com.graspery.www.spicemeup.Dialogs.MovieInfoDialog;
import com.graspery.www.spicemeup.Models.ArchiveModelMovie;
import com.graspery.www.spicemeup.R;

import java.util.List;

public class ArchiveListAdapter extends ArrayAdapter<ArchiveModelMovie> {
    private int resourceLayout;
    private Context mContext;
    private ImageView moviePoster;
    private LinearLayout detailsLinear;
    private MovieInfoDialog movieInfoDialog;

    public ArchiveListAdapter(Context context, int resource, List<ArchiveModelMovie> items) {
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

        detailsLinear = v.findViewById(R.id.saved_movie_details_linear);
        ArchiveModelMovie movie = getItem(position);

        if (movie != null) {
            NormalTextView genreTitle = v.findViewById(R.id.movie_title_archive);

            if (genreTitle != null) {
                genreTitle.setText(movie.getMovieName());
            }

            moviePoster = v.findViewById(R.id.archive_poster);

            Glide.with(mContext)
                    .load("https://image.tmdb.org/t/p/w600_and_h900_bestv2" + movie.getPosterPath()).asBitmap()
                    .into(imageUpload(moviePoster));

            detailsLinear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(movieInfoDialog != null && movieInfoDialog.isShowing()) {
                        movieInfoDialog.dismiss();
                    }


                    movieInfoDialog = new MovieInfoDialog((Activity)mContext, movie);
                    movieInfoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    View view = View.inflate(mContext, R.layout.movie_info_dialog, null);
                    movieInfoDialog.setContentView(view);
                    BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(((View) view.getParent()));
                    //bottomSheetBehavior.setPeekHeight((int)(height/(1.1)));
                    movieInfoDialog.show();
                }
            });

        }

        return v;
    }

    private SimpleTarget<Bitmap> imageUpload(ImageView poster) {
        return new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                poster.setImageBitmap(getRoundedCornerBitmap(resource, 40));
            }
            @Override
            public void onLoadFailed(Exception e, Drawable errorDrawable) {
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
