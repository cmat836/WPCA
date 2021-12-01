package com.cmat.wpca.fragments;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.cmat.wpca.data.entry.TeamEntry;
import com.cmat.wpca.data.event.Game;
import com.cmat.wpca.ui.DisplayAdapter;
import com.cmat.wpca.ui.DisplayViewHolder;
import com.cmat.wpca.ui.SelectableAdapter;
import com.cmat.wpca.ui.SelectableViewHolder;
import com.cmat.wpca.ui.SimplifiedPopupWindow;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class TeamFragment  extends Fragment {
    DataStore<PlayerEntry> playerData = new DataStore<>("players", PlayerEntry.class);
    DataStore<TeamEntry> teamData = new DataStore<>("teams", TeamEntry.class);

    SimplifiedPopupWindow newTeamWindow;
    SimplifiedPopupWindow newPlayerWindow;
    SimplifiedPopupWindow removeTeamWindow;
    SimplifiedPopupWindow removePlayerWindow;
    SimplifiedPopupWindow transferPlayerWindow;
    SimplifiedPopupWindow filterPlayerWindow;
    SimplifiedPopupWindow modifyTeamWindow;
    SimplifiedPopupWindow modifyPlayerWindow;

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

        playerData.load(getContext());
        teamData.load(getContext());

        getView().findViewById(R.id.teams_button_filter).setOnClickListener(this::teamsButtonFilter_Clicked);
        getView().findViewById(R.id.teams_fab_player_add).setOnClickListener(this::teamsFabPlayerAdd_Clicked);
        getView().findViewById(R.id.teams_fab_player_edit).setOnClickListener(this::teamsFabPlayerEdit_Clicked);
        getView().findViewById(R.id.teams_fab_player_transfer).setOnClickListener(this::teamsFabPlayerTransfer_Clicked);
        getView().findViewById(R.id.teams_fab_player_remove).setOnClickListener(this::teamsFabPlayerRemove_Clicked);
        getView().findViewById(R.id.teams_fab_team_add).setOnClickListener(this::teamsFabTeamAdd_Clicked);
        getView().findViewById(R.id.teams_fab_team_edit).setOnClickListener(this::teamsFabTeamEdit_Clicked);
        getView().findViewById(R.id.teams_fab_team_remove).setOnClickListener(this::teamsFabTeamRemove_Clicked);
        getView().findViewById(R.id.teams_button_return).setOnClickListener(this::teamsButtonReturn_Clicked);

        getView().findViewById(R.id.teams_fab_player_remove).setEnabled(false);
        getView().findViewById(R.id.teams_fab_team_remove).setEnabled(false);

        RecyclerView teamList = (RecyclerView)view.findViewById(R.id.teams_recycler_team_list);
        teamList.setLayoutManager(new LinearLayoutManager(getContext()));
        TeamAdapter teamAdapter = new TeamAdapter(teamData.getEntries(teamData.getArrayOfEntryNames()));
        teamList.setAdapter(teamAdapter);

        RecyclerView playerList = (RecyclerView)view.findViewById(R.id.teams_recycler_player_list);
        playerList.setLayoutManager(new LinearLayoutManager(getContext()));
        PlayerAdapter playerAdapter = new PlayerAdapter(playerData.getEntries(playerData.getArrayOfEntryNames()));
        playerList.setAdapter(playerAdapter);

        int backgroundcolor = getColor(R.attr.colorOnPrimary);

        newPlayerWindow = new SimplifiedPopupWindow(null, LinearLayout.LayoutParams.WRAP_CONTENT, true, false, backgroundcolor);
        newTeamWindow = new SimplifiedPopupWindow(null, LinearLayout.LayoutParams.WRAP_CONTENT, true, false, backgroundcolor);
        removePlayerWindow = new SimplifiedPopupWindow(null, LinearLayout.LayoutParams.WRAP_CONTENT, true, true, backgroundcolor);
        removeTeamWindow = new SimplifiedPopupWindow(null, LinearLayout.LayoutParams.WRAP_CONTENT, true, true, backgroundcolor);
        transferPlayerWindow = new SimplifiedPopupWindow(null, LinearLayout.LayoutParams.WRAP_CONTENT, true, false, backgroundcolor);
        modifyTeamWindow = new SimplifiedPopupWindow(null, LinearLayout.LayoutParams.WRAP_CONTENT, true, false, backgroundcolor);
        modifyPlayerWindow = new SimplifiedPopupWindow(null, LinearLayout.LayoutParams.WRAP_CONTENT, true, false, backgroundcolor);
        filterPlayerWindow = new SimplifiedPopupWindow(null, LinearLayout.LayoutParams.WRAP_CONTENT, true, true, backgroundcolor);
    }

    public int getColor(int resid) {
        TypedValue typedValue = new TypedValue();
        getContext().getTheme().resolveAttribute(resid, typedValue, true);
        return typedValue.data;
    }

    private void sanityCheck() {
        for (TeamEntry t : teamData.getEntries(teamData.getArrayOfEntryNames())) {
            for (String p : t.getNameList()) {
                if (playerData.getEntry(p).getName().equals("blank")) {
                    t.removePlayer(p);
                    teamData.markModified(t.getName());
                }
            }
        }

        for (PlayerEntry p : playerData.getEntries(playerData.getArrayOfEntryNames())) {
            for (String t : p.getTeamList()) {
                if (teamData.getEntry(t).getName().equals("blank")) {
                    p.removeTeam(t);
                    playerData.markModified(p.getName());
                }
            }
        }

        playerData.refresh(getContext());
        teamData.refresh(getContext());

        refreshPlayerDisplay();
        refreshTeamDisplay();
    }

    private void refreshTeamDisplay() {
        RecyclerView teamList = (RecyclerView)getView().findViewById(R.id.teams_recycler_team_list);
        TeamAdapter teamAdapter = new TeamAdapter(teamData.getEntries(teamData.getArrayOfEntryNames()));
        teamList.setAdapter(teamAdapter);
    }

    private void refreshPlayerDisplay() {
        RecyclerView playerList = (RecyclerView)getView().findViewById(R.id.teams_recycler_player_list);
        PlayerAdapter playerAdapter = new PlayerAdapter(playerData.getEntries(playerData.getArrayOfEntryNames()));
        playerList.setAdapter(playerAdapter);
    }

    private void teamsButtonReturn_Clicked(View view) {
        NavHostFragment.findNavController(TeamFragment.this).popBackStack(R.id.StartPage, false);
    }

    private void teamsFabTeamRemove_Clicked(View view) {
        View content = this.getLayoutInflater().inflate(R.layout.popup_team_remove, null);
        removeTeamWindow.setContentView(content);
        removeTeamWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        // Find the selected teams
        RecyclerView teamList = (RecyclerView)getView().findViewById(R.id.teams_recycler_team_list);
        TeamAdapter adapter = (TeamAdapter) teamList.getAdapter();

        // Send them to the window
        RecyclerView removedTeamList = (RecyclerView)content.findViewById(R.id.team_remove_recycler_list);
        DisplayAdapter<TeamEntry> teamAdapter = new DisplayAdapter<TeamEntry>(adapter.getSelectedData()) {
            @Override
            public String getNameText(TeamEntry object) {
                return object.getName();
            }

            @Override
            public String getInfoText(TeamEntry object) {
                return String.valueOf(object.getSize());
            }
        };
        removedTeamList.setLayoutManager(new LinearLayoutManager(getContext()));
        removedTeamList.setAdapter(teamAdapter);

        if (adapter.getSelectedData().size() > 1) {
            ((TextView)content.findViewById(R.id.team_remove_text_warning)).setText(R.string.team_remove_text_warning_plural);
        }

        content.findViewById(R.id.team_remove_button_remove).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Find the selected teams
                RecyclerView teamList = (RecyclerView)getView().findViewById(R.id.teams_recycler_team_list);
                TeamAdapter adapter = (TeamAdapter) teamList.getAdapter();

                for (TeamEntry t : adapter.getSelectedData()) {
                    teamData.removeEntry(t);
                    if (t.getSize() > 0) {
                        for (String p : t.getNameList()) {
                            playerData.getEntry(p).removeTeam(t.getName());
                            playerData.markModified(p);
                        }
                    }
                }

                playerData.refresh(getContext());
                teamData.refresh(getContext());

                refreshPlayerDisplay();
                refreshTeamDisplay();

                removeTeamWindow.dismiss();
            }
        });
    }

    private void teamsFabTeamEdit_Clicked(View view) {
    }

    private void teamsFabTeamAdd_Clicked(View view) {

    }

    private void teamsFabPlayerRemove_Clicked(View view) {
        View content = this.getLayoutInflater().inflate(R.layout.popup_player_remove, null);
        removePlayerWindow.setContentView(content);
        removePlayerWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        // Find the selected players
        RecyclerView playerList = (RecyclerView)getView().findViewById(R.id.teams_recycler_player_list);
        PlayerAdapter adapter = (PlayerAdapter) playerList.getAdapter();

        // Send them to the window
        RecyclerView removedPlayerList = (RecyclerView)content.findViewById(R.id.player_remove_recycler_list);
        DisplayAdapter<PlayerEntry> playerAdapter = new DisplayAdapter<PlayerEntry>(adapter.getSelectedData()) {
            @Override
            public String getNameText(PlayerEntry object) {
                return object.getName();
            }

            @Override
            public String getInfoText(PlayerEntry object) {
                return object.getNumber();
            }
        };
        removedPlayerList.setLayoutManager(new LinearLayoutManager(getContext()));
        removedPlayerList.setAdapter(playerAdapter);

        if (adapter.getSelectedData().size() > 1) {
            ((TextView)content.findViewById(R.id.player_remove_text_warning)).setText(R.string.player_remove_text_warning_plural);
        }

        content.findViewById(R.id.player_remove_button_remove).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Find the selected players
                RecyclerView playerList = (RecyclerView)getView().findViewById(R.id.teams_recycler_player_list);
                PlayerAdapter adapter = (PlayerAdapter) playerList.getAdapter();

                for (PlayerEntry p : adapter.getSelectedData()) {
                    playerData.removeEntry(p);
                    if (p.hasTeam()) {
                        for (String t : p.getTeamList()) {
                            teamData.getEntry(t).removePlayer(p.getName());
                            teamData.markModified(t);
                        }
                    }
                }

                playerData.refresh(getContext());
                teamData.refresh(getContext());

                refreshPlayerDisplay();
                refreshTeamDisplay();

                removePlayerWindow.dismiss();
            }
        });

    }

    private void teamsFabPlayerTransfer_Clicked(View view) {
    }

    private void teamsFabPlayerEdit_Clicked(View view) {
        sanityCheck();
    }

    private void teamsFabPlayerAdd_Clicked(View view) {
        View content = this.getLayoutInflater().inflate(R.layout.popup_player_create, null);
        newPlayerWindow.setContentView(content);
        newPlayerWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        Spinner teamSpinner = (Spinner)content.findViewById(R.id.player_create_spinner_team_assign);
        teamSpinner.setAdapter(new ArrayAdapter<String>(getContext(), R.layout.item_spinner, teamData.getArrayOfEntryNames()));

        content.findViewById(R.id.player_create_button_create).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlayerEntry newPlayer = new PlayerEntry.PlayerBuilder(
                        ((EditText)content.findViewById(R.id.player_create_edit_name)).getText().toString(),
                        ((EditText)content.findViewById(R.id.player_create_edit_number)).getText().toString()
                ).handedness(
                        ((RadioButton)content.findViewById(R.id.player_create_radiobutton_left)).isChecked() ? PlayerEntry.Handedness.LEFT : PlayerEntry.Handedness.RIGHT
                ).setGoalie(
                        ((CheckBox)content.findViewById(R.id.player_create_checkbox_goalie)).isChecked()
                ).build();
                Spinner teamSpinner = (Spinner)content.findViewById(R.id.player_create_spinner_team_assign);
                String teamName = teamSpinner.getItemAtPosition(teamSpinner.getSelectedItemPosition()).toString();

                if (!teamName.equals("none")) {
                    newPlayer.addTeam(teamName);
                    teamData.getEntry(teamName).addPlayer(newPlayer.getName());
                    teamData.markModified(teamName);
                    teamData.refresh(getContext());
                }

                playerData.setEntry(newPlayer);
                playerData.refresh(getContext());

                refreshPlayerDisplay();
                refreshTeamDisplay();

                newPlayerWindow.dismiss();
            }
        });
    }

    private void teamsButtonFilter_Clicked(View view) {
    }

    public class TeamAdapter extends SelectableAdapter<TeamEntry> {

        public TeamAdapter(ArrayList<TeamEntry> data) {
            super(data);
        }

        @Override
        public String getNameText(TeamEntry object) {
            return object.getName();
        }

        @Override
        public String getInfoText(TeamEntry object) {
            return String.valueOf(object.getSize());
        }

        @Override
        public boolean isSelectionAllowed() {
            return true;
        }

        @Override
        public void onSelectViewHolder(TeamEntry object, boolean selected) {
            if (getSelectedData().size() > 1) {
                ((FloatingActionButton)getView().findViewById(R.id.teams_fab_team_edit)).setClickable(false);
                ((FloatingActionButton)getView().findViewById(R.id.teams_fab_team_edit)).setFocusable(false);
                ((FloatingActionButton)getView().findViewById(R.id.teams_fab_team_edit)).setEnabled(false);
            } else {
                ((FloatingActionButton)getView().findViewById(R.id.teams_fab_team_edit)).setClickable(true);
                ((FloatingActionButton)getView().findViewById(R.id.teams_fab_team_edit)).setFocusable(true);
                ((FloatingActionButton)getView().findViewById(R.id.teams_fab_team_edit)).setEnabled(true);
            }
            if (getSelectedData().size() == 0) {
                ((FloatingActionButton)getView().findViewById(R.id.teams_fab_team_remove)).setClickable(false);
                ((FloatingActionButton)getView().findViewById(R.id.teams_fab_team_remove)).setFocusable(false);
                ((FloatingActionButton)getView().findViewById(R.id.teams_fab_team_remove)).setEnabled(false);     
            } else {
                ((FloatingActionButton)getView().findViewById(R.id.teams_fab_team_remove)).setClickable(true);
                ((FloatingActionButton)getView().findViewById(R.id.teams_fab_team_remove)).setFocusable(true);
                ((FloatingActionButton)getView().findViewById(R.id.teams_fab_team_remove)).setEnabled(true);
            }
        }
    }

    public class PlayerAdapter extends SelectableAdapter<PlayerEntry> {

        public PlayerAdapter(ArrayList<PlayerEntry> data) {
            super(data);
        }

        @Override
        public String getNameText(PlayerEntry object) {
            return object.getName();
        }

        @Override
        public String getInfoText(PlayerEntry object) {
            return object.getNumber();
        }

        @Override
        public boolean isSelectionAllowed() {
            return true;
        }

        @Override
        public void onSelectViewHolder(PlayerEntry object, boolean selected) {
            if (getSelectedData().size() > 1) {
                ((FloatingActionButton)getView().findViewById(R.id.teams_fab_player_edit)).setClickable(false);
                ((FloatingActionButton)getView().findViewById(R.id.teams_fab_player_edit)).setFocusable(false);
                ((FloatingActionButton)getView().findViewById(R.id.teams_fab_player_edit)).setEnabled(false);
            } else {
                ((FloatingActionButton)getView().findViewById(R.id.teams_fab_player_edit)).setClickable(true);
                ((FloatingActionButton)getView().findViewById(R.id.teams_fab_player_edit)).setFocusable(true);
                ((FloatingActionButton)getView().findViewById(R.id.teams_fab_player_edit)).setEnabled(true);
            }
            if (getSelectedData().size() == 0) {
                ((FloatingActionButton)getView().findViewById(R.id.teams_fab_player_remove)).setClickable(false);
                ((FloatingActionButton)getView().findViewById(R.id.teams_fab_player_remove)).setFocusable(false);
                ((FloatingActionButton)getView().findViewById(R.id.teams_fab_player_remove)).setEnabled(false);
            } else {
                ((FloatingActionButton)getView().findViewById(R.id.teams_fab_player_remove)).setClickable(true);
                ((FloatingActionButton)getView().findViewById(R.id.teams_fab_player_remove)).setFocusable(true);
                ((FloatingActionButton)getView().findViewById(R.id.teams_fab_player_remove)).setEnabled(true);
            }
        }
    }

    public class FilterAdapter extends SelectableAdapter<TeamEntry> {

        public FilterAdapter(ArrayList<TeamEntry> data) {
            super(data);
        }

        @Override
        public String getNameText(TeamEntry object) {
            return object.getName();
        }

        @Override
        public String getInfoText(TeamEntry object) {
            return String.valueOf(object.getSize());
        }

        @Override
        public boolean isSelectionAllowed() {
            return true;
        }

        @Override
        public void onSelectViewHolder(TeamEntry object, boolean selected) {

        }
    }
}
