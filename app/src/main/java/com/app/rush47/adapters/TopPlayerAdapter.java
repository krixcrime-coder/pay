package com.app.rush47.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.rush47.R;
import com.app.rush47.models.TopPlayer;

import java.util.List;

public class TopPlayerAdapter extends RecyclerView.Adapter<TopPlayerAdapter.ViewHolder> {

    private final List<TopPlayer> players;

    public TopPlayerAdapter(List<TopPlayer> players) {
        this.players = players;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_top_player, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TopPlayer player = players.get(position);
        holder.rankText.setText(player.getRank());
        holder.usernameText.setText(player.getUserName());
        holder.earningsText.setText("₹" + player.getTotalEarnings());
    }

    @Override
    public int getItemCount() {
        return players.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView rankText, usernameText, earningsText;

        ViewHolder(View itemView) {
            super(itemView);
            rankText = itemView.findViewById(R.id.rank_text);
            usernameText = itemView.findViewById(R.id.username_text);
            earningsText = itemView.findViewById(R.id.earnings_text);
        }
    }
}
