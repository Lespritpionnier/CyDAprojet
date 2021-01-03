package com.cyu.viveda;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;

import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.util.ArrayList;


public class FindFile extends AppCompatActivity {
    ListView listView;
    String[] items;
    int count=0;
    public static final int PERMISSIONS_REQUEST = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tunes);
        listView = (ListView) findViewById(R.id.listView);

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                PERMISSIONS_REQUEST);
        display();

        ArrayList<String> list_items=new ArrayList<String>();

        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                count=count+1;
                mode.setTitle(count+" item's selected");
                list_items.add(listView.getItemAtPosition(position).toString());
            }

            @SuppressLint("ResourceType")
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.my_context_menu,menu
                        );
                return true ;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
              switch (item.getItemId()){
                  case(R.id.add) :
                  //send the selected song in the new playlist
                      AddPlayList playlist= new AddPlayList();
                      playlist.setListSelected(list_items);

                      startActivity(new Intent(getApplicationContext(), AddPlayList.class));

                      count=0;
                      mode.finish();
                      return true;

                      //break;
                  default: return false;
              }
            }
            @Override
            public void onDestroyActionMode(ActionMode mode) {
                count=0;
            }
        });
    }

    public ArrayList<File> findSong(File root){
        ArrayList<File> at = new ArrayList<File>();
        File[] files = root.listFiles();
        for(File singleFile : files){
            if(singleFile.isDirectory() && !singleFile.isHidden()){
                at.addAll(findSong(singleFile));
            }
            else{
                if(singleFile.getName().endsWith(".mp3") ||
                        singleFile.getName().endsWith(".wav")){
                    at.add(singleFile);
                }
            }
        }
        return at;
    }

    void display(){
        final ArrayList<File> mySongs = findSong(Environment.getExternalStorageDirectory());
        items = new String[ mySongs.size() ];
        for(int i=0;i<mySongs.size();i++){
            //toast(mySongs.get(i).getName().toString());
            items[i] = mySongs.get(i).getName().toString().replace(".mp3","").replace(".wav","");
        }
        ArrayAdapter<String> adp = new
                ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,items);
        listView.setAdapter(adp);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String songName = listView.getItemAtPosition(position).toString();
               Intent intent = new Intent();
                        intent.putExtra("songs",mySongs).putExtra("pos",position)
                        .putExtra("songname",songName);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }
}
