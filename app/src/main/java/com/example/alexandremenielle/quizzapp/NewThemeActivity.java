package com.example.alexandremenielle.quizzapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import com.example.duelmanagerlib.Builder.QuestionBuilder;
import com.example.duelmanagerlib.Builder.ThemeBuilder;
import com.example.duelmanagerlib.Factory.QuestionFactory;
import com.example.duelmanagerlib.Model.Question;
import com.example.duelmanagerlib.Model.Theme;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NewThemeActivity extends AppCompatActivity {

    @BindView(R.id.newTheme) EditText newTheme;

    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_theme);

        ButterKnife.bind(this);
    }

    @OnClick(R.id.buttonSubmitTheme)
    public void submitTheme() {
        System.out.println("Submit");
        String id = mDatabase.push().getKey();
        Theme theme = new Theme();
        theme.setId(id);
        theme.setName(newTheme.getText().toString());
        /*Theme theme = new ThemeBuilder()
                .id(id)
                .name(newTheme.getText().toString())
                .build();*/
        mDatabase.child("themes").child(id).updateChildren(theme.toMap());
        finish();
    }
}
