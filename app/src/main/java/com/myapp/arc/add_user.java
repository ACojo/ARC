package com.myapp.arc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.NoSuchAlgorithmException;

public class add_user extends AppCompatActivity {

    private DatabaseReference mdatabase;
    private DatabaseReference userDatabase;
    private DatabaseReference markerDatabase;
    private encryption Encryption;
    private String hash = "8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918";

    Button button_marker_create_update;
    Button button_user_create_update;
    Button button_user_delete;
    Button button_marker_delete;


    EditText text_user_user;
    EditText text_user_password;
    EditText text_user_name;
    EditText text_user_surname;
    EditText text_user_marker;




    EditText text_marker_id;
    EditText text_marker_command;
    EditText text_marker_description;
    EditText text_marker_name;









    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        button_marker_delete = (Button) findViewById(R.id.button_marker_delete);
        button_user_delete= (Button) findViewById(R.id.button_user_delete);
        button_user_create_update = (Button) findViewById(R.id.button_user_create_update);
        button_marker_create_update = (Button) findViewById(R.id.button_marker_create_update);

        text_user_marker = (EditText) findViewById(R.id.text_user_marker);
        text_user_surname= (EditText) findViewById(R.id.text_user_surname);
        text_user_name= (EditText) findViewById(R.id.text_user_name);
        text_user_password= (EditText) findViewById(R.id.text_user_password);
        text_user_user= (EditText) findViewById(R.id.text_user_user);


        text_marker_id = (EditText) findViewById(R.id.text_marker_id);
        text_marker_command = (EditText) findViewById(R.id.text_marker_command);
        text_marker_description = (EditText) findViewById(R.id.text_marker_description);
        text_marker_name = (EditText) findViewById(R.id.text_marker_name);



