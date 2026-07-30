package com.app.rush47.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.rush47.R;
import com.app.rush47.models.Announcement;

import java.util.List;

/** Feeds announcement rows into the RecyclerView on AnnouncementActivity. */
public class AnnouncementAdapter extends RecyclerView.Adapter<AnnouncementAdapter.ViewHolder> {

    private final List<Announcement> announcements;

    public AnnouncementAdapter(List<Announcement> announcements) {
        this.announcements = announcements;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_announcement_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Announcement item = announcements.get(position);
        holder.message.setText(item.getMessage());
        holder.date.setText(item.getCreatedAt());
    }

    @Override
    public int getItemCount() {
        return announcements.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView message;
        final TextView date;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.announcementMessage);
            date = itemView.findViewById(R.id.announcementDate);
        }
    }
}
