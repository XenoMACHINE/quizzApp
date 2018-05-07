package com.example.alexandremenielle.quizzapp.Model;

/**
 * Created by alexandremenielle on 01/05/2018.
 */

public class User {

    private String id;
    private String firstname;
    private String lastname;
    private Boolean isOnline;

    public User() {
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
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


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
