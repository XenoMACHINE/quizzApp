package com.example.duelmanagerlib.Observable;

public class ObserverClass {
    static final ConcreteObservable observable  = new ConcreteObservable();


    public static void AddObserver(Observer activity){
        // On crée un objet qui hérite d'Observable
        // On "inscrit" une classe implémentant Observer auprès de l'Observable
        observable.AddObserver(activity);
    }

    public static void Notify(String text){
        observable.NotifiyObservers(text);
    }
}
