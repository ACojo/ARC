package com.myapp.arc;

public class marker_db_cass {
    public String command;
    public String description;
    public String name;
    public int id;


    public marker_db_cass(String command, String description, String name, int id) {
        this.command = command;
        this.description = description;
        this.name = name;
        this.id = id;
    }

    public marker_db_cass() {

    }

    public marker_db_cass(marker_db_cass obj){
        this.command = obj.command;
        this.description = obj.description;
        this.name = obj.name;
        this.id = obj.id;
    }


}
