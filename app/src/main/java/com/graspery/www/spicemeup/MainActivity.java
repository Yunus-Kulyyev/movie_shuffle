package com.graspery.www.spicemeup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.graspery.www.spicemeup.Platforms.NetflixActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button netflix;
    Button hulu;
    Button hbo;
    Button amazon;

    LinearLayout platformLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initiate();
    }

    private void initiate() {
        netflix = findViewById(R.id.netflix_button);
        netflix.setOnClickListener(this);

        hulu = findViewById(R.id.hulu_button);
        hulu.setOnClickListener(this);

        hbo = findViewById(R.id.hbo_button);
        hbo.setOnClickListener(this);

        amazon = findViewById(R.id.amazon_button);
        amazon.setOnClickListener(this);

        platformLayout = findViewById(R.id.platform_layout);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.netflix_button: {
                //platformLayout.setVisibility(View.INVISIBLE);
                startActivity(new Intent(MainActivity.this, NetflixActivity.class));
                break;
            }
            default: {
                Toast.makeText(this, "Coming soon!", Toast.LENGTH_SHORT).show();
                break;
            }
        }
    }
}
