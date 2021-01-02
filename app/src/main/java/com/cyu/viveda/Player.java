package com.cyu.viveda;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Player extends AppCompatActivity {
   Player getPlayer (){
       return Player.this;
   }
    private TextView songName, singerName;
    private ImageView diskImage;
    private SeekBar musicProgress;
    private TextView currentTime, totalTime;
    private ImageButton prevBtn, playPauseBtn, nextBtn;
    int position;
    ArrayList<File> mySongs;


    public static MediaPlayer player;
    String sname;
    private int currentPlaying = 0;
    private ArrayList<Integer> playList = new ArrayList<>();

    private boolean isPausing = false;
    private boolean isPlaying = false;

    /* juste pour s'amuser, tourner l'image pendant music */
    private ObjectAnimator animator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_main);

        init();
        prepareMedia();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                if (player != null) {
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

        animator = ObjectAnimator.ofFloat(diskImage, "rotation", 0, 360.0F);
        animator.setDuration(5000);
        animator.setInterpolator(new LinearInterpolator());
        animator.setRepeatCount(-1);
    }

 /*   private void preparePlaylist() {
        if (player !=null) {
            player.stop();}

        Field[] fields = R.raw.class.getFields();
        for (int count = 0; count < fields.length; count++) {
            try {
                int resId = fields[count].getInt(fields[count]);
                playList.add(resId);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }*/

    private void prepareMedia() {

        if (player !=null) {
            player.stop();
            player.release();
        }
        Intent i = getIntent();
        Bundle b = i.getExtras();

        mySongs = (ArrayList) b.getParcelableArrayList("songs");

        sname = mySongs.get(position).getName().toString();

        position = b.getInt("pos",0);
        Uri u = Uri.parse(mySongs.get(position).toString());

        songName.setText(sname);
        songName.setSelected(true);

        player = MediaPlayer.create(getApplicationContext(),u);

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
                    animator.start();
                    break;
                case R.id.btn_play_pause:
                    if (!isPausing && !isPlaying) {
                        playPauseBtn.setImageResource(R.drawable.pause);
                        prepareMedia();
                        isPlaying = true;
                        animator.start();
                    } else if (isPausing && isPlaying) {
                        playPauseBtn.setImageResource(R.drawable.pause);
                        player.start();
                        animator.resume();
                        isPausing = !isPausing;
                    } else {
                        playPauseBtn.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                        player.pause();
                        animator.pause();
                        isPausing = !isPausing;
                    }
                    break;
                case R.id.btn_next:
                    playPauseBtn.setImageResource(R.drawable.pause);
                    currentPlaying = ++currentPlaying % playList.size();
                    prepareMedia();
                    isPausing = false;
                    isPlaying = true;
                    animator.start();
                    break;
            }
        }
    }

    public void Stop(){

       if(isPlaying)

           player.stop();

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
