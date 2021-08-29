package com.cmat.wpca.fragments;

import android.annotation.SuppressLint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cmat.wpca.R;
import com.cmat.wpca.data.DataStore;
import com.cmat.wpca.data.entry.GameEntry;
import com.cmat.wpca.data.event.ExclusionEvent;
import com.cmat.wpca.data.event.Game;
import com.cmat.wpca.data.TaskTimer;
import com.cmat.wpca.data.event.GoalEvent;
import com.cmat.wpca.data.event.IGameEvent;
import com.cmat.wpca.data.event.MisconductEvent;
import com.cmat.wpca.data.entry.PlayerEntry;
import com.cmat.wpca.data.entry.RulesetEntry;
import com.cmat.wpca.data.event.OppositionGoalEvent;
import com.cmat.wpca.data.event.OppositionPlayerChangeEvent;
import com.cmat.wpca.data.event.PossessionChangeEvent;
import com.cmat.wpca.data.event.QuarterBreakEvent;
import com.cmat.wpca.data.event.StatisticEvent;
import com.cmat.wpca.data.event.SubstitutionEvent;
import com.cmat.wpca.data.event.SwimoffEvent;
import com.cmat.wpca.data.entry.TeamEntry;
import com.cmat.wpca.data.event.TimeoutEvent;
import com.cmat.wpca.ui.DisplayViewHolder;

import java.util.ArrayList;
import java.util.Arrays;

/*
-//opp goal//
-review
-//uneven -on/off//
-//scoreboard//
-//noteable//
-//Timer stored in game//
-//Timer starts on swimoff//
-//Timer pauses on quarter//
-//Displayed timer during timeouts displays relative to event start//
-uneven and timeout button, flash every 5 seconds
-//time show when uneven or timeout//
-//possession toggle//
-brutality, misconduct and exclusion, trigger uneven
-track exclusions on players
-uneven triggerd = self, manual triggerd = them
-Turnover submenu
-automatically turn's over ball

quarter button
-//trigger quarter mode//
-mass sub
-//pause time//
-//swimoff starts quarter//
-after final quarter close button

Timeout opp vs home

-Goalie always bottom

-BIG UNDO ON LONG PRESS
    -Set up gameevents to have apply and undo functions in interface
    -Functions recieve a Game instance and need to use only that to perform actions
    -Timers must be moved into Game and abstraction provided
 */

@SuppressLint("ClickableViewAccessibility")
public class GameRecordFragment extends Fragment {
    DataStore<PlayerEntry> Players = new DataStore<>("players", PlayerEntry.class);
    DataStore<TeamEntry> Teams = new DataStore<>("teams", TeamEntry.class);
    DataStore<RulesetEntry> Rulesets = new DataStore<>("rulesets", RulesetEntry.class);
    DataStore<GameEntry> SavedGames = new DataStore<>("savedgames", GameEntry.class);

    Game game;

