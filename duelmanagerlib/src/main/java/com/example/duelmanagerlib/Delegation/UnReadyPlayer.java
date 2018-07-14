package com.example.duelmanagerlib.Delegation;

import java.util.HashMap;

public  class UnReadyPlayer implements IPlayerDelegate{

    @Override
    public HashMap<String, Object> makePlayer(String id) {
        HashMap<String, Object> player = new HashMap<>();
        player.put("isReady", false);
        player.put("score", 0);
        player.put("id", id);
        player.put("questionNumber", 0);
        return player;
    }
}