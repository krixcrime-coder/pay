//For showing list of transaction
package com.di.battlemaniaV5.ui.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.di.battlemaniaV5.R;
import com.di.battlemaniaV5.models.TransactionDetails;
import com.di.battlemaniaV5.utils.LocaleHelper;

import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.MyViewHolder> {
    private Context mContext;
    private List<TransactionDetails> mData;

    Context context;
    Resources resources;

    public TransactionAdapter(Context mContext, List<TransactionDetails> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.transaction_data, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        context = LocaleHelper.setLocale(mContext);
        resources = context.getResources();

        TransactionDetails transactionDetails = mData.get(position);
        SharedPreferences sp = mContext.getSharedPreferences("currencyinfo", Context.MODE_PRIVATE);
        //String selectedcurrency = sp.getString("currency", "₹");

        SpannableStringBuilder builder = new SpannableStringBuilder();

        if (transactionDetails.getNoteid().matches("1") || transactionDetails.getNoteid().matches("2") || transactionDetails.getNoteid().matches("8")|| transactionDetails.getNoteid().matches("10")|| transactionDetails.getNoteid().matches("12")|| transactionDetails.getNoteid().matches("14")|| transactionDetails.getNoteid().matches("15")|| transactionDetails.getNoteid().matches("18")) {
            holder.crOrDb.setText(resources.getString(R.string.debit));
            holder.crOrDb.setTextColor(mContext.getResources().getColor(R.color.newred));
            holder.status.setVisibility(View.GONE);

            builder.append(Html.fromHtml("- "))
                    .append(" ", new ImageSpan(mContext, R.drawable.resize_coin1617,ImageSpan.ALIGN_BASELINE), 0)
                    .append(" ")
                    .append(Html.fromHtml(""+transactionDetails.getWithdraw()));
            holder.amount.setText(builder);
            holder.amount.setTextColor(mContext.getResources().getColor(R.color.newred));
        } else if (transactionDetails.getNoteid().matches("0") || transactionDetails.getNoteid().matches("3") || transactionDetails.getNoteid().matches("4") || transactionDetails.getNoteid().matches("5") || transactionDetails.getNoteid().matches("6") || transactionDetails.getNoteid().matches("7")|| transactionDetails.getNoteid().matches("11")|| transactionDetails.getNoteid().matches("13")|| transactionDetails.getNoteid().matches("16")|| transactionDetails.getNoteid().matches("17")) {
            holder.crOrDb.setText(resources.getString(R.string.credit));
            holder.crOrDb.setTextColor(mContext.getResources().getColor(R.color.newgreen));
            holder.status.setVisibility(View.GONE);
            builder = new SpannableStringBuilder();
            builder.append(Html.fromHtml("+ "))
                    .append(" ", new ImageSpan(mContext, R.drawable.resize_coin1617,ImageSpan.ALIGN_BASELINE), 0)
                    .append(" ")
                    .append(Html.fromHtml(""+transactionDetails.getDeposit()));
            holder.amount.setText(builder);
            holder.amount.setTextColor(mContext.getResources().getColor(R.color.newgreen));
        } else if (transactionDetails.getNoteid().matches("9")){
            holder.status.setVisibility(View.VISIBLE);
            holder.crOrDb.setText(resources.getString(R.string.debit));
            holder.status.setText(resources.getString(R.string.pending));
            builder = new SpannableStringBuilder();
            builder.append(Html.fromHtml("- "))
                    .append(" ", new ImageSpan(mContext, R.drawable.resize_coin1617,ImageSpan.ALIGN_BASELINE), 0)
                    .append(" ")
                    .append(Html.fromHtml(""+transactionDetails.getWithdraw()));
            holder.amount.setText(builder);
        }

        if (transactionDetails.getMatchid().matches("0")) {
            holder.detail.setText(transactionDetails.getNote() + " - #" + transactionDetails.getTransactionid());
        } else {
            holder.detail.setText(transactionDetails.getNote() + " - #" + transactionDetails.getMatchid());
        }

        builder = new SpannableStringBuilder();
        builder.append(Html.fromHtml(""))
                .append(" ", new ImageSpan(mContext, R.drawable.resize_coin1617,ImageSpan.ALIGN_BASELINE), 0)
                .append(" ")
                .append(Html.fromHtml(""+String.valueOf(Double.parseDouble(transactionDetails.getWinmoney()) + Double.parseDouble(transactionDetails.getJoinmoney()))));
        holder.available.setText(builder);
        holder.time.setText(transactionDetails.getDate());
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

        TextView crOrDb;
        TextView detail;
        TextView time;
        TextView status;
        TextView amount;
        TextView available;
        TextView mobile;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            crOrDb = (TextView) itemView.findViewById(R.id.crordb_t);
            detail = (TextView) itemView.findViewById(R.id.detail_t);
            time = (TextView) itemView.findViewById(R.id.time_t);
            status = (TextView) itemView.findViewById(R.id.status_t);
            amount = (TextView) itemView.findViewById(R.id.amount_t);
            available = (TextView) itemView.findViewById(R.id.available_t);
            mobile = (TextView) itemView.findViewById(R.id.mobile_t);

        }
    }
}