    PopupWindow eventMenu = new PopupWindow(null, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
    PopupWindow shotMenu = new PopupWindow(null, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
    PopupWindow extraMenu = new PopupWindow(null, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
    PopupWindow substitutionMenu = new PopupWindow(null, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
    PopupWindow misconductMenu = new PopupWindow(null, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
    PopupWindow swimoffMenu = new PopupWindow(null, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
    PopupWindow turnoverMenu = new PopupWindow(null, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
    PopupWindow playbackMenu = new PopupWindow(null, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);

    String rulesetName;
    String teamName;

    ArrayList<Button> playerButtons = new ArrayList<>();

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_game_record, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Load Datastores
        Players.load(getContext());
        Teams.load(getContext());
        Rulesets.load(getContext());
        //SavedGames.load(getContext());

        int backgroundcolor = getColor(R.attr.colorOnPrimary);

        // Set base properties on the popupwindows
        eventMenu.setAnimationStyle(android.R.style.Animation);
        eventMenu.setBackgroundDrawable(new ColorDrawable(backgroundcolor));
        eventMenu.setElevation(20);
        shotMenu.setAnimationStyle(android.R.style.Animation);
        shotMenu.setBackgroundDrawable(new ColorDrawable((backgroundcolor)));
        shotMenu.setElevation(20);
        extraMenu.setAnimationStyle(android.R.style.Animation);
        extraMenu.setBackgroundDrawable(new ColorDrawable((backgroundcolor)));
        extraMenu.setElevation(20);
        substitutionMenu.setAnimationStyle(android.R.style.Animation);
        substitutionMenu.setBackgroundDrawable(new ColorDrawable((backgroundcolor)));
        substitutionMenu.setElevation(20);
        misconductMenu.setAnimationStyle(android.R.style.Animation);
        misconductMenu.setBackgroundDrawable(new ColorDrawable((backgroundcolor)));
        misconductMenu.setElevation(20);
        swimoffMenu.setAnimationStyle(android.R.style.Animation);
        swimoffMenu.setBackgroundDrawable(new ColorDrawable((backgroundcolor)));
        swimoffMenu.setElevation(20);
        turnoverMenu.setAnimationStyle(android.R.style.Animation);
        turnoverMenu.setBackgroundDrawable(new ColorDrawable((backgroundcolor)));
        turnoverMenu.setElevation(20);
        playbackMenu.setAnimationStyle(android.R.style.Animation);
        playbackMenu.setBackgroundDrawable(new ColorDrawable((backgroundcolor)));
        playbackMenu.setElevation(20);

        // Get arguments from game setup
        rulesetName = getArguments().getString("rulesetName");
        ArrayList<String> selectedPlayersArg = new ArrayList<>(Arrays.asList(getArguments().getStringArray("selectedPlayers")));
        teamName = getArguments().getString("teamName");

        Teams.setSelected(teamName);

        ArrayList<PlayerEntry> players = new ArrayList<>();
        ArrayList<PlayerEntry> selectedPlayers = new ArrayList<>();
        for (String s : Teams.getSelected().getNameList()) {
            players.add(Players.getEntry(s));
            if (selectedPlayersArg.contains(s)) {
                selectedPlayers.add(Players.getEntry(s));
            }
        }

        game = new Game(players, selectedPlayers);



        // Refresh the UI
        refreshScoreboard();

        // Setup event listeners for UI elements
        ((ToggleButton)requireView().findViewById(R.id.record_toggle_possession)).setOnCheckedChangeListener(this::possessionButton_Toggled);

        getButton(R.id.record_button_oppgoal).setOnClickListener(this::oppGoalButton_Pressed);
        getButton(R.id.record_button_notable_play).setOnClickListener(this::notablePlayButton_Pressed);
        getButton(R.id.record_button_playback_open).setOnClickListener(this::playbackButton_Pressed);
        getButton(R.id.record_button_uneven).setOnClickListener(this::unevenButton_Pressed);
        getButton(R.id.record_button_quarter).setOnLongClickListener(this::quarterButton_Pressed);
        getButton(R.id.record_button_timeout).setOnClickListener(this::timeoutButton_Pressed);

        getView().findViewById(R.id.record_linearlayout_replay_container).setOnLongClickListener(this::replayContainer_LongPressed);

        // Put all the player buttons in an array for easy access
        playerButtons.add(getButton(R.id.record_button_player_1));
        playerButtons.add(getButton(R.id.record_button_player_2));
        playerButtons.add(getButton(R.id.record_button_player_3));
        playerButtons.add(getButton(R.id.record_button_player_4));
        playerButtons.add(getButton(R.id.record_button_player_5));
        playerButtons.add(getButton(R.id.record_button_player_6));
        playerButtons.add(getButton(R.id.record_button_player_7));

        // Dynamically add their listeners and set content
        for (int i = 0; i < playerButtons.size(); i++) {
            int finalI = i;
            playerButtons.get(i).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return playerButton_Pressed(finalI, v, event);
                }
            });
            playerButtons.get(i).setText(game.getSelectedPlayers().get(i).getName());
        }


    }

    private void playbackButton_Pressed(View v) {
        View content = this.getLayoutInflater().inflate(R.layout.popup_playback_display, null);
        playbackMenu.setContentView(content);
        playbackMenu.showAtLocation(v, Gravity.CENTER, 0, 0);
        //v.performClick();

        RecyclerView r = ((RecyclerView)content.findViewById(R.id.playback_display_recycler_events));
        r.setLayoutManager(new LinearLayoutManager(getContext()));
        PlaybackAdapter adapter = new PlaybackAdapter(this);
        r.setAdapter(adapter);

        content.findViewById(R.id.playback_display_button_return).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playbackMenu.dismiss();
            }
        });
    }

