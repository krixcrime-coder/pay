package com.di.battlemaniaV5.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.di.battlemaniaV5.R;
import com.di.battlemaniaV5.utils.LocaleHelper;

public class FragmentSelectedLotteryDescription extends Fragment {

    TextView titletv;
    TextView timetv;
    TextView prizetv;
    TextView feetv;
    TextView abouttv;
    TextView aboutlotrytitle;

    Context context;
    Resources resources;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_selectedlottery_description, container, false);

        context = LocaleHelper.setLocale(getContext());
        resources = context.getResources();

        titletv=(TextView)root.findViewById(R.id.lotterytitledesc);
        timetv=(TextView)root.findViewById(R.id.lotterytimedesc);
        prizetv=(TextView)root.findViewById(R.id.lotteryplayfordesc);
        feetv=(TextView)root.findViewById(R.id.lotteryfeedesc);
        abouttv=(TextView)root.findViewById(R.id.aboutlottery);
        aboutlotrytitle = (TextView)root.findViewById(R.id.aboutlotterytitleid);

        Intent intent=getActivity().getIntent();
        String title=intent.getStringExtra("TITLE");
        String lid=intent.getStringExtra("LID");
        String time=intent.getStringExtra("TIME");
        String prize=intent.getStringExtra("PRIZE");
        String entryfee=intent.getStringExtra("ENTRYFEE");
        String about=intent.getStringExtra("ABOUT");

        aboutlotrytitle.setText(resources.getString(R.string.about_lottery));
        titletv.setText(title+" "+resources.getString(R.string.lottery)+" #"+lid);
        timetv.setText(resources.getString(R.string.result_on)+"\n"+time);
        prizetv.setText(resources.getString(R.string.play_for)+"\n"+prize);
        feetv.setText(resources.getString(R.string.fees)+"\n"+entryfee);
        abouttv.setText(Html.fromHtml(about));

        return root;
    }
}
