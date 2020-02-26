package com.graspery.www.spicemeup.Dialogs;


import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.graspery.www.spicemeup.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Random;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

public class YearCounterDialog extends Dialog {

    Activity c;
    int minYear;
    TextView counterTextView;
    Button startTimer;
    TextView fact;
    SharedPreferences mPreferences;

    public YearCounterDialog(Activity a) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.year_counter_layout);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        counterTextView = findViewById(R.id.counterTextView);

        fact = findViewById(R.id.year_fact);
        fact.setVisibility(View.INVISIBLE);

        mPreferences = c.getSharedPreferences("grasperySettings", MODE_PRIVATE);
        int maxRangeYear = mPreferences.getInt("range_max_year", 2019);
        int minRangeYear = mPreferences.getInt("range_min_year", 1970);

        if(maxRangeYear-minRangeYear <= 0) {
            minYear = new Random().nextInt(50) + 1970;
        } else {
             minYear = new Random().nextInt(maxRangeYear-minRangeYear) + minRangeYear + 1;
        }

        if(mPreferences.getBoolean("quotes", false)) {
            new ReadDatabase().execute();
        }

        startTimer = findViewById(R.id.start_timer_button);
        startTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YoYo.with(Techniques.ZoomOut).withListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        startTimer.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                })
                        .duration(1000)
                        .repeat(0)
                        .playOn(findViewById(R.id.start_timer_button));

                startCountAnimation();
            }
        });

        YoYo.with(Techniques.Pulse).pivot(150,50)
                .duration(700)
                .repeat(5)
                .playOn(findViewById(R.id.start_timer_button));
    }

    private void startCountAnimation() {

        ValueAnimator animator = ValueAnimator.ofInt(0, minYear);
        animator.setDuration(1500);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                if(Integer.parseInt(animation.getAnimatedValue().toString()) < 10) {
                    counterTextView.setText("000" + animation.getAnimatedValue().toString());
                } else if(Integer.parseInt(animation.getAnimatedValue().toString()) < 100) {
                    counterTextView.setText("00" + animation.getAnimatedValue().toString());
                } else if(Integer.parseInt(animation.getAnimatedValue().toString()) < 1000) {
                    counterTextView.setText("0" + animation.getAnimatedValue().toString());
                } else {
                    counterTextView.setText(animation.getAnimatedValue().toString());
                }
            }
        });

        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                fact.setVisibility(View.VISIBLE);

                //int millisecs = 800;
                int millisecs = 3000;
                if(!mPreferences.getBoolean("quotes", false)) {
                    millisecs = 800;
                }
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SharedPreferences.Editor editor = c.getSharedPreferences("grasperySettings", MODE_PRIVATE).edit();
                        editor.putInt("year", minYear);
                        editor.apply();
                        dismiss();
                    }
                }, millisecs);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();
    }

    private final class ReadDatabase extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url("https://numbersapi.p.rapidapi.com/" + minYear + "/year?fragment=false&json=true")
                    .get()
                    .addHeader("x-rapidapi-host", "numbersapi.p.rapidapi.com")
                    .addHeader("x-rapidapi-key", "0c16cc9525msh198c9d532d110a4p15c2cejsn5b8bc7012a5e")
                    .build();

            try {
                Response response = client.newCall(request).execute();
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
                fact.setText("The year when " + jsonObject.getString("text"));
            } catch (JSONException e){

            } catch (NullPointerException e) {

            }


            //fact.setText(result);
        }
    }

}
