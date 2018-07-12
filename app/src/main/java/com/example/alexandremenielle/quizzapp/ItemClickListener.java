package com.example.alexandremenielle.quizzapp;

import android.view.View;

import com.example.duelmanagerlib.Model.Theme;
import com.example.duelmanagerlib.Model.User;


/**
 * Created by alexandremenielle on 01/05/2018.
 */

public interface ItemClickListener {
    void onClick(View view, User user);
    void onClick(View view, Theme theme);
}
