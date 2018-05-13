package com.example.alexandremenielle.quizzapp;

import android.content.Context;
import android.content.Intent;

import com.example.alexandremenielle.quizzapp.Model.Duel;
import com.example.alexandremenielle.quizzapp.Model.Theme;
import com.example.alexandremenielle.quizzapp.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by alexandremenielle on 13/05/2018.
 */

public class DuelManager {

    private static DuelManager sharedInstance;

    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    public static DuelManager getInstance(){

        if (sharedInstance == null){
            sharedInstance = new DuelManager();
        }

        return  sharedInstance;
    }

    public Context mContext;

    public String currentIdDuel = "";

    public void sendDuelTo(User selectedUser, Theme selectedTheme){
        User currentUser = AppManager.getInstance().currentUser;

        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> players = new HashMap<>();

        Map<String, Object> currentUserHm = new HashMap<>();
        currentUserHm.put("isReady", true);
        currentUserHm.put("score", 0);
        currentUserHm.put("id", currentUser.getId());
        currentUserHm.put("questionNumber", 0);

        Map<String, Object> selectedUserHm = new HashMap<>();
        selectedUserHm.put("isReady", false);
        selectedUserHm.put("score", 0);
        selectedUserHm.put("id", selectedUser.getId());
        selectedUserHm.put("questionNumber", 0);

        players.put(currentUser.getId(), currentUserHm);
        players.put(selectedUser.getId(),selectedUserHm);

        childUpdates.put("status","0");
        childUpdates.put("theme", selectedTheme.getId());
        childUpdates.put("players", players);

        Map<String, Object> pushData = new HashMap<>();

        currentIdDuel = mDatabase.push().getKey();
        pushData.put(currentIdDuel, childUpdates);

        mDatabase.child("duels").updateChildren(pushData);

        Map<String, Object> pushDuelPlayer = new HashMap();
        pushDuelPlayer.put(currentIdDuel, true);
        mDatabase.child("users").child(currentUser.getId()).child("duels").updateChildren(pushDuelPlayer);
        pushDuelPlayer.put(currentIdDuel, false);
        mDatabase.child("users").child(selectedUser.getId()).child("duels").updateChildren(pushDuelPlayer);

        launchDuelListener(selectedUser.getId());
    }

    public void cancelSentDuel(){
        //TODO
    }

    public void launchDuelListener(final String selectedUserId){

        final DatabaseReference ref = mDatabase.child("duels").child(currentIdDuel);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Duel duel = dataSnapshot.getValue(Duel.class);
                HashMap<String,Object> playerHm = (HashMap<String,Object>) duel.getPlayers().get(selectedUserId);
                if (playerHm != null && (Boolean) playerHm.get("isReady") == true){
                    Intent intentConnexion = new Intent(mContext, DuelActivity.class);
                    mContext.startActivity(intentConnexion);
                    ref.removeEventListener(this);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
