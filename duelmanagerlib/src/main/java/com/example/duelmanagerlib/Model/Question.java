package com.example.duelmanagerlib.Model;

import android.util.Pair;

import com.example.duelmanagerlib.Factory.QuestionFactory;
import com.example.duelmanagerlib.Iterator.QuestionIterator;
import com.example.duelmanagerlib.Iterator.QuestionRepository;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by alexandremenielle on 14/07/2018.
 */

public abstract class Question {

    public abstract Map<String, Boolean> getPropositions();

    public abstract String getText();

    public Map<String,Object> toMap(){
        HashMap<String,Object> map = new HashMap<>();
        map.put("text",getText());

        QuestionRepository questionRepository = new QuestionRepository(getPropositions());

        HashMap<String,Object> propositionMap = new HashMap<>();

        for(QuestionIterator iter = questionRepository.getIterator(); iter.hasNext();){
            Pair<String, Boolean> values = iter.next();
            propositionMap.put(values.first,values.second);
        }

        map.put("propositions", propositionMap);

        return map;
    }

    @Override
    public String toString() {
        return "Question{" +
                "text='" + getText() + '\'' +
                ", propositions=" + getPropositions() +
                '}';
    }
}

class QuestionBuilder{ // Builder
    private QuestionFactory.Type type = QuestionFactory.Type.SINGLEANSWER;
    private String text = "";
    private HashMap<String,Boolean> propositions = new HashMap<>();

    // /!\ Type need to be set before addPropostitions /!\
    QuestionBuilder withType(QuestionFactory.Type type){
        this.type = type;
        return this;
    }

    QuestionBuilder withText(String text){
        this.text = text;
        return this;
    }

    QuestionBuilder addProposition(String text, Boolean isGood){
        if (type == QuestionFactory.Type.SINGLEANSWER && this.propositions.containsValue(true)){
            this.propositions.put(text, false);
        }else{
            this.propositions.put(text, isGood);
        }
        return this;
    }

    Question build(){
        return new QuestionFactory().getQuestion(type, text, propositions);
    }
}
