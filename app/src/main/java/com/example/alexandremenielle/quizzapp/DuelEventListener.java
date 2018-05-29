package com.example.alexandremenielle.quizzapp;

import com.example.alexandremenielle.quizzapp.Model.Duel;
import com.example.alexandremenielle.quizzapp.Model.Question;
import com.example.alexandremenielle.quizzapp.Model.User;

import java.util.ArrayList;

/**
 * Created by alexandremenielle on 13/05/2018.
 */

public interface DuelEventListener {

    void onReceiveDuel(User user);
    void onReceiveEndDuel(Duel duel);
    void duelRequestAnswered(String answer);
}

interface QuestionsEventListener {
    void onNextQuestion();
    void onDuelFinished(String reason);
}
