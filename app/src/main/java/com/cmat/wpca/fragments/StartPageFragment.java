package com.cmat.wpca.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.cmat.wpca.R;

public class StartPageFragment extends Fragment {

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.start_page, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.button_game_setup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(StartPageFragment.this).navigate(R.id.action_StartPage_to_GameSetup);
            }
        });

        view.findViewById(R.id.button_evaluation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(StartPageFragment.this).navigate(R.id.action_StartPage_to_Evaluation);
            }
        });

        view.findViewById(R.id.button_teams).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(StartPageFragment.this).navigate(R.id.action_StartPage_to_TeamPage);
            }
        });

        view.findViewById(R.id.button_ruleset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(StartPageFragment.this).navigate(R.id.action_StartPage_to_RuleSets);
            }
        });
    }
}