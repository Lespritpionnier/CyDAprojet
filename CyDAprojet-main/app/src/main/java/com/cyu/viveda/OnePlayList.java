package com.cyu.viveda;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;

public class OnePlayList extends AppCompatActivity {

        String thisName;
        ListView listView;
        ArrayList<String> listOfSong = new ArrayList<>();
        String[] items;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_one_play_list);
            listView = findViewById(R.id.SongList);

            Intent intent=getIntent();
            thisName = intent.getStringExtra("name");

            SharedPreferences savedData = getSharedPreferences(thisName, MODE_PRIVATE);
            int size = savedData.getInt(thisName+"size",0);
            listOfSong.clear();
            for(int i=0;i<size;i++) {
                listOfSong.add(savedData.getString(thisName + i, null));
            }
            show();

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    Intent intent = new Intent(OnePlayList.this, MainPlayer.class);

                    Log.e("BEFORE MAIN PLAYER","Restart with:"+listOfSong.toString());

                    intent.putExtra("stringList", listOfSong).putExtra("index",position)
                            .putExtra("name", listView.getItemAtPosition(position).toString().replace(".mp3","").replace(".wav",""));
                    startActivity(intent);
                }
            });
        }


        private void show() {
            items = new String[listOfSong.size()];
            for (int i=0;i<listOfSong.size();i++){
                items[i]= new File(listOfSong.get(i)).getName().replace(".mp3","").replace(".wav","");
            }
            ArrayAdapter<String> adp = new
                    ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
            listView.setAdapter(adp);
        }

        private void saveState() {
            SharedPreferences.Editor edit= getSharedPreferences(thisName, MODE_PRIVATE).edit();
            edit.putInt(thisName+"size",listOfSong.size());

            Log.i("ONE PLAY LIST","STATE saved");

            for(int i=0;i<listOfSong.size();i++) {
                edit.remove(thisName + i);
                edit.putString(thisName + i, listOfSong.get(i));
                edit.commit();
            }
        }

    public void AddSong(View view) {
        Log.i("ONE PLAY LIST","ADD Clicked");
        Intent intent8 = new Intent(OnePlayList.this,FindFile.class);
        startActivityForResult(intent8,8);
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 8:
                if (resultCode == RESULT_OK) {
                    Bundle b = data.getExtras();
                    ArrayList<File> temp =(ArrayList) b.getParcelableArrayList("songs");
                    int position = b.getInt("pos",0);
                    listOfSong.add(temp.get(position).toString());
                    Log.w("ONE PLAY LIST",temp.get(position).toString());
                    saveState();
                    show();
                }
                break;
            default:
        }
    }
}