    private boolean replayContainer_LongPressed(View view) {
        game.removeEvent(game.getEvent());
        refreshReplayTable();
        return false;
    }
    private void timeoutButton_Pressed(View view) {
        if (game.isTimeout()) {
            game.addEvent(new TimeoutEvent(TimeoutEvent.TimeoutState.END, this::refreshTimeoutButton));
        } else {
            if (!game.isQuarterBreak()) {
                game.addEvent(new TimeoutEvent(TimeoutEvent.TimeoutState.START, this::refreshTimeoutButton));
            }
        }
        refreshReplayTable();
        refreshTimeoutButton(0L);
    }

    private void possessionButton_Toggled(CompoundButton compoundButton, boolean b) {
        game.addEvent(new PossessionChangeEvent(null, b ? PossessionChangeEvent.PossessionChangeType.TURNOVER : PossessionChangeEvent.PossessionChangeType.STEAL));
        refreshReplayTable();
    }

    private boolean quarterButton_Pressed(View view) {
        if (!game.isQuarterBreak()) {
            game.addEvent(new QuarterBreakEvent(game.getQuarter()));

            Button qb = getButton(R.id.record_button_quarter);
            qb.setText(R.string.record_button_quarter_between);
            qb.setBackgroundColor(getColor(R.attr.colorAlert));
            refreshReplayTable();
        }
        return false;
    }


    private void refreshPlayerButtons() {
        for (int i = 0; i < playerButtons.size(); i++) {
            playerButtons.get(i).setText(game.getSelectedPlayers().get(i).getName());
        }
    }

    public void refreshScoreboard() {
        ((TextView)getView().findViewById(R.id.record_text_score)).setText(game.getHomeGoals() + ":" + game.getOppGoals());
    }

    public void refreshTurnoverButton() {
        ((ToggleButton)getView().findViewById(R.id.record_toggle_possession)).setChecked(game.getPossession() == Game.Team.OPPOSITION);
    }

    public void refreshTimeoutButton(long millis) {
        Button timeoutButton = getButton(R.id.record_button_timeout);
        if (game.isTimeout()) {
            timeoutButton.setBackgroundColor(getColor(R.attr.colorAlert));
            timeoutButton.setText(TaskTimer.millisToString(millis));
        } else {
            timeoutButton.setBackgroundColor(getColor(R.attr.colorPrimary));
            timeoutButton.setText(R.string.record_button_timeout);
        }
    }

    public void refreshUnevenButton(long millis) {
        Button unevenButton = getButton(R.id.record_button_uneven);
        if (game.isUneven()) {
            unevenButton.setBackgroundColor(getColor(R.attr.colorAlert));
            unevenButton.setText(TaskTimer.millisToString(millis));
        } else {
            unevenButton.setBackgroundColor(getColor(R.attr.colorPrimary));
            unevenButton.setText(R.string.record_button_uneven);
        }
        refreshReplayTable();
    }

    public void refreshQuarterButton() {
        Button qb = getButton(R.id.record_button_quarter);
        if (game.isQuarterBreak()) {
            qb.setText(R.string.record_button_quarter_between);
            qb.setBackgroundColor(getColor(R.attr.colorAlert));
        } else {
            qb.setText(game.getQuarter().toString());
            qb.setBackgroundColor(getColor(R.attr.colorPrimary));
        }
    }

