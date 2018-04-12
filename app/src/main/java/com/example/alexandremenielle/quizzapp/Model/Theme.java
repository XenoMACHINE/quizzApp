package com.example.alexandremenielle.quizzapp.Model;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by alexandremenielle on 11/04/2018.
 */

public class Theme {

    private int id;
    private String name;
    private HashMap<String,Boolean> questions;

    public Theme() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    @Override

    public String toString() {
        return "Theme{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", questions=" + questions +
                '}';
    }

    public void setName(String name) {
        this.name = name;
    }

}
