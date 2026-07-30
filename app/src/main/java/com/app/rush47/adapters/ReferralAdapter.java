package com.app.rush47.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.rush47.R;
import com.app.rush47.models.Referral;

import java.util.List;

/** Feeds referral rows into the RecyclerView on MyReferralsActivity. */
public class ReferralAdapter extends RecyclerView.Adapter<ReferralAdapter.ReferralViewHolder> {

    private final List<Referral> referrals;

    public ReferralAdapter(List<Referral> referrals) {
        this.referrals = referrals;
    }

    @NonNull
    @Override
    public ReferralViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_referral_row, parent, false);
        return new ReferralViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReferralViewHolder holder, int position) {
        Referral referral = referrals.get(position);
        holder.userName.setText(referral.getUserName());
        holder.joinedAt.setText(referral.getJoinedAt());
    }

    @Override
    public int getItemCount() {
        return referrals.size();
    }

    static class ReferralViewHolder extends RecyclerView.ViewHolder {
        final TextView userName;
        final TextView joinedAt;

        ReferralViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.referredUsername);
            joinedAt = itemView.findViewById(R.id.referredJoinedAt);
        }
    }
}
