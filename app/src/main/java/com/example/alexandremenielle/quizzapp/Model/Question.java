package com.example.alexandremenielle.quizzapp.Model;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by alexandremenielle on 12/04/2018.
 */

public class Question {

    private String text;

    public Question() {
    }

    @Override
    public String toString() {
        return "Question{" +
                "text='" + text + '\'' +
                '}';
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
