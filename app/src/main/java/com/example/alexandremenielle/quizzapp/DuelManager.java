package com.example.alexandremenielle.quizzapp;

import android.content.Context;
import android.content.Intent;
import android.os.UserManager;
import android.support.constraint.solver.widgets.Snapshot;
import android.widget.Toast;

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

    public DuelEventListener duelEventListener;

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
                    Intent intent = new Intent(mContext, DuelActivity.class);
                    mContext.startActivity(intent);
                    ref.removeEventListener(this);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void waitDuelListener(){
        String currentUserId = AppManager.getInstance().currentUser.getId();
        mDatabase.child("users").child(currentUserId).child("duels").limitToLast(1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String,Boolean> duelHm = (HashMap<String,Boolean>) dataSnapshot.getValue();

                for (Map.Entry<String, Boolean> duel : duelHm.entrySet()) {
                    if(duel.getValue() == false){
                        manageDuelListener(duel.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void manageDuelListener(String duelId){
        mDatabase.child("duels").child(duelId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Duel duel = dataSnapshot.getValue(Duel.class);
                currentIdDuel = dataSnapshot.getKey();

                //Duel pas encore commencé
                if(duel.getStatus().equals("0")){
                    notifyDuelReceived(duel);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void notifyDuelReceived(final Duel duel){
        String currentUserId = AppManager.getInstance().currentUser.getId();
        for (Map.Entry<String, Object> player : duel.getPlayers().entrySet())
        {
            if (!player.getKey().equals(currentUserId)){
                mDatabase.child("users").child(player.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        duelEventListener.onReceiveDuel(user);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }
    }

    public void acceptDuel(){
        String currentUserId = AppManager.getInstance().currentUser.getId();
        HashMap<String, Object> pushData = new HashMap<>();
        pushData.put(currentIdDuel, true);
        mDatabase.child("users").child(currentUserId).child("duels").updateChildren(pushData);

        pushData.clear();
        pushData.put("isReady",true);
        mDatabase.child("duels").child(currentIdDuel).child("players").child(currentUserId).updateChildren(pushData);

        pushData.clear();
        pushData.put("status","1");
        mDatabase.child("duels").child(currentIdDuel).updateChildren(pushData);
    }

    public void rejectDuel(){
        String currentUserId = AppManager.getInstance().currentUser.getId();
        HashMap<String, Object> pushData = new HashMap<>();
        pushData.put(currentIdDuel, true);
        mDatabase.child("users").child(currentUserId).child("duels").updateChildren(pushData);

        pushData.clear();
        pushData.put("status","4");
        mDatabase.child("duels").child(currentIdDuel).updateChildren(pushData);
    }

}
