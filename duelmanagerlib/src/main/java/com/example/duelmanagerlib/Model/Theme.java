package com.example.duelmanagerlib.Model;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by alexandremenielle on 11/04/2018.
 */

public class Theme {

    private String id;
    private String name;
    private HashMap<String,Boolean> questions;

    public Theme() {
    }

    public String getId() {
        return id;
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

}
