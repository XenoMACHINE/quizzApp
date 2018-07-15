package com.example.duelmanagerlib;

import com.example.duelmanagerlib.Delegation.ReadyPlayer;
import com.example.duelmanagerlib.Delegation.UnReadyPlayer;
import com.example.duelmanagerlib.Factory.QuestionSingleAnswer;
import com.example.duelmanagerlib.Model.Duel;
import com.example.duelmanagerlib.Model.Question;
import com.example.duelmanagerlib.Model.Theme;
import com.example.duelmanagerlib.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by alexandremenielle on 03/06/2018.
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

    public String currentIdDuel = "";

    public User ennemie;

    public ArrayList<String> duelQuestionsIds = new ArrayList<>();

    public ArrayList<Question> duelQuestions = new ArrayList<>();

    private Boolean hasCancel = false;

    public void getDuelQuestionsIds(){
        duelQuestionsIds.clear();
        mDatabase.child("duels").child(currentIdDuel).child("questions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, Object> questionsIds = (HashMap) dataSnapshot.getValue();
                if (questionsIds != null){
                    for(String questionId : questionsIds.keySet()){
                        duelQuestionsIds.add(questionId);
                    }
                    getDuelQuestions();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void getDuelQuestions(){
        duelQuestions.clear();
        Collections.shuffle(duelQuestionsIds);
        for (String questionId : duelQuestionsIds){
            mDatabase.child("questions").child(questionId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Question question = dataSnapshot.getValue(QuestionSingleAnswer.class);
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
        final Map<String, Object> questions = new HashMap<>();
        mDatabase.child("themes").child(theme.getId()).child("questions").limitToFirst(5).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, Object> questionsIds = (HashMap) dataSnapshot.getValue();
                if (questionsIds != null) {
                    mDatabase.child("duels").child(currentIdDuel).child("questions").updateChildren(questionsIds);
                    for(String questionId : questionsIds.keySet()){
                        duelQuestionsIds.add(questionId);
                    }
                    getDuelQuestions();
                }
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
        ennemie = selectedUser;
        User currentUser = AppManager.getInstance().currentUser;

        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> players = new HashMap<>();

        Map<String, Object> currentUserHm =  new ReadyPlayer().makePlayer(currentUser.getId());
        Map<String, Object> selectedUserHm = new UnReadyPlayer().makePlayer(selectedUser.getId());

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

    public void cancelSentDuel(User ennemie){
        HashMap<String, Object> pushData = new HashMap<>();
        pushData.put(currentIdDuel, true);
        mDatabase.child("users").child(ennemie.getId()).child("duels").updateChildren(pushData);
        pushData.clear();
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
                    manageDuelListener(currentIdDuel);
                    duelEventListener.duelRequestAnswered("accepted");
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
        hasCancel = false;
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
                if(duel.getStatus().equals("4") && !hasCancel){
                    if(duelEventListener != null){
                        //TODO que si les 2 users sont pas ready
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
                        ennemie = user;
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
        pushData.put("status","1");
        mDatabase.child("duels").child(currentIdDuel).updateChildren(pushData);

        pushData.clear();
        pushData.put("isReady",true);
        mDatabase.child("duels").child(currentIdDuel).child("players").child(currentUserId).updateChildren(pushData);
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

    public void updateDuelStatus(String status){
        if (status == "4"){
            hasCancel = true;
        }
        Map<String, Object> pushMap = new HashMap<>();
        pushMap.put("status",status);
        mDatabase.child("duels").child(currentIdDuel).updateChildren(pushMap);
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
                                calculScore(duel);
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

    private void calculScore(Duel duel){
        int maxScore = 0;
        int minScore = 0;
        int tmp = 0;
        String winnerId = null;
        for(Object object : duel.getPlayers().values()) {
            HashMap<String,Object> player = (HashMap<String, Object>) object;
            minScore = ((Long) player.get("score")).intValue();

            if (minScore >= maxScore) {
                tmp = maxScore;
                maxScore = minScore;
                minScore = tmp;
                winnerId = (String) player.get("id");
            }
        }

        if (minScore == maxScore){
            questionsEventListener.onDuelFinished("Partie terminée, égalité ! ( " + minScore + " - " + minScore + " )");
            return;
        }
        String winnerName = "";
        if (winnerId.equals(AppManager.getInstance().currentUser.getId())){
            winnerName = AppManager.getInstance().currentUser.getFirstname();
        }else if (ennemie != null){
            winnerName = ennemie.getFirstname();
        }
        questionsEventListener.onDuelFinished(  " Partie terminée, " + winnerName + " à gagné(e) ! ( " + maxScore + " - " + minScore + " )");
    }

}