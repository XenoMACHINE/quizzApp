package com.example.alexandremenielle.quizzapp;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.alexandremenielle.quizzapp.Model.Theme;
import com.example.alexandremenielle.quizzapp.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Comparator;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.recycleView) RecyclerView recyclerView;
    @BindView(R.id.playerRV) RecyclerView playersRecyclerView;

    private final String TAG = "MainActivity";
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private ArrayList<Theme> allThemes;
    private ArrayList<User> allUsers;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 3);
        playersRecyclerView.setLayoutManager(mLayoutManager);

        database.getReference("themes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Theme> themes = new ArrayList<>();
                for (DataSnapshot themeSnap : dataSnapshot.getChildren()){
                    Theme theme = themeSnap.getValue(Theme.class);
                    themes.add(theme);
                }
                allThemes = themes;
                ThemeAdapter themeAdapter = new ThemeAdapter(allThemes);
                recyclerView.setAdapter(themeAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, databaseError.toString());
            }
        });

        database.getReference("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<User> users = new ArrayList<>();
                for (DataSnapshot userSnap : dataSnapshot.getChildren()){
                    User user = userSnap.getValue(User.class);
                    users.add(user);
                }
                allUsers = sortUsersByOnline(users);
                PlayersAdapter playersAdapter = new PlayersAdapter(allUsers);
                playersRecyclerView.setAdapter(playersAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, databaseError.toString());
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        //check connection
        //if(mAuth.getCurrentUser() == null){
          //  Intent intent = new Intent(this, ConnexionActivity.class);
            //startActivity(intent);
        //}
        mAuth.getInstance().signOut();
    }

    private ArrayList<User> sortUsersByOnline(ArrayList<User> users){

        ArrayList<User> sortedUsers = new ArrayList<>();

        for (User user : users){
            if (user.getIsOnline()){
                sortedUsers.add(user);
            }
        }

        for (User user : users){
            if (!user.getIsOnline()){
                sortedUsers.add(user);
            }
        }

        return sortedUsers;
    }
}
