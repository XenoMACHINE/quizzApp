package com.example.duelmanagerlib.Factory;

import android.util.Pair;

import com.example.duelmanagerlib.Iterator.QuestionIterator;
import com.example.duelmanagerlib.Iterator.QuestionRepository;
import com.example.duelmanagerlib.Model.Question;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by alexandremenielle on 14/07/2018.
 */

public class QuestionMultipleAnswer extends Question {

    private String text;
    private Map<String,Boolean> propositions;

    public QuestionMultipleAnswer(String text, Map<String, Boolean> propositions) {
        this.text = text;
        this.propositions = propositions;
    }

    @Override
    public Map<String, Boolean> getPropositions() {
        return this.propositions;
    }

    @Override
    public String getText() {
        return this.text;
    }

    public ArrayList<String> getAnswer() {
        ArrayList<String> result = new ArrayList<>();
        QuestionRepository questionRepository = new QuestionRepository(propositions);
        for(QuestionIterator iter = questionRepository.getIterator(); iter.hasNext();){
            Pair<String, Boolean> values = iter.next();
            if(values.second == true){
                result.add(values.first);
            }
        }
        return result;
    }
}
