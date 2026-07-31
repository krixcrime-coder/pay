//For show data in completed tab
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
import com.di.battlemaniaV5.models.AllGameResultData;
import com.di.battlemaniaV5.ui.activities.SelectedResultActivity;
import com.di.battlemaniaV5.utils.LocaleHelper;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AllgameResultAdapter extends RecyclerView.Adapter<AllgameResultAdapter.MyViewHolder> {

    private Context mContext;
    private List<AllGameResultData> mData;

    Context context;
    Resources resources;

    public AllgameResultAdapter(Context mContext, List<AllGameResultData> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public AllgameResultAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.allgameresultdata, parent, false);
        return new AllgameResultAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AllgameResultAdapter.MyViewHolder holder, int position) {

        context = LocaleHelper.setLocale(mContext);
        resources = context.getResources();

        SharedPreferences sp = mContext.getSharedPreferences("currencyinfo", Context.MODE_PRIVATE);
        //String selectedcurrency = sp.getString("currency", "₹");
        SpannableStringBuilder builder = new SpannableStringBuilder();

        final AllGameResultData data = mData.get(position);
        if (!data.getMatchbanner().equals("")) {
            Picasso.get().load(data.getMatchbanner()).placeholder(R.drawable.default_battlemania).fit().into(holder.imageView);
        } else {
            holder.imageView.setVisibility(View.GONE);
            //holder.imageView.setImageDrawable(mContext.getDrawable(R.drawable.default_battlemania));
        }

        holder.allGametvMatchTitle.setText(data.getMatchname() + " - "+resources.getString(R.string.match)+" #" + data.getMid());
        String newdate = data.getMatchtime().replace(data.getMatchtime().substring(11, 18), "<br><b>" + data.getMatchtime().substring(11, 18));
        holder.allGametvTime.setText(Html.fromHtml(newdate));

        builder.append(Html.fromHtml(resources.getString(R.string.PRIZE_POOL)+"<br>"))
                /*.append(" ", new ImageSpan(mContext, R.drawable.resize_coin1618,ImageSpan.ALIGN_BASELINE), 0)
                .append(" ")*/
                .append(Html.fromHtml("<b>"+data.getWinprize())).append("(%)");
        holder.allGametvPrizeWin.setText(builder);

        builder = new SpannableStringBuilder();
        builder.append(Html.fromHtml(resources.getString(R.string.PER_KILL)+"<br>"))
                /*.append(" ", new ImageSpan(mContext, R.drawable.resize_coin1618,ImageSpan.ALIGN_BASELINE), 0)
                .append(" ")*/
                .append(Html.fromHtml("<b>"+data.getPerkill()))
                .append("(%)");
        holder.allGametvPerKill.setText(builder);

        builder = new SpannableStringBuilder();
        builder.append(Html.fromHtml(""))
                .append(" ", new ImageSpan(mContext, R.drawable.resize_coin1618,ImageSpan.ALIGN_BASELINE), 0)
                .append(" ")
                .append(Html.fromHtml("<b>"+data.getEntryfee()));
        holder.allGametvEntryFee.setText(Html.fromHtml("<b>"+data.getEntryfee()));

        holder.allGametvType.setText(Html.fromHtml("<b>" + data.getType() + "</b>"));
        holder.allGametvVersion.setText(Html.fromHtml("<b>" + data.getVersion() + "</b>"));
        holder.allGametvMap.setText(Html.fromHtml("<b>" + data.getMap() + "</b>"));
        holder.allGameResultCardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, SelectedResultActivity.class);
                intent.putExtra("M_ID", data.getMid());
                intent.putExtra("BANER", data.getMatchbanner());
                mContext.startActivity(intent);
            }
        });
        holder.watchMatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(data.getMatchurl()));
              /*  intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setPackage("com.google.android.youtube");*/
                mContext.startActivity(intent);
            }
        });
        if (TextUtils.equals(data.getJoinstatus(), "false")) {
            holder.allGameResultStatus.setText(resources.getString(R.string.NOT_JOINED));
        } else {
            holder.allGameResultStatus.setText(resources.getString(R.string.JOINED));
            holder.joinStatusll.setBackgroundColor(mContext.getResources().getColor(R.color.newgreen));
            holder.allGametvEntryFee.setBackgroundColor(mContext.getResources().getColor(R.color.newgreen));
            holder.allGameResultStatus.setBackgroundColor(mContext.getResources().getColor(R.color.newgreen));
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
        ImageView imageView;
        TextView allGametvMatchTitle;
        TextView allGametvTime;
        TextView allGametvPrizeWin;
        TextView allGametvPerKill;
        TextView allGametvEntryFee;
        TextView allGametvType;
        TextView allGametvVersion;
        TextView allGametvMap;
        TextView watchMatch;
        CardView allGameResultCardview;
        TextView allGameResultStatus;
        LinearLayout joinStatusll;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.imageview);
            allGametvMatchTitle = (TextView) itemView.findViewById(R.id.allgametvmatchtitle);
            allGametvTime = (TextView) itemView.findViewById(R.id.allgametvtime);
            allGametvPrizeWin = (TextView) itemView.findViewById(R.id.allgametvprizewin);
            allGametvPerKill = (TextView) itemView.findViewById(R.id.allgametvperkill);
            allGametvEntryFee = (TextView) itemView.findViewById(R.id.allgametventryfee);
            allGametvType = (TextView) itemView.findViewById(R.id.allgametvtype);
            allGametvVersion = (TextView) itemView.findViewById(R.id.allgametvversion);
            allGametvMap = (TextView) itemView.findViewById(R.id.allgametvmap);
            watchMatch = (TextView) itemView.findViewById(R.id.watchmatch);
            allGameResultCardview = (CardView) itemView.findViewById(R.id.allgameresultcardview);
            allGameResultStatus = (TextView) itemView.findViewById(R.id.allgameresultstatus);
            joinStatusll = (LinearLayout) itemView.findViewById(R.id.joinstatusll);
        }
    }
}
