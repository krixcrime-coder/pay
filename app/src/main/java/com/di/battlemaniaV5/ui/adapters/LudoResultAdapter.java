package com.di.battlemaniaV5.ui.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.di.battlemaniaV5.R;
import com.di.battlemaniaV5.models.CurrentUser;
import com.di.battlemaniaV5.models.LudoLivematchData;
import com.di.battlemaniaV5.utils.LoadingDialog;
import com.di.battlemaniaV5.utils.UserLocalStore;
import com.squareup.picasso.Picasso;

import java.util.List;

public class LudoResultAdapter extends RecyclerView.Adapter<LudoResultAdapter.MyViewHolder> {

    private Context mContext;
    private List<LudoLivematchData> mData;

    CurrentUser user;
    UserLocalStore userLocalStore;
    LoadingDialog loadingDialog;

    public LudoResultAdapter(Context mContext, List<LudoLivematchData> mData) {
        this.mContext = mContext;
        this.mData = mData;

    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(mContext).inflate(R.layout.live_challenges_data, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        userLocalStore = new UserLocalStore(mContext);
        user = userLocalStore.getLoggedInUser();

        loadingDialog = new LoadingDialog(mContext);

        final LudoLivematchData data = mData.get(position);

        holder.autoTv.setText(data.getAutoId());

        holder.coinTv.setText(data.getCoin() + " Coins");

        holder.creatorNameTv.setText(data.getFirstName() + " " + data.getLastName());


        if (TextUtils.equals(data.getProfileImage(), "null") || TextUtils.equals(data.getProfileImage(), "")) {

        } else {
            Picasso.get().load(data.getProfileImage()).placeholder(R.drawable.battlemanialogo).fit().into(holder.creatorIv);
        }

        if (TextUtils.equals(data.getAcceptedProfileImage(), "null") || TextUtils.equals(data.getAcceptedProfileImage(), "")) {

        } else {
            Picasso.get().load(data.getAcceptedProfileImage()).placeholder(R.drawable.battlemanialogo).fit().into(holder.acceptedIv);
        }

        holder.winningTv.setText("Winning " + data.getWinningPrice() + " Coins");

        if (TextUtils.equals(data.getAcceptStatus(), "0")) {
            holder.acceptedNameTv.setText("");
        } else {
            holder.acceptedNameTv.setText(data.getAcceptedMemberName());
        }

        if (TextUtils.equals(data.getChallengeStatus(), "1")) {//active

            if (TextUtils.equals(data.getRoomCode(), "")) {

                holder.acceptTv.setText("Cancel");
            } else {
                holder.acceptTv.setVisibility(View.GONE);
            }


        } else if (TextUtils.equals(data.getChallengeStatus(), "2")) {//canceled
            holder.acceptTv.setText("Canceled");

        } else if (TextUtils.equals(data.getChallengeStatus(), "3")) {//completed

            holder.acceptTv.setText("Completed");

        } else if (TextUtils.equals(data.getChallengeStatus(), "4")) {//pending

            holder.acceptTv.setText("Wait for result");

        }

        holder.acceptTv.setEnabled(false);

        Log.d(data.getMemberId(), data.getWinnerId());
        if (!TextUtils.equals(data.getWinnerId(), "0")) {
            if (TextUtils.equals(data.getWinnerId(), data.getMemberId())) {
                holder.winnerCreator.setVisibility(View.VISIBLE);
            } else {
                holder.winnerAccept.setVisibility(View.VISIBLE);
            }
        }


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

        TextView autoTv;
        TextView coinTv;
        TextView creatorNameTv;
        TextView acceptedNameTv;
        TextView winningTv;
        TextView acceptTv;
        ImageView creatorIv;
        ImageView acceptedIv;
        TextView winnerCreator;
        TextView winnerAccept;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            autoTv = (TextView) itemView.findViewById(R.id.autocodetv);
            coinTv = (TextView) itemView.findViewById(R.id.cointv);
            creatorNameTv = (TextView) itemView.findViewById(R.id.creatorname_tv);
            acceptedNameTv = (TextView) itemView.findViewById(R.id.acceptedname_tv);
            winningTv = (TextView) itemView.findViewById(R.id.winingcointv);
            acceptTv = (TextView) itemView.findViewById(R.id.accepttv);//btn
            creatorIv = (ImageView) itemView.findViewById(R.id.creater_iv);
            acceptedIv = (ImageView) itemView.findViewById(R.id.acceptediv);
            winnerCreator = (TextView) itemView.findViewById(R.id.winner_creator);
            winnerAccept = (TextView) itemView.findViewById(R.id.winner_accept);
        }
    }

}

