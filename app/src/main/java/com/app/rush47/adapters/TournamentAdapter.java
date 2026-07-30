package com.app.rush47.adapters;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.rush47.R;
import com.app.rush47.models.Tournament;
import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Locale;

/**
 * Feeds match cards on the Matches list and My Matches screens. The
 * card's bottom button changes meaning depending on where it's shown:
 * on the browse list it's a real "Join" action; on My Matches (where
 * you're already joined) it's disabled and just says "JOINED" - pass
 * showJoinAction=false there so taps don't try to join again.
 *
 * Card layout was rebuilt 1:1 from the original decompiled app
 * (tournament_data.xml): banner image, pinned indicator, game logo,
 * a Room ID/Password reveal strip, and the Type/Version/Map row.
 */
public class TournamentAdapter extends RecyclerView.Adapter<TournamentAdapter.TournamentViewHolder> {

    public interface OnTournamentClickListener {
        void onCardClick(Tournament tournament);

        void onJoinClick(Tournament tournament);
    }

    private final List<Tournament> tournaments;
    private final OnTournamentClickListener listener;
    private final boolean showJoinAction;

    public TournamentAdapter(List<Tournament> tournaments, boolean showJoinAction, OnTournamentClickListener listener) {
        this.tournaments = tournaments;
        this.showJoinAction = showJoinAction;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TournamentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tournament_card, parent, false);
        return new TournamentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TournamentViewHolder holder, int position) {
        Tournament t = tournaments.get(position);
        android.content.Context context = holder.itemView.getContext();

        holder.title.setText(t.getTitle());

        String mapText = TextUtils.isEmpty(t.getMapName()) ? "" : t.getMapName() + "  |  ";
        holder.mapTime.setText(mapText + formatMatchTime(t.getMatchTime()));

        holder.entryFee.setText(t.getEntryFee());
        holder.prize.setText(t.getPrize());
        holder.perKill.setText(t.getPerKill());
        holder.typeText.setText(t.getType());
        holder.versionText.setText(t.getVersion());
        holder.mapText.setText(t.getMapName());

        Glide.with(context)
                .load(t.getImageUrl())
                .placeholder(R.drawable.battlemanialogo)
                .error(R.drawable.battlemanialogo)
                .into(holder.bannerImage);

        holder.pinIcon.setVisibility(t.isPinned() ? View.VISIBLE : View.GONE);

        boolean showRoomStrip = t.isJoined() && !TextUtils.isEmpty(t.getRoomDescription());
        holder.roomStrip.setVisibility(showRoomStrip ? View.VISIBLE : View.GONE);
        if (showRoomStrip) {
            holder.roomText.setText(t.getRoomDescription());
        }

        holder.slotsProgress.setMax(Math.max(t.getSlotTotal(), 1));
        holder.slotsProgress.setProgress(t.getSlotsFilled());
        holder.slotsText.setText(context.getString(R.string.slots_filled_format, t.getSlotsFilled(), t.getSlotTotal()));

        bindJoinButton(holder, t, context);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onCardClick(t);
        });
    }

    private void bindJoinButton(TournamentViewHolder holder, Tournament t, android.content.Context context) {
        if (!showJoinAction) {
            holder.joinButton.setText(R.string.joined);
            holder.joinButton.setAlpha(0.6f);
            holder.joinButton.setOnClickListener(null);
            return;
        }

        holder.joinButton.setAlpha(1f);
        if (t.isJoined()) {
            holder.joinButton.setText(R.string.joined);
            holder.joinButton.setAlpha(0.6f);
            holder.joinButton.setOnClickListener(null);
        } else if (t.isFull() || t.isCompleted()) {
            holder.joinButton.setText(t.isFull() ? R.string.match_full : R.string.completed);
            holder.joinButton.setAlpha(0.6f);
            holder.joinButton.setOnClickListener(null);
        } else {
            holder.joinButton.setText(R.string.join);
            holder.joinButton.setOnClickListener(v -> {
                if (listener != null) listener.onJoinClick(t);
            });
        }
    }

    /** "2026-07-27 20:30:00" -> "27 Jul, 08:30 PM" (falls back to the raw string on parse failure). */
    private String formatMatchTime(String raw) {
        if (TextUtils.isEmpty(raw)) return "";
        try {
            java.text.SimpleDateFormat in = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            java.text.SimpleDateFormat out = new java.text.SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault());
            return out.format(in.parse(raw));
        } catch (Exception e) {
            return raw;
        }
    }

    @Override
    public int getItemCount() {
        return tournaments.size();
    }

    static class TournamentViewHolder extends RecyclerView.ViewHolder {
        TextView title, mapTime, entryFee, prize, perKill, typeText, versionText, mapText,
                slotsText, joinButton, roomText;
        ImageView bannerImage, pinIcon;
        View roomStrip;
        ProgressBar slotsProgress;

        TournamentViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.matchTitle);
            mapTime = itemView.findViewById(R.id.matchMapTime);
            entryFee = itemView.findViewById(R.id.matchEntryFee);
            prize = itemView.findViewById(R.id.matchPrize);
            perKill = itemView.findViewById(R.id.matchPerKill);
            typeText = itemView.findViewById(R.id.matchTypeText);
            versionText = itemView.findViewById(R.id.matchVersionText);
            mapText = itemView.findViewById(R.id.matchMapText);
            slotsProgress = itemView.findViewById(R.id.matchSlotsProgress);
            slotsText = itemView.findViewById(R.id.matchSlotsText);
            joinButton = itemView.findViewById(R.id.matchJoinButton);
            bannerImage = itemView.findViewById(R.id.matchBannerImage);
            pinIcon = itemView.findViewById(R.id.matchPinIcon);
            roomStrip = itemView.findViewById(R.id.roomIdPasswordStrip);
            roomText = itemView.findViewById(R.id.roomIdPasswordText);
        }
    }
}
