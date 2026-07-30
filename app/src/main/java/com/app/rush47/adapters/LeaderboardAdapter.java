package com.app.rush47.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.rush47.R;
import com.app.rush47.models.LeaderboardEntry;

import java.util.List;

/** Feeds ranked rows into the RecyclerView on LeaderboardActivity. */
public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.ViewHolder> {

    private final List<LeaderboardEntry> entries;

    public LeaderboardAdapter(List<LeaderboardEntry> entries) {
        this.entries = entries;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_leaderboard_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LeaderboardEntry item = entries.get(position);
        holder.rank.setText(item.getRank());
        holder.userName.setText(item.getUserName());
        holder.kills.setText(item.getTotalKills() + " kills");
        holder.earnings.setText(item.getTotalEarnings());
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView rank;
        final TextView userName;
        final TextView kills;
        final TextView earnings;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            rank = itemView.findViewById(R.id.leaderboardRank);
            userName = itemView.findViewById(R.id.leaderboardUsername);
            kills = itemView.findViewById(R.id.leaderboardKills);
            earnings = itemView.findViewById(R.id.leaderboardEarnings);
        }
    }
}
