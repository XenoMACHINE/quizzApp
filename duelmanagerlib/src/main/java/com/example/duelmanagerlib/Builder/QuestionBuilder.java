package com.example.duelmanagerlib.Builder;

import com.example.duelmanagerlib.Factory.QuestionFactory;
import com.example.duelmanagerlib.Model.Question;

import java.util.HashMap;

/**
 * Created by alexandremenielle on 14/07/2018.
 */

public class QuestionBuilder{
    private QuestionFactory.Type type = QuestionFactory.Type.SINGLEANSWER;
    private String text = "";
    private HashMap<String,Boolean> propositions = new HashMap<>();

    // /!\ Type need to be set before addPropostitions /!\
    public QuestionBuilder withType(QuestionFactory.Type type){
        this.type = type;
        return this;
    }

    public QuestionBuilder withText(String text){
        this.text = text;
        return this;
    }

    public QuestionBuilder addProposition(String text, Boolean isGood){
        if (type == QuestionFactory.Type.SINGLEANSWER && this.propositions.containsValue(true)){
            this.propositions.put(text, false);
        }else{
            this.propositions.put(text, isGood);
        }
        return this;
    }

    public Question build(){
        return new QuestionFactory().getQuestion(type, text, propositions);
    }
}
