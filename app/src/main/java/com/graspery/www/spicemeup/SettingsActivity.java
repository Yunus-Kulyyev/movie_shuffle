package com.graspery.www.spicemeup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.graspery.www.spicemeup.Platforms.NetflixActivity;

import org.florescu.android.rangeseekbar.RangeSeekBar;

import java.util.Calendar;

public class SettingsActivity extends AppCompatActivity {
    Button saveBtn;
    Button defaultBtn;
    SwitchCompat adultSwitch;
    SwitchCompat quotesSwitch;
    SwitchCompat autoplaySwitch;
    SwitchCompat vibrationSwitch;
    SharedPreferences mSharedPreferences;
    SharedPreferences.Editor editor;

    RelativeLayout helpLayout;

    RangeSeekBar<Integer> rangeSeekBar;
    int minYear;
    int maxYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mSharedPreferences = getSharedPreferences("grasperySettings", MODE_PRIVATE);
        editor = mSharedPreferences.edit();

        maxYear = mSharedPreferences.getInt("range_max_year", 2020);
        minYear = mSharedPreferences.getInt("range_min_year", 1970);

        helpLayout = findViewById(R.id.help_layout);
        helpLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_EMAIL  , new String[] { "graspery@gmail.com" });
                intent.putExtra(Intent.EXTRA_SUBJECT, "Movie App User");

                startActivity(Intent.createChooser(intent, "Email via..."));
            }
        });

        ImageView back = findViewById(R.id.back_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAndExit();
            }
        });

        defaultBtn = findViewById(R.id.default_btn);
        defaultBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetSettings();
            }
        });

        // Setup the new range seek bar
        rangeSeekBar = new RangeSeekBar<>(this);
        // Set the range
        int currentyear = Calendar.getInstance().get(Calendar.YEAR);
        rangeSeekBar.setRangeValues(currentyear, 1940);
        rangeSeekBar.setSelectedMinValue(maxYear);
        rangeSeekBar.setSelectedMaxValue(minYear);
        /*rangeSeekBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
                SharedPreferences.Editor editor= mSharedPreferences.edit();
                editor.putInt("range_max_year", minValue);
                editor.putInt("range_min_year", maxValue);
                editor.apply();
            }
        });*/

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
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAndExit();
            }
        });
    }

    @Override
    public void onBackPressed() {
        saveAndExit();
    }

    private void saveAndExit() {
        editor.putInt("range_max_year",rangeSeekBar.getSelectedMinValue());
        editor.putInt("range_min_year",rangeSeekBar.getSelectedMaxValue());
        editor.apply();

        Intent intent = new Intent(SettingsActivity.this, NetflixActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void resetSettings() {
        rangeSeekBar.setSelectedMinValue(2019);
        rangeSeekBar.setSelectedMaxValue(1970);

        adultSwitch.setChecked(true);
        quotesSwitch.setChecked(true);
        editor.putBoolean("adult", true);
        editor.putBoolean("quotes", true);
        editor.putInt("range_max_year",rangeSeekBar.getSelectedMinValue());
        editor.putInt("range_min_year",rangeSeekBar.getSelectedMaxValue());
        editor.apply();
    }
}
