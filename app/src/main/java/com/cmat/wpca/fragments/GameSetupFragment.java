package com.cmat.wpca.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.cmat.wpca.R;
import com.cmat.wpca.data.DataStore;
import com.cmat.wpca.data.PlayerEntry;
import com.cmat.wpca.data.RulesetEntry;
import com.cmat.wpca.data.TeamEntry;

public class GameSetupFragment extends Fragment {
    DataStore<RulesetEntry> rulesetData = new DataStore<>("rulesets", RulesetEntry.class);
    DataStore<TeamEntry> teamData = new DataStore<>("teams", TeamEntry.class);

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.game_setup, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rulesetData.load(getContext());
        teamData.load(getContext());

        Spinner s = this.getView().findViewById(R.id.gamesetup_rules_spinner);
        ArrayAdapter adapter = new ArrayAdapter(getContext(), R.layout.spinner_item, rulesetData.getArrayOfEntryNames());
        s.setAdapter(adapter);

        Spinner st = this.getView().findViewById(R.id.gamesetup_team_spinner);
        ArrayAdapter adaptert = new ArrayAdapter(getContext(), R.layout.spinner_item, teamData.getArrayOfEntryNames());
        st.setAdapter(adaptert);

        view.findViewById(R.id.gamesetup_go_button).setOnClickListener(this::gameStartButtonClick);
        view.findViewById(R.id.backtostart_button).setOnClickListener(this::onBackToStartButtonClick);
    }

    private void gameStartButtonClick(View view) {
        Spinner st = this.getView().findViewById(R.id.gamesetup_team_spinner);
        Spinner s = this.getView().findViewById(R.id.gamesetup_rules_spinner);
        String teamName = st.getItemAtPosition(st.getSelectedItemPosition()).toString();
        String rulesetName = s.getItemAtPosition(s.getSelectedItemPosition()).toString();
        Bundle dat = new Bundle();
        dat.putString("teamName", teamName);
        dat.putString("rulesetName", rulesetName);
        NavHostFragment.findNavController(GameSetupFragment.this).navigate(R.id.action_GameSetup_to_GameRecord, dat);
    }

    private void onBackToStartButtonClick(View view) {
        NavHostFragment.findNavController(GameSetupFragment.this).popBackStack(R.id.StartPage, false);
    }
}