package com.example.alexandremenielle.quizzapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ConnexionActivity extends AppCompatActivity {

    @BindView(R.id.login) EditText loginEt;
    @BindView(R.id.password) EditText passwordEt;
    @BindView(R.id.loader) ProgressBar loader;

    private final String TAG = "ConnexionActivity";
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connexion);

        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
    }

    @OnClick(R.id.btnConnect) public void onConnect(){

        String email = loginEt.getText().toString();
        String password = passwordEt.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getApplicationContext(),"Il manque un ou plusieurs champs. Veuillez réessayer.",Toast.LENGTH_SHORT).show();
            return;
        }
        loader.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                loader.setVisibility(View.INVISIBLE);
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success");
                    Intent intentConnexion = new Intent(ConnexionActivity.this, MainActivity.class);;
                    startActivity(intentConnexion);
                    finish();
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }

                // ...
            }
        });
    }

    @Override
    public void onBackPressed() {
        System.exit(0);
    }
}
