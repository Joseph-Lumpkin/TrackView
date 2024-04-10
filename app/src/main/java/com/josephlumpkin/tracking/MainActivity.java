package com.josephlumpkin.tracking;

import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private float mProgress = 0f;
    private float mIncrement = 0.05f;
    private final Handler mProgressHandler = new Handler();
    private static final int dnDELAY = 500;

    private final Runnable mProgressRunnable = new Runnable() {
        @Override
        public void run() {
            mProgress = (mProgress + mIncrement);
            TrackView track = findViewById(R.id.trackView);
            track.setCursorProgress(mProgress);
            track.setPacerProgress(mProgress);
            mProgressHandler.postDelayed(this, dnDELAY);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Begin a recurring progress stepper
        mProgressRunnable.run();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}