package com.example.duelmanagerlib;

import com.example.duelmanagerlib.Model.Duel;
import com.example.duelmanagerlib.Model.User;

/**
 * Created by alexandremenielle on 03/06/2018.
 */

public interface DuelEventListener {
    void onReceiveDuel(User user);
    void onReceiveEndDuel(Duel duel);
    //void duelRequestAnswered(String answer);
}
