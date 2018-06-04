package com.example.duelmanagerlib.Model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by alexandremenielle on 01/05/2018.
 */

public class User {

    private String id;
    private String firstname;
    private String lastname;
    private String mail;
    private Boolean isOnline;

    public User() {
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getFullName() {
        return firstname + " " + lastname;
    }

    public Boolean getIsOnline() {
        return isOnline;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void setIsOnline(Boolean online) {
        isOnline = online;
    }

    public String getMail() {return mail;}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setMail(String mail) {this.mail = mail;}

    public Map<String,Object> toMap(){
        HashMap<String,Object> map = new HashMap<>();
        map.put("firstname",firstname);
        map.put("lastname",lastname);
        map.put("mail",mail);
        return map;
    }
}
