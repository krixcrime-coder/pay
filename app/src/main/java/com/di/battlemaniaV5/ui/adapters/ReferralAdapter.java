//For show data in referrals list
package com.di.battlemaniaV5.ui.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.di.battlemaniaV5.R;
import com.di.battlemaniaV5.models.ReferralInfo;
import com.di.battlemaniaV5.utils.LocaleHelper;

import java.util.List;

public class ReferralAdapter extends RecyclerView.Adapter<ReferralAdapter.MyViewHolder> {
    private Context mContext;
    private List<ReferralInfo> mData;

    Context context;
    Resources resources;

    public ReferralAdapter(Context mContext, List<ReferralInfo> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public ReferralAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.referral_data, parent, false);
        return new ReferralAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ReferralAdapter.MyViewHolder holder, int position) {

        context = LocaleHelper.setLocale(mContext);
        resources = context.getResources();

        ReferralInfo referralInfo = mData.get(position);
        holder.rDate.setText(referralInfo.getData());
        holder.rPlayerName.setText(referralInfo.getPlayername());
        holder.rStatus.setText(referralInfo.getStatus());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView rDate;
        TextView rPlayerName;
        TextView rStatus;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            rDate = (TextView) itemView.findViewById(R.id.rdate);
            rPlayerName = (TextView) itemView.findViewById(R.id.rplayername);
            rStatus = (TextView) itemView.findViewById(R.id.rstatus);
        }
    }
}
