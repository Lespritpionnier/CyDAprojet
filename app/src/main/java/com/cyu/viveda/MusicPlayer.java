package com.cyu.viveda;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MusicPlayer extends AppCompatActivity {
    private TextView songName, singerName;
    private ImageView diskImage;

    private SeekBar musicProgress;
    private TextView currentTime, totalTime;
    private ImageButton prevBtn, playPauseBtn, nextBtn;

    private MediaPlayer player;

    private int currentPlaying = 0;
    private ArrayList<Integer> playList = new ArrayList<>();

    private boolean isPausing = false;
    private boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_main);
        init();
        preparePlaylist();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                if (isPlaying) {
                    updateTimer();
                }
            }
        };
        new Timer().scheduleAtFixedRate(timerTask, 0, 500);
    }

    void init() {
        songName = findViewById(R.id.song_name);
        singerName = findViewById(R.id.song_singer);
        diskImage = findViewById(R.id.disk);
        musicProgress = findViewById(R.id.play_progress);
        currentTime = findViewById(R.id.current_progress);
        totalTime = findViewById(R.id.total_progress);
        prevBtn = findViewById(R.id.btn_prev);
        playPauseBtn = findViewById(R.id.btn_play_pause);
        nextBtn = findViewById(R.id.btn_next);

        OnClickControl onClick = new OnClickControl();
        prevBtn.setOnClickListener(onClick);
        playPauseBtn.setOnClickListener(onClick);
        nextBtn.setOnClickListener(onClick);

        OnSeekBarChangeControl onChange = new OnSeekBarChangeControl();
        musicProgress.setOnSeekBarChangeListener(onChange);
    }

    private void preparePlaylist() {
        Field[] fields = R.raw.class.getFields();
        for (int count = 0; count < fields.length; count++) {
            try {
                int resId = fields[count].getInt(fields[count]);
                playList.add(resId);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private void prepareMedia() {
        if (isPlaying) {
            player.stop();
            player.reset();
        }
        player = MediaPlayer.create(getApplicationContext(), playList.get(currentPlaying));
        int musicDuration = player.getDuration();
        musicProgress.setMax(musicDuration);
        int sec = musicDuration / 1000;
        int min = sec / 60;
        sec -= min * 60;
        String musicTime = String.format("%02d:%02d", min, sec);
        totalTime.setText(musicTime);
        player.start();
    }

    private void updateTimer() {
        runOnUiThread(() -> {
            int currentMs = player.getCurrentPosition();
            int sec = currentMs / 1000;
            int min = sec / 60;
            sec -= min * 60;
            String time = String.format("%02d:%02d", min, sec);
            musicProgress.setProgress(currentMs);
            currentTime.setText(time);
        });
    }

    private class OnClickControl implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_prev:
                    playPauseBtn.setImageResource(R.drawable.pause);
                    if (!player.isPlaying()) {
                        currentPlaying = --currentPlaying % playList.size();
                    }
                    prepareMedia();
                    isPausing = false;
                    isPlaying = true;
                    break;
                case R.id.btn_play_pause:
                    if (!isPausing && !isPlaying) {
                        playPauseBtn.setImageResource(R.drawable.pause);
                        prepareMedia();
                        isPlaying = true;
                    } else if (isPausing && isPlaying) {
                        playPauseBtn.setImageResource(R.drawable.pause);
                        player.start();
                    } else {
                        playPauseBtn.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                        player.pause();
                    }
                    isPausing = !isPausing;
                    break;
                case R.id.btn_next:
                    playPauseBtn.setImageResource(R.drawable.pause);
                    currentPlaying = ++currentPlaying % playList.size();
                    prepareMedia();
                    isPausing = false;
                    isPlaying = true;
                    break;
            }
        }
    }

    private class OnSeekBarChangeControl implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {player.seekTo(progress); }
        }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            player.pause();
        }
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) { player.start(); }
        }
    }
