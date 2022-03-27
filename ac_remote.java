package com.myapp.arc;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.camera2.internal.compat.quirk.StillCaptureFlashStopRepeatingQuirk;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class ac_remote extends AppCompatActivity {


    TextView textView;
    TextView id_text_req_status;
    SeekBar speed_seekbar;
    Switch rotation_switch;
    int speed_value;
    String url = "http://192.168.1.100:5000/ac_motor/";
    public RequestQueue queue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ac_remote);



        textView = (TextView)findViewById(R.id.marker_ac_id_text);
        speed_seekbar = (SeekBar)findViewById(R.id.speed_seekbar);
        rotation_switch = (Switch)findViewById(R.id.rotation_switch);
        id_text_req_status = (TextView)findViewById(R.id.id_text_req_status);

        Intent intent = getIntent();
        int current_marker_id= intent.getIntExtra("current_marker_id",-1);
        Log.d("AC", String.valueOf(current_marker_id));

        textView.setText(String.valueOf(current_marker_id));


        queue = Volley.newRequestQueue(this);



        StringRequest stringRequest =  new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //current_values_text.setText(response);
                String[]values = response.split("\\s");
                speed_seekbar.setProgress( Integer.valueOf(values[0]));
                rotation_switch.setChecked( Boolean.valueOf(values[1]));
                id_text_req_status.setText( new String("Request succesful"));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Internet", "ON create error");
                id_text_req_status.setText( new String("Request unsuccesful"));
            }
        });

        queue.add(stringRequest);










        speed_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                speed_value = progress;
                StringRequest stringRequestPut = create_string_request(url);
                queue.add(stringRequestPut);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });




    }

    private StringRequest create_string_request(String url) {

        StringRequest stringRequest = new StringRequest(Request.Method.PUT, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("AC REQUEST",response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("AC REQUEST ERROR", String.valueOf(error.toString()));
            }
        }){
            @Override
            protected Map<String, String> getParams(){
                Map<String,String> params = new HashMap<String,String>();
                params.put("speed_value" , String.valueOf(speed_value));
                params.put("rotation", String.valueOf(rotation_switch.isChecked()));
                return params;
            }
        };
        return stringRequest;
    }



}