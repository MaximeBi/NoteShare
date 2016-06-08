package com.example.maxime.noteshare;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class SmartContentActivity extends AppCompatActivity {

    private String request, result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_content);

        Intent intent = getIntent();
        this.request = intent.getStringExtra(MainActivity.REQUEST);
        this.result = intent.getStringExtra(MainActivity.RESULT);

        TextView requestView = (TextView) findViewById(R.id.request);
        requestView.setText(request);

        TextView resultView = (TextView) findViewById(R.id.result);
        resultView.setText(result);
    }
}
