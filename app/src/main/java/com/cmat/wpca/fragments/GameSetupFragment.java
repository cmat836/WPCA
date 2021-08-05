package com.cmat.wpca.fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cmat.wpca.R;
import com.cmat.wpca.data.DataStore;
import com.cmat.wpca.data.entry.PlayerEntry;
import com.cmat.wpca.data.entry.RulesetEntry;
import com.cmat.wpca.data.entry.TeamEntry;

import java.util.ArrayList;

public class GameSetupFragment extends Fragment {
    DataStore<RulesetEntry> rulesetData = new DataStore<>("rulesets", RulesetEntry.class);
    DataStore<TeamEntry> teamData = new DataStore<>("teams", TeamEntry.class);
    DataStore<PlayerEntry> playerData = new DataStore<>("players", PlayerEntry.class);

    int playersRemaining;

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
        playerData.load(getContext());

        Spinner sr = this.getView().findViewById(R.id.gamesetup_rules_spinner);
        ArrayAdapter adapter = new ArrayAdapter(getContext(), R.layout.spinner_item, rulesetData.getArrayOfEntryNames());
        sr.setAdapter(adapter);


        Spinner st = this.getView().findViewById(R.id.gamesetup_team_spinner);
        ArrayAdapter adaptert = new ArrayAdapter(getContext(), R.layout.spinner_item, teamData.getArrayOfEntryNames());
        st.setAdapter(adaptert);
        st.setOnItemSelectedListener(new TeamSpinnerSelected());

        RecyclerView playerSelect = (RecyclerView)view.findViewById(R.id.playerselection_recyclerview);
        playerSelect.setLayoutManager(new LinearLayoutManager(getContext()));
        PlayerSelectAdapter playerAdapter = new GameSetupFragment.PlayerSelectAdapter(this);
        playerSelect.setAdapter(playerAdapter);

        view.findViewById(R.id.gamesetup_go_button).setOnClickListener(this::gameStartButtonClick);
        view.findViewById(R.id.backtostart_button).setOnClickListener(this::onBackToStartButtonClick);

        playersRemaining = 7;
    }

    private void refreshTeamDisplay() {
        RecyclerView teamDisplay = (RecyclerView)this.getView().findViewById(R.id.playerselection_recyclerview);
        PlayerSelectAdapter playerAdapter = new GameSetupFragment.PlayerSelectAdapter(this);
        teamDisplay.setAdapter(playerAdapter);
    }

    private void gameStartButtonClick(View view) {
        Spinner st = this.getView().findViewById(R.id.gamesetup_team_spinner);
        Spinner sr = this.getView().findViewById(R.id.gamesetup_rules_spinner);
        String teamName = st.getItemAtPosition(st.getSelectedItemPosition()).toString();
        String rulesetName = sr.getItemAtPosition(sr.getSelectedItemPosition()).toString();
        String[] selectedPlayers = new String[7];
        ArrayList<String> players = ((PlayerSelectAdapter)((RecyclerView)getView().findViewById(R.id.playerselection_recyclerview)).getAdapter()).selectedPlayers;
        for (int i = 0; i < players.size(); i++) {
            selectedPlayers[i] = players.get(i);
        }
        Bundle dat = new Bundle();
        dat.putString("teamName", teamName);
        dat.putString("rulesetName", rulesetName);
        dat.putStringArray("selectedPlayers", selectedPlayers);
        NavHostFragment.findNavController(GameSetupFragment.this).navigate(R.id.action_GameSetup_to_GameRecord, dat);
    }

    private void onBackToStartButtonClick(View view) {
        NavHostFragment.findNavController(GameSetupFragment.this).popBackStack(R.id.StartPage, false);
    }

    public class PlayerSelectAdapter extends RecyclerView.Adapter<GameSetupFragment.SelectableViewHolder>
    {
        GameSetupFragment context;
        ArrayList<String> selectedPlayers = new ArrayList<>();

        public PlayerSelectAdapter(GameSetupFragment gameSetupFragment) {
            super();
            context = gameSetupFragment;
        }

        @Override
        public SelectableViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.selectable_item, parent, false);
            return new SelectableViewHolder(v, context.getContext());
        }

        @Override
        public void onBindViewHolder(SelectableViewHolder holder, int position) {
            TeamEntry t = context.teamData.getSelected();
            if (t.getName() == "blank") {
                holder.getNameText().setText("name");
                holder.getInfoText().setText("0");
                return;
            }
            String name = t.getNameList().get(position);
            holder.getNameText().setText(name);
            holder.getInfoText().setText(playerData.getEntry(name).getNumber());
            holder.setSelected(selectedPlayers.contains(name));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!holder.isSelected() && playersRemaining > 0) {
                        holder.setSelected(true);
                        selectedPlayers.remove(name);
                        selectedPlayers.add(name);

                    } else {
                        holder.setSelected(false);
                        selectedPlayers.remove(name);
                    }
                    playersRemaining = 7 - selectedPlayers.size();
                    ((TextView)context.getView().findViewById(R.id.playersremaining_textview)).setText((String.valueOf(playersRemaining)));
                }
            });
        }

        @Override
        public int getItemCount() {
            if (teamData.getSelected().getName() == "blank") {
                return 0;
            } else {
                return teamData.getSelected().getNameList().size();
            }
        }
    }

    public class TeamSpinnerSelected implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String name = (String)parent.getItemAtPosition(position);
            teamData.setSelected(name);
            refreshTeamDisplay();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    public static class SelectableViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameText;
        private final TextView infoText;
        private final FrameLayout layout;

        private Context context;

        private boolean selected;

        public SelectableViewHolder(@NonNull View itemView, Context c) {
            super(itemView);
            nameText = (TextView) itemView.findViewById(R.id.textViewName);
            infoText = (TextView) itemView.findViewById(R.id.textViewInfo);
            layout = (FrameLayout) itemView.findViewById(R.id.selectable_layout);
            selected = false;
            context = c;
        }

        public void setSelected(boolean selected) {
            TypedValue typedValue = new TypedValue();
            context.getTheme().resolveAttribute(R.attr.colorOnPrimary, typedValue, true);
            int backgroundcolor = typedValue.data;
            context.getTheme().resolveAttribute(R.attr.colorSecondary, typedValue, true);
            int selectedcolor = typedValue.data;


            getLayout().setSelected(selected);
            this.selected = selected;
            getLayout().setBackground(selected ? new ColorDrawable((selectedcolor)) : new ColorDrawable((backgroundcolor)));

        }

        public boolean isSelected() {
            return selected;
        }

        public TextView getNameText() {
            return nameText;
        }

        public TextView getInfoText() {
            return infoText;
        }

        public FrameLayout getLayout() { return layout; }
    }
}