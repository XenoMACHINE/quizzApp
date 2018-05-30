package com.example.alexandremenielle.quizzapp;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.UserManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.TextView;

import com.example.alexandremenielle.quizzapp.Model.Duel;
import com.example.alexandremenielle.quizzapp.Model.Question;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DuelActivity extends AppCompatActivity implements QuestionsEventListener {

    @BindView(R.id.timeBar) View timeBar;
    @BindView(R.id.questionNumberLabel) TextView questionNumberLabel;
    @BindView(R.id.questionLabel) TextView questionLabel;
    @BindView(R.id.answerBtn1) Button answerBtn1;
    @BindView(R.id.answerBtn2) Button answerBtn2;
    @BindView(R.id.answerBtn3) Button answerBtn3;
    @BindView(R.id.answerBtn4) Button answerBtn4;

    String currentGoodAnswer;
    int score = 0;

    int questionNumber = 0;
    private AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_duel);
        ButterKnife.bind(this);

        if(DuelManager.getInstance().duelQuestions.size() == 5){
            execNextQuestion();
        }

        if(DuelManager.getInstance().duelQuestionsIds.size() == 0){
            DuelManager.getInstance().getDuelQuestionsIds();
        }

        DuelManager.getInstance().waitTwoPlayersSameQuestion();

        DuelManager.getInstance().questionsEventListener = this;
    }

    public void disableAllBtns(){
        answerBtn1.setEnabled(false);
        answerBtn2.setEnabled(false);
        answerBtn3.setEnabled(false);
        answerBtn4.setEnabled(false);
    }

    public void enableAllBtns(){
        answerBtn1.setEnabled(true);
        answerBtn2.setEnabled(true);
        answerBtn3.setEnabled(true);
        answerBtn4.setEnabled(true);
    }

    @OnClick(R.id.answerBtn1) public void onAnswerBtn1(){
        if (currentGoodAnswer.equals(answerBtn1.getText())){
            score += 10;
        }
        DuelManager.getInstance().updateDuelAfterAnswer(score,++questionNumber);
        disableAllBtns();
    }

    @OnClick(R.id.answerBtn2) public void onAnswerBtn2(){
        if (currentGoodAnswer.equals(answerBtn2.getText())){
            score += 10;
        }
        DuelManager.getInstance().updateDuelAfterAnswer(score,++questionNumber);
        disableAllBtns();
    }

    @OnClick(R.id.answerBtn3) public void onAnswerBtn3(){
        if (currentGoodAnswer.equals(answerBtn3.getText())){
            score += 10;
        }
        DuelManager.getInstance().updateDuelAfterAnswer(score,++questionNumber);
        disableAllBtns();
    }

    @OnClick(R.id.answerBtn4) public void onAnswerBtn4(){
        if (currentGoodAnswer.equals(answerBtn4.getText())){
            score += 10;
        }
        DuelManager.getInstance().updateDuelAfterAnswer(score,++questionNumber);
        disableAllBtns();
    }

    public void execNextQuestion(){
        questionNumberLabel.setText("Question " + (questionNumber + 1) + "/5");
        if(questionNumber < DuelManager.getInstance().duelQuestions.size()){
            launchTimerAnimation();
            enableAllBtns();
            Question question = DuelManager.getInstance().duelQuestions.get(questionNumber);
            questionLabel.setText(question.getText());
            int index = 0;
            for (String answer : question.getPropositions().keySet()){
                if (question.getPropositions().get(answer) == true){
                    currentGoodAnswer = answer;
                }
                switch (index){
                    case 0:
                        answerBtn1.setText(answer);
                        break;
                    case 1:
                        answerBtn2.setText(answer);
                        break;
                    case 2:
                        answerBtn3.setText(answer);
                        break;
                    case 3:
                        answerBtn4.setText(answer);
                        break;
                }
                index++;
            }
        }
    }

    @Override
    public void onNextQuestion() {
        execNextQuestion();
    }

    @Override
    public void onDuelFinished(String reason) {
        builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        AlertDialog alert = builder.setTitle(reason)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .create();
        alert.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DuelManager.getInstance().questionsEventListener = null;
    }

    public void launchTimerAnimation(){
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        final int width = size.x;
        ValueAnimator anim = ValueAnimator.ofInt(0, width);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = timeBar.getLayoutParams();
                layoutParams.width = val;
                timeBar.setLayoutParams(layoutParams);
                if(val == width){
                    DuelManager.getInstance().updateDuelAfterAnswer(score,++questionNumber);
                }
            }
        });
        anim.setDuration(10000);
        anim.start();
    }
}
