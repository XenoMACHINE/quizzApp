package com.example.duelmanagerlib.Iterator;

import android.util.Pair;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by alexandremenielle on 26/06/2018.
 */

public class QuestionRepository implements Container{
    public ArrayList<String> propositionsText = new ArrayList<>();
    public ArrayList<Boolean> propositionsAnswer = new ArrayList<>();

    public QuestionRepository(Map<String, Boolean> propositions){

        for(Map.Entry<String, Boolean> entry : propositions.entrySet()) {
            propositionsText.add(entry.getKey());
            propositionsAnswer.add(entry.getValue());
        }
    }

    @Override
    public QuestionIterator getIterator() {
        return new PropositionsQuestionIterator();
    }

    private class PropositionsQuestionIterator implements QuestionIterator {

        int index;

        @Override
        public boolean hasNext() {

            if(index < 4){ //limit to 4 propositions
                return true;
            }
            return false;
        }

        @Override
        public Pair<String, Boolean> next() {

            if(this.hasNext()){
                Pair<String, Boolean> pair = new Pair<>(propositionsText.get(index),propositionsAnswer.get(index));
                index++;
                return pair;
            }
            return null;
        }
    }
}
