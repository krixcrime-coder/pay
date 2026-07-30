package com.app.rush47.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.app.rush47.R;

/**
 * Placeholder for a bottom tab that hasn't been built yet.
 * Once a tab (e.g. Upcoming, Ongoing, Result, Earn, Me) is ready, replace
 * its entry in HomeActivity's tab list with a real Fragment class - this
 * one is only here so the tab bar is complete and doesn't crash on tap.
 */
public class StubTabFragment extends Fragment {

    private static final String ARG_LABEL = "label";

    public static StubTabFragment newInstance(String label) {
        StubTabFragment fragment = new StubTabFragment();
        Bundle args = new Bundle();
        args.putString(ARG_LABEL, label);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stub_tab, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String label = getArguments() != null ? getArguments().getString(ARG_LABEL, "") : "";
        TextView title = view.findViewById(R.id.stub_title);
        title.setText(label + " - coming soon");
    }
}
