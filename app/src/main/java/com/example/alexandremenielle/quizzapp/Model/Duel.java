package com.example.alexandremenielle.quizzapp.Model;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by alexandremenielle on 13/05/2018.
 */

public class Duel {

    private Boolean closed;
    private String id;
    private String theme;
    private String status;

    public Boolean getClosed() {
        return closed;
    }

    public String getStatus() {
        return status;
    }

    private HashMap<String, Object> players;

    public String getId() {
        return id;
    }

    public String getTheme() {
        return theme;
    }

    public HashMap<String, Object> getPlayers() {
        return players;
    }

    public Duel() {

    }
}
