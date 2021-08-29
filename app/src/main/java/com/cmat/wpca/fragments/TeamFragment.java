package com.cmat.wpca.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.cmat.wpca.R;
import com.cmat.wpca.data.DataStore;
import com.cmat.wpca.data.entry.PlayerEntry;
import com.cmat.wpca.data.entry.TeamEntry;

public class TeamFragment  extends Fragment {
    DataStore<PlayerEntry> playerData = new DataStore<>("players", PlayerEntry.class);
    DataStore<TeamEntry> teamData = new DataStore<>("teams", TeamEntry.class);

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_teams, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
    }
