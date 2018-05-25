package com.example.alexandremenielle.quizzapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DuelActivity extends AppCompatActivity {

    @BindView(R.id.timeBar) View timeBar;
    @BindView(R.id.questionNumberLabel) TextView questionNumberLabel;
    @BindView(R.id.questionLabel) TextView questionLabel;
    @BindView(R.id.answerBtn1) Button answerBtn1;
    @BindView(R.id.answerBtn2) Button answerBtn2;
    @BindView(R.id.answerBtn3) Button answerBtn3;
    @BindView(R.id.answerBtn4) Button answerBtn4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_duel);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.answerBtn1) public void onAnswerBtn1(){

    }

    @OnClick(R.id.answerBtn2) public void onAnswerBtn2(){

    }
    @OnClick(R.id.answerBtn3) public void onAnswerBtn3(){

    }

    @OnClick(R.id.answerBtn4) public void onAnswerBtn4(){

    }

}
