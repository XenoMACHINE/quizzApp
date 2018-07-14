package com.example.alexandremenielle.quizzapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.duelmanagerlib.Model.Theme;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NewQuestionActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    @BindView(R.id.spinnerThemes) Spinner spinnerThemes;
    @BindView(R.id.questionName) EditText questionName;
    @BindView(R.id.firstAnswer) EditText firstAnswer;
    @BindView(R.id.secondAnswer) EditText secondAnswer;
    @BindView(R.id.thirdAnswer) EditText thirdAnswer;
    @BindView(R.id.goodAnswer) EditText goodAnswer;
    @BindView(R.id.buttonSubmitQuestion) Button btnSubmit;
    private final String TAG = "NewQuestionActivity";

    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    ArrayList<Theme> allThemes = new ArrayList<>();
    ArrayList<String> allThemesString = new ArrayList<>();
    private FirebaseAuth mAuth;
    private Theme themeSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_question);

        ButterKnife.bind(this);

        spinnerThemes.setOnItemSelectedListener(this);

        //Get all themes
        mDatabase.child("themes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Theme> themes = new ArrayList<>();
                for (DataSnapshot themeSnap : dataSnapshot.getChildren()){
                    Theme theme = themeSnap.getValue(Theme.class);
                    themes.add(theme);
                    allThemesString.add(theme.getName());
                }
                allThemes = themes;
                ArrayAdapter<Theme> adapter = new ArrayAdapter<>(NewQuestionActivity.this, android.R.layout.simple_spinner_item, allThemes);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerThemes.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, databaseError.toString());
            }
        });

    }

    @OnClick(R.id.buttonSubmitQuestion)
    public void submitQuestion() {
        System.out.println("Submit");
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        this.themeSelected = (Theme) adapterView.getItemAtPosition(i);
        System.out.println(themeSelected);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
