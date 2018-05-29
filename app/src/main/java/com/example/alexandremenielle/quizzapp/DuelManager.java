package com.example.alexandremenielle.quizzapp;

import android.content.Context;
import android.content.Intent;
import android.os.UserManager;
import android.support.constraint.solver.widgets.Snapshot;
import android.widget.Toast;

import com.example.alexandremenielle.quizzapp.Model.Duel;
import com.example.alexandremenielle.quizzapp.Model.Question;
import com.example.alexandremenielle.quizzapp.Model.Theme;
import com.example.alexandremenielle.quizzapp.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by alexandremenielle on 13/05/2018.
 */

public class DuelManager {

    private static DuelManager sharedInstance;

    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    public static DuelManager getInstance(){

        if (sharedInstance == null){
            sharedInstance = new DuelManager();
        }

        return  sharedInstance;
    }

    public DuelEventListener duelEventListener;

    public QuestionsEventListener questionsEventListener;

    public Context mContext;

    public String currentIdDuel = "";

    public ArrayList<String> duelQuestionsIds = new ArrayList<>();

    public ArrayList<Question> duelQuestions = new ArrayList<>();

    public void getDuelQuestionsIds(){
        duelQuestionsIds.clear();
        mDatabase.child("duels").child(currentIdDuel).child("questions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, Object> questionsIds = (HashMap) dataSnapshot.getValue();
                for(String questionId : questionsIds.keySet()){
                    duelQuestionsIds.add(questionId);
                }
                getDuelQuestions();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void getDuelQuestions(){
        duelQuestions.clear();
        for (String questionId : duelQuestionsIds){
            mDatabase.child("questions").child(questionId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Question question = dataSnapshot.getValue(Question.class);
                    duelQuestions.add(question);

                    if(duelQuestions.size() == 5){ //Fini
                        if(questionsEventListener != null)
                            questionsEventListener.onNextQuestion();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }


    private void setQuestionsForTheme(Theme theme){
        //TODO Changer pour 5 randoms
        final Map<String, Object> questions = new HashMap<>();
        mDatabase.child("themes").child(theme.getId()).child("questions").limitToFirst(5).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, Object> questionsIds = (HashMap) dataSnapshot.getValue();
                mDatabase.child("duels").child(currentIdDuel).child("questions").updateChildren(questionsIds);
                for(String questionId : questionsIds.keySet()){
                    duelQuestionsIds.add(questionId);
                }
                getDuelQuestions();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Map<String, Object> pushMap = new HashMap<>();
        pushMap.put("questions", questions);
        mDatabase.child("duels").child(currentIdDuel).updateChildren(pushMap);
    }

    public void sendDuelTo(User selectedUser, Theme selectedTheme){
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
        selectedUserHm.put("id", selectedUser.getId());
        selectedUserHm.put("questionNumber", 0);

        players.put(currentUser.getId(), currentUserHm);
        players.put(selectedUser.getId(),selectedUserHm);

        childUpdates.put("status","0");
        childUpdates.put("theme", selectedTheme.getId());
        childUpdates.put("players", players);

        Map<String, Object> pushData = new HashMap<>();

        currentIdDuel = mDatabase.push().getKey();
        pushData.put(currentIdDuel, childUpdates);

        mDatabase.child("duels").updateChildren(pushData);
        setQuestionsForTheme(selectedTheme);

        Map<String, Object> pushDuelPlayer = new HashMap();
        pushDuelPlayer.put(currentIdDuel, true);
        mDatabase.child("users").child(currentUser.getId()).child("duels").updateChildren(pushDuelPlayer);
        pushDuelPlayer.put(currentIdDuel, false);
        mDatabase.child("users").child(selectedUser.getId()).child("duels").updateChildren(pushDuelPlayer);

        launchDuelListener(selectedUser.getId());
    }

    public void cancelSentDuel(){
        HashMap<String, Object> pushData = new HashMap<>();
        pushData.put("status", "4");
        pushData.put("closed", true);
        mDatabase.child("duels").child(currentIdDuel).updateChildren(pushData);
    }

    public void launchDuelListener(final String selectedUserId){

        final DatabaseReference ref = mDatabase.child("duels").child(currentIdDuel);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Duel duel = dataSnapshot.getValue(Duel.class);
                HashMap<String,Object> playerHm = (HashMap<String,Object>) duel.getPlayers().get(selectedUserId);
                if (playerHm != null && (Boolean) playerHm.get("isReady") == true){
                    duelEventListener.duelRequestAnswered(null);
                    Intent intent = new Intent(mContext, DuelActivity.class);
                    mContext.startActivity(intent);
                    ref.removeEventListener(this);
                }
                if (duel.getStatus().equals("4")){
                    duelEventListener.duelRequestAnswered("Duel refusé");
                    ref.removeEventListener(this);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void manageDuelListener(String duelId){
        final DatabaseReference ref = mDatabase.child("duels").child(duelId);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Duel duel = dataSnapshot.getValue(Duel.class);
                currentIdDuel = dataSnapshot.getKey();

                //Duel pas encore commencé
                if(duel.getStatus().equals("0")){
                    notifyDuelReceived(duel);
                }

                //Gestion des fins de parties
                if(duel.getStatus().equals("4")){
                    if(duelEventListener != null){
                        duelEventListener.onReceiveEndDuel(duel);
                    }
                    HashMap<String, Object> pushData = new HashMap<>();
                    pushData.put("closed", true);
                    mDatabase.child("duels").child(currentIdDuel).updateChildren(pushData);

                    if(questionsEventListener != null){
                        questionsEventListener.onDuelFinished("L'adversaire à quitté la partie");
                    }

                    //on peut maintenant couper le listener sur ce duel
                    ref.removeEventListener(this);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void waitDuelListener(){
        String currentUserId = AppManager.getInstance().currentUser.getId();
        mDatabase.child("users").child(currentUserId).child("duels").limitToLast(1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String,Boolean> duelHm = (HashMap<String,Boolean>) dataSnapshot.getValue();

                if(duelHm != null){
                    for (Map.Entry<String, Boolean> duel : duelHm.entrySet()) {
                        if(duel.getValue() == false){
                            manageDuelListener(duel.getKey());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void notifyDuelReceived(final Duel duel){
        String currentUserId = AppManager.getInstance().currentUser.getId();
        for (Map.Entry<String, Object> player : duel.getPlayers().entrySet())
        {
            if (!player.getKey().equals(currentUserId)){
                mDatabase.child("users").child(player.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        duelEventListener.onReceiveDuel(user);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }
    }

    public void acceptDuel(){
        String currentUserId = AppManager.getInstance().currentUser.getId();
        HashMap<String, Object> pushData = new HashMap<>();
        pushData.put(currentIdDuel, true);
        mDatabase.child("users").child(currentUserId).child("duels").updateChildren(pushData);

        pushData.clear();
        pushData.put("isReady",true);
        mDatabase.child("duels").child(currentIdDuel).child("players").child(currentUserId).updateChildren(pushData);

        pushData.clear();
        pushData.put("status","1");
        mDatabase.child("duels").child(currentIdDuel).updateChildren(pushData);
    }

    public void rejectDuel(){
        String currentUserId = AppManager.getInstance().currentUser.getId();
        HashMap<String, Object> pushData = new HashMap<>();
        pushData.put(currentIdDuel, true);
        mDatabase.child("users").child(currentUserId).child("duels").updateChildren(pushData);

        pushData.clear();
        pushData.put("status","4");
        mDatabase.child("duels").child(currentIdDuel).updateChildren(pushData);
    }

    public void updateDuelAfterAnswer(int score, int questionNumber){
        Map<String, Object> pushMap = new HashMap<>();
        pushMap.put("score",score);
        pushMap.put("questionNumber",questionNumber);
        mDatabase.child("duels").child(currentIdDuel).child("players").child(AppManager.getInstance().currentUser.getId()).updateChildren(pushMap);
    }

    public void waitTwoPlayersSameQuestion(){
        final DatabaseReference ref = mDatabase.child("duels").child(currentIdDuel);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Duel duel = dataSnapshot.getValue(Duel.class);
                HashMap<String,Object> playerHm = duel.getPlayers();
                int questionNumber = -1;
                for(Object object : duel.getPlayers().values()){
                    HashMap<String,Object> player = (HashMap<String, Object>) object;
                    if (questionNumber == -1){
                        questionNumber = ((Long) player.get("questionNumber")).intValue();
                    }else {
                        int secondQuestionNumber = ((Long) player.get("questionNumber")).intValue();
                        if(questionsEventListener != null && questionNumber != 0 && (questionNumber == secondQuestionNumber)){
                            if(questionNumber == 5){
                                questionsEventListener.onDuelFinished("Partie terminé !");
                            }else{
                                questionsEventListener.onNextQuestion();
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
