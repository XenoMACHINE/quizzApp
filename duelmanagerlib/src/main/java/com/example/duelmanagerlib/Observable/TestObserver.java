package com.example.duelmanagerlib.Observable;

import android.util.Log;

public class TestObserver implements Observer{

    public static void test(){
        // On crée un objet qui hérite d'Observable
        Observable observable = new Observable();

        // On "inscrit" une classe implémentant Observer auprès de l'Observable
        observable.AddObserver(new TestObserver());

        // On déclenche les méthodes Update() au sein des classes inscrites auprès de l'Observable
        observable.NotifiyObservers();

    }
    @Override
    public void Update() {
        Log.d("Observer", "ça marche");
    }
}
