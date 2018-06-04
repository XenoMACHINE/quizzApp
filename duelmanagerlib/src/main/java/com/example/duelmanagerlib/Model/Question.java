package com.example.duelmanagerlib.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by alexandremenielle on 12/04/2018.
 */

public class Question {

    private String text;
    private Map<String,Boolean> propositions;

    public Question() {
    }

    @Override
    public String toString() {
        return "Question{" +
                "text='" + text + '\'' +
                ", propositions=" + propositions +
                '}';
    }

    public String getText() {
        return text;
    }

    public Map<String, Boolean> getPropositions() {
        return propositions;
    }

    public void setPropositions(Map<String, Boolean> propositions) {
        this.propositions = propositions;
    }

    public void setText(String text) {
        this.text = text;
    }
}
