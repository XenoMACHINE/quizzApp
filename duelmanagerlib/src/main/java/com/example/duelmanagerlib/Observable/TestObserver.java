package com.example.duelmanagerlib.Observable;

import android.util.Log;

public class TestObserver implements Observer{

    @Override
    public void Update() {
        Log.d("Observer", "There is successful modification in firebase");
    }
}
