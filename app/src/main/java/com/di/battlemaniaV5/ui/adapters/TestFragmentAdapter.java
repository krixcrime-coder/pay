package com.di.battlemaniaV5.ui.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.di.battlemaniaV5.models.BanerData;
import com.di.battlemaniaV5.ui.fragments.TestFragment;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TestFragmentAdapter extends FragmentStatePagerAdapter {

    List<BanerData> mData;
    private Context context;



    public TestFragmentAdapter(FragmentManager fm,
                               Context context, List<BanerData> mData) {
        super(fm);
        this.context = context;
        this.mData=mData;
    }

    @NotNull
    @Override
    public Object instantiateItem(@NotNull ViewGroup container, final int position) {
        return super.instantiateItem(container, position);
    }

    @Override
    public boolean isViewFromObject(@NotNull View view, @NotNull Object object) {
        if (object != null) {
            return ((Fragment) object).getView() == view;
        } else {
            return false;
        }
    }

    @Override
    public int getItemPosition(@NotNull Object object) {
        // Causes adapter to reload all Fragments when
        // notifyDataSetChanged is called
        return POSITION_NONE;
    }

    @NotNull
    @Override
    public Fragment getItem(int position) {
        BanerData data=mData.get(position);
        return TestFragment.newInstance(data.getImage(),data.getLink(),
                context,mData,position);
    }

    @Override
    public int getCount() {
        return mData == null ? 0 : mData.size();
    }

}
