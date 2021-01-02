package com.cyu.viveda;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    Button btn_info;
    private MediaPlayer player;

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
        btn_info = findViewById(R.id.btn_info);

        OnClickControl onClick = new OnClickControl();
        prevBtn.setOnClickListener(onClick);
        playPauseBtn.setOnClickListener(onClick);
        nextBtn.setOnClickListener(onClick);
        btn_info.setOnClickListener(onClick);

        OnSeekBarChangeControl onChange = new OnSeekBarChangeControl();
        musicProgress.setOnSeekBarChangeListener(onChange);

        animator = ObjectAnimator.ofFloat(diskImage, "rotation", 0, 360.0F);
        animator.setDuration(5000);
        animator.setInterpolator(new LinearInterpolator());
        animator.setRepeatCount(-1);
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

    //info button





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
                    } else {
                        playPauseBtn.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                        player.pause();
                        animator.pause();
                    }
                    isPausing = !isPausing;
                    break;
                case R.id.btn_next:
                    playPauseBtn.setImageResource(R.drawable.pause);
                    currentPlaying = ++currentPlaying % playList.size();
                    prepareMedia();
                    isPausing = false;
                    isPlaying = true;
                    animator.start();
                    break;

                case R.id.btn_info:

// Instantiate the RequestQueue.
                    RequestQueue queue = Volley.newRequestQueue(MusicPlayer.this);
                    String url ="http://ws.audioscrobbler.com/2.0/?method=track.search&track=hello&limit=2&api_key=24f4c03a73a91359dc8a79fe0108d9d8&format=json";


                    JsonObjectRequest  request = new JsonObjectRequest(Request.Method.GET, url,null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                    JSONArray songdetails = response.getJSONArray("results");
                                            for(int i=0; i< songdetails.length(); i++) {
                                                //  String songname = songdetails.getString("name");
                                                //String singername = songdetails.getString("track");
                                                JSONObject myIterator = songdetails.getJSONObject( i );
                                                JSONArray arrayOne = myIterator.getJSONArray( "track" );

                                                for(int j=0; j<arrayOne.length(); j++){
                                                    JSONObject myInnerIterator = arrayOne.getJSONObject( j );
                                                    if(myInnerIterator.has( "name" )) {//check if 'size' key is present
                                                        String nomchanteur = myInnerIterator.getString("name");
                                                        Toast.makeText(MusicPlayer.this, "song Name =" +nomchanteur , Toast.LENGTH_SHORT).show();

                                                    }
                                                }
                                            }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            Toast.makeText(MusicPlayer.this, "song Name ="  , Toast.LENGTH_SHORT).show();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(MusicPlayer.this, "An error has occured wjile fetching the data", Toast.LENGTH_SHORT).show();

                        }
                    });

                    // Add the request to the RequestQueue.
                    queue.add(request);
// Request a string response from the provided URL.
                /*    StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    // Display the first 500 characters of the response string.
                                    Toast.makeText(MusicPlayer.this, response, Toast.LENGTH_SHORT).show();
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(MusicPlayer.this, "An error has occured while fetching the data", Toast.LENGTH_SHORT).show();

                        }
                    });  */


                    //Toast.makeText(MusicPlayer.this,"The name of the artist is: -------", Toast.LENGTH_SHORT).show();
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