        mdatabase =  FirebaseDatabase.getInstance("https://arc-db-46a32-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
        userDatabase = mdatabase.child("users");
        markerDatabase = mdatabase.child("markers");


        Intent intent = getIntent();
        String admin_markers = intent.getStringExtra("admin_markers");


        try {
            Encryption = new encryption();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            Toast.makeText(add_user.this,"Could not initialize encryption algorithms",Toast.LENGTH_SHORT).show();
        }


        button_marker_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String id = text_marker_id.getText().toString();
                if( !id.isEmpty()){


//                    markerDatabase.addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                            if (!snapshot.hasChild(id)){
//                                //Toast.makeText(add_user.this, "Id does not exist", Toast.LENGTH_SHORT).show();
//                            }
//
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//
//                        }
//                    });
                    if(markerDatabase.child(id) != null){

                        userDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Log.d("FIREBASE2",id);
                                for (DataSnapshot Datasnapshot : snapshot.getChildren()){
                                    user_class user = Datasnapshot.getValue(user_class.class);

                                    if (user.marker.contains(id)){
                                        Log.d("FIREBASE X",id+" "+ user.name);
                                        String new_marker = new String();
//                                        for (int i = 0 ; i <user.marker.length(); i ++){
//                                            if (i == user.marker.indexOf(id)){
//                                                i = i + 2;
//                                            }
//                                            new_marker += user.marker.charAt(i);
//                                        }

                                        String[] user_markers = user.marker.split(",");
                                        for (int i = 0 ; i <user_markers.length; i ++){
                                            if ( !user_markers[i].equals(id) ){
                                                new_marker += user_markers[i];
                                                new_marker += ",";
                                            }

                                        }
                                        if (new_marker.endsWith(",")){
                                            new_marker = new_marker.substring(0, new_marker.length()-1);
                                        }

                                        userDatabase.child(Datasnapshot.getKey()).child("marker").setValue(new_marker);
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        markerDatabase.child(id).removeValue();
                        Toast.makeText(add_user.this, "Marker removed", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(add_user.this, "No marker with this id", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(add_user.this, "Please provide an id", Toast.LENGTH_SHORT).show();
                }
                //Toast.makeText(add_user.this, new String(String.valueOf(markerDatabase.child("asasdsayed"))), Toast.LENGTH_SHORT).show();


            }
        });




        button_user_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


//                String marker = text_user_marker.getText().toString();
//                String surname = text_user_surname.getText().toString();
//                String name = text_user_name.getText().toString();
//                String password =text_user_password.getText().toString();
                String user = text_user_user.getText().toString();
                String user_hash;
                if( !user.isEmpty()){


//                    userDatabase.addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                            if (!snapshot.hasChild(user)){
//                                Toast.makeText(add_user.this, "User does not exist", Toast.LENGTH_SHORT).show();
//                            }
//
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//
//                        }
//                    });
                    user_hash = Encryption.hashing(user);
                    if(userDatabase.child(user_hash) != null) {
                        userDatabase.child(user_hash).removeValue();
                        Toast.makeText(add_user.this, "User removed", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(add_user.this, "No user with this available", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(add_user.this, "Please provide an user", Toast.LENGTH_SHORT).show();
                }


            }
        });


        button_user_create_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String marker = text_user_marker.getText().toString();
                String surname = text_user_surname.getText().toString();
                String name = text_user_name.getText().toString();
                String password =text_user_password.getText().toString();
                String user = text_user_user.getText().toString();

                String user_hash;
                String password_hash;

                if(!(marker.isEmpty() || surname.isEmpty() || name.isEmpty() || password.isEmpty() || user.isEmpty())){

//                    userDatabase.addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
////                            if (snapshot.hasChild(user)){
////                                Toast.makeText(add_user.this, "user already exists, updating...", Toast.LENGTH_SHORT).show();
////                                //Toast.makeText(add_user.this, user, Toast.LENGTH_SHORT).show();
////                                //user_class update_user = new user_class( marker,  name,  password,  surname);
////                                // sometimes, the values do not update so I remote the child and recreate it
////                                //userDatabase.child(user).removeValue();
////
////                            }
////                            else{
////                                Toast.makeText(add_user.this, "Creating user...", Toast.LENGTH_SHORT).show();
////
////                                //userDatabase.child(user).setValue(update_user);
////                            }
//                            //Toast.makeText(add_user.this, "Updating...", Toast.LENGTH_SHORT).show();
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//
//                        }
//                    });
                    user_hash = Encryption.hashing(user);
                    password_hash = Encryption.hashing(password);
                    if(userDatabase.child(user_hash) != null) {
                        user_class update_user = new user_class(marker, name, password_hash, surname);
                        userDatabase.child(user_hash).setValue(update_user);
                        Toast.makeText(add_user.this, "User updated", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(add_user.this, "User created", Toast.LENGTH_SHORT).show();
                    }
                }





            }
        });


        button_marker_create_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String id = text_marker_id.getText().toString();
                String command = text_marker_command.getText().toString();
                String description = text_marker_description.getText().toString();
                String name =text_marker_name.getText().toString();

                if(!(id.isEmpty() || command.isEmpty() || name.isEmpty() || description.isEmpty())){


                    if(markerDatabase.child(id) != null){
                        marker_db_cass update_marker = new marker_db_cass(command,  description,  name,  Integer.parseInt(id));
                        markerDatabase.child(id).setValue(update_marker);
                        Toast.makeText(add_user.this, "Marker updated", Toast.LENGTH_SHORT).show();
                        //admin_markers = admin_markers + ","+id;

                        // the new marker will be automatically added to the admin
                        userDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                user_class db_admin;
                                db_admin = snapshot.child(hash).getValue(user_class.class);
                                if(!db_admin.marker.contains(id)){
                                    userDatabase.child(hash).child("marker").setValue(db_admin.marker + ","+id);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                    else{
                        Toast.makeText(add_user.this, "Marker created", Toast.LENGTH_SHORT).show();
                    }

                }


            }
        });




    }
}