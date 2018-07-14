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