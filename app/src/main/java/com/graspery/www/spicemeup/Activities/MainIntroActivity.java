package com.graspery.www.spicemeup.Activities;

import android.os.Bundle;

import com.graspery.www.spicemeup.R;
import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide;

public class MainIntroActivity extends IntroActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setFullscreen(true);
        super.onCreate(savedInstanceState);

        addSlide(new SimpleSlide.Builder()
                .title("Movie Shuffle")
                .description("This app will take you into an amazing journey through time")
                .image(R.drawable.vhs_logo)
                .background(R.color.pieces_cherry)
                .backgroundDark(R.color.pieces_cherry)
                .scrollable(false)
                .build());

    }
}
