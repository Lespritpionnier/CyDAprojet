package com.cyu.viveda;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MusicPlayer extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);

            }

    public void Player(View view) {

        Intent AudioPlayer = new Intent(getApplicationContext(), Player.class);

        startActivity(AudioPlayer);

    }

    public void FindFile(View view) {
        Intent FindSound = new Intent(getApplicationContext(), FindFile.class);

        startActivity(FindSound);
    }
}
