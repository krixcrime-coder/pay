package com.app.rush47.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.rush47.R;

import java.util.HashSet;
import java.util.Set;

/**
 * One cell per position number, 1..slotTotal (matches the original
 * "Select Match Position" screen - a plain numbered grid of checkboxes,
 * greyed out and pre-checked for taken slots, tappable for open ones).
 */
public class PositionAdapter extends RecyclerView.Adapter<PositionAdapter.PositionViewHolder> {

    public interface OnPositionSelectedListener {
        void onPositionSelected(int position);
    }

    private final int slotTotal;
    private final Set<Integer> takenSlots;
    private final OnPositionSelectedListener listener;
    private int selectedPosition = 0;

    public PositionAdapter(int slotTotal, Set<Integer> takenSlots, OnPositionSelectedListener listener) {
        this.slotTotal = slotTotal;
        this.takenSlots = takenSlots != null ? takenSlots : new HashSet<>();
        this.listener = listener;
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    @NonNull
    @Override
    public PositionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.selectmatchposition_data, parent, false);
        return new PositionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PositionViewHolder holder, int viewHolderPosition) {
        int slotNumber = viewHolderPosition + 1;
        boolean taken = takenSlots.contains(slotNumber);
        boolean selected = slotNumber == selectedPosition;

        holder.checkTv.setText(String.valueOf(slotNumber));
        holder.checkbox.setChecked(taken || selected);
        holder.checkbox.setEnabled(!taken);
        holder.itemView.setAlpha(taken ? 0.5f : 1f);

        holder.itemView.setOnClickListener(v -> {
            if (taken) return;
            int previouslySelected = selectedPosition;
            selectedPosition = slotNumber;
            if (previouslySelected > 0) {
                notifyItemChanged(previouslySelected - 1);
            }
            notifyItemChanged(viewHolderPosition);
            if (listener != null) {
                listener.onPositionSelected(selectedPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return Math.max(slotTotal, 0);
    }

    static class PositionViewHolder extends RecyclerView.ViewHolder {
        TextView checkTv;
        CheckBox checkbox;

        PositionViewHolder(@NonNull View itemView) {
            super(itemView);
            checkTv = itemView.findViewById(R.id.checktv);
            checkbox = itemView.findViewById(R.id.checkbox);
        }
    }
}
