package com.cyu.viveda;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MusicPlayer extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);

            }

    public void Player(View view) {

        Intent AudioPlayer = new Intent(getApplicationContext(), MainPlayer.class);

        startActivity(AudioPlayer);

    }

    public void FindFile(View view) {
        Intent FindSound = new Intent(getApplicationContext(), FindFile.class);

        startActivity(FindSound);
    }
}
