//For show data in ongoing tab
package com.di.battlemaniaV5.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.di.battlemaniaV5.R;
import com.di.battlemaniaV5.models.AllOngoingMatchData;
import com.di.battlemaniaV5.ui.activities.SelectedTournamentActivity;
import com.di.battlemaniaV5.utils.LocaleHelper;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AllOngoingMatchAdapter extends RecyclerView.Adapter<AllOngoingMatchAdapter.MyViewHolder> {

    private Context mContext;
    private List<AllOngoingMatchData> mData;

    Context context;
    Resources resources;

    public AllOngoingMatchAdapter(Context mContext, List<AllOngoingMatchData> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public AllOngoingMatchAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.allongoingmatchdata, parent, false);
        return new AllOngoingMatchAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AllOngoingMatchAdapter.MyViewHolder holder, int position) {

        context = LocaleHelper.setLocale(mContext);
        resources = context.getResources();

        final AllOngoingMatchData data = mData.get(position);
        if (!data.getMatchbanner().matches("")) {
            Picasso.get().load(Uri.parse(data.getMatchbanner())).placeholder(R.drawable.default_battlemania).fit().into(holder.ongoingImageView);
        } else {
            holder.ongoingImageView.setVisibility(View.GONE);
           // holder.ongoingImageView.setImageDrawable(mContext.getDrawable(R.drawable.default_battlemania));
        }
        SharedPreferences sp = mContext.getSharedPreferences("currencyinfo", Context.MODE_PRIVATE);
        //String selectedcurrency = sp.getString("currency", "₹");
        SpannableStringBuilder builder = new SpannableStringBuilder();

        holder.ongoingMatchTitle.setText(data.getMatchname() + " - "+resources.getString(R.string.match)+" #" + data.getmId());

        String newdate = data.getMatchtime().replace(data.getMatchtime().substring(11, 18), "<br><b>" + data.getMatchtime().substring(11, 18));
        holder.ongoingTime.setText(Html.fromHtml(newdate));

        builder.append(Html.fromHtml(resources.getString(R.string.PRIZE_POOL)+"<br>"))
                .append(" ", new ImageSpan(mContext, R.drawable.resize_coin1618,ImageSpan.ALIGN_BASELINE), 0)
                .append(" ")
                .append(Html.fromHtml("<b>"+data.getWinprize()));
        holder.ongoingPrizeWin.setText(builder);

        builder = new SpannableStringBuilder();
        builder.append(Html.fromHtml(resources.getString(R.string.PER_KILL)+"<br>"))
                .append(" ", new ImageSpan(mContext, R.drawable.resize_coin1618,ImageSpan.ALIGN_BASELINE), 0)
                .append(" ")
                .append(Html.fromHtml("<b>"+data.getPerkill()));
        holder.ongoingPerKill.setText(builder);

        builder = new SpannableStringBuilder();
        builder.append(Html.fromHtml(""))
                .append(" ", new ImageSpan(mContext, R.drawable.resize_coin1618,ImageSpan.ALIGN_BASELINE), 0)
                .append(" ")
                .append(Html.fromHtml("<b>"+data.getEntryfee()));
        holder.ongoingEntryFee.setText(Html.fromHtml("<b>"+data.getEntryfee()));

        holder.ongoingType.setText(Html.fromHtml("<b>" + data.getType() + "</b>"));
        holder.ongoingVersion.setText(Html.fromHtml("<b>" + data.getVersion() + "</b>"));
        holder.ongoingMap.setText(Html.fromHtml("<b>" + data.getMap() + "</b>"));

        holder.roomIdPassll.setVisibility(View.GONE);
        if (TextUtils.equals(data.getRoomDescription(), "") || !TextUtils.equals(data.getJoinstatus(), "true")) {
            holder.roomIdPassll.setVisibility(View.GONE);
        } else {
            holder.roomIdPassll.setVisibility(View.VISIBLE);
            holder.ongoingLl.setBackgroundColor(mContext.getResources().getColor(R.color.newgreen));
            holder.ongoingEntryFee.setBackgroundColor(mContext.getResources().getColor(R.color.newgreen));
            holder.ongoingSpactateTv.setBackgroundColor(mContext.getResources().getColor(R.color.newgreen));
        }
        holder.ongoingTournamentCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, SelectedTournamentActivity.class);
                intent.putExtra("FROM", "LIVE");
                intent.putExtra("M_ID", data.getmId());
                intent.putExtra("BANER", data.getMatchbanner());
                mContext.startActivity(intent);
            }
        });

        holder.spectate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(data.getMatchurl()));
              /*  intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setPackage("com.google.android.youtube");*/
                mContext.startActivity(intent);
            }
        });
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

        ImageView ongoingImageView;
        TextView ongoingMatchTitle;
        TextView ongoingTime;
        TextView ongoingPrizeWin;
        TextView ongoingPerKill;
        TextView ongoingEntryFee;
        TextView ongoingType;
        TextView ongoingVersion;
        TextView ongoingMap;
        CardView ongoingTournamentCardView;
        CardView spectate;
        LinearLayout ongoingLl;
        TextView ongoingSpactateTv;
        LinearLayout roomIdPassll;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            ongoingImageView = (ImageView) itemView.findViewById(R.id.ongoingimageview);
            ongoingMatchTitle = (TextView) itemView.findViewById(R.id.ongoingmatchtitle);
            ongoingTime = (TextView) itemView.findViewById(R.id.ongoingtime);
            ongoingPrizeWin = (TextView) itemView.findViewById(R.id.ongoingprizewin);
            ongoingPerKill = (TextView) itemView.findViewById(R.id.ongoingperkill);
            ongoingEntryFee = (TextView) itemView.findViewById(R.id.ongoingentryfee);
            ongoingType = (TextView) itemView.findViewById(R.id.ongoingtype);
            ongoingVersion = (TextView) itemView.findViewById(R.id.ongoingversion);
            ongoingMap = (TextView) itemView.findViewById(R.id.ongoingtvmap);
            ongoingTournamentCardView = (CardView) itemView.findViewById(R.id.ongoingtournamentcardview);
            spectate = (CardView) itemView.findViewById(R.id.spectate);
            ongoingLl = (LinearLayout) itemView.findViewById(R.id.ongoingll);
            ongoingSpactateTv = (TextView) itemView.findViewById(R.id.ongoingspactatetv);
            roomIdPassll = (LinearLayout) itemView.findViewById(R.id.roomidpassllinlive);
        }
    }
}
