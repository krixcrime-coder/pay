//For show data in upcoming tab
package com.di.battlemaniaV5.ui.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.di.battlemaniaV5.R;
import com.di.battlemaniaV5.models.TournamentData;
import com.di.battlemaniaV5.ui.activities.SelectMatchPositionActivity;
import com.di.battlemaniaV5.ui.activities.SelectedTournamentActivity;
import com.di.battlemaniaV5.utils.LoadingDialog;
import com.di.battlemaniaV5.utils.LocaleHelper;
import com.squareup.picasso.Picasso;

import java.util.List;

import me.toptas.fancyshowcase.FancyShowCaseQueue;
import me.toptas.fancyshowcase.FancyShowCaseView;
import me.toptas.fancyshowcase.FocusShape;
import me.toptas.fancyshowcase.listener.DismissListener;

import static java.lang.Integer.parseInt;

public class TournamentAdapter extends RecyclerView.Adapter<TournamentAdapter.MyViewHolder> {

    LoadingDialog loadingDialog;
    RequestQueue mQueue;
    private Context mContext;
    private List<TournamentData> mData;

    Context context;
    Resources resources;

    public TournamentAdapter(Context mContext, List<TournamentData> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public TournamentAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.tournament_data, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final TournamentAdapter.MyViewHolder holder, int position) {

        context = LocaleHelper.setLocale(mContext);
        resources = context.getResources();

        final TournamentData tournamentData = mData.get(position);

        loadingDialog=new LoadingDialog(mContext);
        SpannableStringBuilder builder = new SpannableStringBuilder();

        if(TextUtils.equals(tournamentData.getPinStatus(),"1")){
            holder.pincv.setVisibility(View.VISIBLE);
        }else if(TextUtils.equals(tournamentData.getPinStatus(),"mymatch")){
            holder.pincv.setVisibility(View.GONE);
        }else {
            holder.pincv.setVisibility(View.GONE);
        }

        holder.tvMatchTitleandNumber.setText(tournamentData.getMatchname() + " - "+resources.getString(R.string.match)+" #" + tournamentData.getMid());
        String newdate = tournamentData.getMatchtime().replace(tournamentData.getMatchtime().substring(11, 18), "<br><b>" + tournamentData.getMatchtime().substring(11, 18));
        holder.tvTime.setText(Html.fromHtml(newdate));

        builder.append(Html.fromHtml(resources.getString(R.string.PRIZE_POOL)+"<br>"))
                /*.append(" ", new ImageSpan(mContext, R.drawable.resize_coin1618,ImageSpan.ALIGN_BASELINE), 0)
                .append(" ")*/
                .append(Html.fromHtml("<b>"+tournamentData.getWinprize()))
                .append("(%)");
        holder.tvTotalWin.setText(builder);

        builder = new SpannableStringBuilder();
        builder.append(Html.fromHtml(resources.getString(R.string.PER_KILL)+"<br>"))
                /*.append(" ", new ImageSpan(mContext, R.drawable.resize_coin1618,ImageSpan.ALIGN_BASELINE), 0)
                .append(" ")*/
                .append(Html.fromHtml("<b>"+tournamentData.getPerkill()))
                .append("(%)");
        holder.tvPerKill.setText(builder);

        builder = new SpannableStringBuilder();
        builder.append(Html.fromHtml(""))
                .append(" ", new ImageSpan(mContext, R.drawable.resize_coin1618,ImageSpan.ALIGN_BASELINE), 0)
                .append(" ")
                .append(Html.fromHtml("<b>"+tournamentData.getEntryfee()));
        holder.tvEntryFee.setText(Html.fromHtml("<b>"+tournamentData.getEntryfee()));

        holder.tvType.setText(tournamentData.getType());
        holder.tvVersion.setText(tournamentData.getVersion());
        holder.tvMap.setText(tournamentData.getMap());
        holder.roomIdPassll.setVisibility(View.GONE);
        if (TextUtils.equals(tournamentData.getRoomDescription(), "") || !TextUtils.equals(tournamentData.getJoinstatus(), "true")) {
            holder.roomIdPassll.setVisibility(View.GONE);
        } else {
            holder.roomIdPassll.setVisibility(View.VISIBLE);
        }
        holder.tvTotalWin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showallprize(tournamentData.getMatchname(), tournamentData.getMid(), tournamentData.getPrizedescreption());
            }
        });

        if (!tournamentData.getMatchbanner().equals("")) {
            Picasso.get().load(Uri.parse(tournamentData.getMatchbanner())).placeholder(R.drawable.default_battlemania).fit().into(holder.imageView);
        } else {
            holder.imageView.setVisibility(View.GONE);
            //holder.imageView.setImageDrawable(mContext.getDrawable(R.drawable.default_battlemania));
        }
        holder.tounamentCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, SelectedTournamentActivity.class);
                intent.putExtra("M_ID", tournamentData.getMid());
                intent.putExtra("FROM", "PLAY");
                intent.putExtra("BANER", tournamentData.getMatchbanner());
                intent.putExtra("GAME_NAME", tournamentData.getGamename());
                mContext.startActivity(intent);
            }
        });

        if (parseInt(tournamentData.getNoofplayer()) == Integer.parseInt(tournamentData.getNumberofposition()) || parseInt(tournamentData.getNoofplayer()) >= Integer.parseInt(tournamentData.getNumberofposition())) {
            holder.joinCardView.setEnabled(false);
            holder.remainTotal.setText(tournamentData.getNumberofposition() + "/" + tournamentData.getNumberofposition());
            holder.join.setText(resources.getString(R.string.MATCH_FULL));
            holder.join.setEnabled(false);
            holder.join.setTextColor(Color.WHITE);
            holder.tvEntryFee.setBackgroundColor(mContext.getResources().getColor(R.color.black));
            holder.joinLl.setBackgroundColor(mContext.getResources().getColor(R.color.black));
            holder.join.setBackgroundColor(mContext.getResources().getColor(R.color.black));
        } else {
            holder.remainTotal.setText(String.valueOf(parseInt(tournamentData.getNoofplayer()) + "/" + tournamentData.getNumberofposition()));
        }
        if (tournamentData.getJoinstatus().matches("true")) {
            holder.join.setText(resources.getString(R.string.JOINED));
            holder.tvEntryFee.setBackgroundColor(mContext.getResources().getColor(R.color.newdisablegreen));
            holder.joinLl.setBackgroundColor(mContext.getResources().getColor(R.color.newdisablegreen));
            holder.join.setBackgroundColor(mContext.getResources().getColor(R.color.newdisablegreen));
        } else {
            holder.join.setEnabled(true);
        }
        holder.progressBar.setMax(parseInt(tournamentData.getNumberofposition()));
        holder.progressBar.setProgress(parseInt(tournamentData.getNoofplayer()));
        int currentprogressinpercetege = (Integer.parseInt(tournamentData.getNoofplayer()) * 100) / Integer.parseInt(tournamentData.getNumberofposition());

        if (currentprogressinpercetege >= 75 && currentprogressinpercetege < 95) {
            holder.progressBar.getProgressDrawable().setColorFilter(mContext.getResources().getColor(R.color.neworange), PorterDuff.Mode.SRC_IN);
        } else if (currentprogressinpercetege >= 95) {
            holder.progressBar.getProgressDrawable().setColorFilter(mContext.getResources().getColor(R.color.newred), PorterDuff.Mode.SRC_IN);
        }

        new FancyShowCaseQueue()
                .add(addView( holder.joinCardView,resources.getString(R.string.show_case_3),"3"))
                .show();
        holder.joinCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tournamentData.getJoinstatus().matches("true")) {
                    Toast.makeText(mContext, resources.getString(R.string.you_are_already_joined), Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(mContext, SelectMatchPositionActivity.class);
                    intent.putExtra("MATCH_ID", tournamentData.getMid());
                    intent.putExtra("GAME_NAME", tournamentData.getGamename());
                    intent.putExtra("MATCH_NAME", tournamentData.getMatchname());
                    intent.putExtra("TYPE", tournamentData.getType());
                    intent.putExtra("TOTAL", tournamentData.getNumberofposition());
                    intent.putExtra("JOIN_STATUS", tournamentData.getJoinstatus());
                    mContext.startActivity(intent);
                }
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

    public void showallprize(String matchname, String matchnumber, String prizedesc) {

        final Dialog builder = new Dialog(mContext);
        builder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams wlmp = builder.getWindow().getAttributes();
        wlmp.gravity = Gravity.BOTTOM;
        wlmp.y = 100; // bottom margin
        builder.getWindow().setAttributes(wlmp);
        builder.setContentView(R.layout.showallprizelayout);

        TextView title_number = (TextView) builder.findViewById(R.id.title_number);
        TextView winner = (TextView) builder.findViewById(R.id.winner);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            winner.setText(Html.fromHtml(prizedesc, Html.FROM_HTML_MODE_COMPACT));
        } else {
            winner.setText(Html.fromHtml(prizedesc));
        }

        title_number.setText(matchname + " - "+resources.getString(R.string.match)+" #" + matchnumber);
        ImageView cancel = (ImageView) builder.findViewById(R.id.cancelprize);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.dismiss();
            }
        });
        builder.show();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvMatchTitleandNumber;
        TextView tvTime;
        TextView tvTotalWin;
        TextView tvPerKill;
        TextView tvEntryFee;
        TextView tvType;
        TextView tvVersion;
        TextView tvMap;
        ImageView imageView;
        CardView tounamentCardView;
        TextView remainTotal;
        TextView join;
        CardView joinCardView;
        ProgressBar progressBar;
        LinearLayout roomIdPassll, joinLl;
        TextView rId;
        ImageView pincv;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvMatchTitleandNumber = (TextView) itemView.findViewById(R.id.tvmatchtitleandnumber);
            tvTime = (TextView) itemView.findViewById(R.id.tvtime);
            tvTotalWin = (TextView) itemView.findViewById(R.id.tvtotalwin);
            tvPerKill = (TextView) itemView.findViewById(R.id.tvperkill);
            tvEntryFee = (TextView) itemView.findViewById(R.id.tventryfee);
            tvType = (TextView) itemView.findViewById(R.id.tvtype);
            tvVersion = (TextView) itemView.findViewById(R.id.tvversion);
            tvMap = (TextView) itemView.findViewById(R.id.tvmap);
            remainTotal = (TextView) itemView.findViewById(R.id.remainingtotal);
            join = (TextView) itemView.findViewById(R.id.join);
            joinCardView = (CardView) itemView.findViewById(R.id.joincardview);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressbar);
            imageView = (ImageView) itemView.findViewById(R.id.imageview);
            tounamentCardView = (CardView) itemView.findViewById(R.id.tournamentcardview);
            roomIdPassll = (LinearLayout) itemView.findViewById(R.id.roomidpassll);
            rId = (TextView) itemView.findViewById(R.id.rid);
            joinLl = (LinearLayout) itemView.findViewById(R.id.joinll);
            pincv=(ImageView) itemView.findViewById(R.id.piniv);
        }
    }

    public FancyShowCaseView addView(View view, String title, String n){
        FancyShowCaseView view1=new FancyShowCaseView.Builder((Activity) mContext)
                .focusOn(view)
                .title(title)
                //.titleGravity(Gravity.BOTTOM)
                .titleSize(20,1)
                .focusShape(FocusShape.CIRCLE)
                .enableAutoTextPosition()
                .roundRectRadius(10)
                .focusBorderSize(1)
                .showOnce(n)
                .focusBorderColor(mContext.getResources().getColor(R.color.newblue))
                .dismissListener(new DismissListener() {
                    @Override
                    public void onDismiss(String s) {

                    }
                    @Override
                    public void onSkipped(String s) {

                    }
                })
                .build();

        return view1;
    }
}

