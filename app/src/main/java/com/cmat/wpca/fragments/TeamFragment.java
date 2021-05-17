package com.cmat.wpca.fragments;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cmat.wpca.R;
import com.cmat.wpca.data.DataStore;
import com.cmat.wpca.data.PlayerEntry;
import com.cmat.wpca.data.RulesetEntry;
import com.cmat.wpca.data.TeamEntry;

public class TeamFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    DataStore<PlayerEntry> playerData = new DataStore<>("players", PlayerEntry.class);
    DataStore<TeamEntry> teamData = new DataStore<>("teams", TeamEntry.class);

    final PopupWindow newTeamWindow = new PopupWindow(null,LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
    final PopupWindow newPlayerWindow = new PopupWindow(null,LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
    final PopupWindow removeTeamWindow = new PopupWindow(null,LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
    final PopupWindow removePlayerWindow = new PopupWindow(null,LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
    final PopupWindow transferPlayerWindow = new PopupWindow(null,LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
    final PopupWindow modifyTeamWindow = new PopupWindow(null,LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
    final PopupWindow modifyPlayerWindow = new PopupWindow(null,LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.team_page, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        playerData.load(getContext());
        teamData.load(getContext());

        Spinner teamSelector = (Spinner)view.findViewById(R.id.spinner_team_select);
        teamSelector.setOnItemSelectedListener(this);

        RecyclerView teamDisplay = (RecyclerView)view.findViewById(R.id.teamdisplay_recyclerview);
        teamDisplay.setLayoutManager(new LinearLayoutManager(getContext()));
        TeamAdapter teamAdapter = new TeamAdapter(this);
        teamDisplay.setAdapter(teamAdapter);

        RecyclerView playerDisplay = (RecyclerView)view.findViewById(R.id.playerdisplay_recyclerview);
        playerDisplay.setLayoutManager(new LinearLayoutManager(getContext()));
        PlayerAdapter playerAdapter = new PlayerAdapter(this);
        playerDisplay.setAdapter(playerAdapter);

        refreshTeamSelector();

        view.findViewById(R.id.backtostart_button).setOnClickListener(this::onBackToStartButtonClick);
        view.findViewById(R.id.fab_addplayer).setOnClickListener(this::onAddPlayerButtonClick);
        view.findViewById(R.id.fab_removeplayer).setOnClickListener(this::onRemovePlayerButtonClick);
        view.findViewById(R.id.fab_addteam).setOnClickListener(this::onAddTeamButtonClick);
        view.findViewById(R.id.fab_removeteam).setOnClickListener(this::onRemoveTeamButtonClick);

        newPlayerWindow.setBackgroundDrawable(new ColorDrawable((Color.WHITE)));
        newPlayerWindow.setElevation(20);
        newTeamWindow.setBackgroundDrawable(new ColorDrawable((Color.WHITE)));
        newTeamWindow.setElevation(20);
        removePlayerWindow.setBackgroundDrawable(new ColorDrawable((Color.WHITE)));
        removePlayerWindow.setElevation(20);
        removeTeamWindow.setBackgroundDrawable(new ColorDrawable((Color.WHITE)));
        removeTeamWindow.setElevation(20);
        transferPlayerWindow.setBackgroundDrawable(new ColorDrawable((Color.WHITE)));
        transferPlayerWindow.setElevation(20);
        modifyTeamWindow.setBackgroundDrawable(new ColorDrawable((Color.WHITE)));
        modifyTeamWindow.setElevation(20);
        modifyPlayerWindow.setBackgroundDrawable(new ColorDrawable((Color.WHITE)));
        modifyPlayerWindow.setElevation(20);

    }

    private void refreshTeamSelector() {
        Spinner s = this.getView().findViewById(R.id.spinner_team_select);
        ArrayAdapter adapter = new ArrayAdapter(getContext(), R.layout.spinner_item, teamData.getArrayOfEntryNames());
        s.setAdapter(adapter);
    }

    private void onRemoveTeamButtonClick(View view) {
    }

    private void onAddTeamButtonClick(View view) {
    }

    private void onRemovePlayerButtonClick(View view) {
    }

    private void onAddPlayerButtonClick(View view) {
    }

    private void onBackToStartButtonClick(View view) {
        NavHostFragment.findNavController(TeamFragment.this).popBackStack(R.id.StartPage, false);
    }

    private void onPlayerSelect(View v, String p) {
        playerData.setSelected(p);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public static class TeamAdapter extends RecyclerView.Adapter<InfoViewHolder> {
        TeamFragment context;

        public TeamAdapter(TeamFragment context) {
            this.context = context;
        }

        @NonNull
        @Override
        public InfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.display_item, parent, false);
            return new InfoViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull InfoViewHolder holder, int position) {
            if (context.teamData.getSelected().getName() == "blank" || context.teamData.getSelected().getName() == "") {
                holder.getNameText().setText("");
                holder.getInfoText().setText("");
            } else {
                String p = context.teamData.getSelected().getNameList().get(position);
                PlayerEntry player = context.playerData.getEntry(p);
                holder.getNameText().setText(player.getName());
                holder.getInfoText().setText(player.getNumber());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        context.onPlayerSelect(v, p);
                    }
                });
            }
        }


        @Override
        public int getItemCount() {
            return 5;
        }

    }

    public static class PlayerAdapter extends RecyclerView.Adapter<InfoViewHolder> {
        TeamFragment context;

        public PlayerAdapter(TeamFragment context) {
            this.context = context;
        }

        @NonNull
        @Override
        public InfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.display_item, parent, false);
            return new InfoViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull InfoViewHolder holder, int position) {
            if (context.playerData.getSelected().getName() == "blank" || context.playerData.getSelected().getName() == "") {
                holder.getNameText().setText("");
                holder.getInfoText().setText("");
            } else {
                PlayerEntry p = context.playerData.getSelected();

                switch (position) {
                    case 0: {
                        holder.getNameText().setText(R.string.label_playerinfo_name);
                        holder.getInfoText().setText(p.getName());
                        break;
                    }
                    case 1: {
                        holder.getNameText().setText(R.string.label_playerinfo_number);
                        holder.getInfoText().setText(String.valueOf(p.getNumber()));
                        break;
                    }
                    case 2: {
                        holder.getNameText().setText(R.string.label_playerinfo_birthday);
                        holder.getInfoText().setText(p.getBirthday());
                        break;
                    }
                    case 3: {
                        holder.getNameText().setText(R.string.label_playerinfo_handedness);
                        holder.getInfoText().setText(p.getHandedness().toString());
                        break;
                    }
                    case 4: {
                        holder.getNameText().setText(R.string.label_playerinfo_goalie);
                        holder.getInfoText().setText(String.valueOf(p.isGoalie()));
                        break;
                    }
                    case 5: {
                        holder.getNameText().setText(R.string.label_playerinfo_teams);
                        holder.getInfoText().setText(p.getTeamList().toString());
                        break;
                    }
                }
            }
        }

        @Override
        public int getItemCount() {
            return 5;
        }

    }


    public static class InfoViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameText;
        private final TextView infoText;

        public InfoViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = (TextView) itemView.findViewById(R.id.textViewName);
            infoText = (TextView) itemView.findViewById(R.id.textViewInfo);
        }

        public TextView getNameText() {
            return nameText;
        }

        public TextView getInfoText() {
            return infoText;
        }
    }
}
