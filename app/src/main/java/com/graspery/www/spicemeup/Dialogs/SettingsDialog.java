package com.graspery.www.spicemeup.Dialogs;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.graspery.www.spicemeup.R;

import org.florescu.android.rangeseekbar.RangeSeekBar;
import org.json.JSONObject;

import java.util.Calendar;

import androidx.appcompat.widget.SwitchCompat;

import static android.content.Context.MODE_PRIVATE;

public class SettingsDialog  extends BottomSheetDialog implements View.OnClickListener {

    Activity c;
    Button saveBtn;
    Button defaultBtn;
    SwitchCompat adultSwitch;
    SwitchCompat quotesSwitch;
    SwitchCompat autoplaySwitch;
    SwitchCompat vibrationSwitch;
    SharedPreferences mSharedPreferences;
    SharedPreferences.Editor editor;

    RelativeLayout helpLayout;
    RelativeLayout aboutMe;

    RangeSeekBar<Integer> rangeSeekBar;
    int minYear;
    int maxYear;

    public SettingsDialog(Activity a) {
        super(a,R.style.SheetDialog);
        c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
 //       setContentView(R.layout.activity_settings);

        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);

        mSharedPreferences = c.getSharedPreferences("grasperySettings", MODE_PRIVATE);
        editor = mSharedPreferences.edit();

        maxYear = mSharedPreferences.getInt("range_max_year", 2020);
        minYear = mSharedPreferences.getInt("range_min_year", 1970);

        helpLayout = findViewById(R.id.help_layout);
        helpLayout.setOnClickListener(this);

        ImageView back = findViewById(R.id.back_btn);
        back.setOnClickListener(this);

        defaultBtn = findViewById(R.id.default_btn);
        defaultBtn.setOnClickListener(this);

        // Setup the new range seek bar
        rangeSeekBar = new RangeSeekBar<>(c);
        // Set the range
        int currentyear = Calendar.getInstance().get(Calendar.YEAR);
        rangeSeekBar.setRangeValues(currentyear, 1940);
        rangeSeekBar.setSelectedMinValue(maxYear);
        rangeSeekBar.setSelectedMaxValue(minYear);
        rangeSeekBar.setTextAboveThumbsColor(R.color.pieces_cherry);

       /* rangeSeekBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
                if(minValue == maxValue) {
                    rangeSeekBar.setSelectedMinValue(2020);
                    rangeSeekBar.setSelectedMaxValue(2019);
                }
                *//*if(minValue-maxValue < 5) {
                    Toast.makeText(c, "Range must be 5 years apart", Toast.LENGTH_SHORT).show();
                    if(minValue + (minValue-maxValue) < 2020) {
                        rangeSeekBar.setSelectedMinValue(minValue + minValue - maxValue);
                    } else {
                        rangeSeekBar.setSelectedMinValue(2020);
                        rangeSeekBar.setSelectedMaxValue(2015);
                    }
                }*//*
            }
        });
*/

        // Add to layout
        FrameLayout layout = (FrameLayout) findViewById(R.id.seekbar_placeholder);
        layout.addView(rangeSeekBar);

