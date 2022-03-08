package com.myapp.arc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;


public class rgb_remote extends AppCompatActivity {



    TextView current_id_textView;
    TextView current_values_text;
    SeekBar r_seekBar;
    SeekBar g_seekBar;
    SeekBar b_seekBar;

    Button id_button_get;

    int r_value;
    int g_value;
    int b_value;

    //the http queue
    public RequestQueue queue;


    //the http part
    String url = "http://192.168.1.106:5000/led/100,0,0";
    //String url = "http://192.168.0.125:5000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rgb_remote);


        current_id_textView = (TextView) findViewById(R.id.marker_rgb_id_text);
        current_values_text = (TextView) findViewById(R.id.id_text_get);
        r_seekBar = (SeekBar) findViewById(R.id.r_seekBar);

        g_seekBar = (SeekBar) findViewById(R.id.g_seekBar);;
        b_seekBar = (SeekBar) findViewById(R.id.b_seekBar);

        id_button_get = (Button)findViewById(R.id.id_button_get);


        //the http part
        queue = Volley.newRequestQueue(this);
        StringRequest stringRequestGet = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                current_values_text.setText(new String("succes"));

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                current_values_text.setText(new String("FAIL"));
                Log.d("INTERNET", String.valueOf(error.toString()));
            }
        });

        queue.add(stringRequestGet);

        id_button_get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                queue.add(stringRequestGet);
            }
        });



        StringRequest stringRequestPut = new StringRequest(Request.Method.PUT, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                current_values_text.setText(new String("succes"));

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                current_values_text.setText(new String("FAIL"));
                Log.d("INTERNET", String.valueOf(error.toString()));
            }
        });


        r_seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                r_value = progress;
                //fill_text();
                //queue.add(stringRequest);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                //TODO CALL THE POST FUNCTIONS
                queue.add(stringRequestGet);
            }

            
        });

        g_seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                g_value = progress;
                fill_text();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                //TODO CALL THE POST FUNCTIONS
            }
        });

        b_seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                b_value = progress;
                fill_text();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                //TODO CALL THE POST FUNCTIONS
            }
        });



        Intent intent = getIntent();
        int current_marker_id= intent.getIntExtra("current_marker_id",-1);
        Log.d("RGB", String.valueOf(current_marker_id));



        current_id_textView.setText(String.valueOf(current_marker_id));








    }

    void fill_text(){
        current_values_text.setText((String.valueOf(r_value) +" "+ String.valueOf(g_value)+" " + String.valueOf(b_value)));
        Log.d("CURRENT_VALUES", String.valueOf(r_value) + String.valueOf(g_value) + String.valueOf(b_value));
    }



    @Override
    protected void onStop(){
        super.onStop();
        if(queue != null){
            queue.cancelAll("TAG");
        }
    }
}