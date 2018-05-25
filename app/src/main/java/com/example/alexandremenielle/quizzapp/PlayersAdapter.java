package com.example.alexandremenielle.quizzapp;

import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.alexandremenielle.quizzapp.Model.User;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by alexandremenielle on 01/05/2018.
 */

public class PlayersAdapter extends RecyclerView.Adapter<PlayersAdapter.ViewHolder> {
    private ArrayList<User> users;
    private ItemClickListener clickListener;

    public PlayersAdapter(ArrayList<User> users) {
        this.users = sortUsersByOnline(users);
    }

    @Override
    public PlayersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.players_cell,parent,false);
        return new PlayersAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PlayersAdapter.ViewHolder holder, final int position) {
        final User user = users.get(position);
        holder.playerCellName.setText(user.getFirstname());
        if (user.getIsOnline()) {
            holder.playerCellName.setTextColor(Color.GREEN);
        }else{
            holder.playerCellName.setTextColor(Color.RED);
        }
        holder.playerCell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickListener != null) clickListener.onClick(view, user);
            }
        });
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
    public int getItemCount() {
        return users == null ? 0 : users.size();
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.playerCellName) TextView playerCellName;
        @BindView(R.id.playerCell) ConstraintLayout playerCell;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
