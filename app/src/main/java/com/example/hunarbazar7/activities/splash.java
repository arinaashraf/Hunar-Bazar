package com.example.hunarbazar7.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hunarbazar7.R;
import com.example.hunarbazar7.activities.LoginActivity;
import com.example.hunarbazar7.activities.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class splash extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        VideoView videoView = findViewById(R.id.splashVideo);
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.splashvideo);
        videoView.setVideoURI(videoUri);
        videoView.start();

        mAuth = FirebaseAuth.getInstance();
    //    mAuth.signOut();

        videoView.setOnCompletionListener(mp -> {
            FirebaseUser currentUser = mAuth.getCurrentUser();

            if (currentUser != null) {
                startActivity(new Intent(splash.this, MainActivity.class));
            } else {
                startActivity(new Intent(splash.this, LoginActivity.class));
            }
            finish();
        });
    }
}
