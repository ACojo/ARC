package com.myapp.arc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

public class unknown_remote extends AppCompatActivity {

    String function_not_available_message = "Sorry, the current marker does not have a functionality yet";
    String no_access_message = "You do not have access to this marker. Please contact the admin.";
    String username;


    int access;



    TextView id_text_message;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unknown_remote);



        id_text_message = (TextView)findViewById(R.id.id_text_message);




        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        access = intent.getIntExtra("access",0);


        if (access == 1  || username.equals("admin")){

            id_text_message.setText(function_not_available_message);


        }else{
            id_text_message.setText(no_access_message);
        }


    }
}