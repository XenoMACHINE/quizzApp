package com.example.duelmanagerlib.Observable;

import java.util.List;

public class Observable {
    // Cette liste va accueillir les classes, implémentant l'interface Observer, et qui souhaitent être notifiées
    protected List<Observer> observers;

    // Cette méthode permet d'ajouter une implémentation d'Observer à la liste à notifier
    public void AddObserver(Observer observer){
        observers.add(observer);
    }

    // Cette méthode permet de retirer une implémentation d'Observer à la liste à notifier
    public void RemoveObserver(Observer observer){
        observers.remove(observer);
    }

    // Cette méthode permet de notifier toutes les implémentations d'Observer inscrites, au travers de leur méthode Update()
    public void NotifiyObservers(){
        for (Observer obs:observers) {
            obs.Update();
        }
    }
}
