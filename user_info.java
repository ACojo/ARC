package com.myapp.arc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class user_info extends AppCompatActivity {



    TextView id_hello_textview;





    TextView id_current_marker_text;
    TextView id_marker_command;
    TextView id_marker_name_text;
    TextView id_marker_description;



    private DatabaseReference mdatabase;
    private DatabaseReference userDatabase;
    private DatabaseReference markerDatabase;
    String username;
    String[] available_markers;
    String admin_markers;
    List<marker_db_cass> marker_lst;

    Button id_button_register;
    Button id_previous_button;
    Button id_next_button;



    ImageView id_marker_img;




    int index;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        //to keep track of the current marker that is displayed
        // -1 meaning no marker is available
        index = -1;

        id_hello_textview = (TextView) findViewById(R.id.id_hello_textview);



        //the marker part
        id_current_marker_text = (TextView) findViewById(R.id.id_current_marker_text);
        id_marker_command = (TextView) findViewById(R.id.id_marker_command);
        id_marker_name_text = (TextView)findViewById(R.id.id_marker_name_text);
        id_marker_description = (TextView)findViewById(R.id.id_marker_description);


        id_next_button = (Button)findViewById(R.id.id_next_button);
        id_previous_button = (Button)findViewById(R.id.id_previous_button);
        id_button_register = (Button)findViewById(R.id.id_button_register);





        id_marker_img = (ImageView) findViewById(R.id.id_marker_img);




        mdatabase =  FirebaseDatabase.getInstance("https://arc-db-46a32-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
        userDatabase = mdatabase.child("users");
        markerDatabase = mdatabase.child("markers");

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        admin_markers = intent.getStringExtra("admin_markers");




        id_hello_textview.append(username);
        //available_markers = new ArrayList<>();



        if (!username.equals("admin")){
            id_button_register.setEnabled(false);
        }

        userDatabase.child(username).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


            //must add the code to get what markers can this user see

                user_class user = snapshot.getValue(user_class.class);
                //user = snapshot.getValue(user_class.class);

                if (user != null) {
                    available_markers = user.marker.split(",");

                    if (!available_markers[0].equals("-1")){

                        index = 0;
                        id_marker_description.setText("here");
                        id_previous_button.setEnabled(false);
                        Firebase_marker_listener(available_markers[index],markerDatabase);
                        if (available_markers.length == 1){
                            id_next_button.setEnabled(false);
                            id_previous_button.setEnabled(false);
                        }
                    }
                }

                //id_marker_command.setText(available_markers.length);
                //id_marker_command.setText();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //if there is a marker for this user


        id_button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), add_user.class );
                intent.putExtra("admin_markers", admin_markers);
                startActivity(intent);
            }
        });

        id_next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (  (index + 1) < (available_markers.length - 1 )     ){
                    Firebase_marker_listener(available_markers[index+1],markerDatabase);
                    index = index +1;
                    id_previous_button.setEnabled(true);
                    id_next_button.setEnabled(true);
                }
                else {
                    //meaning index + 1 == available_markers
                    Firebase_marker_listener(available_markers[index+1],markerDatabase);
                    index = index + 1;
                    id_next_button.setEnabled(false);
                    id_previous_button.setEnabled(true);

                }
            }
        });

        id_previous_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(index == 1){

                    Firebase_marker_listener(available_markers[index-1],markerDatabase);
                    id_previous_button.setEnabled(false);
                    id_next_button.setEnabled(true);
                    index = 0;
                }
                else if(index > 1){
                    Firebase_marker_listener(available_markers[index-1],markerDatabase);
                    id_previous_button.setEnabled(true);
                    id_next_button.setEnabled(true);
                    index = index - 1;
                }

            }
        });
    }



    private void Firebase_marker_listener(String id, DatabaseReference markerDatabase){

        //marker_db_cass current_marker;
        markerDatabase.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                marker_db_cass marker_response = snapshot.getValue(marker_db_cass.class);
                //current_marker = new marker_db_cass(marker_response);
                if (marker_response != null) {
                    id_marker_command.setText("Command: " + marker_response.command);
                    id_marker_name_text.setText("Name: " + marker_response.name);
                    id_marker_description.setText("Description: "+ marker_response.description);
                    id_current_marker_text.setText("Marker id: " + id);

                    if(Integer.valueOf(id) /10 == 2) {
                        Bitmap replaceBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img21);
                        id_marker_img.setImageBitmap(replaceBitmap);
                    }
                    else if (Integer.valueOf(id) /10 == 1){
                        Bitmap replaceBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img11);
                        id_marker_img.setImageBitmap(replaceBitmap);
                    }else{
                        Bitmap replaceBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img100);
                        id_marker_img.setImageBitmap(replaceBitmap);
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //return current_marker;

    }





}