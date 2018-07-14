package com.example.duelmanagerlib.Delegation;

import java.util.HashMap;

public class ReadyPlayer implements IPlayerDelegate{

    @Override
    public HashMap<String, Object> makePlayer(String id) {
        HashMap<String, Object> player = new HashMap<>();
        player.put("isReady", true);
        player.put("score", 0);
        player.put("id", id);
        player.put("questionNumber", 0);
        return player;
    }
}