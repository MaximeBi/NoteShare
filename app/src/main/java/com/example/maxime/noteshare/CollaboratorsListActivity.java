package com.example.maxime.noteshare;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.internal.widget.ListViewCompat;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class CollaboratorsListActivity extends AppCompatActivity {

    private ArrayAdapter<String> adapter;
    private ArrayList<String> collaborators;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collaborators_list);

        Intent intent = getIntent();
        String title = intent.getStringExtra(MainActivity.NOTE_TITLE);
        collaborators= intent.getStringArrayListExtra(MainActivity.NOTE_COLLABORATORS);

        TextView titleView = (TextView) findViewById(R.id.title);
        titleView.setText(title);
        ListView collaboratorsView = (ListView) findViewById(R.id.collaborators_list);
        titleView.setText(title);
        this.adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, collaborators);
        collaboratorsView.setAdapter(adapter);
    }
}
