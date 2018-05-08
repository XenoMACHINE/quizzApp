package com.example.alexandremenielle.quizzapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.alexandremenielle.quizzapp.Model.Theme;
import com.example.alexandremenielle.quizzapp.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements ItemClickListener{

    @BindView(R.id.recycleView) RecyclerView recyclerView;
    @BindView(R.id.playerRV) RecyclerView playersRecyclerView;
    @BindView(R.id.playersPopup) ConstraintLayout playersPopup;

    private final String TAG = "MainActivity";
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private ArrayList<Theme> allThemes;
    private ArrayList<User> allUsers;
    private FirebaseAuth mAuth;
    private ItemClickListener itemClickListener;

    private Theme selectedTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        //Set userdefault for emulator which FirebaseAuth dont work
        mDatabase.child("users").child("3b0r0KJAktWoANT1s07l9iE7GM03").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                user.setId(dataSnapshot.getKey());
                AppManager.getInstance().currentUser = user;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mAuth = FirebaseAuth.getInstance();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 3);
        playersRecyclerView.setLayoutManager(mLayoutManager);

        itemClickListener = this;

        //Get all themes
        mDatabase.child("themes").addValueEventListener(new ValueEventListener() {
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
                themeAdapter.setClickListener(itemClickListener);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, databaseError.toString());
            }
        });


        //Get all users and sort by online status
        mDatabase.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<User> users = new ArrayList<>();
                for (DataSnapshot userSnap : dataSnapshot.getChildren()){
                    User user = userSnap.getValue(User.class);
                    user.setId(userSnap.getKey());
                    users.add(user);
                }
                allUsers = sortUsersByOnline(users);
                PlayersAdapter playersAdapter = new PlayersAdapter(allUsers);
                playersRecyclerView.setAdapter(playersAdapter);
                playersAdapter.setClickListener(itemClickListener);
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
        /*if(mAuth.getCurrentUser() == null){
            Intent intent = new Intent(this, ConnexionActivity.class);
            startActivity(intent);
        }*/
        //mAuth.getInstance().signOut();
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

    @Override
    public void onClick(View view, User user) {
        sendDuel(user);
        playersPopup.setVisibility(View.INVISIBLE);
        AlertDialog.Builder builder;
        final Context context = this;
        builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        builder.setTitle("Défi envoyé !")
                .setMessage("En attente de l'adversaire...")
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                //.setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    public void onClick(View view, Theme theme) {
        selectedTheme = theme;
        if (playersPopup.getVisibility() == View.VISIBLE){
            playersPopup.setVisibility(View.INVISIBLE);
            return;
        }
        playersPopup.setVisibility(View.VISIBLE);
    }

    public void sendDuel(User selectedUser){
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
        selectedUserHm.put("id", currentUser.getId());
        selectedUserHm.put("questionNumber", 0);

        players.put(currentUser.getId(), currentUserHm);
        players.put(selectedUser.getId(),selectedUserHm);

        childUpdates.put("status","0");
        childUpdates.put("theme", selectedTheme.getId());
        childUpdates.put("players", players);

        Map<String, Object> pushData = new HashMap<>();
        pushData.put(mDatabase.push().getKey(), childUpdates);

        mDatabase.child("duels").updateChildren(pushData);
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.settings:
                Intent intentSettings = new Intent(this, SettingsActivity.class);
                /*EditText editText = (EditText) findViewById(R.id.editText);
                String message = editText.getText().toString();
                intent.putExtra(EXTRA_MESSAGE, message);*/
                startActivity(intentSettings);
                finish();
                return true;

            case R.id.disconnect:
                Intent intentConnexion = new Intent(this, ConnexionActivity.class);
                /*EditText editText = (EditText) findViewById(R.id.editText);
                String message = editText.getText().toString();
                intent.putExtra(EXTRA_MESSAGE, message);*/
                FirebaseAuth.getInstance().signOut();
                startActivity(intentConnexion);
                finish();
                return true;

        }
        return super.onOptionsItemSelected(item);

    }
}
