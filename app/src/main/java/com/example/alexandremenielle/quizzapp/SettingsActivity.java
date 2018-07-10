package com.example.alexandremenielle.quizzapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.example.duelmanagerlib.AppManager;
import com.example.duelmanagerlib.Model.User;
import com.example.duelmanagerlib.Observable.ConcreteObservable;
import com.example.duelmanagerlib.Observable.Observer;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;



public class SettingsActivity extends AppCompatActivity implements Observer {

    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth mAuth;
    private AppController controller = new AppController();

    @BindView(R.id.firstname) EditText firstName;
    @BindView(R.id.lastname) EditText lastName;
    @BindView(R.id.email) EditText email;
    User user = AppManager.getInstance().currentUser;

    public ConcreteObservable observable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        lastName.setText(user.getLastname());
        firstName.setText(user.getFirstname());
        email.setText(user.getMail());

    }

    @OnClick(R.id.save)
    public void save() {
        // On crée un objet qui hérite d'Observable
        //observable = new ConcreteObservable();

        // On "inscrit" une classe implémentant Observer auprès de l'Observable
        //observable.AddObserver(new TestObserver());

        user.setMail(email.getText().toString());
        user.setFirstname(firstName.getText().toString());
        user.setLastname(lastName.getText().toString());
        mDatabase.child("users").child(user.getId()).updateChildren(user.toMap())
        .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Write was successful!
                // ...
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Write failed
                // ...
            }
        });
        // On déclenche les méthodes Update() au sein des classes inscrites auprès de l'Observable
        //observable.NotifiyObservers();
        finish();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.settings:
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
