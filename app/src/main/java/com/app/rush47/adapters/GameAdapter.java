package com.app.rush47.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.rush47.R;
import com.app.rush47.models.Game;
import com.bumptech.glide.Glide;

import java.util.List;

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.GameViewHolder> {

    /** Implement this in the fragment/activity to react to a game tap. */
    public interface OnGameClickListener {
        void onGameClick(Game game);
    }

    private final List<Game> games;
    private final OnGameClickListener listener;

    public GameAdapter(List<Game> games, OnGameClickListener listener) {
        this.games = games;
        this.listener = listener;
    }

    @NonNull
    @Override
    public GameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_game_card, parent, false);
        return new GameViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GameViewHolder holder, int position) {
        Game game = games.get(position);
        holder.gameName.setText(game.getName());
        holder.matchesAvailable.setText(
                holder.itemView.getContext().getString(R.string.match_available_)
                        + " " + game.getMatchesAvailable());

        Glide.with(holder.itemView.getContext())
                .load(game.getBannerUrl())
                .placeholder(R.color.newblack)
                .into(holder.gameBanner);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onGameClick(game);
            }
        });
    }

    @Override
    public int getItemCount() {
        return games.size();
    }

    static class GameViewHolder extends RecyclerView.ViewHolder {
        ImageView gameBanner;
        TextView gameName;
        TextView matchesAvailable;

        GameViewHolder(@NonNull View itemView) {
            super(itemView);
            gameBanner = itemView.findViewById(R.id.gamebanner);
            gameName = itemView.findViewById(R.id.gamename);
            matchesAvailable = itemView.findViewById(R.id.matchesavailable);
        }
    }
}
