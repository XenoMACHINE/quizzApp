package com.example.duelmanagerlib.Observable;

import java.util.ArrayList;
import java.util.List;

public class Observable {
    // Cette liste va accueillir les classes, implémentant l'interface Observer, et qui souhaitent être notifiées
    private List<Observer> observers  = new ArrayList<>();

    // Cette méthode permet d'ajouter une implémentation d'Observer à la liste à notifier
    public void AddObserver(Observer observer){
        observers.add(observer);
    }

    // Cette méthode permet de retirer une implémentation d'Observer à la liste à notifier
    public void RemoveObserver(Observer observer){
        observers.remove(observer);
    }

    // Cette méthode permet de notifier toutes les implémentations d'Observer inscrites, au travers de leur méthode Update()
    public void NotifiyObservers(String answer){
        for (Observer obs:observers) {
            obs.duelRequestAnswered(answer);
        }
    }
}
