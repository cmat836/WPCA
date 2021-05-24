package com.cmat.wpca.fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
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
import com.cmat.wpca.data.TeamEntry;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class TeamFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    DataStore<PlayerEntry> playerData = new DataStore<>("players", PlayerEntry.class);
    DataStore<TeamEntry> teamData = new DataStore<>("teams", TeamEntry.class);

    final PopupWindow newTeamWindow = new PopupWindow(null,LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
    final PopupWindow newPlayerWindow = new PopupWindow(null,LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
    final PopupWindow removeTeamWindow = new PopupWindow(null,LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
    final PopupWindow removePlayerWindow = new PopupWindow(null,LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
    final PopupWindow transferPlayerWindow = new PopupWindow(null,LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
    final PopupWindow filterTeamWindow = new PopupWindow(null,LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
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

        view.findViewById(R.id.button_team_filter).setOnClickListener(this::onTeamFilterButtonClick);

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
        filterTeamWindow.setBackgroundDrawable(new ColorDrawable((Color.WHITE)));
        filterTeamWindow.setElevation(20);

    }

    private ArrayList<PlayerEntry> getTeamlessPlayers() {
        ArrayList<PlayerEntry> ret = new ArrayList<>();
        for (String p : playerData.getArrayOfEntryNames()) {
            if (!playerData.getEntry(p).hasTeam()) {
                ret.add(playerData.getEntry(p));
            }
        }
        return ret;
    }

    private ArrayList<PlayerEntry> getPlayersMatchingFilter() {
        ArrayList<PlayerEntry> ret = new ArrayList<>();
        for (String pname : playerData.getArrayOfEntryNames()) {
            for (String tname : playerData.getEntry(pname).getTeamList()) {
                if (teamData.getSelectedArray().contains(teamData.getEntry(tname))) {
                    ret.add(playerData.getEntry(pname));
                }
            }
        }
        return ret;
    }

    private void refreshTeamSelector() {
        //Spinner s = this.getView().findViewById(R.id.button_team_filter);
        //String [] data = teamData.getArrayOfEntryNames(true);
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, data);
        //s.setAdapter(adapter);
    }

    private void refreshTeamDisplay() {
        RecyclerView teamDisplay = (RecyclerView)this.getView().findViewById(R.id.teamdisplay_recyclerview);
        TeamAdapter teamAdapter = new TeamAdapter(this);
        teamDisplay.setAdapter(teamAdapter);
    }

    private void refreshPlayerDisplay() {
        RecyclerView playerDisplay = (RecyclerView)this.getView().findViewById(R.id.playerdisplay_recyclerview);
        PlayerAdapter playerAdapter = new PlayerAdapter(this);
        playerDisplay.setAdapter(playerAdapter);
    }

    private void onTeamFilterButtonClick(View view) {
        View filterTeamView = this.getLayoutInflater().inflate(R.layout.popupwindow_team_filter, null);

        filterTeamWindow.setContentView(filterTeamView);
        filterTeamWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        RecyclerView filterrec = (RecyclerView)filterTeamView.findViewById(R.id.teamfilter_recyclerview);
        filterrec.setLayoutManager(new LinearLayoutManager(getContext()));
        FilterAdapter filterAdapter = new FilterAdapter(this);
        filterrec.setAdapter(filterAdapter);

        filterTeamView.findViewById(R.id.filterteams_button).setOnClickListener(this::onTeamFiltered);

    }

    private void onTeamFiltered(View view) {
        View v = filterTeamWindow.getContentView();
        FilterAdapter f = (FilterAdapter) ((RecyclerView)v.findViewById(R.id.teamfilter_recyclerview)).getAdapter();
        teamData.setSelected(f.getNewSelectedList());
        refreshTeamDisplay();
        filterTeamWindow.dismiss();
    }

    private void onRemoveTeamButtonClick(View view) {
        View removeTeamView = this.getLayoutInflater().inflate(R.layout.popupwindow_team_remove, null);

        removeTeamWindow.setContentView(removeTeamView);
        removeTeamWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        removeTeamView.findViewById(R.id.removeteam_button).setOnClickListener(this::onTeamRemoved);
    }

    private void onTeamRemoved(View view) {

    }

    private void onAddTeamButtonClick(View view) {
        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View addTeamView = inflater.inflate(R.layout.popupwindow_team_create, null);

        newTeamWindow.setContentView(addTeamView);
        newTeamWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        addTeamView.findViewById(R.id.createteam_button).setOnClickListener(this::onTeamCreated);
    }

    private void onRemovePlayerButtonClick(View view) {
        View removePlayerView = this.getLayoutInflater().inflate(R.layout.popupwindow_player_remove, null);

        removePlayerWindow.setContentView(removePlayerView);
        removePlayerWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        removePlayerView.findViewById(R.id.removeplayer_button).setOnClickListener(this::onPlayerRemoved);
    }

    private void onPlayerRemoved(View view) {

    }

    private void onAddPlayerButtonClick(View view) {
        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View addPlayerView = inflater.inflate(R.layout.popupwindow_player_create, null);

        newPlayerWindow.setContentView(addPlayerView);
        newPlayerWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        addPlayerView.findViewById(R.id.createplayer_button).setOnClickListener(this::onPlayerCreated);
        Spinner team = (Spinner)addPlayerView.findViewById(R.id.spinner_team_assign);
        team.setAdapter(new ArrayAdapter<String>(getContext(), R.layout.spinner_item, teamData.getArrayOfEntryNames(true)));
    }

    private void onTeamCreated(View view) {
        View v = newTeamWindow.getContentView();
        String name = ((EditText)v.findViewById(R.id.team_name_edittext)).getText().toString();

        TeamEntry newTeam = new TeamEntry();
        newTeam.setName(name);

        teamData.setEntry(newTeam);
        teamData.refresh(getContext());

        refreshTeamSelector();
        refreshTeamDisplay();

        newTeamWindow.dismiss();
    }

    private void onPlayerCreated(View view) {
        View v = newPlayerWindow.getContentView();
        Spinner spin = (Spinner)v.findViewById(R.id.spinner_team_assign);
        String team = spin.getItemAtPosition(spin.getSelectedItemPosition()).toString();
        String name = ((EditText)v.findViewById(R.id.player_name_edittext)).getText().toString();
        String number = ((EditText)v.findViewById(R.id.player_number_edittext)).getText().toString();
        PlayerEntry.Handedness h = ((RadioButton)v.findViewById(R.id.left_radio)).isChecked() ? PlayerEntry.Handedness.LEFT : PlayerEntry.Handedness.RIGHT;
        boolean goalie = ((CheckBox)v.findViewById(R.id.goalie_checkbox)).isChecked();

        PlayerEntry newPlayer = new PlayerEntry.PlayerBuilder(name, number).handedness(h).setGoalie(goalie).build();
        if (team != "none") {
            newPlayer.addTeam(team);
            teamData.getEntry(team).addPlayer(name);
            teamData.markModified(team);
            teamData.refresh(getContext());
        }

        playerData.setEntry(newPlayer);
        playerData.refresh(getContext());

        refreshTeamDisplay();

        newPlayerWindow.dismiss();
    }

    private void onBackToStartButtonClick(View view) {
        NavHostFragment.findNavController(TeamFragment.this).popBackStack(R.id.StartPage, false);
    }

    private void onPlayerSelect(View v, String p) {
        playerData.setSelected(p);
        refreshPlayerDisplay();
    }

    private boolean onPlayerTransfer(View v, String name) {
        View content = this.getLayoutInflater().inflate(R.layout.popupwindow_player_transfer, null);
        transferPlayerWindow.setContentView(content);
        transferPlayerWindow.showAtLocation(v, Gravity.CENTER, 0, 0);

        ((TextView)transferPlayerWindow.getContentView().findViewById(R.id.playertransfer_name_textview)).setText(name);
        transferPlayerWindow.getContentView().findViewById(R.id.transferplayer_button).setOnClickListener(this::onTransferPlayer);

        RecyclerView filterrec = (RecyclerView)content.findViewById(R.id.playertransfer_recyclerview);
        filterrec.setLayoutManager(new LinearLayoutManager(getContext()));
        FilterAdapter filterAdapter = new FilterAdapter(this, playerData.getEntry(name).getTeamList());
        filterrec.setAdapter(filterAdapter);

        return true;
    }

    private void onTransferPlayer(View view) {
        String pname = ((TextView)transferPlayerWindow.getContentView().findViewById(R.id.playertransfer_name_textview)).getText().toString();
        View v = transferPlayerWindow.getContentView();
        FilterAdapter f = (FilterAdapter) ((RecyclerView)v.findViewById(R.id.playertransfer_recyclerview)).getAdapter();
        playerData.getEntry(pname).setTeams(f.getNewSelectedList());
        playerData.markModified(pname);
        for (String tname : teamData.getArrayOfEntryNames()) {
            if (f.getNewSelectedList().contains(tname)) {
                teamData.getEntry(tname).addPlayer(pname);
            } else {
                teamData.getEntry(tname).removePlayer(pname);
            }
            teamData.markModified(tname);
        }
        playerData.refresh(getContext());
        teamData.refresh(getContext());
        refreshTeamDisplay();
        transferPlayerWindow.dismiss();
    }

    private boolean onChangeName(View v) {
        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String name = (String)parent.getItemAtPosition(position);
        teamData.setSelected(name);
        refreshTeamDisplay();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public static class FilterAdapter extends RecyclerView.Adapter<CheckboxViewHolder> {
        TeamFragment context;
        ArrayList<String> tempList = new ArrayList<>();

        public FilterAdapter(TeamFragment context) {
            this.context = context;
            for (TeamEntry t : context.teamData.getSelectedArray()) {
                tempList.add(t.getName());
            }
        }

        public FilterAdapter(TeamFragment context, ArrayList<String> teamlist) {
            this.context = context;
            tempList = teamlist;
        }

        @NonNull
        @Override
        public CheckboxViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.checkbox_item, parent, false);
            return new CheckboxViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull TeamFragment.CheckboxViewHolder holder, int position) {
            String name = context.teamData.getEntry(context.teamData.getArrayOfEntryNames()[position]).getName();
            holder.getNameText().setText(name);
            if (tempList.contains(name)) {
                holder.getInfoText().setChecked(true);
            }
            holder.getInfoText().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    onTeamChecked(buttonView, isChecked, name);
                }
            });
        }

        public ArrayList<String> getNewSelectedList() {
            return tempList;
        }

        private void onTeamChecked(CompoundButton compoundButton, boolean b, String t) {
            if (b) {
                tempList.add(t);
            } else {
                tempList.remove(t);
            }
        }

        @Override
        public int getItemCount() {
            return context.teamData.getCount();
        }
    }
    public static class TeamAdapter extends RecyclerView.Adapter<InfoViewHolder> {
        TeamFragment context;

        ArrayList<PlayerEntry> entries;

        public TeamAdapter(TeamFragment context) {
            this.context = context;
            entries = context.getPlayersMatchingFilter();
        }

        @NonNull
        @Override
        public InfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.display_item, parent, false);
            return new InfoViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull InfoViewHolder holder, int position) {
            if (entries.size() == 0) {
                PlayerEntry player = context.playerData.getEntry(context.playerData.getArrayOfEntryNames()[position]);
                holder.getNameText().setText(player.getName());
                holder.getInfoText().setText(player.getNumber());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        context.onPlayerSelect(v, player.getName());
                    }
                });
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        return context.onPlayerTransfer(v, player.getName());
                    }
                });
            } else {
                PlayerEntry player = entries.get(position);
                holder.getNameText().setText(player.getName());
                holder.getInfoText().setText(player.getNumber());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        context.onPlayerSelect(v, player.getName());
                    }
                });
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        return context.onPlayerTransfer(v, player.getName());
                    }
                });
            }
        }


        @Override
        public int getItemCount() {
            if (entries.size() == 0) {
                return context.playerData.getCount();
            } else {
                return entries.size();
            }
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
                        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                return context.onChangeName(v);
                            }
                        });
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
            return 6;
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

    public static class CheckboxViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameText;
        private final CheckBox checkbox;

        public CheckboxViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = (TextView) itemView.findViewById(R.id.textViewName);
            checkbox = (CheckBox) itemView.findViewById(R.id.checkboxbox);
        }

        public TextView getNameText() {
            return nameText;
        }

        public CheckBox getInfoText() {
            return checkbox;
        }
    }
}