    private boolean playerButton_Pressed(int buttonNumber, View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View content = this.getLayoutInflater().inflate(R.layout.popup_event_select, null);
            eventMenu.setContentView(content);
            eventMenu.showAtLocation(v, Gravity.CENTER, 0, 0);
            v.performClick();
            return true;
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (checkEventWithinView(eventMenu.getContentView().findViewById(R.id.event_select_button_shot), event)) {
                eventMenu_shotButton_Pressed(buttonNumber, v);
                return true;
            }
            if (checkEventWithinView(eventMenu.getContentView().findViewById(R.id.event_select_button_go_back), event)) {
                eventMenu.dismiss();
                return true;
            }
            if (checkEventWithinView(eventMenu.getContentView().findViewById(R.id.event_select_button_turnover), event)) {
                eventMenu_turnoverButton_Pressed(buttonNumber, v);
                return true;
            }
            if (checkEventWithinView(eventMenu.getContentView().findViewById(R.id.event_select_button_exclusion), event)) {
                eventMenu_exclusionButton_Pressed(buttonNumber, v);
                return true;
            }
            if (checkEventWithinView(eventMenu.getContentView().findViewById(R.id.event_select_button_block), event)) {
                eventMenu_blockButton_Pressed(buttonNumber, v);
                return true;
            }
            if (checkEventWithinView(eventMenu.getContentView().findViewById(R.id.event_select_button_badpass), event)) {
                eventMenu_badpassButton_Pressed(buttonNumber, v);
                return true;
            }
            if (checkEventWithinView(eventMenu.getContentView().findViewById(R.id.event_select_button_extra), event)) {
                eventMenu_extraButton_Pressed(buttonNumber, v);
                return true;
            }
        }
        return false;

    }

    public boolean checkEventWithinView(View v, MotionEvent event) {
        int x = (int) event.getRawX();
        int y = (int) event.getRawY();
        int[] loc = new int[2];
        v.getLocationOnScreen(loc);
        int vx = loc[0];
        int vy = loc[1];
        int w = v.getWidth();
        int h = v.getHeight();
        if (x > vx && x < vx + w && y > vy && y < vy + h) {
            return true;
        }
        return false;
    }

    public int getColor(int resid) {
        TypedValue typedValue = new TypedValue();
        getContext().getTheme().resolveAttribute(resid, typedValue, true);
        return typedValue.data;
    }

    private void refreshReplayTable() {
        ((TextView)getView().findViewById(R.id.record_text_replay_playernumber)).setText(game.getEvent().getPlayer().getNumber());
        ((TextView)getView().findViewById(R.id.record_text_replay_event)).setText(game.getEvent().getEventText());
        ((TextView)getView().findViewById(R.id.record_text_replay_timestamp)).setText(TaskTimer.millisToString(game.getEvent().getTime()));
    }

    private void oppGoalButton_Pressed(View view) {
        game.addEvent(new OppositionGoalEvent());
        refreshScoreboard();
        refreshReplayTable();
    }

    private void unevenButton_Pressed(View view) {
        if (!game.isUneven() && !game.isQuarterBreak()) {
            game.addEvent(new OppositionPlayerChangeEvent(OppositionPlayerChangeEvent.ChangeType.LOST, this::refreshUnevenButton));
        } else {
            game.addEvent(new OppositionPlayerChangeEvent(OppositionPlayerChangeEvent.ChangeType.GAINED, this::refreshUnevenButton));
        }
        refreshReplayTable();
    }

    private void notablePlayButton_Pressed(View view) {
        game.getEvent().setNotable(true);
        //SavedGames.setEntry(game.buildEntry("g1"));
        //SavedGames.refresh(getContext());
        //GameEntry g = SavedGames.getEntry(SavedGames.getArrayOfEntryNames()[0]);
        //g.getName();
    }

    private void eventMenu_shotButton_Pressed(int buttonNumber, View v) {
        eventMenu.dismiss();
        View content = this.getLayoutInflater().inflate(R.layout.popup_shot_menu, null);
        shotMenu.setContentView(content);
        shotMenu.showAtLocation(v, Gravity.CENTER, 0, 0);

        content.findViewById(R.id.shot_menu_button_goal).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                game.addEvent(new GoalEvent(game.getSelectedPlayers().get(buttonNumber)));
                refreshScoreboard();
                refreshReplayTable();
                shotMenu.dismiss();
            }
        });
        content.findViewById(R.id.shot_menu_button_block).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                game.addEvent(new StatisticEvent(game.getSelectedPlayers().get(buttonNumber), StatisticEvent.StatisticEventType.BLOCKED));
                refreshReplayTable();
                shotMenu.dismiss();
            }
        });
        content.findViewById(R.id.shot_menu_button_miss).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                game.addEvent(new StatisticEvent(game.getSelectedPlayers().get(buttonNumber), StatisticEvent.StatisticEventType.MISS));
                refreshReplayTable();
                shotMenu.dismiss();
            }
        });
    }

    private void eventMenu_turnoverButton_Pressed(int buttonNumber, View v) {
        eventMenu.dismiss();
        View content = this.getLayoutInflater().inflate(R.layout.popup_turnover_menu, null);
        turnoverMenu.setContentView(content);
        turnoverMenu.showAtLocation(v, Gravity.CENTER, 0, 0);

        content.findViewById(R.id.turnover_menu_button_steal).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                game.addEvent(new PossessionChangeEvent(game.getSelectedPlayers().get(buttonNumber), PossessionChangeEvent.PossessionChangeType.STEAL));
                turnoverMenu.dismiss();
                refreshTurnoverButton();
                refreshReplayTable();
            }
        });
        content.findViewById(R.id.turnover_menu_button_turnover).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                game.addEvent(new PossessionChangeEvent(game.getSelectedPlayers().get(buttonNumber), PossessionChangeEvent.PossessionChangeType.TURNOVER));
                turnoverMenu.dismiss();
                refreshTurnoverButton();
                refreshReplayTable();
            }
        });
    }

    private void eventMenu_blockButton_Pressed(int buttonNumber, View v) {
        game.addEvent(new StatisticEvent(game.getSelectedPlayers().get(buttonNumber), StatisticEvent.StatisticEventType.BLOCK));
        eventMenu.dismiss();
        refreshReplayTable();
    }

    private void eventMenu_badpassButton_Pressed(int buttonNumber, View v) {
        game.addEvent(new StatisticEvent(game.getSelectedPlayers().get(buttonNumber), StatisticEvent.StatisticEventType.BADPASS));
        eventMenu.dismiss();
        refreshReplayTable();
    }

    private void eventMenu_extraButton_Pressed(int buttonNumber, View v) {
        eventMenu.dismiss();
        View content = this.getLayoutInflater().inflate(R.layout.popup_extra_select, null);
        extraMenu.setContentView(content);
        extraMenu.showAtLocation(v, Gravity.CENTER, 0, 0);

        content.findViewById(R.id.extra_select_button_sub).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                extraMenu.dismiss();
                extraMenu_subButton_Pressed(buttonNumber, v, false);
            }
        });
        content.findViewById(R.id.extra_select_button_card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                extraMenu.dismiss();
                extraMenu_cardButton_Pressed(buttonNumber, v);
            }
        });
        content.findViewById(R.id.extra_select_button_swimoff).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                extraMenu.dismiss();
                extraMenu_swimoffButton_Pressed(buttonNumber, v);
            }
        });
        content.findViewById(R.id.extra_select_button_penalty).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                extraMenu.dismiss();
                extraMenu_penaltyButton_Pressed(buttonNumber, v);
            }
        });
    }

    private void extraMenu_subButton_Pressed(int buttonNumber, View v, boolean mandatory) {
        View content = this.getLayoutInflater().inflate(R.layout.popup_substitution_menu, null);
        substitutionMenu.setContentView(content);
        substitutionMenu.showAtLocation(v, Gravity.CENTER, 0, 0);
        RecyclerView r = ((RecyclerView)content.findViewById(R.id.subsitution_menu_recycler_players));
        r.setLayoutManager(new LinearLayoutManager(getContext()));
        PlayerSubAdapter adapter = new PlayerSubAdapter(this, game.getSelectedPlayers().get(buttonNumber).getName(), buttonNumber, mandatory);
        r.setAdapter(adapter);
        content.findViewById(R.id.substitution_menu_button_return).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                substitutionMenu.dismiss();
            }
        });

    }

    private void extraMenu_cardButton_Pressed(int buttonNumber, View v) {
        View content = this.getLayoutInflater().inflate(R.layout.popup_misconduct_menu, null);
        misconductMenu.setContentView(content);
        misconductMenu.showAtLocation(v, Gravity.CENTER, 0, 0);
        content.findViewById(R.id.misconduct_menu_button_brutality).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                misconductMenu_Button_Pressed(buttonNumber, v, MisconductEvent.MisconductType.BRUTALITY);
                misconductMenu.dismiss();
            }
        });
        content.findViewById(R.id.misconduct_menu_button_misconduct).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                misconductMenu_Button_Pressed(buttonNumber, v, MisconductEvent.MisconductType.MISCONDUCT);
                misconductMenu.dismiss();
            }
        });
    }

    private void extraMenu_swimoffButton_Pressed(int buttonNumber, View v) {
        View content = this.getLayoutInflater().inflate(R.layout.popup_swimoff_menu, null);
        swimoffMenu.setContentView(content);
        swimoffMenu.showAtLocation(v, Gravity.CENTER, 0, 0);
        content.findViewById(R.id.swimoff_menu_button_win).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                game.addEvent(new SwimoffEvent(game.getSelectedPlayers().get(buttonNumber), SwimoffEvent.SwimoffResult.WIN));
                swimoffMenu.dismiss();
                refreshReplayTable();
                refreshQuarterButton();
            }
        });
        content.findViewById(R.id.swimoff_menu_button_lose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                game.addEvent(new SwimoffEvent(game.getSelectedPlayers().get(buttonNumber), SwimoffEvent.SwimoffResult.LOSS));
                swimoffMenu.dismiss();
                refreshReplayTable();
                refreshQuarterButton();
            }
        });
    }

    private void extraMenu_penaltyButton_Pressed(int buttonNumber, View v) {
        //AddGameEvent(new ShotEvent(ShotEvent.ShotType.GOAL, Players.getEntry(selectedPlayers.get(buttonNumber))));
    }

    private void eventMenu_exclusionButton_Pressed(int buttonNumber, View v) {
        ExclusionEvent e =  new ExclusionEvent(game.getSelectedPlayers().get(buttonNumber), new Consumer<Long>() {
            @Override
            public void accept(Long aLong) {
                playerButtons.get(buttonNumber).setBackgroundColor(getColor(R.attr.colorAlert));
                playerButtons.get(buttonNumber).setText(game.getSelectedPlayers().get(buttonNumber).getNumber() + " " + TaskTimer.millisToString(20000 - aLong));
            }
        }, new Consumer<Long>() {
            @Override
            public void accept(Long aLong) {
                playerButtons.get(buttonNumber).setText(game.getSelectedPlayers().get(buttonNumber).getName());
                playerButtons.get(buttonNumber).setBackgroundColor(getColor(R.attr.colorDisabled));
            }
        });
        game.addEvent(e);
        refreshReplayTable();

        // Disable to event menu for this player while they're excluded
        playerButtons.get(buttonNumber).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });

        // Set it so that it will End of long press
        playerButtons.get(buttonNumber).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                e.end(game);
                playerButtons.get(buttonNumber).setText(game.getSelectedPlayers().get(buttonNumber).getName());
                playerButtons.get(buttonNumber).setBackground(getButton(R.id.record_button_oppgoal).getBackground());

                // Restore the event menu
                playerButtons.get(buttonNumber).setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return playerButton_Pressed(buttonNumber, v, event);
                    }
                });
                return false;
            }
        });
        eventMenu.dismiss();
    }

    private void misconductMenu_Button_Pressed(int buttonNumber, View v, MisconductEvent.MisconductType type) {
        String note = ((EditText)misconductMenu.getContentView().findViewById(R.id.misconduct_menu_edit_note)).getText().toString();
        MisconductEvent e = new MisconductEvent(game.getSelectedPlayers().get(buttonNumber), type, note, new Consumer<Long>() {
            @Override
            public void accept(Long aLong) {
                int time = (type == MisconductEvent.MisconductType.MISCONDUCT ? 20000 : 240000);
                playerButtons.get(buttonNumber).setBackgroundColor(getColor(R.attr.colorAlert));
                playerButtons.get(buttonNumber).setText(game.getSelectedPlayers().get(buttonNumber).getNumber() + " " + TaskTimer.millisToString(time - aLong));
            }
        }, new Consumer<Long>() {
            @Override
            public void accept(Long aLong) {
                playerButtons.get(buttonNumber).setText(game.getSelectedPlayers().get(buttonNumber).getName());
                playerButtons.get(buttonNumber).setBackgroundColor(getColor(R.attr.colorDisabled));
            }
        });
        game.addEvent(e);

        // Disable to event menu for this player while they're excluded
        playerButtons.get(buttonNumber).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });

        // Set it so that it will End of long press, but needs a sub
        playerButtons.get(buttonNumber).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                extraMenu_subButton_Pressed(buttonNumber, v, true);
                e.end(game);
                playerButtons.get(buttonNumber).setText(game.getSelectedPlayers().get(buttonNumber).getName());
                playerButtons.get(buttonNumber).setBackgroundColor(getColor(R.attr.colorDisabled));
                return false;
            }
        });

        refreshReplayTable();
    }

    private Button getButton(int id) {
        return (Button)(getView().findViewById(id));
    }

    private TextView getTextView(int id) {
        return (TextView)(getView().findViewById(id));
    }

    private EditText getEditText(int id) {
        return (EditText)(getView().findViewById(id));
    }

    public class PlaybackAdapter extends RecyclerView.Adapter<DisplayViewHolder> {
        GameRecordFragment context;

        public PlaybackAdapter(GameRecordFragment gameRecordFragment) {
            context = gameRecordFragment;
        }

        @NonNull
        @Override
        public DisplayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_display, parent, false);
            return new DisplayViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull DisplayViewHolder holder, int position) {
            IGameEvent event = context.game.getEvent(position);
            holder.getNameText().setText(event.getPlayer().getNumber() + " " + event.getEventText());
            holder.getInfoText().setText(TaskTimer.millisToString(event.getTime()));
        }

        @Override
        public int getItemCount() {
            return context.game.getEventCount();
        }
    }

    public class PlayerSubAdapter extends RecyclerView.Adapter<DisplayViewHolder>
    {
        GameRecordFragment context;
        String playerName;
        ArrayList<String> playerNames = new ArrayList<>();
        boolean mandatory = false;
        int buttonNumber;

        public PlayerSubAdapter(GameRecordFragment gameRecordFragment, String playerName, int buttonNumber, boolean mandatory) {
            super();
            context = gameRecordFragment;
            this.playerName = playerName;
            for (String s : context.Teams.getSelected().getNameList()) {
                if (!context.game.getSelectedPlayers().contains(context.Players.getEntry(s))) {
                    playerNames.add(s);
                }
            }
            this.mandatory = mandatory;
            this.buttonNumber = buttonNumber;
        }

        @Override
        public DisplayViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_display, parent, false);
            return new DisplayViewHolder(v);
        }

        @Override
        public void onBindViewHolder(DisplayViewHolder holder, int position) {
            TeamEntry t = context.Teams.getSelected();
            String name = playerNames.get(position);
            holder.getNameText().setText(name);
            holder.getInfoText().setText(context.Players.getEntry(name).getNumber());
            holder.itemView.setLongClickable(true);
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    substitutionMenu.dismiss();
                    game.addEvent(new SubstitutionEvent(context.Players.getEntry(playerName), context.Players.getEntry(name)));

                    if (mandatory) {
                        playerButtons.get(buttonNumber).setText(game.getSelectedPlayers().get(buttonNumber).getName());
                        playerButtons.get(buttonNumber).setBackground(new ColorDrawable(getColor(R.attr.colorPrimary)));

                        // Restore the event menu
                        playerButtons.get(buttonNumber).setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                return playerButton_Pressed(buttonNumber, v, event);
                            }
                        });
                    }

                    refreshPlayerButtons();
                    refreshReplayTable();
                    return false;
                }
            });
        }

        @Override
        public int getItemCount() {
            return playerNames.size();
        }
    }

}