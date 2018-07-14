package com.example.duelmanagerlib.Iterator;

import android.util.Pair;

/**
 * Created by alexandremenielle on 26/06/2018.
 */

public interface QuestionIterator {
    public boolean hasNext();
    public Pair<String, Boolean> next();
}