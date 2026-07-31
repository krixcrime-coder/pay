//for show data in result fragment
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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.di.battlemaniaV5.R;
import com.di.battlemaniaV5.models.LotteryData;
import com.di.battlemaniaV5.ui.activities.SelectedLotteryActivity;
import com.di.battlemaniaV5.utils.LocaleHelper;
import com.squareup.picasso.Picasso;

import java.util.List;

public class LotteryResultAdapter extends RecyclerView.Adapter<LotteryResultAdapter.MyViewHolder> {

    private Context mContext;
    private List<LotteryData> mData;

    Context context;
    Resources resources;

    public LotteryResultAdapter(Context mContext, List<LotteryData> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }
    @NonNull
    @Override
    public LotteryResultAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.lottery_result_layout, parent, false);
        return new LotteryResultAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final LotteryResultAdapter.MyViewHolder holder, int position) {

        context = LocaleHelper.setLocale(mContext);
        resources = context.getResources();

        SharedPreferences sp = mContext.getSharedPreferences("currencyinfo", Context.MODE_PRIVATE);
       // String selectedcurrency = sp.getString("currency", "₹");
        SpannableStringBuilder builder = new SpannableStringBuilder();


        final LotteryData data = mData.get(position);

        if(!TextUtils.equals(data.getlImage(),"")){
            Picasso.get().load(Uri.parse(data.getlImage())).placeholder(R.drawable.lucky_draw).fit().into(holder.limageview);
        }

        builder.append(Html.fromHtml(resources.getString(R.string.won_prize__)+" "))
                .append(" ", new ImageSpan(mContext, R.drawable.resize_coin1618,ImageSpan.ALIGN_BASELINE), 0)
                .append(" ")
                .append(Html.fromHtml(""+data.getlPrize()));
        holder.winprize.setText(builder);

        holder.title.setText(data.getlTitla()+" - "+resources.getString(R.string.lottery)+" #"+data.getlId());
        holder.time.setText(resources.getString(R.string.draw_on)+" : "+data.getlTime().substring(0,11));
        holder.wonby.setText(resources.getString(R.string.won_by)+" : "+data.getlWonBy());


        holder.lotteryresultcv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(mContext, SelectedLotteryActivity.class);
                intent.putExtra("FROM","RESULT");
                intent.putExtra("LID",data.getlId());
                intent.putExtra("TITLE",data.getlTitla());
                intent.putExtra("BANER",data.getlImage());
                intent.putExtra("TIME",data.getlTime());
                intent.putExtra("ENTRYFEE",data.getLfee());
                intent.putExtra("PRIZE",data.getlPrize());
                intent.putExtra("WONBY",data.getlWonBy());
                intent.putExtra("ABOUT",data.getlRule());
                intent.putExtra("JOINMEMBER",data.getlJoinedMember());
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

        CardView lotteryresultcv;
        ImageView limageview;
        TextView winprize;
        TextView title;
        TextView time;
        TextView wonby;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);


            lotteryresultcv=(CardView)itemView.findViewById(R.id.lotteryresultcardview);
            limageview=(ImageView)itemView.findViewById(R.id.lrimageview);
            winprize = (TextView) itemView.findViewById(R.id.lrwonprize);
            title = (TextView) itemView.findViewById(R.id.lrtitle);
            time = (TextView) itemView.findViewById(R.id.lrtime);
            wonby = (TextView) itemView.findViewById(R.id.lrwonby);
        }
    }
}
