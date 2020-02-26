package com.graspery.www.spicemeup.Dialogs;


import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.graspery.www.spicemeup.CustomAdapters.GenreListViewAdapter;
import com.graspery.www.spicemeup.R;
import com.graspery.www.spicemeup.Utility.AppStatus;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class CastProfileDialog extends Dialog {

    Activity c;
    JSONObject mJSONObject;

    TextView castName;
    TextView castBirth;
    TextView castDeath;
    TextView castPlace;
    TextView castBiography;
    ImageView castImage;
    ImageView closeImage;

    LinearLayout linksLayout;
    ImageView instagram;
    ImageView linkedin;

    public CastProfileDialog(Activity a, JSONObject jsonObject) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
        this.mJSONObject = jsonObject;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.cast_profile_dialog_layout);

        getWindow().getAttributes().windowAnimations = R.style.CoolDialogAnimation;
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        getWindow().setGravity(Gravity.CENTER);
/*
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);*/

        castName = findViewById(R.id.cast_profile_name);
        castBirth = findViewById(R.id.cast_profile_birth);
        castDeath = findViewById(R.id.cast_profile_death);
        castPlace = findViewById(R.id.cast_profile_place);
        castBiography = findViewById(R.id.cast_profile_biography);
        castImage = findViewById(R.id.cast_profile_picture);
        closeImage = findViewById(R.id.close_imageview);
        linksLayout = findViewById(R.id.links_linear);
        instagram = findViewById(R.id.instagram_link);
        linkedin = findViewById(R.id.linkedin_link);

        closeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        initObjects();
    }

    private void initObjects(){
        try {
            if(!mJSONObject.getString("name").equals("null")) {
                castName.setText(mJSONObject.getString("name"));
            } else {
                castName.setText("Unknown");
            }
            if(!mJSONObject.getString("birthday").equals("null")) {
                castBirth.setText(mJSONObject.getString("birthday"));
            } else {
                castBirth.setVisibility(View.GONE);
            }

            if(!mJSONObject.getString("place_of_birth").equals("null")) {
                castPlace.setText(mJSONObject.getString("place_of_birth"));
            } else {
                castPlace.setVisibility(View.GONE);
            }

            if(!mJSONObject.getString("biography").equals("null")) {
                castBiography.setText(mJSONObject.getString("biography"));
            }

            if(!mJSONObject.getString("deathday").equals("null")) {
                castDeath.setText(mJSONObject.getString("deathday"));
            } else {
                castDeath.setVisibility(View.GONE);
            }

            if(mJSONObject.getString("name").equals("Yunus Kulyyev")) {
                Glide.with(c).load(mJSONObject.getString("profile_path")).asBitmap()
                        .into(imageUpload(castImage));

                linksLayout.setVisibility(View.VISIBLE);

                instagram.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String url = "https://www.instagram.com/aka_yuska/";
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        c.startActivity(i);
                    }
                });
                linkedin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String url = "https://www.linkedin.com/in/yunus-kulyyev-62b421120/";
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        c.startActivity(i);
                    }
                });
            } else {
                Glide.with(c).load("https://image.tmdb.org/t/p/w600_and_h900_bestv2" + mJSONObject.getString("profile_path")).asBitmap()
                        .into(imageUpload(castImage));
            }
        } catch (JSONException e){}
    }

    private SimpleTarget<Bitmap> imageUpload(ImageView poster) {
        return new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                //animFadeIn.reset();
                //poster3.clearAnimation();
                //poster.startAnimation(animFadeIn);
                poster.setImageBitmap(getRoundedCornerBitmap(resource, 40));
            }
            @Override
            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                //animFadeIn.reset();
                //poster3.clearAnimation();
                //poster.startAnimation(animFadeIn);
                poster.setImageDrawable(c.getResources().getDrawable(R.drawable.ic_launcher_foreground));
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