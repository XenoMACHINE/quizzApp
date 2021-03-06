package com.example.duelmanagerlib;

import com.example.duelmanagerlib.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

/**
 * Created by alexandremenielle on 07/05/2018.
 */

public class AppManager {

    private static AppManager sharedInstance;

    public User currentUser;
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    public static AppManager getInstance(){ //Singleton

        if (sharedInstance == null){
            sharedInstance = new AppManager();
        }

        return  sharedInstance;
    }

    public void setCurrentUser(){
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        pushFCMToken(userId);
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                user.setId(dataSnapshot.getKey());
                currentUser = user;
                DuelManager.getInstance().waitDuelListener();
                setUserConnected(true);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println(databaseError);
            }
        });
    }

    public void pushFCMToken(String id){
        String token = FirebaseInstanceId.getInstance().getToken();
        HashMap<String,Object> map = new HashMap<>();
        map.put("FCMToken",token);
        mDatabase.child("users").child(id).updateChildren(map);
    }

    public void setUserConnected(Boolean connected){
        if(currentUser != null){
            HashMap<String,Object> map = new HashMap<>();
            map.put("isOnline",connected);
            mDatabase.child("users").child(currentUser.getId()).updateChildren(map);
        }
    }
}
