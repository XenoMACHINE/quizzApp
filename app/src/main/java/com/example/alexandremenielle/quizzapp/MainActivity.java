package com.example.alexandremenielle.quizzapp;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.duelmanagerlib.AppManager;
import com.example.duelmanagerlib.DuelEventListener;
import com.example.duelmanagerlib.DuelManager;
import com.example.duelmanagerlib.Model.Duel;
import com.example.duelmanagerlib.Model.Theme;
import com.example.duelmanagerlib.Model.User;
import com.example.duelmanagerlib.Observable.ConcreteObservable;
import com.example.duelmanagerlib.Observable.Observer;
import com.example.duelmanagerlib.TemplateMethod.FirebaseObject;
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

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

public class MainActivity extends AppCompatActivity implements ItemClickListener, DuelEventListener, Observer {

    @BindView(R.id.recycleView) RecyclerView recyclerView;
    @BindView(R.id.playerRV) RecyclerView playersRecyclerView;
    @BindView(R.id.playersPopup) ConstraintLayout playersPopup;
    @BindView(R.id.container) ConstraintLayout container;
    @BindView(R.id.homeLoader) ProgressBar loader;
    private final static String ADMIN_CHANNEL_ID = "channel";
    private final String TAG = "MainActivity";

    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private ArrayList<Theme> allThemes;
    private FirebaseAuth mAuth;
    private ItemClickListener itemClickListener;
    private AlertDialog.Builder builder;
    private AlertDialog alert;
    private AppController controller = new AppController();

    public static boolean isAppRunning;

    private Theme selectedTheme;

    public ConcreteObservable observable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        DuelManager.getInstance().duelEventListener = this;

        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser() == null){
            Intent intent = new Intent(this, ConnexionActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        AppManager.getInstance().setCurrentUser();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 3);
        playersRecyclerView.setLayoutManager(mLayoutManager);

        itemClickListener = this;

        loader.setVisibility(View.VISIBLE);
        //Get all themes
        mDatabase.child("themes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                loader.setVisibility(View.INVISIBLE);
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

        //Get all users
        mDatabase.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<User> users = new ArrayList<>();
                for (DataSnapshot userSnap : dataSnapshot.getChildren()){
                    User user = userSnap.getValue(User.class);
                    user.setId(userSnap.getKey());
                    if (!user.getId().equals(AppManager.getInstance().currentUser.getId())){
                        users.add(user);
                    }
                }
                PlayersAdapter playersAdapter = new PlayersAdapter(users);
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
    public void onClick(View view, final User user) {
        builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        if (user.getIsOnline() == false){
            HashMap<String,Object> map = new HashMap<>();
            map.put("FCMToken",user.getFCMToken());
            map.put("username",AppManager.getInstance().currentUser.getFirstname());
            mDatabase.child("notifications").push().updateChildren(map);
            /*alert = builder.setTitle("Le joueur est hors ligne")
                    .setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .create();
            alert.show();
            return;*/
        }

        DuelManager.getInstance().sendDuelTo(user, selectedTheme);
        playersPopup.setVisibility(View.INVISIBLE);
        alert = builder.setTitle("Défi envoyé !")
                .setMessage("En attente de l'adversaire...")
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        container.setBackgroundColor(getResources().getColor(android.R.color.white));
                        DuelManager.getInstance().cancelSentDuel(user);
                    }
                })
                .create();
        alert.show();


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        String channelId = "admin_channel";
        String channel2 = "2";

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(channelId,
                    "Channel 1", NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.setDescription("This is BNT");
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setShowBadge(true);
            notificationManager.createNotificationChannel(notificationChannel);

            NotificationChannel notificationChannel2 = new NotificationChannel(channel2,
                    "Channel 2", NotificationManager.IMPORTANCE_MIN);

            notificationChannel.setDescription("This is bTV");
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setShowBadge(true);
            notificationManager.createNotificationChannel(notificationChannel2);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isAppRunning = false;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onClick(View view, Theme theme) {
        selectedTheme = theme;
        if (playersPopup.getVisibility() == View.VISIBLE){
            container.setBackgroundColor(getResources().getColor(android.R.color.white));
            playersPopup.setVisibility(View.INVISIBLE);
            return;
        }
        container.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        playersPopup.setVisibility(View.VISIBLE);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.settings:
                Intent intentSettings = new Intent(this, SettingsActivity.class);
                startActivity(intentSettings);
                return true;

            case R.id.disconnect:
                Intent intentConnexion = new Intent(this, ConnexionActivity.class);
                FirebaseAuth.getInstance().signOut();
                startActivity(intentConnexion);
                finish();
                return true;

            case R.id.action_add_question:
                Intent intentNewQuestion = new Intent(this, NewQuestionActivity.class);
                startActivity(intentNewQuestion);
                return true;

            case R.id.action_add_theme:
                Intent intentNewTheme = new Intent(this, NewThemeActivity.class);
                startActivity(intentNewTheme);
                return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onReceiveDuel(User user) {
        if (alert != null) {
            alert.cancel();
        }
        final Context context = this;
        builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        alert = builder.setTitle(user.getFullName() + " vous défie !")
                .setPositiveButton("Accepter", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DuelManager.getInstance().acceptDuel();
                        Intent intent = new Intent(context, DuelActivity.class);
                        startActivity(intent);
                        alert.cancel();
                    }
                })
                .setNegativeButton("Refuser", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        DuelManager.getInstance().rejectDuel();
                        alert.cancel();
                    }
                })
                .create();
        alert.show();
    }

    @Override
    public void onReceiveEndDuel(Duel duel) {
        if (alert != null){
            alert.cancel();
            Toast.makeText(getApplicationContext(),"Défi annulé.",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void duelRequestAnswered(String answer) {
        if (answer.equals("accepted")){
            Intent intent = new Intent(this, DuelActivity.class);
            this.startActivity(intent);
        }
        container.setBackgroundColor(getResources().getColor(android.R.color.white));
        if (alert != null){
            alert.cancel();
            if(answer != null && answer != "accepted") {
                Toast.makeText(getApplicationContext(), answer, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onPause() {
        controller.onActivityPaused(this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        controller.onActivityResumed(this);
        super.onResume();
    }

    @Override
    public void Update() {
    }

}
