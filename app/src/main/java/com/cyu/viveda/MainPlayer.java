package com.cyu.viveda;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainPlayer extends AppCompatActivity implements SensorEventListener {
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

    private SensorManager sensorManager;
    private Sensor accelerometerSensor;
    private Boolean isAccelerometerSensorAvailable , itIsNotTheSameValue = false;
    private float currentX, currentY, currentZ, lastX, lastY, lastZ;
    private float xDifference, yDifference, zDifference;
    private float shakeThresold =5f;
    private Vibrator vibrator;

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

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            isAccelerometerSensorAvailable = true;
        }
        else{
            isAccelerometerSensorAvailable = false;
        }

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

        File test8 = new File(mySongs.get(position).toString());
        Uri u = Uri.fromFile(test8);
        sname=mySongs.get(position).getName();

        songName.setText(sname);
        songName.setSelected(true);

        //Idea for stocage
        //String test = u.toString();
        //Uri good = Uri.parse(test);

        player = MediaPlayer.create(getApplicationContext(),u);

        int musicDuration = player.getDuration();
        musicProgress.setMax(musicDuration);
        int sec = musicDuration / 1000;
        int min = sec / 60;
        sec -= min * 60;
        String musicTime = String.format("%02d:%02d", min, sec);
        totalTime.setText(musicTime);

        musicPrepared= true;
        infoMusicInternet();
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
                    startActivity(intent7);
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

    //TROP FORT
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        Log.e("onNewIntent","onNewIntent");

        Intent listInfo = getIntent();
        ArrayList<String> pathList = listInfo.getStringArrayListExtra("stringList");
        if(pathList!=null){
            Log.e("MAIN PLAYER","Nice Intent");
            ArrayList<File> renewList = new ArrayList<>();
            for(String stringFile : pathList){
                renewList.add(new File(stringFile));
            }
            mySongs = renewList;
            position=listInfo.getIntExtra("index",0);;
            sname = listInfo.getStringExtra("name");
            prepareMedia();
            musicPrepared = true;
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



    public void infoMusicInternet (){
        //Voici la partie independant pour API en ligne
        RequestQueue queue = Volley.newRequestQueue(MainPlayer.this);
        String url ="https://ws.audioscrobbler.com/2.0/?method=track.search&track=" +
                sname.replace(".mp3","").replace(".wav","") +
                "&limit=2&api_key=24f4c03a73a91359dc8a79fe0108d9d8&format=json";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONObject actualtrackInfo = null;
                String name = "";
                String artist = "";
                try {
                    JSONObject songdetails = response.getJSONObject("results");
                    JSONObject trackmatches = songdetails.getJSONObject("trackmatches");
                    JSONArray track = trackmatches.getJSONArray("track");
                    actualtrackInfo = track.optJSONObject(0);
                    name = actualtrackInfo.getString("name");
                    artist = actualtrackInfo.getString("artist");

                    // name = trackInfo.getString("artist");

                    //  String songname = songdetails.getString("name");
                    //String singername = songdetails.getString("track");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Toast.makeText(MainPlayer.this, "Song Name =" + name +"\n"+ "Artist ="+ artist, Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainPlayer.this, "An error has occured while fetching the data", Toast.LENGTH_SHORT).show();

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


    //There is the part for sensor
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        currentX= sensorEvent.values[0];
        currentY = sensorEvent.values[1];
        currentZ = sensorEvent.values[2];

        if (itIsNotTheSameValue == true) {
            xDifference = Math.abs(lastX - currentX);
            yDifference = Math.abs(lastY - currentY);
            zDifference = Math.abs(lastZ - currentZ);

            if ((xDifference > shakeThresold && yDifference > shakeThresold) ||
                    (xDifference > shakeThresold && zDifference > shakeThresold) ||
                    (yDifference > shakeThresold && zDifference > shakeThresold)
            ) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                    if(musicPrepared) {
                        playPauseBtn.setImageResource(R.drawable.pause);
                        position = ++position % mySongs.size();
                        prepareMedia();
                        player.start();
                        isPausing = false;
                        isPlaying = true;
                        animator.start();
                    }
                } else {
                    vibrator.vibrate(500);
                }
            }
        }
        lastX= currentX;
        lastY = currentY;
        lastZ= currentZ;
        itIsNotTheSameValue = true;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    @Override
    protected void onPostResume() {
        super.onPostResume();

        if(isAccelerometerSensorAvailable){
            sensorManager.registerListener(this,accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(isAccelerometerSensorAvailable){
            sensorManager.unregisterListener(this);
        }
    }
}
