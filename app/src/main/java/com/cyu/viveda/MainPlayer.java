package com.cyu.viveda;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainPlayer extends AppCompatActivity {
    private int requestCode;
    private int resultCode;
    private Intent data;

    MainPlayer getPlayer (){
       return MainPlayer.this;
   }
    private TextView songName, singerName;
    private ImageView diskImage;
    private SeekBar musicProgress;
    private TextView currentTime, totalTime;
    private ImageButton prevBtn, playPauseBtn, nextBtn, findBtn, listBtn;
    int position = 0;

    ArrayList<File> mySongs;


    public static MediaPlayer player;
    String sname = null;


/* for test before
    private int currentPlaying = 0;
    private ArrayList<Integer> playList = new ArrayList<>();

 */

    private boolean isPausing = false;
    private boolean isPlaying = false;

    /* juste pour s'amuser, tourner l'image pendant music */
    private ObjectAnimator animator;

    boolean musicPrepared = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_main);

        init();
     //   Intent intent = new Intent(MainPlayer.this,FindFile.class);
     //   startActivityForResult(intent,6);
        //
        //    prepareMedia();

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
        findBtn = findViewById(R.id.btn_find);
        listBtn = findViewById(R.id.btn_list);

        OnClickControl onClick = new OnClickControl();
        prevBtn.setOnClickListener(onClick);
        playPauseBtn.setOnClickListener(onClick);
        nextBtn.setOnClickListener(onClick);
        findBtn.setOnClickListener(onClick);
        listBtn.setOnClickListener(onClick);

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
       // Intent i = getIntent();
      //  Bundle b = i.getExtras();

       // mySongs = (ArrayList) b.getParcelableArrayList("songs");

     //   sname = mySongs.get(position).getName().toString();

     //   position = b.getInt("pos",0);

      //  Uri sharedFileUri = FileProvider.getUriForFile(this, FindFile, mySongs.get(position));
        Uri u = Uri.fromFile(mySongs.get(position));
        sname=mySongs.get(position).getName();

        songName.setText(sname);
        songName.setSelected(true);

        String test = u.toString();
        Uri good = Uri.parse(test);
        player = MediaPlayer.create(getApplicationContext(),good);

        int musicDuration = player.getDuration();
        musicProgress.setMax(musicDuration);
        int sec = musicDuration / 1000;
        int min = sec / 60;
        sec -= min * 60;
        String musicTime = String.format("%02d:%02d", min, sec);
        totalTime.setText(musicTime);

        musicPrepared= true;
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

                //NEW PART ICI TRES FORT
                case R.id.btn_find:
                    Intent intent6 = new Intent(MainPlayer.this,FindFile.class);
                    startActivityForResult(intent6,6);
                    break;
                case R.id.btn_list:
                    Intent intent7 = new Intent(MainPlayer.this,PlayList.class);
                    startActivityForResult(intent7,7);
                    break;

                case R.id.btn_prev:
                    if(musicPrepared) {
                        playPauseBtn.setImageResource(R.drawable.pause);
                            //Better to presenter in this way
                        //if (!player.isPlaying())
                        if(position!=0)
                            position = --position % mySongs.size();
                        else position = mySongs.size()-1;
                        prepareMedia();
                        player.start();
                        isPausing = false;
                        isPlaying = true;
                        animator.start();
                    }
                    break;
                case R.id.btn_play_pause:
                    if(musicPrepared) {
                        if (!isPausing && !isPlaying) {
                            playPauseBtn.setImageResource(R.drawable.pause);
                            player.start();
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
                    }
                    break;
                case R.id.btn_next:
                    if(musicPrepared) {
                        playPauseBtn.setImageResource(R.drawable.pause);
                        position = ++position % mySongs.size();
                        prepareMedia();
                        player.start();
                        isPausing = false;
                        isPlaying = true;
                        animator.start();
                        break;
                    }
                default:
            }
        }
    }

    //CONTINUER FORT
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 6:
            case 7:
                if (resultCode == RESULT_OK) {
                    Bundle b = data.getExtras();
                    mySongs=(ArrayList) b.getParcelableArrayList("songs");
                    position = b.getInt("pos",0);
                    sname = b.getString("songname");
                    prepareMedia();
                }
                break;
            default:
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
