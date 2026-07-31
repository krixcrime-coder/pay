package com.di.battlemaniaV5.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.di.battlemaniaV5.R;
import com.di.battlemaniaV5.models.BanerData;
import com.di.battlemaniaV5.ui.activities.AboutusActivity;
import com.di.battlemaniaV5.ui.activities.AnnouncementActivity;
import com.di.battlemaniaV5.ui.activities.CustomerSupportActivity;
import com.di.battlemaniaV5.ui.activities.HowtoActivity;
import com.di.battlemaniaV5.ui.activities.LeaderboardActivity;
import com.di.battlemaniaV5.ui.activities.LotteryActivity;
import com.di.battlemaniaV5.ui.activities.MyProfileActivity;
import com.di.battlemaniaV5.ui.activities.MyReferralsActivity;
import com.di.battlemaniaV5.ui.activities.MyRewardedActivity;
import com.di.battlemaniaV5.ui.activities.MyStatisticsActivity;
import com.di.battlemaniaV5.ui.activities.MyWalletActivity;
import com.di.battlemaniaV5.ui.activities.ProductActivity;
import com.di.battlemaniaV5.ui.activities.ReferandEarnActivity;
import com.di.battlemaniaV5.ui.activities.SelectedGameActivity;
import com.di.battlemaniaV5.ui.activities.TermsandConditionActivity;
import com.di.battlemaniaV5.ui.activities.TopPlayerActivity;
import com.di.battlemaniaV5.ui.activities.WatchAndEarnActivity;
import com.di.battlemaniaV5.utils.LocaleHelper;
import com.squareup.picasso.Picasso;

import java.util.List;

public class TestFragment extends Fragment {

    List<BanerData> mData;
    int position;
    private TextView title;
    private RelativeLayout card;
    private String mContent = "???";
    private String mContenturl = "";

    Context context;
    Resources resources;

    public static TestFragment newInstance(String content, String contenturl, Context c, List<BanerData> data, int pos) {

        TestFragment fragment = new TestFragment();
        fragment.mContent = content;
        fragment.mContenturl = contenturl;
        fragment.mData = data;
        fragment.position = pos;
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup Rootview = (ViewGroup) inflater.inflate(R.layout.slider_layout,
                container, false);

        context = LocaleHelper.setLocale(getContext());
        resources = context.getResources();

        try {
            BanerData data = mData.get(position);
            ImageView baneriv = (ImageView) Rootview.findViewById(R.id.imagitem);

            if(TextUtils.equals(data.getLink(),"Refer and Earn")){
                Picasso.get().load(Uri.parse(data.getImage())).placeholder(R.drawable.refer_and_earn).fit().into(baneriv);
            }else if(TextUtils.equals(data.getLink(),"Luckey Draw")){
                Picasso.get().load(Uri.parse(data.getImage())).placeholder(R.drawable.lucky_draw).fit().into(baneriv);
            }else if(TextUtils.equals(data.getLink(),"Watch and Earn")){
                Picasso.get().load(Uri.parse(data.getImage())).placeholder(R.drawable.watch_and_earn).fit().into(baneriv);
            }else if(TextUtils.equals(data.getLink(),"Buy Product")){
                Picasso.get().load(Uri.parse(data.getImage())).placeholder(R.drawable.buy_product).fit().into(baneriv);
            }else {
                Picasso.get().load(Uri.parse(data.getImage())).placeholder(R.drawable.default_battlemania).fit().into(baneriv);
            }

            baneriv.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (TextUtils.equals(data.getLinkType(), "app")) {
                        if (TextUtils.equals(data.getLink(), "Refer and Earn")) {
                            Intent intent = new Intent(getActivity(), ReferandEarnActivity.class);
                            startActivity(intent);
                        } else if (TextUtils.equals(data.getLink(), "Luckey Draw")) {
                            Intent intent = new Intent(getActivity(), LotteryActivity.class);
                            startActivity(intent);
                        } else if (TextUtils.equals(data.getLink(), "Watch and Earn")) {
                            Intent intent = new Intent(getActivity(), WatchAndEarnActivity.class);
                            startActivity(intent);
                        } else if (TextUtils.equals(data.getLink(), "My Profile")) {
                            Intent intent = new Intent(getActivity(), MyProfileActivity.class);
                            startActivity(intent);
                        } else if (TextUtils.equals(data.getLink(), "My Wallet")) {
                            Intent intent = new Intent(getActivity(), MyWalletActivity.class);
                            startActivity(intent);
                        } else if (TextUtils.equals(data.getLink(), "My Matches")) {
                            Intent intent = new Intent(getActivity(), SelectedGameActivity.class);
                            SharedPreferences sp = getActivity().getSharedPreferences("gameinfo", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("gametitle", "My Matches");
                            editor.putString("gameid", "not");
                            editor.apply();
                            startActivity(intent);
                        } else if (TextUtils.equals(data.getLink(), "My Statics")) {
                            Intent intent = new Intent(getActivity(), MyStatisticsActivity.class);
                            startActivity(intent);
                        } else if (TextUtils.equals(data.getLink(), "My Referral")) {
                            Intent intent = new Intent(getActivity(), MyReferralsActivity.class);
                            startActivity(intent);
                        } else if (TextUtils.equals(data.getLink(), "My Rewards")) {
                            Intent intent = new Intent(getActivity(), MyRewardedActivity.class);
                            startActivity(intent);
                        } else if (TextUtils.equals(data.getLink(), "Announcement")) {
                            Intent intent = new Intent(getActivity(), AnnouncementActivity.class);
                            startActivity(intent);
                        } else if (TextUtils.equals(data.getLink(), "Top Players")) {
                            Intent intent = new Intent(getActivity(), TopPlayerActivity.class);
                            startActivity(intent);
                        } else if (TextUtils.equals(data.getLink(), "Leaderboard")) {
                            Intent intent = new Intent(getActivity(), LeaderboardActivity.class);
                            startActivity(intent);
                        } else if (TextUtils.equals(data.getLink(), "App Tutorials")) {
                            Intent intent = new Intent(getActivity(), HowtoActivity.class);
                            startActivity(intent);
                        } else if (TextUtils.equals(data.getLink(), "About us")) {
                            Intent intent = new Intent(getActivity(), AboutusActivity.class);
                            startActivity(intent);
                        } else if (TextUtils.equals(data.getLink(), "Customer Support")) {
                            Intent intent = new Intent(getActivity(), CustomerSupportActivity.class);
                            startActivity(intent);
                        } else if (TextUtils.equals(data.getLink(), "Terms and Condition")) {
                            Intent intent = new Intent(getActivity(), TermsandConditionActivity.class);
                            startActivity(intent);
                        } else if (TextUtils.equals(data.getLink(), "Game")) {
                            Intent intent = new Intent(getActivity(), SelectedGameActivity.class);
                            SharedPreferences sp = getActivity().getSharedPreferences("gameinfo", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("gametitle", data.getLinkname());
                            editor.putString("gameid", data.getLinkId());
                            editor.apply();
                            startActivity(intent);
                        } else if (TextUtils.equals(data.getLink(), "Buy Product")) {
                            Intent intent = new Intent(getActivity(), ProductActivity.class);
                            startActivity(intent);
                        }
                    } else if (TextUtils.equals(data.getLinkType(), "web")) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(data.getLink()));
                        startActivity(browserIntent);
                    } else {

                    }
                }
            });
        } catch (Exception e) {

        }

        return Rootview;
    }
}