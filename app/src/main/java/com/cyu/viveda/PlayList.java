package com.cyu.viveda;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import java.io.File;
import java.util.ArrayList;

public class PlayList extends AppCompatActivity {

    ArrayList<File> mySongs;
    int position = 0;
    String sname = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_list);
    }

    public void AddPlayList(View view) {
    }
}