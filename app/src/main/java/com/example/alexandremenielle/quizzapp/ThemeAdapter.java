package com.example.alexandremenielle.quizzapp;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.alexandremenielle.quizzapp.Model.Theme;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by alexandremenielle on 11/04/2018.
 */

public class ThemeAdapter extends RecyclerView.Adapter<ThemeAdapter.ViewHolder>{

    private ArrayList<Theme> themes;
    private ItemClickListener clickListener;

    public ThemeAdapter(ArrayList<Theme> themes) {
        this.themes = themes;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.theme_cell,parent,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Theme theme = themes.get(position);
        holder.name.setText(theme.getName());
        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickListener != null) clickListener.onClick(view, theme);
            }
        });
    }

    @Override
    public int getItemCount() {
        return themes == null ? 0 : themes.size();
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.name) TextView name;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
