package com.example.alexandremenielle.quizzapp;

import android.graphics.Color;
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

    public PlayersAdapter(ArrayList<User> users) {
        this.users = users;
    }

    @Override
    public PlayersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.players_cell,parent,false);
        return new PlayersAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PlayersAdapter.ViewHolder holder, int position) {
        User user = users.get(position);
        holder.playerCellName.setText(user.getFirstname());
        if (user.getIsOnline()) {
            holder.playerCellName.setTextColor(Color.GREEN);
        }else{
            holder.playerCellName.setTextColor(Color.RED);
        }
    }

    @Override
    public int getItemCount() {
        return users == null ? 0 : users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.playerCellName) TextView playerCellName;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
