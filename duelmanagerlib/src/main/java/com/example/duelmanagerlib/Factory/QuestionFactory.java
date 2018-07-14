package com.example.duelmanagerlib.Factory;

import com.example.duelmanagerlib.Model.Question;

import java.util.HashMap;

/**
 * Created by alexandremenielle on 14/07/2018.
 */

public class QuestionFactory {

    public enum Type {
        SINGLEANSWER,
        MULTIPLEANSWERS;
    }

    public Question getQuestion(Type type, String text, HashMap<String,Boolean> propositions){
        if(type == Type.SINGLEANSWER){
            return new QuestionSingleAnswer(text,propositions);
        }
        else if (type == Type.MULTIPLEANSWERS){
            return new QuestionMultipleAnswer(text, propositions);
        }
        return null;
    }
}

