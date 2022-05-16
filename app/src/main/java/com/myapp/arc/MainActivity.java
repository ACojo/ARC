package com.myapp.arc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.NoSuchAlgorithmException;


public class MainActivity extends AppCompatActivity {

    TextView received_data_text;
    EditText id_username;
    EditText id_password;
    Button id_button;

    //the new branch works
    private DatabaseReference mDatabase;
    private DatabaseReference userDatabase;
    private encryption Encryption;
    private String hash = "8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918";


    String[] available_markers;

    //test
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        received_data_text = (TextView) findViewById(R.id.received_data_text);
        id_username = (EditText) findViewById(R.id.id_username);
        id_password = (EditText) findViewById(R.id.id_password);
        id_button = (Button) findViewById(R.id.id_button);
        try {
            Encryption = new encryption();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this,"Could not initialize encryption algorithms",Toast.LENGTH_SHORT).show();

        }

        try {
            FirebaseDatabase.getInstance("https://arc-db-46a32-default-rtdb.europe-west1.firebasedatabase.app/").setPersistenceEnabled(true);
        }catch (Exception e){
            Log.d("OFFLINE DATABASE", "already enabled this feature");
        }

        //mDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl("https://arc-db-46a32-default-rtdb.europe-west1.firebasedatabase.app/");
        mDatabase = FirebaseDatabase.getInstance("https://arc-db-46a32-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

        userDatabase = mDatabase.child("users");
        userDatabase.keepSynced(true);


        id_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




                String user_input = id_username.getText().toString();
                String password_input = id_password.getText().toString();

                if(user_input.isEmpty() || password_input.isEmpty()){
                    Toast.makeText(MainActivity.this,"Please enter both the user and the password",Toast.LENGTH_SHORT).show();
                }
                else{
                    //Toast.makeText(marker_detection.this,"click",Toast.LENGTH_SHORT).show();
                    final String hashed_username;
                    hashed_username = Encryption.hashing(user_input);

                    userDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {





                            //Toast.makeText(marker_detection.this,new String(String.valueOf(snapshot.hasChild(user_input))),Toast.LENGTH_SHORT).show();
                            if(snapshot.hasChild(hashed_username)){
                                // the name will be addressed in the marker_detection activity
//                                final String db_name = snapshot.child(user_input).child("name").getValue(String.class);
//                                final String db_password = snapshot.child(user_input).child("password").getValue(String.class);

                                final String db_name = snapshot.child(hashed_username).child("name").getValue(String.class);
                                final String db_password = snapshot.child(hashed_username).child("password").getValue(String.class);

                                final String hashed_password;
                                //Toast.makeText(marker_detection.this,db_password,Toast.LENGTH_SHORT).show();


                                hashed_password = Encryption.hashing(password_input);

                                if (db_password.equals(hashed_password)){
                                    Toast.makeText(MainActivity.this,"Login succesful",Toast.LENGTH_SHORT).show();
                                    user_class user = snapshot.child(hashed_username).getValue(user_class.class);
                                    available_markers = user.marker.split(",");
                                    String admin_markers = snapshot.child(hash).child("marker").getValue(String.class);
                                    //done sada

                                    Intent intent = new Intent(getApplicationContext(), marker_detection.class );

                                    intent.putExtra("markers",available_markers);
                                    intent.putExtra("name", db_name);
                                    intent.putExtra("username",user_input);
                                    intent.putExtra("admin_markers", admin_markers);
                                    id_username.setText("");
                                    id_password.setText("");

                                    startActivity(intent);
                                }
                                else{
                                    Toast.makeText(MainActivity.this,"Incorect password or user",Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(MainActivity.this,error.getMessage().toString(),Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
















    }



}
