package com.myapp.arc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;


public class rgb_remote extends AppCompatActivity {



    TextView current_id_textView;
    TextView request_response;
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
    String url = "http://192.168.1.100:5000/led/";
    //String url = "http://192.168.1.105:5000/led/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rgb_remote);


        current_id_textView = (TextView) findViewById(R.id.marker_rgb_id_text);
        request_response = (TextView) findViewById(R.id.request_response);

        r_seekBar = (SeekBar) findViewById(R.id.r_seekBar);
        g_seekBar = (SeekBar) findViewById(R.id.g_seekBar);;
        b_seekBar = (SeekBar) findViewById(R.id.b_seekBar);



        queue = Volley.newRequestQueue(this);


        // to get the current status of the rgb led
        StringRequest stringRequest =  new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //current_values_text.setText(response);
                String[]values = response.split("\\s");
                //Log.d("VALUES", values[0] + " " + values[1] + " " + values[2]);
                r_seekBar.setProgress( Integer.valueOf(values[0]));
                g_seekBar.setProgress( Integer.valueOf(values[1]));
                b_seekBar.setProgress( Integer.valueOf(values[2]));
                request_response.setText( new String("Request succesful"));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Internet", "ON create error");
                request_response.setText( new String("Request unsuccesful"));
            }
        });

        queue.add(stringRequest);
        r_seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                r_value = progress;
                //fill_text();
                //queue.add(stringRequest);
                StringRequest stringRequestPut = create_string_request(url,r_value,g_value,b_value);
                queue.add(stringRequestPut);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                //TODO CALL THE POST FUNCTIONS
                StringRequest stringRequestPut = create_string_request(url,r_value,g_value,b_value);
                queue.add(stringRequestPut);
            }

            
        });

        g_seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                g_value = progress;
                //fill_text();
                StringRequest stringRequestPut = create_string_request(url,r_value,g_value,b_value);
                queue.add(stringRequestPut);
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
                //fill_text();
                StringRequest stringRequestPut = create_string_request(url,r_value,g_value,b_value);
                queue.add(stringRequestPut);
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




        //the http part

//        StringRequest stringRequestGet = new StringRequest(Request.Method.PUT, url, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                current_values_text.setText(new String("succes"));
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                current_values_text.setText(new String("FAIL"));
//                Log.d("INTERNET", String.valueOf(error.toString()));
//            }
//        });
//
//        queue.add(stringRequestGet);
//
//        id_button_get.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                queue.add(stringRequestGet);
//            }
//        });







    }

    void fill_text(){
        request_response.setText((String.valueOf(r_value) +" "+ String.valueOf(g_value)+" " + String.valueOf(b_value)));
        Log.d("CURRENT_VALUES", String.valueOf(r_value) + String.valueOf(g_value) + String.valueOf(b_value));
    }

    private StringRequest create_string_request(String url,int r_value, int g_value,int b_value){
        //http://192.168.1.106:5000/led/?r_value=10&g_value=10&b_value=10
        //String custom_url = url + new String("r_value=") + String.valueOf(r_value) + new String("&g_value=") +String.valueOf(g_value)
                //+ new String("&b_value=") + String.valueOf(b_value);
        StringRequest stringRequestPut = new StringRequest(Request.Method.PUT, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                request_response.setText(new String("Request succesful"));
                Log.d("MUST SEE",response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                request_response.setText(new String("FAIL"));
                Log.d("INTERNET", String.valueOf(error.toString()));
                request_response.setText(new String("Request unsuccesful"));
            }
        }){
            @Override
            protected Map<String,String> getParams(){

                Map<String,String> params = new HashMap<String,String>();
                params.put("r_value" , String.valueOf(r_value));
                params.put("g_value" , String.valueOf(g_value));
                params.put("b_value" , String.valueOf(b_value));
                return params;
            }
        };
        return stringRequestPut;
    }


    @Override
    protected void onStop(){
        super.onStop();
        if(queue != null){
            queue.cancelAll("TAG");
        }
    }
}