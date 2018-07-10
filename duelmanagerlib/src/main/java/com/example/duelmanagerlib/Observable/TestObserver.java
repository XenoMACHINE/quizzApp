package com.example.duelmanagerlib.Observable;

import android.util.Log;

public class TestObserver implements Observer{
    @Override
    public void Update() {
        Log.d("Observer", "ça marche");
    }
}
