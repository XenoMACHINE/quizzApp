package com.example.alexandremenielle.quizzapp;

import com.example.alexandremenielle.quizzapp.Model.User;

/**
 * Created by alexandremenielle on 07/05/2018.
 */

public class AppManager {

    private static AppManager sharedInstance;

    public User currentUser;

    public static AppManager getInstance(){

        if (sharedInstance == null){
            sharedInstance = new AppManager();
        }

        return  sharedInstance;
    }
}