        adultSwitch = findViewById(R.id.adult_switch);
        if(mSharedPreferences.getBoolean("adult", true)) {
            adultSwitch.setChecked(true);
        } else {
            adultSwitch.setChecked(false);
        }
        adultSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean("adult", isChecked);
                editor.apply();
            }
        });

        quotesSwitch = findViewById(R.id.quotes_switch);
        if(mSharedPreferences.getBoolean("quotes", true)) {
            quotesSwitch.setChecked(true);
        } else {
            quotesSwitch.setChecked(false);
        }

        quotesSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean("quotes", isChecked);
                editor.apply();
            }
        });

        autoplaySwitch = findViewById(R.id.autoplay_video_switch);
        if(mSharedPreferences.getBoolean("autoplay", true)) {
            autoplaySwitch.setChecked(true);
        } else {
            autoplaySwitch.setChecked(false);
        }
        autoplaySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean("autoplay", isChecked);
                editor.apply();
            }
        });

        vibrationSwitch = findViewById(R.id.vibration_switch);
        if(mSharedPreferences.getBoolean("vibration", true)) {
            vibrationSwitch.setChecked(true);
        } else {
            vibrationSwitch.setChecked(false);
        }
        vibrationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean("vibration", isChecked);
                editor.apply();
            }
        });

        saveBtn = findViewById(R.id.save_exit_btn);
        saveBtn.setOnClickListener(this);

        aboutMe = findViewById(R.id.about_me);
        aboutMe.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.help_layout: {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_EMAIL  , new String[] { "graspery@gmail.com" });
                intent.putExtra(Intent.EXTRA_SUBJECT, "Movie App User");

                c.startActivity(Intent.createChooser(intent, "Email via..."));
                break;
            }
            case R.id.back_btn: {
                dismiss();
                break;
            }
            case R.id.default_btn: {
                resetSettings();
                break;
            }
            case R.id.save_exit_btn: {
                saveAndExit();
                break;
            }
            case R.id.about_me: {
                try {
                    JSONObject jsonObject = new JSONObject(aboutMeJSON());
                    CastProfileDialog castProfileDialog = new CastProfileDialog(c, jsonObject);
                    castProfileDialog.show();
                } catch (Exception e) {}
                break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        dismiss();
    }

    private void saveAndExit() {
        if(rangeSeekBar.getSelectedMinValue().compareTo(rangeSeekBar.getSelectedMaxValue()) == 0) {
            editor.putInt("range_max_year",2020);
            editor.putInt("range_min_year",2019);
        } else {
            editor.putInt("range_max_year", rangeSeekBar.getSelectedMinValue());
            editor.putInt("range_min_year", rangeSeekBar.getSelectedMaxValue());
        }
        editor.apply();

        dismiss();
        /*Intent intent = new Intent(c, NetflixActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        c.startActivity(intent);
        c.finish();*/
    }

    private void resetSettings() {
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        rangeSeekBar.setSelectedMinValue(thisYear);
        rangeSeekBar.setSelectedMaxValue(1970);

        adultSwitch.setChecked(true);
        //quotesSwitch.setChecked(true);
        editor.putBoolean("adult", true);
        editor.putBoolean("quotes", true);
        editor.putInt("range_max_year",rangeSeekBar.getSelectedMinValue());
        editor.putInt("range_min_year",rangeSeekBar.getSelectedMaxValue());
        editor.apply();
    }

    private String aboutMeJSON() {
        String me = "{\n" +
                "  \"birthday\": \"1995-04-18\",\n" +
                "  \"known_for_department\": \"Programmer\",\n" +
                "  \"deathday\": null,\n" +
                "  \"id\": 0,\n" +
                "  \"name\": \"Yunus Kulyyev\",\n" +
                "  \"also_known_as\": [\n" +
                "    \"Yuska\",\n" +
                "  ],\n" +
                "  \"gender\": 2,\n" +
                "  \"biography\": \"Hey, my name is Yunus Kulyyev. Thanks for trying this app. One sleepless night, I was scrolling through a list of movies on Netflix while couldn't decide where to invest my precious time. Long story short, I got angry and irritated and ended up just sleeping. And that night I decided to do something against that crippling indecisiveness. And that's where this app came from. You can find me on Instagram and LinkedIn\",\n" +
                "  \"popularity\": 10,\n" +
                "  \"place_of_birth\": \"Ashgabat, Turkmenistan\",\n" +
                "  \"profile_path\": \"https://media-exp1.licdn.com/dms/image/C5603AQEARgQHgWgwfg/profile-displayphoto-shrink_200_200/0?e=1586995200&v=beta&t=DtTKCBunJ5cX6Jb7G8D4YLCLFs_atNED7rCCgzN14dU\",\n" +
                "  \"adult\": false,\n" +
                "  \"imdb_id\": \"nm0000093\",\n" +
                "  \"homepage\": null\n" +
                "}";
        return me;
    }

}
