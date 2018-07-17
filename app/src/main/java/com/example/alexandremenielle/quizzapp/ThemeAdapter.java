package com.example.alexandremenielle.quizzapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;


import com.example.duelmanagerlib.Model.Theme;

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
        switch (theme.getName()){
            case "Ingenierie du Big Data":
                holder.frame.setBackgroundResource(R.drawable.bd);
                break;
            case "Mobilité et Objet Connecté":
                holder.frame.setBackgroundResource(R.drawable.moc);
                break;
            case "Architecture des Logiciels":
                holder.frame.setBackgroundResource(R.drawable.reseaux);
                break;
            case "Securite Informatique":
                holder.frame.setBackgroundResource(R.drawable.secu);
                break;
            case "Ingenierie Blockchain":
                holder.frame.setBackgroundResource(R.drawable.bc);
                break;
            case "Ingenierie de la 3d et des jeux-video":
                holder.frame.setBackgroundResource(R.drawable.jv);
                break;
            case "Ingenierie du Web et e-business":
                holder.frame.setBackgroundResource(R.drawable.web);
                break;
            case "Management et conseil en Système d'Informatique":
                holder.frame.setBackgroundResource(R.drawable.management);
                break;
            case "Systemes Reseaux et Cloud Computing":
                holder.frame.setBackgroundResource(R.drawable.cc);
                break;
            default:
                holder.frame.setBackgroundResource(R.drawable.placeholder);
                break;
        }

        holder.frame.setOnClickListener(new View.OnClickListener() {
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
        @BindView(R.id.framelayout) FrameLayout frame;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
