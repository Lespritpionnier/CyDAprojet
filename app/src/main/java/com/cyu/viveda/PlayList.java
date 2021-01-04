package com.cyu.viveda;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class PlayList extends AppCompatActivity {


    ListView listView;
    ArrayList<String> listOfList = new ArrayList<>();
    String[] items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_list);
        listView = findViewById(R.id.listview);


        SharedPreferences savedData = getSharedPreferences("only",MODE_PRIVATE);
        int size = savedData.getInt("size",0);
        listOfList.clear();
        for(int i=0;i<size;i++) {
            listOfList.add(savedData.getString("only" + i, null));
        }

        show();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(PlayList.this, OnePlayList.class);
                intent.putExtra("name", listView.getItemAtPosition(position).toString());
                startActivity(intent);
            }
        });
    }

    public void AddPlayList(View view) {
        EditText nameList = findViewById(R.id.nameList);
        String name = nameList.getText().toString();
        if(name.trim().equals("")) {
            nameList.setError( "Name is required" );
            return;
        }
        listOfList.add(name);
        saveState();
        show();
    }

    private void show() {
        items = listOfList.toArray(new String[listOfList.size()]);
        ArrayAdapter<String> adp = new
                ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        listView.setAdapter(adp);
    }

    private void saveState() {
        SharedPreferences.Editor edit = getSharedPreferences("only", MODE_PRIVATE).edit();
        edit.putInt("size", listOfList.size());

        for (int i = 0; i < listOfList.size(); i++) {
            edit.remove("only" + i);
            edit.putString("only" + i, listOfList.get(i));
            edit.commit();
        }
    }
}