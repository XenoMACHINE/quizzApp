package com.example.duelmanagerlib;

/**
 * Created by alexandremenielle on 03/06/2018.
 */

public interface QuestionsEventListener {
    void onNextQuestion();
    void onDuelFinished(String reason);
}
