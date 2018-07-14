package com.example.duelmanagerlib.Factory;

import android.util.Pair;

import com.example.duelmanagerlib.Iterator.QuestionIterator;
import com.example.duelmanagerlib.Iterator.QuestionRepository;
import com.example.duelmanagerlib.Model.Question;

import java.util.Map;

/**
 * Created by alexandremenielle on 14/07/2018.
 */

public class QuestionSingleAnswer extends Question {

    private String text;
    private Map<String,Boolean> propositions;

    public QuestionSingleAnswer() {}

    public QuestionSingleAnswer(String text, Map<String, Boolean> propositions) {
        this.text = text;
        this.propositions = propositions;
    }

    @Override
    public Map<String, Boolean> getPropositions() {
        return propositions;
    }

    @Override
    public String getText() {
        return text;
    }

    public String getAnswer() {
        QuestionRepository questionRepository = new QuestionRepository(propositions);
        for(QuestionIterator iter = questionRepository.getIterator(); iter.hasNext();){
            Pair<String, Boolean> values = iter.next();
            if(values.second == true){
                return values.first;
            }
        }
        return "";
    }
}