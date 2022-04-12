package com.myapp.arc;


import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class user_class {
    public String marker;
    public String name;
    public String password;
    public String surname;

    public user_class(){

    }


    public user_class(String marker, String name, String password, String surname) {
        this.marker = marker;
        this.name = name;
        this.password = password;
        this.surname = surname;
    }
}
