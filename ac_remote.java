package com.myapp.arc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class ac_remote extends AppCompatActivity {


    TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ac_remote);



        textView = (TextView)findViewById(R.id.marker_ac_id_text);

        Intent intent = getIntent();
        int current_marker_id= intent.getIntExtra("current_marker_id",-1);
        Log.d("AC", String.valueOf(current_marker_id));



        textView.setText(String.valueOf(current_marker_id));
    }
}