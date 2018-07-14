package com.example.duelmanagerlib.Model;

import android.util.Pair;

import com.example.duelmanagerlib.Iterator.QuestionIterator;
import com.example.duelmanagerlib.Iterator.QuestionRepository;

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

    public Question(String text, Map<String,Boolean> propositions) {
        this.text = text;
        this.propositions = propositions;
    }

    @Override
    public String toString() {
        return "Question{" +
                "text='" + text + '\'' +
                ", propositions=" + propositions +
                '}';
    }

    public Map<String,Object> toMap(){
        HashMap<String,Object> map = new HashMap<>();
        map.put("text",text);

        QuestionRepository questionRepository = new QuestionRepository(propositions);

        HashMap<String,Object> propositionMap = new HashMap<>();

        for(QuestionIterator iter = questionRepository.getIterator(); iter.hasNext();){
            Pair<String, Boolean> values = iter.next();
            propositionMap.put(values.first,values.second);
        }

        map.put("propositions", propositionMap);

        return map;
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

    class QuestionBuilder{ // Builder
        private String text;
        private Map<String,Boolean> propositions;

        QuestionBuilder withText(String text){
            this.text = text;
            return this;
        }

        QuestionBuilder addProposition(String text, Boolean isGood){
            this.propositions.put(text, isGood);
            return this;
        }

        Question build(){
            return new Question(this.text, this.propositions);
        }
    }
}
