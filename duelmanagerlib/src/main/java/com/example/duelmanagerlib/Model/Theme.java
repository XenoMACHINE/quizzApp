package com.example.duelmanagerlib.Model;

import com.example.duelmanagerlib.TemplateMethod.FirebaseObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by alexandremenielle on 11/04/2018.
 */

public class Theme extends FirebaseObject {

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

    @Override
    public Map<String, Object> toMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("id",id);
        map.put("name",name);
        return map;
    }
}
